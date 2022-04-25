#!/bin/bash
while true; do
    line=$( tail -n 1 ex2.txt )
    line=$((line+1))

    while ! ln ex2.txt ex2.txt.lock 2> /dev/null; do
        sleep 0.1 # waiting until the file become available
    done

    echo $line >> ex2.txt # critical region
    
    rm ex2.txt.lock
done
