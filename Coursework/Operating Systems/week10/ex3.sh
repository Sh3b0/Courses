touch _ex3.txt
chmod a-x _ex3.txt
ls -l _ex3.txt > ex3.txt
chmod u=rwx _ex3.txt
chmod o=rwx _ex3.txt
chmod g=0 _ex3.txt
ls -l _ex3.txt >> ex3.txt
chmod g=u _ex3.txt
ls -l _ex3.txt >> ex3.txt

