#!/bin/bash
export DISPATCHER_URI=$(<"env/.env")

echo "DISPATCHER_URI=$DISPATCHER_URI"

java -jar $HOME/Payment/target/Payment-0.0.1.jar