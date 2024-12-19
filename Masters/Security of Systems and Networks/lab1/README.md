# Lab 1 - Classical Crypto
> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1

### Vigenère Cipher
According to https://www.dcode.fr/vigenere-cipher,  Vigenère cipher is a polyalphabetic encryption algorithm.
- Polyalphabetic means we have more than one set of alphabet used for encryption.
- In case of Vigenère, two sets are used: the plain text and the key.

Assuming the latin alphabet for both sets, we can construct an encryption table:
- Encryption process uses the table header (first row) for the plain text and the first column for key. The key cycles.
- For example, plain text `WXYZ` with key `ABC` will produce the cyphertext `WYAZ`
    ![](https://www.dcode.fr/tools/vigenere/images/table.png)
    

Applying the same process to encrypt the lorem-ipsum text with key `AHMED`. In this case I just used the provided tool, but we may as well write a small Python script to do it.
```text
Plain text: Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum
Cyphertext: Lvdip iweyp dvxsu spf epea, osqsloxhtbd egiwuwfius ioia, eig dv qmxstah wetbsu iuomgikgrw ua xeeoyq iw dvxsue tmkqa hxmtuh. Gx hnpy eg mpzmp vlzmdm, xgmv nvexuuk qbhrjuxdtpar xlsmqfo smfrrpe rlsp gx dlpcylp lj id cvyqrdv osqslcydt. Kgmv abfi lrbdi gosav ln yqtueoqrgeyux ln capxpamxh vlxmw ezei fisxyp dvxsue lg jxgpmx qusxe sayuewuy. Qbfewfixr zurw ojoehchf gxpppewaa zsq pyamgeuf, wxna ur fusbe tup ajiijue gezqvxna ysolpf eqit uh hsa xeeoygq
```

### Caesar Cipher
Caesar cipher is another very popular cipher that uses substitution.
- Each letter is replaced by a corresponding one from a shifted version of the alphabet.
- Example: the latin alphabet and a shifted version (shifted to the left by 3 positions).
    ```
    ABCDEFGHIJKLMNOPQRSTUVWXYZ
    DEFGHIJKLMNOPQRSTUVWXYZABC
    ```
    Lorem ipsum text encrypted using that alphabet:
```
Plain text: Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum
Ciphertext: Oruhp lsvxp groru vlw dphw, frqvhfwhwxu dglslvflqj holw, vhg gr hlxvprg whpsru lqflglgxqw xw oderuh hw groruh pdjqd doltxd. Xw hqlp dg plqlp yhqldp, txlv qrvwuxg hahuflwdwlrq xoodpfr oderulv qlvl xw doltxls ha hd frpprgr frqvhtxdw. Gxlv dxwh luxuh groru lq uhsuhkhqghulw lq yroxswdwh yholw hvvh flooxp groruh hx ixjldw qxood sduldwxu. Hafhswhxu vlqw rffdhfdw fxslgdwdw qrq surlghqw, vxqw lq fxosd txl riilfld ghvhuxqw proolw dqlp lg hvw oderuxp
```

- I worked alone for this task, assuming I received the same text without knowledge of the used cypher, I can use https://www.dcode.fr/cipher-identifier and try suggested options. In this case, it recommends investigating multiple options, including "Caesar Cipher"

  ![](https://i.postimg.cc/nc2sj3Px/image.png) 

- The tool at https://quipqiup.com/ may also be helpful in decrypting the cipher (without identifying it) using frequency analysis and knowledge of English text.

  ![](https://i.postimg.cc/QN0scvhf/image.png)

## Task 2 - Enigma Machine

The enigma machine was used by the Germans during WW2 to encrypt their communications.
> Reference: "The Imitation Game" movie :)

- The Enigma machine can also be considered a poly-alphabetic cipher since the alphabets is overall being scrambled using other alphabets (by rotors, reflector, and potentially the plugboard).

- This picture explains it nicely, once a button is pressed on the keyboard, an electrical circuit is closed, the electricity goes through a different  encryption/decryption path where the letter is changed multiple times depending on the machine configuration (which is customizable for additional security).

    - Red line follows the encryption process, blue one follows the decryption.


    ![](https://www.scienceabc.com/wp-content/uploads/2015/12/enigma-machine-working.jpg)

- Example using the simulator at https://www.dcode.fr/enigma-machine-cipher with the default setting:
    ```
    Plain text: HAILHYDRA
    Ciphertext: IDGDMBFTL
    ```
