# SNA Lab 10: Docker and `docker-compose`

## Exercise 1

1. Inside the docker container, what user are you logged in as by default?

   > The `root` super user.

   

2. If you start and then exit an interactive container, and then use the `docker run -it ubuntu:xenial /bin/bash` command again, is it the same container? How can you tell?

   > It's not the same container because:
   >
   > 	1. Containers are instantiated from the downloaded image, and destroyed upon exit (unless changes are committed)
   > 	1. We can see different container ID each time we run the command.
   > 	1. If we created a file (`echo "hi" > file.txt`) in the container and then exit and rerun the command, file will be lost. 



## Exercise 2

1. Run the image you just built. What do you observe?

   > 1. Create the `Dockerfile` with the following content
   >
   >    ```dockerfile
   >    FROM ubuntu:bionic
   >    
   >    RUN apt-get update && apt-get install -y python3.6 --no-install-recommends
   >    
   >    CMD ["/usr/bin/python3.6", "-i"]
   >    ```
   >
   > 2. Running `docker run -it mypython:latest`, we can see image being pulled `library/ubuntu`, and the output of `apt` commands, and a warning that some packages were not configured because `apt-util` is not installed.
   >
   > 3. Running `docker run -it mypython:latest`, we can successfully access an interactive python3.6 shell.
   >
   >    ```shell
   >    Python 3.6.9 (default, Jan 26 2021, 15:33:00) 
   >    [GCC 8.4.0] on linux
   >    Type "help", "copyright", "credits" or "license" for more information.
   >    >>> 
   >    ```

   

2. Write and build a `Dockerfile` that deploys a python application (e.g., web server).

   > 1. Create a quick `app.py` serving a static webpage.
   >
   >    ```python
   >    from flask import Flask
   >    
   >    app = Flask(__name__)
   >    
   >    @app.route("/")
   >    def index():
   >    	return '<h1>Hello from Docker!</h1>'
   >    
   >    if __name__ == "__main__":
   >        app.run(host='0.0.0.0', port=8080)
   >    ```
   >
   > 2. Create a `requirements.txt` with the specific version to use (e.g, `flask==2.0.2`)
   >
   > 3. Create the `Dockerfile` with the following content
   >
   >    ```dockerfile
   >    FROM python:3.6
   >    COPY . /app
   >    WORKDIR /app
   >    RUN pip install -r requirements.txt
   >    EXPOSE 8080
   >    CMD ["python", "app.py"]
   >    ```
   >
   >    - **Explanation:**
   >      - The file downloads and uses the publicly available python3.6 image, we could have also used the one we created in previous exercise.
   >      - It copies our application to the container file system to the `/app` directory, which will be the working directory for the subsequent commands.
   >      - Run `pip install -r requirements.txt` in a subshell, this will install required package dependencies (in this case, it's only the flask package).
   >      - Expose port 8080 to be able to access the application from the host machine.
   >      - Run the (debugging) web server using `python app.py`
   >
   > 4. Build the docker image named `flaskapp` using `docker build -t flaskapp .`
   >
   >    - We can ignore the warning about running pip install as root since we are running this in a container anyway, so no strong need for a virtual environment here.  
   >
   > 5. Run a container from the created image `docker run -p8080:8080 flaskapp`
   >
   >    - The `-p` sets the port forwarding between ports 8080 on host and container so we can access the webpage served by Flask. by visiting `http://localhost:8080`
   >
   > 6. Output:
   >
   >    ```shell
   >    $ docker run -p8080:8080 flaskapp
   >     * Serving Flask app 'app' (lazy loading)
   >     * Environment: production
   >       WARNING: This is a development server. Do not use it in a production deployment.
   >       Use a production WSGI server instead.
   >     * Debug mode: off
   >     * Running on all addresses.
   >       WARNING: This is a development server. Do not use it in a production deployment.
   >     * Running on http://172.17.0.2:8080/ (Press CTRL+C to quit)
   >    ```
   >
   >    ![image-20211203154905742](../images/image-20211203154905742.png)

3. Paste the output of your docker images command after questions 1 and 2.

   > ```shell
   > $ docker images
   > REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
   > flaskapp            latest              5c377228b04c        3 minutes ago       913MB
   > <none>              <none>              fd3e49a7eee7        5 minutes ago       902MB
   > mypython            latest              72af9637e46e        31 minutes ago      132MB
   > python              3.6                 ea3a963a14d5        10 hours ago        902MB
   > ubuntu              bionic              5a214d77f5d7        2 months ago        63.1MB
   > hello-world         latest              feb5d9fea6a5        2 months ago        13.3kB
   > ```



## Exercise 3

https://github.com/Atadzhan/express-mongo-lab

- `dockerignore`

  ```
  node_modules
  npm-debug.log
  ```

- `Dockerfile`

  ```dockerfile
  FROM node:8
  WORKDIR /app
  COPY package*.json ./
  RUN npm install
  COPY . .
  EXPOSE 8080
  CMD [ "node", "server.js" ]
  ```

- `docker-compose.yml`

  ```yaml
  version: '3'
  services:
    web:
      depends_on:
       - database
      build: '/app'
      ports:
       - "8080:8080"
    database:
        image: mongo
        volumes:
          - "./mongo-test:/app/database"
        ports:
          - "27017:27017"
  ```

  
