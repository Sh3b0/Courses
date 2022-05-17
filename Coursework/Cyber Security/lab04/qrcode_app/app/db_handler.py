import sqlite3


def connect():
    try:
        con = sqlite3.connect('db.sqlite')
    except sqlite3.Error as error:
        print(f"Failed to connect to the database: {error}")
        return
    return con


def init_db():
    con = connect()
    try:
        cur = con.cursor()
        query = """
            create table if not exists files(
                file_hash text primary key,
                file blob not null
            );
        """
        cur.execute(query)
        con.commit()
        cur.close()
    except sqlite3.Error as error:
        print("Failed to initialize database: ", error)
        return

    con.close()


def insert_blob(file_hash, file):
    con = connect()
    try:
        cur = con.cursor()
        query = """INSERT INTO files (file_hash, file) VALUES (?, ?)"""
        cur.execute(query, (file_hash, file))
        con.commit()
        cur.close()
    except sqlite3.Error as error:
        print("Failed to insert blob: ", error)
        return

    con.close()


def retrieve_blob(file_hash):
    con = connect()
    try:
        cur = con.cursor()
        query = """SELECT file FROM files WHERE file_hash = (?) LIMIT 1"""
        cur.execute(query, (file_hash,))
        con.commit()
        blob = cur.fetchone()
        cur.close()
    except sqlite3.Error as error:
        print("Failed to retrieve blob: ", error)
        return

    con.close()
    return blob[0]
