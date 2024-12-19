#include <linux/module.h>
#include <linux/uaccess.h>
#include <linux/fs.h>
#include <linux/device.h>
#include <linux/slab.h>
#include <linux/ioctl.h>
#include <linux/spinlock.h>

#define KER_BUF_SIZE 100
#define MAJOR_NUM 100 // can't use dynamic registration because of ioctl.

static char ker_buf[KER_BUF_SIZE];
static unsigned long stack_head = 0, stack_size = 1, i;
static int32_t *stack;
static struct class *mydev_class;
static dev_t mydev;
static spinlock_t mydev_lock;

static int dev_open(struct inode *inode, struct file *file);
static ssize_t dev_read(struct file *file, char *buf, size_t len, loff_t *off);
static ssize_t dev_write(struct file *file, const char *buf, size_t len, loff_t *off);
static int dev_release(struct inode *inode, struct file *file);
static long dev_ioctl(struct file *file, unsigned int cmd, unsigned long arg);

// Device operations
static const struct file_operations fops = {
    .read = dev_read,
    .write = dev_write,
    .open = dev_open,
    .release = dev_release,
    .unlocked_ioctl = dev_ioctl,
};

// Called on insmod, registers device
static int __init dev_init(void) {
    int errno = register_chrdev(MAJOR_NUM, "mydev", &fops);
    if (errno < 0) {
        printk(KERN_ERR "mydev: failed to register device\n");
        return errno;
    }
    mydev = MKDEV(MAJOR_NUM, 0);
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
    stack = kmalloc_array(stack_size, sizeof(int32_t), GFP_KERNEL);
    if(stack == NULL) {
        printk(KERN_ERR "mydev: failed to create stack\n");
        return -ENOMEM;
    }
    spin_lock_init(&mydev_lock);
    printk(KERN_INFO "mydev: device initialized (major = %d)\n", MAJOR_NUM);
    return 0;
}

// Called on rmmod, unregisters device
static void __exit dev_exit(void) {
    device_destroy(mydev_class, mydev);
    class_destroy(mydev_class);
    unregister_chrdev(MAJOR_NUM, "mydev");
    printk(KERN_INFO "mydev: device exited\n");
}

// Called on opening device
static int dev_open(struct inode *inode, struct file *file) {
    printk(KERN_INFO "mydev: device opened\n");
    return 0;
}


// Called on reading from device
static ssize_t dev_read(struct file *file, char *buf, size_t len, loff_t *off) {
    spin_lock(&mydev_lock);
    if(!stack_head) {
        spin_unlock(&mydev_lock);
        printk(KERN_INFO "mydev: stack is empty\n");
        return 0;
    }
    if (*off > 0) {
        spin_unlock(&mydev_lock);
        return 0;
    }
    if(!sprintf(ker_buf, "%d\n", stack[--stack_head])) {
        spin_unlock(&mydev_lock);
        printk(KERN_ERR "mydev: failed to read from stack\n");
        stack_head++;
        return -EFAULT;
    }
    if(len > strlen(ker_buf)) {
        len = strlen(ker_buf);
    }
    if(copy_to_user(buf, ker_buf, len)) {
        spin_unlock(&mydev_lock);
        printk(KERN_ERR "mydev: failed to copy to user\n");
        return -EFAULT;
    }
    *off += len;
    spin_unlock(&mydev_lock);
    printk(KERN_INFO "mydev: read %ld bytes from device\n", len);
    return len;
}


// Change stack capacity from stack_size to new_size
int resize_stack(unsigned long new_size) {
    int32_t *new_stack;
    if(new_size == stack_size) {
        return 0;
    }
    new_stack = kmalloc_array(new_size, sizeof(int), GFP_KERNEL);
    if(new_stack == NULL) {
        return -ENOMEM;
    }
    i = 0;
    while(i < min(new_size, stack_size)) {
        new_stack[i] = stack[i];
        i++;
    }
    if(stack_head > new_size) {
        stack_head = new_size;
    }
    stack_size = new_size;
    kfree(stack);
    stack = new_stack;
    printk(KERN_INFO "mydev: stack resized (new size = %ld)", new_size);
    return 0;
}


// Called on writing to device
static ssize_t dev_write(struct file *file, const char __user *buf, size_t len, loff_t *off) {
    spin_lock(&mydev_lock);
    if(stack_head == stack_size) {
        int errno = resize_stack(stack_size * 2);
        if(errno < 0) {
            spin_unlock(&mydev_lock);
            printk(KERN_ERR "mydev: failed to resize stack.\n");
            return errno;
        }
    }
    if(len > KER_BUF_SIZE) {
        len = KER_BUF_SIZE;
    }
    if(kstrtoint_from_user(buf, len, 10, stack + stack_head++)) {
        spin_unlock(&mydev_lock);
        printk(KERN_ERR "mydev: failed to write to device\n");
        stack_head--;
        return -EFAULT;
    }
    spin_unlock(&mydev_lock);
    printk(KERN_INFO "mydev: wrote %ld bytes to device\n", len);
    return len;
}

// Called on ioctl() from userspace app.
static long dev_ioctl(struct file *file, unsigned int cmd, unsigned long arg) {
    int errno;
    spin_lock(&mydev_lock);
    // The device supports only one operation: writing new stack size.
    switch(cmd) {
        case _IOW(MAJOR_NUM, 0, unsigned long):
            errno = resize_stack(arg);
            if(errno < 0) {
                spin_unlock(&mydev_lock);
                printk(KERN_ERR "mydev: failed to resize stack.\n");
                return errno;
            }
            break;
    }
    spin_unlock(&mydev_lock);
    return 0;
}

// Called on closing device
static int dev_release(struct inode *inode, struct file *file) {
    printk(KERN_INFO "mydev: device released\n");
    return 0;
}

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Ahmed Nouralla <a.shaaban@innopolis.university>");
MODULE_DESCRIPTION("Simple character device");

module_init(dev_init);
module_exit(dev_exit);
