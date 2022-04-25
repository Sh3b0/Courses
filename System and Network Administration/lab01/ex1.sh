pwd # Prints current working directory
mkdir mydocs # Creates directory mydocs
cd mydocs # Enters the directory
touch textproc # Create empty file named textproc
ls -la # Lists all (includes hidden) contents of the directory
cd .. # Goes up (back) one directory
cp -R mydocs/textproc ~ # Copies created file to home
echo “My name is $USER and my home directory is $HOME” > simple_echo # Creates a file simple_echo and fill it with text
cat simple_echo # Outputs content of simple_echo
echo "My Salary is \$ 100" >> simple_echo # Appends more text to simple_echo
cat simple_echo
cat simple_echo > new_echo # Copies simple_echo 
# cat nofile # Tries to print an empty file (will output an error)
cat nofile 2> error_out  # Forwads error to a file error_out
cat nofile > allout 2>&1 # Forwards errors to stdout and stderr
cat << foobar # prints text from stdin until 'foobar' is entered
Hello foobar
foobar
echo -e "one\ntwo\nthree\nfour\nfive" > fragmented.txt # creates a text file
nl < fragmented.txt # orders file lines
sort << end > sorted # outputs sorted input to file until 'end' is entered
c
a
b
end
sort << end | nl -ba > sorted_numbered # outputs sorted and numbered input (including blank lines) to a file
c
a

b
end
