#!/bin/sh
source ./conf.sh

service_name="${application_name}-server"
echo "service_name=${service_name}"

containerId=$(docker ps -a | grep -E "${service_name}" | awk '{print $1}')

if [ ! -z $containerId ]
  then docker stop $containerId && docker rm $containerId
fi

imageId=$(docker images | grep -E "${service_name}" | awk '{print $3}')
if [ ! -z $imageId ]
  then docker rmi --force $(docker images | grep "${service_name}" | awk '{print $3}')
fi

echo "image is building"
cd ..
mvn clean install
cd ${service_name}

mvn clean package -Dmaven.test.skip=true docker:build

