# Lab 3 - Software Quality and Reliability

1. Update [.gitlab-ci.yml](.gitlab-ci.yml) by adding a `test` stage

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
         - target/db-0.0.1-SNAPSHOT.jar
   
   linter:
     stage: linter
     image: maven
     script:
       - chmod +x mvnw
       - ./mvnw spotbugs:spotbugs
     artifacts:
       paths:
         - target/db-0.0.1-SNAPSHOT.jar
   
   test:
     stage: test
     image: maven
     script:
      - chmod +x mvnw
      - ./mvnw test
   ```

2. Write tests in [ForumControllerTests.java](./src/test/java/com.hw.db/controllers/ForumControllerTests.java) and [ThreadControllerTests.java](./src/test/java/com.hw.db/controllers/ThreadControllerTests.java)

3. Push code to run pipeline.
