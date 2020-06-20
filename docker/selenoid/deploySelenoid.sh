#el registrador de video y los navegadores son containers que crea
#selenoid, por lo que antes hay que hacer el pull
docker pull selenoid/video-recorder:latest-release
#sudo docker pull selenoid/chrome:latest
docker pull selenoid/vnc:chrome_81.0

#direccion del remote web driver http://<ip>:4444/wd/hub
docker stop selenoid
docker rm selenoid
docker run -d  --name selenoid   --restart always     \                  
    -p 4444:4444                                       \     
    -v //var/run/docker.sock:/var/run/docker.sock       \    
    -v /home/augusto/docker/conf/selenoid/:/etc/selenoid/:ro   \             
    -v /home/augusto/docker/cdat/selenoid/:/opt/selenoid/video/    \         
    -e OVERRIDE_VIDEO_OUTPUT_DIR=/c/T/cdat/selenoid/     \   
    aerokube/selenoid:latest-release 
docker stop selenoid-ui
docker rm selenoid-ui
#con conexiones que no son directas (p.e. desde casa) no sirve
#usar localhost, hay que poner la direccion real (en teste caso 192.168.0.10)
docker run -d --name selenoid-ui  --restart always  \
    -p 8080:8080  \
    aerokube/selenoid-ui  --selenoid-uri=http://192.168.0.103:4444
