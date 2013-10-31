# Description:
#   Our music robot.
#
# Commands:
#   tunebot help
#
# Authors:
#   Walter Blaurock <walter@nextbigsound.com>
#   Kris <kris@nextbigsound.com>

# constants

SERVER = 'http://localhost:8080'
NAME = 'tunebot'
DJ = 'spotify:user:livbuli:playlist:2INcQWFql0Tf62aOvpCr4G'

# Generate the JSON payloads for various commands...

play = (track, enqueue = false) ->
  JSON.stringify({ 'track': track, 'enqueue': enqueue })
search = (query) ->
  JSON.stringify({ 'query': query })
volume = (value) ->
  JSON.stringify({ 'value': value })
playPause = (state) ->
  JSON.stringify({ 'state': state })
next = () ->
  JSON.stringify({})
previous = (restart = false) ->
  JSON.stringify({ 'restart' : restart })

# Some songs just need their own command

songs = {
  'dj': {
    'track': DJ,
    'message': 'Alright, yo. Playing the NBS playlist.'
  },
  'rickroll': {
    'track': 'spotify:track:0FutrWIUM5Mg3434asiwkp',
    'message': 'You\'ve been rickrolled!'
  },
  'pushups': {
    'track': 'spotify:track:2KH16WveTQWT6KOG9Rg6e2',
    'message': 'Drop and give me 50!'
  },
  '(pushups)': {
    'track': 'spotify:track:2KH16WveTQWT6KOG9Rg6e2',
    'message': 'Did someone say pushups?'
  },
}

getSongInfo = (json) ->
  formatSpotifyUrl(json.spotify_url)

formatSpotifyUrl = (raw) ->
  if (raw)
    raw.replace('spotify:track:', 'http://open.spotify.com/track/')
  else
    raw

module.exports = (robot) ->

  # help

  robot.respond /help/, (msg) ->
    options = [
      "what song",
      "play <spotify url>",
      "queue <spotify url>",
      "search <track name>",
      "play <search result #>",
      "queue <search result #>",
      "skip",
      "prev",
      "volume 0-100"
      "state"
    ]
    options.unshift("Usage:")
    msg.send options.join("\n  #{NAME} ")

  # play stuff

  robot.respond /(what|which) (track|song)/, (msg) ->
    msg.http("#{SERVER}/track")
      .get() (err, res, body) ->
         msg.send getSongInfo(JSON.parse(body))
         
  robot.respond /what the f is this/, (msg) ->
    msg.http("#{SERVER}/track")
      .get() (err, res, body) ->
         msg.send getSongInfo(JSON.parse(body))

  robot.respond /search (.*)/, (msg) ->
    msg.http("#{SERVER}/search")
      .post(search(msg.match[1])) (err, res, body) ->
        if res.statusCode != 200
          msg.send "Whoops...something went wrong."
        else if (JSON.parse(body).results.length == 0)
          msg.send "No results!"
        else
          result = ""
          for i in [0...JSON.parse(body).results.length]
            if i > 0
              result += "\n"
            result += "tunebot queue " + (i+1) + ": '" + JSON.parse(body).results[i].name + "' (" + JSON.parse(body).results[i].album + ") - " + JSON.parse(body).results[i].artist
          msg.send result

  robot.respond /play( me)? (.*)/, (msg) ->
    msg.http("#{SERVER}/track")
      .post(play(msg.match[2])) (err, res, body) ->
        if res.statusCode == 200
          msg.send getSongInfo(JSON.parse(body))
        else
          msg.send JSON.parse(body).message

  robot.respond /(dj|rickroll|pushups)$/, (msg) ->
    msg.http("#{SERVER}/track")
      .post(play(songs[msg.match[1]].track)) (err, res, body) ->
        if res.statusCode == 200
          msg.send songs[msg.match[1]].message
        else
          msg.send JSON.parse(body).message

  robot.hear /(\(pushups\))/i, (msg) ->
    msg.http("#{SERVER}/track")
      .post(play(songs[msg.match[1]].track)) (err, res, body) ->
        msg.send songs[msg.match[1]].message

  # queue stuff

  robot.respond /(queue|q)( me)? (.*)/, (msg) ->
    msg.http("#{SERVER}/track")
      .post(play(msg.match[3], true)) (err, res, body) ->
        if res.statusCode == 200
          msg.send "Got it."
        else
          msg.send JSON.parse(body).message

  robot.respond /((show (queue|q))|(q|queue))$/, (msg) ->
    msg.http("#{SERVER}/queue")
      .get() (err, res, body) ->
        if (JSON.parse(body).queue.length == 0)
          msg.send "Queue is empty..."
        else
          msg.send formatSpotifyUrl(song) for song in JSON.parse(body).queue

  # control volume

  robot.respond /volume (.*)/, (msg) ->
    msg.http("#{SERVER}/volume")
      .post(volume(msg.match[1])) (err, res, body) ->
        if (res.statusCode == 200)
          msg.send "Roger that."
        else
          msg.send JSON.parse(body).message

  robot.respond /(turn it up|higher|louder|up|lower|softer|turn it down|quieter|down|turn it down!|unmute|loud|off|bumpin|quiet|shut up|low|raise the roof|mute|on)$/, (msg) ->
    msg.http("#{SERVER}/volume")
      .post(volume(msg.match[1])) (err, res, body) ->
        if (res.statusCode == 200)
          msg.send "Right away!"
        else
          msg.send JSON.parse(body).message

  # controls

  robot.respond /(stop|pause)$/, (msg) ->
    msg.http("#{SERVER}/playPause")
      .post(playPause('pause')) (err, res, body) ->
        if (res.statusCode == 200)
          msg.send "Paused"
        else
          msg.send "Whoops...something went wrong."

  robot.respond /(start|continue|play)$/, (msg) ->
    msg.http("#{SERVER}/playPause")
      .post(playPause('play')) (err, res, body) ->
        if (res.statusCode == 200)
          msg.send "Playing."
        else
          msg.send "Whoops...something went wrong."

  robot.respond /((skip|next)( (track|song))?|(this sucks|veto|this song sucks))$/, (msg) ->
    msg.http("#{SERVER}/nextTrack")
      .post(next()) (err, res, body) ->
        msg.send getSongInfo(JSON.parse(body))

  robot.respond /(prev|previous|last)( (track|song))?$/, (msg) ->
    msg.http("#{SERVER}/previousTrack")
      .post(previous()) (err, res, body) ->
        msg.send getSongInfo(JSON.parse(body))

  robot.respond /(restart|again)$/, (msg) ->
    msg.http("#{SERVER}/previousTrack")
      .post(previous(true)) (err, res, body) ->
        msg.send getSongInfo(JSON.parse(body))

  # state

  robot.respond /state$/, (msg) ->
    msg.http("#{SERVER}/state")
      .get() (err, res, body) ->
        json = JSON.parse(body)
        msg.send "Currently #{json.state} at volume #{json.volume}."

  # other

  robot.respond /blame (.*)$/, (msg) ->
    msg.send "#{msg.match[1]} did it!"
    
  robot.respond /dance/, (msg) ->
    msg.send "http://25.media.tumblr.com/tumblr_lmzbsgiz5l1qlxgvyo1_400.gif"
    