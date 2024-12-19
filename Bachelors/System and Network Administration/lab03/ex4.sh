#!/bin/bash

while true
do
	date | tr -d '\n'
	echo " - " | tr -d '\n'
	du -sh / 2>/dev/null | cut -d$'\t' -f1
	sleep 10
done

