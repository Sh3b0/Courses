# SNA Lab 11: Database Administration 

- Create new user and grant them admin rights.

  ```bash
  $ sudo su - postgres -c "createuser ahmed"
  $ sudo -u postgres psql
  postgres=# GRANT ALL PRIVILEGES ON DATABASE postgres TO ahmed;
  GRANT
  postgres=# exit
  ```

  

- Create new database, [switch to new database](https://koenwoortman.com/postgresql-switch-database-with-connect/) and [create table](https://www.postgresqltutorial.com/postgresql-create-table/) in your new database.

  ````bash
  $ sudo -u postgres psql
  postgres=# CREATE DATABASE new_db;
  postgres=# \c new_db
  You are now connected to database "new_db" as user "postgres".
  new_db=# CREATE TABLE users (
  	user_id serial,
      username VARCHAR(50)
  );
  CREATE TABLE
  postgres=# exit
  ````

  

- [Backup](https://www.tecmint.com/backup-and-restore-postgresql-database/) your new database by naming it.

  ```bash
  $ sudo -iu postgres
  $ pg_dump -F c new_db > new_db.dump
  $ exit
  ```

  

- Delete your new database and [restore ](https://www.tecmint.com/backup-and-restore-postgresql-database/)it from backup which you created in 3rd question.

  ```bash
  $ sudo -u postgres psql
  postgres=# DROP DATABASE new_db;
  DROP DATABASE
  postgres=# exit
  ```

  ```bash
  $ sudo -u postgres psql
  postgres=# CREATE DATABASE new_db;
  CREATE DATABASE
  postgres=# exit
  $ sudo su - postgres -c "pg_restore -d new_db new_db.dump"
  ```

  Verify: `SELECT * FROM users`

  ![image-20211204025353068](../images/image-20211204025353068.png)
