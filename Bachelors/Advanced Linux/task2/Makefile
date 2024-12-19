obj-m += mydev.o

all:
	make -C /lib/modules/$(shell uname -r)/build M=$(shell pwd) modules
	insmod mydev.ko
	chmod a+r+w /dev/mydev
test:
	echo "1" > /dev/mydev
	echo "2" > /dev/mydev
	cat /dev/mydev
	cat /dev/mydev
clean:
	make -C /lib/modules/$(shell uname -r)/build M=$(shell pwd) clean
	rmmod mydev
