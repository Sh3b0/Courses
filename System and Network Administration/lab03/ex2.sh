#!/bin/bash
filename="$1"
while line=: read uname pswd; do
	adduser "$uname" --gecos "" --disabled-password
	echo -e "$uname:$pswd" | chpasswd
done < "$filename"
