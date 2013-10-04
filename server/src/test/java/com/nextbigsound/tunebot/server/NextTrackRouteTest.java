package com.nextbigsound.tunebot.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nextbigsound.tunebot.server.NextTrackRoute;
import com.nextbigsound.tunebot.server.Route;

public class NextTrackRouteTest {

	@Test
	public void test() {
		Route r = new NextTrackRoute();
		assertTrue("Route does not match.", r.matches("POST", "/nextTrack"));
		assertTrue("Route does not match with trailing slash.", r.matches("POST", "/nextTrack/"));
	}

}
