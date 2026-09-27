#!/bin/bash
ngrok config add-authtoken $DISPATCHER_NGROK_TOKEN

PORT=${DISPATCHER_PORT}

ngrok http $PORT > /dev/null &

sleep 2

export BOT_OPERATOR_URI=$(curl -s http://127.0.0.1:4040/api/tunnels | grep -o "https://[a-zA-Z0-9.-]*.ngrok-free.app" | head -n 1)

echo "BOT_OPERATOR_URI=$BOT_OPERATOR_URI"

echo $BOT_OPERATOR_URI > env/.env

java -jar $HOME/Dispatcher/target/Dispatcher-0.0.1.jar
