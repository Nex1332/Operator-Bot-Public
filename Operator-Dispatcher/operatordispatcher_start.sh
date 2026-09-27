#!/bin/bash
ngrok config add-authtoken $OPERATOR_DISPATCHER_NGROK_TOKEN

PORT=${OPERATORDISPATCHER_PORT}

ngrok http $PORT > /dev/null &

sleep 2

export BOT_OPERATORS_URI=$(curl -s http://127.0.0.1:4040/api/tunnels | grep -o "https://[a-zA-Z0-9.-]*.ngrok-free.app" | head -n 1)

echo "BOT_OPERATORS_URI=$BOT_OPERATORS_URI"

java -jar $HOME/Operator-Dispatcher/target/Operator-Dispatcher-0.0.1.jar
