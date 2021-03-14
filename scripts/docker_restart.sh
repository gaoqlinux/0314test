#!/bin/sh

source ./conf.sh
source ./fun.sh

service_name="${application_name}-server"
image_version=$(getVersion)

if [ -z $application_name ]
  then echo "application_name  is null" &&  exit 1
fi

host_port=$(getServerPort 30001 32767)

echo "application=${application_name}"

echo "container is stoping and removing"

containerId=$(docker ps -a | grep -E "${service_name}" | awk '{print $1}')

if [ ! -z $containerId ]
  then docker stop $containerId && docker rm $containerId
fi

name=${service_name}"_"${host_port}"_"${image_version}

echo "container is starting"

docker run --name=${name} --privileged=true -p ${host_port}:${host_port} \
       --env SERVER_PORT=${host_port} \
       --env SERVER_HOSTNAME=${server_host_name} \
       --env EUREKA_URL=${eureka_host_name} \
       --env EUREKA_PORT=${eureka_port} \
       --env PROFILE=${profile} \
       --add-host ${pay_hostname}:${pay_hostip} \
       --add-host ${order_hostname}:${order_hostip} \
       -v /data/servers/logs/${service_name}/:/data/servers/logs/${service_name} \
       -t ${service_name}:${image_version}
