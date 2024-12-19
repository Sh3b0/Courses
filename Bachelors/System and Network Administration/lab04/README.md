# SNA Lab 4: Cron Jobs

## Exercise 1 Questions

1. Create backup for any directory with files inside. Create `cron` job which backups directory at the 5th day of every month.

   >1. Create `sudo nano /path/to/backup_dir.sh` with the following content:
   >
   >     ```bash
   >     #!/bin/bash
   >     sudo mkdir -p /backups
   >     sudo cp -r <DIR> /backups/<DIR>_`date +%Y_%m_%d`
   >     ```
   >
   >2. Add execute rights to the script `sudo chmod +x backup_dir.sh`
   >
   >3. Run `sudo crontab -e` and add the following line:
   >
   >     ```bash
   >     0 0 5 * * /bin/bash /path/to/backup_dir.sh
   >     ```
   >
   >4. You can also add `MAILTO=someone@example.com` at the top of the file and configure a mail server such as `postfix` so that you can receive emails from `cron` output.

   

2. Install `nginx` and backup directory with location of `index.html`. Create `cron` job which backups directory at midnight every Sunday. Also script should delete old or previous backups.

   >1. For installing `nginx`, add the following to `/etc/apt/sources.list.d/`
   >
   >     ```bash
   >     ## Replace $release with your corresponding Ubuntu release.
   >     deb https://nginx.org/packages/ubuntu/ $release nginx
   >     deb-src https://nginx.org/packages/ubuntu/ $release nginx
   >     ```
   >2. Then run the following commands
   >     ```bash
   >     sudo apt update
   >     sudo apt install nginx
   >     sudo systemctl start nginx
   >     ```
   >
   >3. For backup, create another script `sudo nano /path/to/backup_html.sh` with the following content:
   >
   >     ```bash
   >     #!/bin/bash
   >     sudo mkdir -p /backups
   >     sudo /bin/cp -rf /usr/share/nginx/html /backups
   >     ```
   >
   >4. Add execute rights to the script `sudo chmod +x backup_html.sh`
   >
   >4. Run `sudo crontab -e` and add the following line:
   >
   >     ```bash
   >     0 0 * * 0 /bin/bash /path/to/backup_html.sh
   >     ```

   

3. [Bonus] create a script which check availability of IP address on network interface (`ethernet` or `wlan`) and put this script to `cron` job which runs scripts every 5 minutes.

   >Script:
   >
   >  ```bash
   >  hostname -I
   >  ```
   >
   >`crontab` entry:
   >
   >  ```bash
   >  */5 * * * * /bin/bash /path/to/script.sh
   >  ```



