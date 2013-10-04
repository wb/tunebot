package com.nextbigsound.tunebot.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nextbigsound.tunebot.commands.CommandException;

/**
 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server.
 * <p>
 * Please note the purpose of this application is demonstrate the usage of HttpCore APIs.
 * It is NOT intended to demonstrate the most efficient way of building an HTTP file server.
 *
 *
 */
public class WebServer {
	
	/**
	 * These are the available routes. Instantiate new routes and append to array here.
	 */
	private static final Route[] routes = {
		new NextTrackRoute(),
		new GetTrackRoute(),
		new PlayTrackRoute(),
		new ChangeVolumeRoute(),
		new PlayPauseRoute(),
		new PreviousTrackRoute(),
		new GetQueueRoute(),
		new SearchTrackRoute(),
		new GetStateRoute(),
	};
    
    public static void start(int port) throws Exception {
    	Thread t = new RequestListenerThread(port);
        t.setDaemon(false);
        t.start();
    }

    static class HttpFileHandler implements HttpRequestHandler  {

    	private static JsonParser jsonParser = new JsonParser();
    	
        public HttpFileHandler() {
            super();
        }

        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }
            String target = request.getRequestLine().getUri();
            System.out.println("Request for " + target);

            String payload = null;
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                byte[] entityContent = EntityUtils.toByteArray(entity);
                if (entityContent.length > 0) {
                    System.out.println("Incoming entity content (bytes): " + entityContent.length);
                	payload = new String(entityContent);
                }
            }
            
            // check each route until we find a match - once match is found, stop looking and return
            System.out.println("Finding match for '" + method + "' '" + target + "'");
            for (Route route : routes) {
            	if (route.matches(method, target)) {
            		
            		String output = null;
					try {
						if (payload != null) {
							System.out.println("Payload: " + payload);
							JsonObject json = (JsonObject) jsonParser.parse(payload);
							output = route.process(json);
						} else {
							output = route.process();
						}
						response.setStatusCode(HttpStatus.SC_OK);
						System.out.println("Ok");
					} catch (CommandException e) {
						JsonObject error = new JsonObject();
						error.addProperty("code", e.code);
						error.addProperty("message", e.getMessage());
						output = error.toString();
						response.setStatusCode(e.code);
						System.out.println("Setting status code to " + e.code + ".");
					} catch (JsonSyntaxException e) {
						JsonObject error = new JsonObject();
						error.addProperty("code", HttpStatus.SC_BAD_REQUEST);
						error.addProperty("message", "Invalid JSON payload.");
						output = error.toString();
						response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
						System.err.println("Bad request.");
					} catch (Exception e) {
						e.printStackTrace();
						JsonObject error = new JsonObject();
						error.addProperty("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
						error.addProperty("message", "Internal server error.");
						output = error.toString();
						response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
						System.err.println("Internal server error.");
					}
					
					if (output != null) {
						response.setEntity(new StringEntity(output, ContentType.create("application/json", "UTF-8")));
						System.out.println("Response: " + output);
					}
					return;
            	}
            }
            
            // if we find no match, 404
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            JsonObject error = new JsonObject();
			error.addProperty("code", 404);
			error.addProperty("message", "Method not found.");
            response.setEntity(new StringEntity(error.toString(), ContentType.create("application/json", "UTF-8")));
        }

    }

    static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;

        public RequestListenerThread(int port) throws IOException {
            this.serversocket = new ServerSocket(port);
            this.params = new SyncBasicHttpParams();
            this.params
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] {
                    new ResponseDate(),
                    new ResponseServer(),
                    new ResponseContent(),
                    new ResponseConnControl()
            });

            // Set up request handlers
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            reqistry.register("*", new HttpFileHandler());

            // Set up the HTTP service
            this.httpService = new HttpService(
                    httpproc,
                    new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory(),
                    reqistry,
                    this.params);
        }

        @Override
        public void run() {
            System.out.println("Listening on port " + this.serversocket.getLocalPort());
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    System.out.println("Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    System.err.println("I/O error initialising connection thread: "
                            + e.getMessage());
                    break;
                }
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(
                final HttpService httpservice,
                final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {}
            }
        }

    }

}