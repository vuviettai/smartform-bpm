#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
ENV=${1:-formsflow}
ENV_FILE=$DIR/.env.${ENV}
OS=$(uname)
DOCKER_COMPOSE=${DIR}/docker-compose.yml

if [ $OS == 'Darwin' ]
then
    # Form macos
    HOST_IP=$(ipconfig getifaddr en0)
    sed -i '' "s|HOST_IP=.*|HOST_IP=$HOST_IP|g" $ENV_FILE
    ARCH=$(uname -m)
    if [ $ARCH == 'arm64' ]
    then
        DOCKER_COMPOSE=${DIR}/docker-compose-arm64.yml
    fi
elif [ $OS == 'Linux' ]
then
    HOST_IP=$(hostname -I | awk '{print $1}')
    sed -i "s|HOST_IP=.*|HOST_IP=$HOST_IP|g" $ENV_FILE
fi 
COMPOSES="-f ${DOCKER_COMPOSE}"
docker-compose ${COMPOSES} up -d