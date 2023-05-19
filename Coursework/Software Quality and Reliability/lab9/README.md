# Lab 9 - Software Quality and Reliability

Execute some security tests manually from [OWASP CSS](https://cheatsheetseries.owasp.org/index.html) against two websites

## Testing [edraak.org](https://www.edraak.org/)

**Requirement**: [Forgot Password](https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html) / Return a consistent message for both existent and non-existent accounts.

| Step                                                 | Result                                                       |
| ---------------------------------------------------- | ------------------------------------------------------------ |
| Open website                                         | Success - home page loads                                    |
| Click on the "دخول" button                           | Success - navigated to [login page](https://programs.edraak.org/login/) |
| Click on "هل نسيت كلمة السر؟"                        | Success - navigated to [reset password](https://programs.edraak.org/reset_password/) |
| Enter a random non-existing email                    | Success - displayed a generic message about recovery email being sent if the account exists. |
| Go back to main page and create a new account        | Success - account created                                    |
| Repeat the above test with the newly created account | Success - displayed the same generic message                 |

**Verdict**: passed - the same message is displayed for existing and non-existing accounts.

---

**Requirement**: [Authentication](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html) / Implement Proper Password Strength Controls

| Step                                                         | Result                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Open website                                                 | Success                                                      |
| Click on "سجل مجانا" button                                  | Success - navigated to [signup page](https://programs.edraak.org/register/) |
| Enter any valid email under "البريد الإلكتروني"              | Success                                                      |
| Enter any name in Latin letters (e.g.: "Test") under "الاسم الكامل بالعربية" | Failure - name is accepted even though the label asks for full name in Arabic only |
| Enter the password `123456` under "كلمة السر"                | Failure - weak password is accepted                          |

**Verdict**: failed - the website does not implement the required level of verification for password strength, which should be at least 8 characters long.

## Testing [i2pdf.com](https://www.i2pdf.com/)

**Requirement**: [File Upload](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html) / List allowed extensions

| Step                                         | Result                                                       |
| -------------------------------------------- | ------------------------------------------------------------ |
| Open website                                 | Success                                                      |
| Click on "Compress PDF"                      | Success - navigated to [compress pdf page](https://www.i2pdf.com/compress-pdf) |
| Click on "Choose Files"                      | Success - the file picker dialog only allows `.pdf` files    |
| Drag an image file into the file picker area | Success - displayed an error message rejecting the file type. |

**Verdict**: passed - the file picker dialog only allows `.pdf` files.

---

**Requirement**:  [File Upload](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html) / Set a filename length limit. Restrict the allowed characters if possible

| Step                                | Result                                                       |
| ----------------------------------- | ------------------------------------------------------------ |
| Open website                        | Success                                                      |
| Click on "Compress PDF"             | Success - navigated to [compress pdf page](https://www.i2pdf.com/compress-pdf) |
| Click on "Choose Files"             | Success                                                      |
| Upload a file with a very long name | Failure - website shown a JS alert "Server returned code 0" and freezes with "100% upload" message. |
| Upload a file with a non-ASCII name | Failure - file was accepted                                  |

**Verdict**: failed - long file names result in server errors, and non-ASCII file names are accepted (resulting downloaded files do not contain the original name and have no extension).
