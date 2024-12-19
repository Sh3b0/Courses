# Lab 4 - Web Servers

> Ahmed Nouralla - a.shaaban@innopolis.university
>
> ID for the task: 14. Domain: st14.sne24.ru

[TOC]

## Task 1 - Install & Configure Virtual Hosts

**1- Fetch, verify, build and install the webserver daemon from the source. Note: Some features of your web server may be built-in or modularized. Enable at least SSL/TLS during your installation.**

- Here we go

  ```bash
  cd ~/Downloads
  
  # Download source and signature
  wget http://nginx.org/download/nginx-1.27.2.tar.gz
  wget http://nginx.org/download/nginx-1.27.2.tar.gz.asc
  
  # Import maintainer's PGP key to my GPG keyring
  curl https://nginx.org/keys/pluknet.key | gpg --import -
  
  # Verify source using the signature file
  gpg --verify nginx-1.27.2.tar.gz.asc nginx-1.27.2.tar.gz
  
  # Extract files
  tar -xzf nginx-1.27.2.tar.gz
  
  # Build with SSL support (options: https://nginx.org/en/docs/configure.html)
  cd nginx-1.27.2
  ./configure --with-http_ssl_module
  
  make
  sudo make install
  ```

- Installation

  ![image-20241122002536638](https://i.postimg.cc/x18cktxL/image.png)

 - Configure command results

   ![image-20241122004317407](https://i.postimg.cc/G2wBk2t9/image.png)	

- Make install results

  ![image-20241122004711726](https://i.postimg.cc/tCCgfBH8/image.png)

**2- Define the root directory and then two virtual hosts (and configure DNS records or wildcard accordingly): `web1.st14.sne24.ru` and `web2.st14.sne24.ru`**

- Add two `A` records in my `st14.sne24.ru.zone` file with the server address.

  ``` bash
  web1     IN      A               192.168.122.190
  web2     IN      A               192.168.122.190
  ```

- Modified the server config at `/usr/local/nginx/conf/nginx.conf` to be as follows:

  ```nginx
  worker_processes 1;
  error_log logs/error.log;
  pid logs/nginx.pid;
  
  events {
      worker_connections 1024;
  }
  
  http {
      include           mime.types;
      default_type      application/octet-stream;
      sendfile          on;
  	
      keepalive_timeout 65;
      
      # Define the root directory for the DNS records
      server {
          listen 80;
          server_name _;
          return 404;  # Default to return a 404 for undefined hosts
      }
  
      # Virtual host for web1.st14.sne24.ru
      server {
          listen 80;
          server_name web1.st14.sne24.ru;
  
          root /var/www/st14/web1;  # Root directory for web1
          index index.html;
  
          location / {
              try_files $uri $uri/ =404;
          }
      }
  
      # Virtual host for web2.st14.sne24.ru
      server {
          listen 80;
          server_name web2.st14.sne24.ru;
  
          root /var/www/st14/web2;  # Root directory for web2
          index index.html;
  
          location / {
              try_files $uri $uri/ =404;
          }
      }
  }
  ```

**3- Create a simple, unique HTML page for each virtual host to make sure that the server can correctly serve it.**

- Creating minimal `index.html` files referenced in the above config

  ```bash
  mkdir -p /var/www/st14/web1
  mkdir -p /var/www/st14/web2
  
  echo "<h1>Web1 Index</h1>" > /var/www/st14/web1/index.html
  echo "<h1>Web2 Index</h1>" > /var/www/st14/web2/index.html
  ```

- Verification is done below (Question 5).

**4- Check the configuration syntax 1, start the daemon and enable it at boot time.**

- Create systemd service at `/etc/systemd/system/nginx.service`. The service provides commands:

  - `ExecStartPre=` command to check config before starting the server
  - `ExecStart=` command to start the server daemon process
  - `ExecReload=` command to reload server to apply config changes
  - `ExecStop=` command to stop the server 

  ```bash
  [Unit]
  Description=A high performance web server and a reverse proxy server
  Documentation=http://nginx.org/en/docs/
  After=network.target
  
  [Service]
  Type=forking
  PIDFile=/usr/local/nginx/logs/nginx.pid
  ExecStartPre=/usr/local/nginx/sbin/nginx -t -q -g 'daemon on; master_process on;'
  ExecStart=/usr/local/nginx/sbin/nginx
  ExecReload=/usr/local/nginx/sbin/nginx -s reload
  ExecStop=/bin/kill -s TERM $MAINPID
  PrivateTmp=true
  LimitNOFILE=65535
  
  [Install]
  WantedBy=multi-user.target
  ```

- Test the service with default configuration. 

  ![image-20241122005931414](https://i.postimg.cc/Gm6h4rs1/image.png)

- Enable service to run at boot

  ![image-20241122020231099](https://i.postimg.cc/0Q08XwX8/image.png)

**5- Use curl to display the contents of a full HTTP/1.1 session served by your server.**

- Testing the service after reconfiguring DNS and nginx.

  ![image-20241122020555408](https://i.postimg.cc/j2qRpSBy/image.png)

**6- Explain the meaning of each request and reply header.**

- Showing the full HTTP session with nginx server.

  ![image-20241122020739800](https://i.postimg.cc/TYwRQhV6/image.png)

- **Request Headers:**

  - **GET / HTTP/1.1:** Specifies the HTTP method (`GET`), resource path (`/`), and protocol version (`HTTP/1.1`).

  - **Host:** web1.st14.sne24.ru: Indicates the target host for the request, allowing virtual host routing.

  - **User-Agent: curl/8.5.0**: Identifies the client making the request (in this case, `curl` version `8.5.0`).

  - **Accept: \*/\***: Indicates the client can accept any content type in the response.

- **Response Headers:**

  - **HTTP/1.1 200 OK**: The status line indicating the protocol version (`HTTP/1.1`), status code (`200`), and message (`OK`), meaning the request was successful.
  - **Server: nginx/1.27.2**: Specifies the software used to serve the request (`nginx` version `1.27.2`).
  - **Date:**: The timestamp when the response was generated by the server.
  - **Content-Type: text/html**: Indicates the MIME type of the response body (`text/html`).
  - **Content-Length: 20**: Specifies the size of the response body in bytes (`20`).
  - **Last-Modified: **: Indicates when the requested resource was last modified.
  - **Connection: keep-alive**: Signals that the connection will remain open for further requests.
  - **ETag: "673fb749-14"**: A unique identifier for the resource version, used for caching and conditional requests.
  - **Accept-Ranges: bytes**: Indicates the server supports partial content requests (byte-range requests).

## Task 2 - SSL/TLS

**1- Enable SSL/TLS and tune the various settings to make it as secure as possible**

> Reference: https://nginx.org/en/docs/http/configuring_https_servers.html

- Update `nginx.conf` to redirect all HTTP traffic to HTTPS using the certificate and key to be placed at `/usr/local/nginx/certs`.

  ```nginx
  worker_processes auto;
  error_log logs/error.log;
  pid logs/nginx.pid;
  
  events {
      worker_connections 1024;
  }
  
  http {
      include       mime.types;
      default_type  application/octet-stream;
  
      # Redirect HTTP to HTTPS
      server {
          listen 80;
          server_name web1.st14.sne24.ru web2.st14.sne24.ru;
  
          return 301 https://$host$request_uri;
      }
  
      # HTTPS server for web1.st14.sne24.ru
      server {
          listen 443 ssl;
          server_name web1.st14.sne24.ru;
  		
          # Load cert and key from the specified locations
          ssl_certificate /usr/local/nginx/certs/tls.crt;
          ssl_certificate_key /usr/local/nginx/certs/tls.key;
  		
          # SSL security settings
          ssl_protocols TLSv1.2 TLSv1.3;
          ssl_prefer_server_ciphers on;
          ssl_session_timeout 1d;
          ssl_session_cache shared:SSL:50m;
          ssl_session_tickets off;
          
           # Security headers
          add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
          add_header X-Content-Type-Options nosniff always;
          add_header X-Frame-Options DENY always;
          add_header X-XSS-Protection "1; mode=block" always;
          add_header Referrer-Policy no-referrer always;
          add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self'" always;
  
          root /var/www/st14/web1;
          index index.html;
  
          location / {
              try_files $uri $uri/ =404;
          }
      }
  
      # HTTPS server for web2.st14.sne24.ru
      server {
          listen 443 ssl;
          server_name web2.st14.sne24.ru;
  		
          # Load cert and key from the specified locations
          ssl_certificate /usr/local/nginx/certs/tls.crt;
          ssl_certificate_key /usr/local/nginx/certs/tls.key;
  		
          # SSL security settings
          ssl_protocols TLSv1.2 TLSv1.3;
          ssl_prefer_server_ciphers on;
          ssl_session_timeout 1d;
          ssl_session_cache shared:SSL:50m;
          ssl_session_tickets off;
          
          # Security headers
          add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
          add_header X-Content-Type-Options nosniff always;
          add_header X-Frame-Options DENY always;
          add_header X-XSS-Protection "1; mode=block" always;
          add_header Referrer-Policy no-referrer always;
          add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self'" always;
  
          root /var/www/st14/web2;
          index index.html;
  
          location / {
              try_files $uri $uri/ =404;
          }
      }
  }
  ```

- **Explanation for the security/performance tuning process**
  - `return 301 https://$host$request_uri;` [line 18]: Ensures HTTP traffic is redirected to HTTPS.
  - **SSL Security settings**
    - `ssl_protocols`: enforce more recent and secure TLS version (`TLSv1.2` and `TLSv1.3`).
    - `ssl_prefer_server_ciphers`: ensures the server's preferred cipher suite is used
    - `ssl_session_timeout`: sets the duration a TLS session can be reused without renegotiation, reducing overhead and improving performance.
    - `ssl_session_cache`: enables a shared session cache for storing TLS session data, improving performance by avoiding full handshakes for repeat connections.
    - `ssl_session_tickets`: disables session tickets to prevent potential vulnerabilities related to ticket reuse across multiple sessions.
  - **Security headers**
    - `Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;`: Use [HSTS](https://en.wikipedia.org/wiki/HTTP_Strict_Transport_Security) (mechanism for protection against man-in-the-middle attacks and cookie hijacking)
    - `X-Content-Type-Options nosniff always`: Prevents browsers from interpreting files as a different MIME type than specified, mitigating content-type confusion attacks.
    - `X-Frame-Options DENY always`: Blocks the site from being embedded in frames or iframes, preventing clickjacking attacks.
    - `X-XSS-Protection`: Activates the browser’s built-in XSS filter and blocks detected attacks (mostly relevant for older browsers).
    - `Referrer-Policy no-referrer always`: Prevents sending the `Referer` header with requests, enhancing privacy by not exposing the origin of the request.
    - `Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self'" always;`: Restricts resources that can be loaded by the site to the same origin (`'self'`), reducing the risk of XSS and data injection attacks.

**2- Describe how you created your own certificate(s) and re-validate every virtual-host.**

- Created locally-trusted certs on my host quickly with [mkcert](https://github.com/FiloSottile/mkcert) and sent them over to the server

  ```bash
  mkdir certs
  cd certs/
  mkcert "*.st14.sne24.ru" # Create self-signed certificate for specified domain
  mkcert -install          # Add it to the system and browser trust store
  
  # Shorter file names used in configs
  mv _wildcard.st14.sne24.ru-key.pem tls.key
  mv _wildcard.st14.sne24.ru.pem tls.crt
  ```

- Command results

  ![image-20241122025455439](https://i.postimg.cc/mrxBbw0L/image.png)

- Reload server `sudo systemctl reload nginx`.

- Testing shows the redirection, server logs, and security headers.

  ![image-20241122034444829](https://i.postimg.cc/bJpPnvy4/image.png)

- Making the requests from browser as well to show HTTPS and certificate information

  ![image-20241122034053994](https://i.postimg.cc/XN2WZ3TD/image.png)

## Task 3 (Bonus) - GeoIP

> Reference: https://github.com/leev/ngx_http_geoip2_module

**Enable GeoIP on your chosen web-server (is only NGINX capable to do this?) and show how to take advantage of it with real examples.**

- [MaxMind](http://www.maxmind.com/) provides GeoLocation database that is free for use. Modules are available for popular server software such as [apache2](https://github.com/maxmind/mod_maxminddb) and [nginx](https://github.com/leev/ngx_http_geoip2_module)

- Download the database

  ```bash
  cd /etc
  wget https://git.io/GeoLite2-Country.mmdb
  wget https://git.io/GeoLite2-City.mmdb
  ```

- Install dependencies and re-compile nginx with dynamic loading of GeoIP2 module

  ```bash
  sudo apt install libmaxminddb-dev
  
  cd ~/Downloads
  git clone https://github.com/leev/ngx_http_geoip2_module
  
  cd nginx-1.27.2
  ./configure --with-http_ssl_module --with-compat --add-dynamic-module=../ngx_http_geoip2_module
  
  make modules
  make install
  ```

- Make modules

![image-20241122051325833](https://i.postimg.cc/PxXGxFKj/image.png)

- Make Install

![image-20241122051841925](https://i.postimg.cc/2yyPdbP9/image.png)

- Update `nginx.conf`

  - Load the module (on top)

    ```nginx
    load_module modules/ngx_http_geoip2_module.so;
    ```

  - Add GeoIP options to extract data from `X-Forwarded-For` header and lookup the database.

    ```nginx
    geoip2 /etc/GeoLite2-Country.mmdb {
            auto_reload 5m;
            $geoip2_metadata_country_build metadata build_epoch;
            $geoip2_data_country_code default=US source=$http_x_forwarded_for country iso_code;
            $geoip2_data_country_name source=$http_x_forwarded_for country names en;
        }
    
        geoip2 /etc/GeoLite2-City.mmdb {
            $geoip2_data_city_name source=$http_x_forwarded_for city names en;
            $geoip2_data_time_zone source=$http_x_forwarded_for location time_zone;
        }
    ```

  - Send extracted variables as response headers

    ```nginx
    add_header X-Country $geoip2_data_country_name;
    add_header X-City $geoip2_data_city_name;
    ```

- Reload the server to apply changes 

- Ironically, for this to work, client has to supply their IP through the `X-Forwarded-For` HTTP header.

  ![image-20241122063452036](https://i.postimg.cc/sxLbTfzj/image.png)

