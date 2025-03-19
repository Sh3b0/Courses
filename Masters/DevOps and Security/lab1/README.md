# Lab 1 - Containerization & ALB

> Ahmed Nouralla - a.shaaban@innopolis.university - Student ID: st14

[TOC]

## Task 1 - Get familiar with Docker Engine

1. Pull Nginx v1.23.3 image from DockerHub and confirm it is listed in local images

   ![image-20250122185817203](https://i.imgur.com/meVAO18.png)

2. Run the pulled Nginx as a container: map the port to 8080, name the container as `nginx-<stX>`, and run it as daemon.

   ![image-20250122190238050](https://i.imgur.com/KO7rzVr.png)

3. Confirm port mapping.
   a. List open ports in host machine.

   ![image-20250122190359237](https://i.imgur.com/HMaGA3W.png)

   b. List open ports inside the running container [had to do `apt update && apt install net-tools` first]

   ![image-20250122190635857](https://i.imgur.com/ntmlLkp.png)

   c. Access the page from your browser.

   ![image-20250122190856449](https://i.imgur.com/8mJhMuY.png)

4. Create a Dockerfile from Nginx v1.23.3 with custom `index.html` to replace the Nginx default web page.

   - Build the image from the `Dockerfile`, tag it during build as `nginx:<stX>`
   - Check/validate local images, and run your custom-made docker image.
   - Access via browser and validate that your custom page is hosted.

   ![image-20250122192556388](https://i.imgur.com/Gtk2xES.png)

   ![image-20250122192632234](https://i.imgur.com/UnAXJbx.png)

## Task 2 - Work with Multi-Container Environment

1. Create another Dockerfile similar to step 1.4 (Let’s call it container B), and an `index.html` with different content.

   ![image-20250122194439487](https://i.imgur.com/Bv0cyw5.png)

2. Write a docker-compose file that builds both Dockerfiles and runs both images. Container A should listen to port 8080 and container B should listen to port 9090. Confirm both websites are accessible

  ![image-20250122194828033](https://i.imgur.com/kIFxyzM.png)

3. Volumes: Mount (bind) a directory from the host file system to Nginx containers. Update the contents of `index.html` in the host file system, re-deploy and confirm in the browser that the web pages' content is updated.

  ![image-20250122201349358](https://i.imgur.com/KH5fTGz.png)

  ![image-20250122201457536](https://i.imgur.com/bzA9R7I.png)

## Task 3 - Configure L7 Load Balancer

1. Add a third container in the docker-compose that will act as loadbalancer, and configure it in front of two containers in a manner that it should distribute the load in a Weighted Round Robin approach.

   - By default, a third container in the compose file can reach the first two by their service name (as they'd all be placed in the compose default network).

   - **Minimal** basic Nginx configuration for L7 WRR load balancing between the services.

     ```nginx
     worker_processes auto;
     
     events {
         worker_connections 1024;
     }
     
     http {
         upstream backend {
             server a:80 weight=3; # 3/4 of traffic goes to A
             server b:80 weight=1; # 1/4 of traffic goes to B
         }
     
         server {
             listen 80;
             location / {
                 proxy_pass http://backend;
             }
         }
     }
     ```

   - Updated compose file

     ```yaml
     services:
       a:
         container_name: containerA
         build: containerA
         volumes: [ "./html1:/usr/share/nginx/html/" ]
       b:
         container_name: containerB
         build: containerB
         volumes: [ "./html2:/usr/share/nginx/html/" ]
       lb:
         container_name: lb
         image: nginx:1.23.3
         ports: [ "80:80" ]
         depends_on: [ "a", "b" ]
         volumes: [ "./nginx.conf:/etc/nginx/nginx.conf" ]
     ```

2. Access the page of Nginx ALB and validate it is load-balancing the traffic (you see two different
   content per page reload).

   - When refreshing multiple times, we may get output from container A or B.

     ![image-20250122205743212](https://i.imgur.com/7DAjZE2.png)

   - Let's validate in terminal by making a 100 curl requests to the load balancer and verify how many of them returned HTML from `ContainerA` and how many for `ContainerB`. We see the 3:1 weight distribution works.

     ![image-20250122205553693](https://i.imgur.com/UXRJz10.png)
