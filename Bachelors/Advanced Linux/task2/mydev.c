#include <linux/module.h>
#include <linux/fs.h>
#include <linux/device.h>

#define KER_BUF_SIZE 100
#define MAX_STACK_SIZE 256

static char ker_buf[KER_BUF_SIZE];
static int32_t stack[MAX_STACK_SIZE];
static int stack_head = 0, device_open = 0, major_num;
static struct class *mydev_class;
static dev_t mydev;

static int dev_open(struct inode *inode, struct file *file);
static ssize_t dev_read(struct file *file, char *buf, size_t len, loff_t *off);
static ssize_t dev_write(struct file *file, const char *buf, size_t len, loff_t *off);
static int dev_release(struct inode *inode, struct file *file);

// Device operations
static const struct file_operations fops = {
    .read = dev_read,
    .write = dev_write,
    .open = dev_open,
    .release = dev_release,
};

// Called on insmod, registers device
static int __init dev_init(void) {
    major_num = register_chrdev(0, "mydev", &fops);
    if (major_num < 0) {
        printk(KERN_ERR "mydev: failed to register device\n");
        return major_num;
    }
    mydev = MKDEV(major_num, 0);
    mydev_class = class_create(THIS_MODULE, "mydev");
    
    if(mydev_class == NULL) {
        printk(KERN_ERR "mydev: failed to create device class\n");
        unregister_chrdev_region(mydev, 1);
        return -ENODEV;
    }
    
    if(device_create(mydev_class, NULL, mydev, NULL, "mydev") == NULL) {
        printk(KERN_ERR "mydev: failed to create device\n");
        class_destroy(mydev_class);
        unregister_chrdev_region(mydev, 1);
        return -ENODEV;
    }

    printk(KERN_INFO "mydev: device initialized (major = %d)\n", major_num);
    
    return 0;
}

// Called on rmmod, unregisters device
static void __exit dev_exit(void) {
    device_destroy(mydev_class, mydev);
    class_destroy(mydev_class);
    unregister_chrdev(major_num, "mydev");
    printk(KERN_INFO "mydev: device exited\n");
}

// Called on opening device
static int dev_open(struct inode *inode, struct file *file) {
    if(device_open) {
        printk(KERN_ERR "mydev: device is busy\n");
        return -EBUSY;
    }
    printk(KERN_INFO "mydev: device opened\n");
    device_open = 1;
    return 0;
}

// Called on reading from device
static ssize_t dev_read(struct file *file, char *buf, size_t len, loff_t *off) {
    if(!stack_head) {
        printk(KERN_INFO "mydev: stack is empty\n");
        return 0;
    }
    if (*off > 0) {
        return 0;
    }
    if(!sprintf(ker_buf, "%d\n", stack[--stack_head])) {
        printk(KERN_ERR "mydev: failed to read from stack\n");
        stack_head++;
        return -EFAULT;
    }
    if(len > strlen(ker_buf)) {
        len = strlen(ker_buf);
    }
    if(copy_to_user(buf, ker_buf, len)) {
        printk(KERN_ERR "mydev: failed to copy to user\n");
        return -EFAULT;
    }
    *off += len;
    printk(KERN_INFO "mydev: read %ld bytes from device\n", len);
    return len;
}

// Called on writing to device
static ssize_t dev_write(struct file *file, const char __user *buf, size_t len, loff_t *off) {
    if(stack_head == MAX_STACK_SIZE) {
        printk(KERN_ERR "mydev: stack is full\n");
        return -ENOMEM;
    }
    if(len > KER_BUF_SIZE) {
        len = KER_BUF_SIZE;
    }
    if(kstrtoint_from_user(buf, len, 10, stack + stack_head++)) {
        printk(KERN_ERR "mydev: failed to write to device\n");
        stack_head--;
        return -EFAULT;
    }
    printk(KERN_INFO "mydev: wrote %ld bytes to device\n", len);
    return len;
}

// Called on closing device
static int dev_release(struct inode *inode, struct file *file) {
    printk(KERN_INFO "mydev: device released\n");
    device_open = 0;
    return 0;
}

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Ahmed Nouralla <a.shaaban@innopolis.university>");
MODULE_DESCRIPTION("Simple character device");

module_init(dev_init);
module_exit(dev_exit);

