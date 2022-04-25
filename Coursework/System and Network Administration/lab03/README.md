# SNA Lab 3: **Shell Scripting and `systemd`**

## Exercise 1 Questions

1. Create a simple script to print odd numbers from 1 to 10.

   > ```bash
   > #!/bin/bash
   > for i in {1..10}
   > do
   > 	rem=$(($i % 2))
   > 	if [ "$rem" -ne "0" ]; then
   > 		echo $i
   > 	fi
   > done
   > ```

2. Create a shell script which makes new users and their corresponding passwords for 10 accounts in the system. Script should read the data from the file `users.txt` (you can write data to this file).

   >```bash
   >#!/bin/bash
   >filename="$1"							# first argument is users file
   >while line=: read uname pswd; do		# read line by line
   >	adduser "$uname" --gecos "" --disabled-password	# create user with no password
   >	echo -e "$uname:$pswd" | chpasswd	# associate password
   >done < "$filename"						# read from file
   >```
   >
   >- Run script using `sudo ./script.sh path/to/users.txt` 
   >- To remove a user use `sudo deluser --remove-home <username>`

3. Provide an example of the shell script where you can pass result (not an exit code) of the executed function in a sub-shell to the parent’s shell

   > ```bash
   > (							# start a subshell
   > 	sum(){					# function in subshell
   > 		return $(($1+$2))
   > 	}
   > 	sum 2 3					# call the function
   > 	echo "$?" > /tmp/result	# save result to a file
   > )&							# send subshell to background
   > 
   > read RESULT </tmp/result 	# read result from parent shell
   > echo "$RESULT"				# output the result
   > ```
   >
   > 

4. Create script that redirects system date and disk utilization in a file with `systemd`.

   > This script - once run - will create and maintain a log file `/var/log/dutil.log` which contains timestamped total disk usage (updates every minute).
   >
   > ```bash
   > #!/bin/bash
   > 
   > while true
   > do
   > 	date | tr -d '\n' >> /var/log/dutil.log
   > 	echo " - " | tr -d '\n' >> /var/log/dutil.log
   > 	du -sh / 2>/dev/null | cut -d$'\t' -f1 >> /var/log/dutil.log
   > 	sleep 60
   > done
   > ```
   >
   > Script can be configured to run as a service with `systemd` by creating the following service file: `/lib/systemd/system/dutil.service` 
   >
   > ```bash
   > [Unit]
   > Description=Disk utilization logger.
   > [Service]
   > ExecStart=path/to/script.sh
   > [Install]
   > WantedBy=multi-user.target
   > ```
   >
   > and then running `sudo systemctl start dutil ; sudo systemctl enable dutil ` to start the service and run it on startup.

