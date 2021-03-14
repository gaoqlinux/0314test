#!/bin/sh

function rand(){   
    min=$1   
    max=$(($2-$min+1))   
    num=$(date +%s%N)   
    echo $(($num%$max+$min))   
} 

function getServerPort(){
    port=$(rand $1 $2)   
    test_port=$(netstat -lnp | grep $port | awk '{print $7}' | awk -F '/' '{print $1}')

    while [ ! -z $test_port ];do
         port=$(rand $1 $2)
         test_port=$(netstat -lnp | grep $port | awk '{print $7}' | awk -F '/' '{print $1}')

    done

    echo ${port}
} 
function getVersion(){
   cd ..
   image_version=image_version= mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q
   cd `cd $(dirname $0);pwd`
   echo ${image_version}
}
