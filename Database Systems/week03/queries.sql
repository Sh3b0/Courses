DROP TABLE IF EXISTS COMPANY;
DROP TABLE IF EXISTS GENRES;
DROP TABLE IF EXISTS MOVIE;
DROP TABLE IF EXISTS MOVIE_GENRE;
DROP TABLE IF EXISTS PERSON;
DROP TABLE IF EXISTS DIRECTS;
DROP TABLE IF EXISTS ACTING;
DROP TABLE IF EXISTS QUOTES;

-- CREATING TABLES --

CREATE TABLE COMPANY
(
    Cid   INT PRIMARY KEY,
    Cname VARCHAR(255),
    Caddr VARCHAR(255)
);

CREATE TABLE GENRES
(
    Gid   INT PRIMARY KEY,
    Gname VARCHAR(255)
);

CREATE TABLE MOVIE
(
    Mid   INT PRIMARY KEY,
    Title VARCHAR(255),
    Yop   INT,
    Len   INT,
    Plot  VARCHAR(2048),
    Cid   INT,
    FOREIGN KEY (Gid) REFERENCES GENRES (Gid),
    FOREIGN KEY (Cid) REFERENCES COMPANY (Cid)
);

CREATE TABLE MOVIE_GENRE
(
    Mid INT,
    Gid INT,
    FOREIGN KEY (Mid) REFERENCES MOVIE (Mid),
    FOREIGN KEY (Gid) REFERENCES GENRES (Gid)
);

CREATE TABLE PERSON
(
    Pid  INT PRIMARY KEY,
    Name VARCHAR(255),
    Dob  DATE
);

CREATE TABLE DIRECTS
(
    Pid INT,
    Mid INT,
    FOREIGN KEY (Pid) REFERENCES PERSON (Pid),
    FOREIGN KEY (Mid) REFERENCES MOVIE (Mid)
);

CREATE TABLE ACTING
(
    Pid  INT,
    Mid  INT,
    FOREIGN KEY (Pid) REFERENCES PERSON (Pid),
    FOREIGN KEY (Mid) REFERENCES MOVIE (Mid),
    Role VARCHAR(255)
);

CREATE TABLE QUOTES
(
    Pid   INT,
    Mid   INT,
    FOREIGN KEY (Pid) REFERENCES PERSON (Pid),
    FOREIGN KEY (Mid) REFERENCES MOVIE (Mid),
    Quote VARCHAR(255)
);

-- SAMPLE DATA --

INSERT INTO PERSON(Pid, Name, Dob)
VALUES (1, 'Chris Evans', TO_DATE('13/06/1981', 'DD/MM/YYYY'));
INSERT INTO PERSON(Pid, Name, Dob)
VALUES (2, 'Joe Russo', TO_DATE('18/07/1971', 'DD/MM/YYYY'));
INSERT INTO PERSON(Pid, Name, Dob)
VALUES (3, 'Anthony Russo', TO_DATE('03/02/1970', 'DD/MM/YYYY'));
INSERT INTO COMPANY(Cid, Cname)
VALUES (1, 'Marvel');
INSERT INTO GENRES(Gid, Gname)
VALUES (1, 'Action'),
       (2, 'Sci-Fi');

INSERT INTO MOVIE(Mid, Title, Yop, Len, Plot, Cid)
VALUES (1,
        'Avengers: Endgame',
        2019,
        182,
        'After Thanos, an intergalactic warlord, disintegrates half of the universe, the Avengers must reunite and assemble again to reinvigorate their trounced allies and restore balance.',
        1);

INSERT INTO DIRECTS(Pid, Mid)
VALUES (2, 1),
       (3, 1);
INSERT INTO ACTING(Pid, Mid, Role)
VALUES (1, 1, 'Captain America');
INSERT INTO QUOTES(Pid, Mid, Quote)
VALUES (1, 1, 'I keep telling everybody they should move on. Some do, but not us.');
INSERT INTO MOVIE_GENRE(mid, gid)
VALUES (1, 1),
       (1, 2);


-- Queries to practice --
-- All actors in a specific Movie with their Role
select Name, Role from PERSON inner join ACTING on PERSON.pid = ACTING.Pid and ACTING.Mid = 1;
-- All action movies
select Title from MOVIE inner join MOVIE_GENRE on MOVIE.Mid = MOVIE_GENRE.Mid and MOVIE_GENRE.Gid = 1;
-- All movies directed by a specific PERSON
select Title from MOVIE inner join DIRECTS on DIRECTS.Pid = 2;
-- All movies produced by some COMPANY
select Title from MOVIE where MOVIE.Cid = 1;
-- All quotes spoken by a certain actor and their respective movie.
select Quote, Title from Quotes inner join MOVIE on Quotes.Mid = 1;
