

docker run -ti --rm -v ~/.elastest:/data -v /var/run/docker.sock:/var/run/docker.sock elastest/platform start --server-address=localhost/ --testlink --jenkins --user=augusto --password=elastest