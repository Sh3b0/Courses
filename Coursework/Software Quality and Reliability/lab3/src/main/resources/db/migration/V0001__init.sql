--  DROP TABLE if EXISTS votes;
--  DROP TABLE if EXISTS posts;
--  DROP TABLE if EXISTS threads;
--  DROP TABLE if EXISTS users_forum;
--  DROP TABLE if EXISTS forums;
--  DROP TABLE if EXISTS users;
CREATE EXTENSION IF NOT EXISTS citext;



CREATE TABLE IF NOT EXISTS users(
  id SERIAL NOT NULL PRIMARY KEY,
  nickname CITEXT COLLATE ucs_basic NOT NULL UNIQUE,
  fullname TEXT ,
  email CITEXT NOT NULL  UNIQUE ,
  about TEXT
);

CREATE TABLE IF NOT EXISTS forums(
  id SERIAL NOT NULL PRIMARY KEY ,
  description TEXT,
  posts INTEGER DEFAULT 0,
  slug CITEXT UNIQUE NOT NULL,
  threads INT DEFAULT 0,
  title VARCHAR(128) NOT NULL,
  creator CITEXT REFERENCES users (nickname) NOT NULL
);


CREATE TABLE IF NOT EXISTS threads(
  id SERIAL NOT NULL PRIMARY KEY ,
  author CITEXT REFERENCES users(nickname) NOT NULL ,
  created TIMESTAMPTZ NOT NULL ,
  forum CITEXT REFERENCES forums(slug) NOT NULL,
  message TEXT NOT NULL,
  slug CITEXT UNIQUE,
  title TEXT NOT NULL ,
  votes INT DEFAULT 0,
  UNIQUE (author,forum,title)
);

CREATE TABLE IF NOT EXISTS posts(
  id SERIAL NOT NULL  PRIMARY KEY ,
  message TEXT NOT NULL ,
  created TIMESTAMPTZ NOT NULL ,
  author CITEXT REFERENCES users (nickname) NOT NULL ,
  parent INTEGER REFERENCES posts(id) DEFAULT 0,
  path INT ARRAY,
  forum CITEXT REFERENCES forums(slug),
  isEdited BOOLEAN DEFAULT FALSE,
  thread INT REFERENCES threads(id),
  branch INT[]
);

CREATE TABLE IF NOT EXISTS votes(
  id SERIAL NOT NULL PRIMARY KEY,
  tid INTEGER REFERENCES threads(id),
  nickname CITEXT REFERENCES users (nickname) NOT NULL ,
  voice INT NOT NULL,
  UNIQUE (tid,nickname)
);


CREATE TABLE IF NOT EXISTS forum_users(
  id SERIAL NOT NULL PRIMARY KEY ,
  forum CITEXT REFERENCES forums(slug),
  nickname CITEXT ,
  fullname TEXT ,
  email CITEXT NOT NULL ,
  about TEXT,
  UNIQUE (forum,nickname)
);

CREATE INDEX threads_slug ON threads(slug);
CREATE INDEX forum_users_forum ON forum_users(forum);
CREATE INDEX thread_forum ON threads(forum);

CREATE INDEX posts_thread_id ON posts(thread, id, created);
CREATE INDEX posts_thread_b ON posts(thread, branch);
CREATE INDEX posts_thread_b1 ON posts(thread, (branch[1]));
