FROM ubuntu:16.04
MAINTAINER Britikov Konstantin
RUN apt-get update -y
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER
USER postgres

RUN /etc/init.d/postgresql start &&        psql --command "CREATE USER chapay WITH SUPERUSER PASSWORD '4rfgt5';" && createdb -E UTF8 -T template0 -O chapay forumdb && /etc/init.d/postgresql stop

RUN echo "local all all trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf
RUN echo "host  all all 127.0.0.1/32 trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "host  all all ::1/128 trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf
RUN echo "host  all all 0.0.0.0/0 trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN cat /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "synchronous_commit = off" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "fsync = off" >> /etc/postgresql/$PGVER/main/postgresql.conf

RUN echo "shared_buffers = 512MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "work_mem = 8MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "maintenance_work_mem = 128MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "wal_buffers = 1MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "effective_cache_size = 1024MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "cpu_tuple_cost = 0.0030" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "cpu_index_tuple_cost = 0.0010" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "cpu_operator_cost = 0.0005" >> /etc/postgresql/$PGVER/main/postgresql.conf

RUN echo "log_statement = none" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_duration = off " >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_lock_waits = on" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_min_duration_statement = 50" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_filename = 'query.log'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_directory = '/var/log/postgresql'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_destination = 'csvlog'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "logging_collector = on" >> /etc/postgresql/$PGVER/main/postgresql.conf
EXPOSE 5432

VOLUME /etc/postgresql /var/log/postgresql /var/lib/postgresql
USER root
RUN apt-get install -y openjdk-9-jdk-headless
RUN apt-get install -y maven

ENV WORK /opt/
ADD / $WORK/
WORKDIR $WORK
RUN mvn package

EXPOSE 5000

CMD service postgresql start && java -Xms200M -Xmx200M -Xss256K -jar target/db-0.0.1-SNAPSHOT.jar
