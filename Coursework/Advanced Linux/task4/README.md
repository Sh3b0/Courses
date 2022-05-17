# Task 4 - USB driver

## Task

- Use a USB device (flash drive, mouse etc.) as an electronic key for the chardev from task3.
- The chardev should appear only when the USB device is inserted.
- Stack itself should not be destroyed when USB device is removed.


## Makefile
- To compile and load module: `sudo make`
- To unload module and clean working directory: `sudo make clean`


## Test

- Unload default USB drivers so that our module’s `probe` function will be called instead of the kernel’s.

  ```bash
  sudo rmmod uas usb_storage
  ```

- Compile and load module: `sudo make`

  ![image-20220510201129008](../images/image-20220510201129008.png)

- Plug in USB and check kernel logs: `sudo dmesg -w`

  ![image-20220510201833437](../images/image-20220510201833437.png)

- `mydev` is now available for usage.

  ![image-20220510202241811](../images/image-20220510202241811.png)

- Unplug USB and check kernel logs.

  ![image-20220510202345350](../images/image-20220510202345350.png)

- `mydev` is no longer available.

  ![image-20220510202609687](../images/image-20220510202609687.png)

- Plug in USB again.

  ![image-20220510202654632](../images/image-20220510202654632.png)

- `mydev` should now be available again with no loss of stack data.

  ![image-20220510202821795](../images/image-20220510202821795.png)

- Unplug USB, unload module, clean working directory, and reload default  modules that we unloaded at the beginning.

  ![image-20220510203648108](../images/image-20220510203648108.png)
