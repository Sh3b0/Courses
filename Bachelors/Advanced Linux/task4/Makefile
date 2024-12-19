obj-m += pendrive.o

all:
	make -C /lib/modules/$(shell uname -r)/build M=$(shell pwd) modules
	insmod pendrive.ko

clean:
	make -C /lib/modules/$(shell uname -r)/build M=$(shell pwd) clean
	rmmod pendrive

