#!/bin/bash

# config
HUBOT_DIR="/opt/hubot"
PORT=8283
NAME="tunebot"
ALIAS="tb"

# run hubot with our custom name and alias
cd $HUBOT_DIR
export PORT
bin/hubot -n $NAME --alias $ALIAS
