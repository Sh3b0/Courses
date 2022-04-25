touch ../week1/file.txt
link ../week1/file.txt _ex2.txt
inode=$(ls -i _ex2.txt | cut -d ' ' -f 1)
find .. -inum "$inode"
find .. -inum "$inode" -exec rm {} \;
