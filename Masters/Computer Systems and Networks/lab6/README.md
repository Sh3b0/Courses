# Lab 6 - Static Code Analysis

> Ahmed Nouralla - a.shaaban@innopolis.university

Static code analyzers are software that analyzes source code for vulnerabilities without running it. It runs a set of rules against the source code to find common patterns. GitLab offers out-of-the-box integration with common analyzers including [Semgrep](https://semgrep.dev/).

In this lab, I use [GitLab SAST](https://docs.gitlab.com/ee/user/application_security/sast/) to practice [Static Application Security Testing (SAST)](https://en.wikipedia.org/wiki/Static_application_security_testing) against a well-known, vulnerable-by-design [Damn Vulnerable Java Application (DVJA)](https://github.com/appsecco/dvja)

[TOC]

## GitLab SAST against DVJA

1. Create a new repository on GitLab to contain the vulnerable application

2. Clone DVJA and push it to the newly-created repository

   ```bash
   # Clone DVJA
   git clone https://github.com/appsecco/dvja
   
   # Remove git history from the project
   cd dvja
   sudo rm -rf .git/
   
   # Create fresh repository and push the application
   git init --initial-branch=main
   git remote add origin git@gitlab.com:Sh3B0/dvja.git
   git add .
   git commit -m "Initial commit"
   git push --set-upstream origin main
   ```

3. Add `.gitlab-ci.yaml` in the project root with the following content

   ```yaml
   stages:
     - test
   
   sast:
     stage: test
   
   include:
     - template: Security/SAST.gitlab-ci.yml
   ```

4. Push the new changes and check the CI results.

   ![image-20241110031317428](https://i.postimg.cc/SxgKYWh1/image.png)

5. The code was checked against several "rule" files for common vulnerable coding patterns and the  results were uploaded as an artifact. The scan results are attached to this report. 

   ![image-20241110031426544](https://i.postimg.cc/XYjJvsTy/image.png)

6. Results hinted on multiple issues in the application. Notably, it detect two cases where the infamous SQL injection vulnerability is present in the following files and respective lines:

   ```java
   // ProductService.java:48
   Query query = entityManager.createQuery("SELECT p FROM Product p WHERE p.name LIKE '%" + name + "%'");
   
   // UserService.java:75
   Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.login = '" + login + "'");
   ```

## GitLab SAST against DVWA

Let's run the same pipeline against Damn Vulnerable Web Application (DVWA).

```bash
git clone https://github.com/digininja/DVWA
# Replace the DVJA files with DVWA files in repo and .gitlab-ci.yml as it is
git add -A
git commit -m "Replace DVJA with DVWA"
git push
```

The scan runs and the resulting JSON is included with this report.

![image-20241110034742601](https://i.postimg.cc/FsQzMrpp/image.png)

Report also shows multiple vulnerabilities detected. This screenshot shows information about a detected OS command injection vulnerability at the file `view_help.php`, line 20.

![image-20241110034515365](https://i.postimg.cc/B6rbQ40q/image.png)



## SonarQube against DVJA

1. Run SonarQube in a docker container using the following command

   ```bash
   docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
   ```

2. Login at http://localhost:9000 with credentials: `admin:admin` and change password as requested.
3. From the Web UI, create a project manually and follow the instructions to setup integration with the repository containing the project.
4. SonarQube will give instructions to modify `pom.xml` and include the analysis token (starts with `sqp_`)
5. It will also provide a CI script like `.gitlab-ci.yml` or `github_action.yaml` to integrate SonarQube scan with the CI.
6. Push changes to see scan results. It shows detected vulnerabilities and bad coding practices.

![issues](https://i.postimg.cc/k46DmDL1/image.png)

