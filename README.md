# tunebot

Our music robot.

## What's Inside?

Tunebot is a script for [hubot](https://github.com/github/hubot) and a server that controls Spotify. When you connect to tunebot through your favorite supported chat client (we use HipChat), tunebot relays commands to the server which in turn executes various AppleScript commands to control the Spotify application.

## Getting Started

The client itself can be run on any computer that can run hubot. However, the server must be run on a Mac that has Spotify installed. To keep things simple, we run the client and server on the same machine. While we've gotten this to run on various versions of OS X, we have had the most success with OS X 10.8 and have used it to help prepare this README.

Before we can get tunebot up and running, we need to get hubot installed, along with an adapter for your favorite chat client. Check out [this document](https://github.com/github/hubot/blob/master/docs/README.md) for help installing hubot, and refer to [this document](https://github.com/github/hubot/blob/master/docs/adapters.md) for help installing your adapter.

### Installing and Configuring the Server

The server is self-contained - to get it working, just run `bin/server.sh`. You should see something like this*:

```
$ bin/server.sh 
Listening on port 8080
```

*The first time you run this script, it will build the server for you, so you may see more output. If you see `Listening on port 8080` at the end, you're all set.

### Installing and Configuring the Client

Installing the client is as easy as copying `tunebot.coffee` into your hubot scripts directory, usually `hubot/scripts`. If you are running your client and server on the same machine, you're done! If you are running your server on a different machine, edit `SERVER` at the top of `tunebot.coffee` so it points at your server.

To help you test tunebot, we've included the script `bin/client.sh` - it assumes you have installed hubot in `/opt/hubot`, so edit the script if you have installed hubot in a different location. Now, verify your client is working by running `bin/client.sh` and typing `tunebot blame Mallory`. You should see the following output:

```
$ bin/client.sh 
tunebot> tunebot blame Mallory
tunebot> Mallory did it!
```

Once this is working, you'll want to modify `bin/client.sh` to configure hubot for your specific chat client - if you haven't done this already, check out the section on [getting started](#getting-started).

### Let's play some music!

Now that both the client and server are running (and Spotify, of course), let's play some music with the command `tunebot dj`. You can use your chat client, but we'll keep using the Shell client from before:

```
tunebot> tunebot dj
tunebot> Alright, yo. Playing the NBS playlist.
```

If the music starts playing, you're done!

## Usage

Typing `tunebot help` will bring up the common commands. We'll let you dig around in `tunebot.coffee` for the complete list :)

```
tunebot> tunebot help
tunebot> Usage:
tunebot what song
tunebot play <spotify url>
tunebot queue <spotify url>
tunebot search <track name>
tunebot play <search result #>
tunebot queue <search result #>
tunebot skip
tunebot prev
tunebot volume 0-100
tunebot state
```

If you're like us, you are (or will soon be) tired of typing `tunebot` before each command. Luckily, we've added a few shortcuts. Use `tb` in place of `tunebot` anywhere you like. And `q` works just as well as `queue`.

## Hacks Welcome

Tunebot began life as a hackday project here at Next Big Sound. We encourage you to continue the tradition by adding your own hacks and sending them back our way with a pull request. We'll also do our best to update tunebot periodically, so keep your eyes out for new releases!

### Contributors

* [kschrodes](https://github.com/kschrodes)
* [wb](https://github.com/wb)

