application_name=$1
kubectl scale --replicas=0 deployment/${application_name} -n pi
echo "scale to 0"
sleep 30
echo "scale to 2"
kubectl scale --replicas=2 deployment/${application_name} -n pi
sleep 30
echo "finished"