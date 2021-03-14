source ./conf.sh
source ./fun.sh
application_name=$1
image_version=$(getVersion)
docker tag ${application_name}-server:${image_version} ${docker_repo}/${application_name}-server:${image_version}
docker push ${docker_repo}/${application_name}-server:${image_version}