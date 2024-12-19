FROM maven

COPY . /monitor-demo
WORKDIR /monitor-demo
RUN ls
RUN mvn install
ENTRYPOINT mvn spring-boot:run

