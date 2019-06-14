docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

cd "C:\Users\crist\OneDrive\Escritorio\full-teaching"
docker-compose up