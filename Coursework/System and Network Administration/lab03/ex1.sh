#!/bin/bash
for i in {1..10}
do
        rem=$(($i % 2))
        if [ "$rem" -ne "0" ]; then
                echo $i
        fi
done

