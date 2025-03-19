# Lab 6 - Penetration Testing

> Ahmed Nouralla - a.shaaban@innopolis.universty

[TOC]

## 0. Preparation

**Goal:** Hijack a session to gain access to a web application without credentials.

**Setup**

- Downloaded the OVA from the given [drive](https://drive.google.com/file/d/1ks39xhrxXEPHnD9rgm2cXMOZto38no5g/view?usp=sharing)

- Extracted the OVA with `tar -xvf` and obtained `ovf`, `mf`, `iso`, and `vmdk`.

- Converted the `.vmdk` to RAW format with `qemu-convert convert -f vmdk -O raw Ubuntu_64-bit-disk1.vmdk disk.raw`

- Imported the disk to `virt-manager` and used NAT networking mode.

  ![image-20250311232240841](https://i.imgur.com/9cnAkSZ.png)

- Using my host as the attacker machine.

## 1. Enumerate the Target VM

- Used `netdiscover` to identify the IP of the VM knowing the network interface and IP range where its hosted (virtual network used by QEMU).

  ```bash
  sudo netdiscover -i virbr0 -r 192.168.122.1/24
  ```

  ![image-20250312005120661](https://i.imgur.com/bivMHU3.png)

- Used `nmap` to do a port scan on the target, it shows three open ports.

  ![image-20250312005517301](https://i.imgur.com/KAA4ivv.png)

- **Note:**

  - There is a fourth open port (3131) serving another service (halborn-invoice)
  - It was not detected in the scan as its outside the checked range (1-1024)
  - To show it, run the full port-scan with `nmap -p1-65535 192.168.122.98`

- Visiting the HTTP server at port 80: it contains an archive

  ![image-20250312010735203](https://i.imgur.com/0trO2mW.png)

- Downloading and extracting the archive

  ```bash
  wget http://192.168.122.98/cryptos.zip
  unzip cryptos.zip
  ```

- **Downloaded content:** the source code for the web app at port 1999, as well as database initialization script.

  ![image-20250313172517216](https://i.imgur.com/3jueU8c.png)

- Application homepage

  ![image-20250315021743796](https://i.imgur.com/fu7xNv7.png)

## 2. Analyze the web app

Inspecting the Python code, we can draw the following interesting conclusions:

- **Database**

  - MariaDB instance, only accessible through `localhost` (not from outside).
  - Credentials used for login are hardcoded in plain: `root:MyN3wP4ssw0rd`
  - A script is used for initializing the database `cryptos` with three tables: `user`, `products`, `cart`. Only `products` table is being populated.
  - Logic in `db_connection.py` looks solid (correctly uses crypto and correctly substitutes values in SQL statements).

- **User registration workflow**

  0. Available only if there is an existing session with `{"autorized": "true"}` 
  0. User submits username and password (8+ characters) through a POST request at the `/register` endpoint.

  2. A database INSERT is executed, storing `username` along with:
     - Bcrypt-hashed `password` (with salt).
     - An `identifier`: randomly-generated **64-byte** token that is hashed with SHA256.
  2. Upon correct registration, the generated `identifier` is sent to the user as a cookie, to be re-sent from browser when logging-in.

- **Authorization checks**

  - Internal endpoints such as `/shop` are only accessible when there is an active session.
  - A valid request for such endpoint must have a cookie `encodedJWT`.
  - Token encoding and decoding utilizes a single global **32-byte** randomly-generated `token_jwt`.
  - JWT must decode successfully given the same token used to encode it and `HS256` algorithm.
  - JSON content of the decoded token must contain the key-value pair `{"autorized": "true"}` 

- **Secret generation (`secrets.py`)**

  ![image-20250315022548865](https://i.imgur.com/Q8CFvql.png)
  
  - `token_hex`: randomly generated five-digits hexadecimal number.
    - Used for generating Flask `app.secret_key`
    - The key is used for signing `Flask.session` cookie.
    - The `value` parameter passed to the function is not being used, constant `5` is always used instead.
    - :warning: ​**This token can be brute-forced** as the total possible combinations are $10^5$.
  - `token_bytes`: randomly generated UTF-encoded N-byte string of uppercase/lowercase English letters and digits (0-9). 
    - Used for generating `token_jwt` and `identifier` mentioned above. 
    - :grey_exclamation: **Not feasible to brute-force for 32 and 64 bytes**.

## 3. Exploit the WebApp

1. Generate a wordlist of all possible values of `token_hex`

   - It's guaranteed that `app.secret_key` is one of them

   - Used Google colab environment for efficiency of wordlist generation.

     ![image-20250315053605617](https://i.imgur.com/YyVkset.png)

1. Obtain any signed cookie from the server. An HTTP `HEAD` request to the `/refreshTime` endpoint does the job.

   ![image-20250315024050515](https://i.imgur.com/WmUEcU0.png)

1. Used [Flask-Unsign](https://github.com/Paradoxis/Flask-Unsign?) to brute-force the app secret given the wordlist and the obtained signed cookie.

   ![image-20250315025031110](https://i.imgur.com/eZUUPbo.png)

1. Knowing app secret, we can sign our custom payload `{"authorized": True}` and use it to access the `/register` endpoint to access the sign-up page of the service.

   ![image-20250315030154880](https://i.imgur.com/kRce7qz.png)

1. Let's replicate this in Burp Suite to register an account on the server

   ![image-20250315054446823](https://i.imgur.com/VXAH3H9.png)

1. Successfully created an account and obtained an identifier

   ![image-20250315031029566](https://i.imgur.com/ojp9Meq.png)

1. To correctly login at `/` endpoint, we have to supply the obtained `identifer` in the POST request, along with the created `username` and `password`.

## 4. Verify Gaining Access

1. Logged-in successfully and obtained the JWT.

   ![image-20250315031423017](https://i.imgur.com/Ed57J19.png)

2. Server redirects to the `/shop` endpoint. The browser should supply the session token and obtained JWT in all subsequent requests for authorization.

   ![image-20250315032011373](https://i.imgur.com/opzYeNT.png)

3. Add a product to cart

   ![image-20250315032311885](https://i.imgur.com/WdVdP7v.png)

4. Show cart.

   ![image-20250315034141546](https://i.imgur.com/wR3QWV9.png)

5. Button checkout does nothing) Probably the idea was to redirect to invoice app at port 3131.

## Bonus: Privilege Escalation

Who needs complex exploits when you have the VM image just locked with a password?

Execute the following as root to unlock the VM:

```bash
fdisk -l disk.raw # rootfs happens to start at 3719168 with bs=512

# needed for working with LVM partitions
apt-get install lvm2

# attach raw image to a loopback device (showed /dev/loop25)
losetup -f --show -o $((3719168 * 512)) disk.raw

# scan for LVM physical volumes
pvscan

# activate the volume group associated with the physical volume
vg-change -ay

# list logical volumes (showed ubuntu-lv in ubuntu-vg)
lvs

# mount the logical volume at /mnt
mount /dev/ubuntu-vg/ubuntu-lv /mnt

# access the file system
chroot /mnt

# reset password for the halborn user in the machine and exit
passwd halborn
exit

# unmount the partition
umount /mnt

# remove the created loopback device
losetup -d /dev/loop25
```

Then re-run and access the VM with the configured password:

![image-20250315041634074](https://i.imgur.com/TpzGRFu.png)

### Flags

Flag in `/var/www/crypto` folder could be accessible through a **server-side template injection** due to the vulnerable logic in `render.py`

![image-20250315042622110](https://i.imgur.com/uIZfrG0.png)

- This custom `render_template` is used to render all pages of the app, and the `kwargs` are partially controlled by the attacker (e.g., `username`). The showCart page includes a direct substation of username in `{{message}}`

- Create a user named `{{request.application.__globals__.__builtins__.__import__('os').popen('cat /var/www/crypto/flag.txt').read()}}`. We may also carefully craft reverse shell payloads using https://www.revshells.com/

- Logged in as the user (URL-encoded payload) and obtained the JWT

  ![image-20250315225136722](https://i.imgur.com/rqnts82.png)

- Used the JWT to access `/showCart` and obtained the flag.

  ![image-20250315225425431](https://i.imgur.com/QFDDxsZ.png)

---

Flag from the service `halborn-invoice` accessible at port `3131` could be obtained through a **Server-Side Request Forgery (SSRF)** vulnerability

- The idea is to manipulate the query parameters for `/renderInvoice` endpoint to cause a request to `/secret` that passes multiple checks:

  - Request has to originate from within the server
  - Parameter `role` is set to `Offensive Security Engineer`
  - Cookie  `"name": "bot"` is not present

- If the conditions were met, the value for `FLAG` env var should appear in the `Notes` section of the generated PDF invoice (however, the variable does not seem to be set anywhere in NodeJS or Docker).

  ![image-20250315045124247](https://i.imgur.com/76TwbsE.png)

