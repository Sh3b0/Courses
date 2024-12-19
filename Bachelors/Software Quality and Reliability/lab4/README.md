# Lab4 - Testing coverage

[![pipeline status](https://gitlab.com/Sh3B0/sqr-lab4/badges/main/pipeline.svg)](https://gitlab.com/Sh3B0/sqr-lab1/-/commits/main)

1. Add tests to `/src/test`

2. Update CI to run tests in parallel

   ```yaml
   stages:
     - build
     - linter
     - test
   
   build:
     stage: build
     image: maven
     script:
       - chmod +x mvnw
       - ./mvnw package
     artifacts:
       paths:
         - target/demo-0.0.1-SNAPSHOT.jar
   
   linter:
     stage: linter
     image: maven
     script:
       - chmod +x mvnw
       - ./mvnw spotbugs:spotbugs
     artifacts:
       paths:
         - target/demo-0.0.1-SNAPSHOT.jar
   
   test:
     stage: test
     image: maven
     parallel:
       matrix:
         - TEST_NAME: forumControllerTests
         - TEST_NAME: forumDAOTests
         - TEST_NAME: postDAOTests
         - TEST_NAME: threadControllerTests
         - TEST_NAME: threadDAOTests
         - TEST_NAME: userDAOTests
     script:
       - chmod +x mvnw
       - ./mvnw test -Dtest=$TEST_NAME
   ```
