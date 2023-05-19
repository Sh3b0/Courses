# Lab 1 - Introduction to the quality gates

[![pipeline status](https://gitlab.com/Sh3B0/sqr-lab1/badges/main/pipeline.svg)](https://gitlab.com/Sh3B0/sqr-lab1/-/commits/main)

## Lab

1. Create your branch of the `Lab1 - Introduction To the quality gates` repo. [***Here***](https://gitlab.com/sqr-inno/s23-lab-1-introduction-to-the-quality-gates)

2. Create `.gitlab-ci.yml` file inside of the repo

3. Add this code to your CI file and push it to your branch:

   ```yaml
   stages:
     - build
     
   build:
     stage: build
     image: maven
     script:
       - chmod +x mvnw
       - ./mvnw package
     artifacts:
       paths:
         - target/main-0.0.1-SNAPSHOT.jar
   ```

4. You should have working pipeline that automatically builds your project by now.

5. Open `Readme.md` and then follow these steps:

   - Go to Settings > CI / CD

   - Expand the General pipelines settings section

   - Scroll down to Pipeline status and/or Coverage report

   - Select your branch

   - Copy link for MD and paste it to the `Readme.md`

   - You should see a label `pipeline|{CI status}`

