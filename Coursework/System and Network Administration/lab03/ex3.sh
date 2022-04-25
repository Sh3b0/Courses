( # start a subshell
	sum(){ # function in subshell
		return $(($1+$2))
	}

	sum 2 3 # call the function
	echo "$?" > /tmp/file # save result to a file
)& # send subshell to background

read RESULT </tmp/file	# read result from parent shell
echo "$RESULT"		# output the result

