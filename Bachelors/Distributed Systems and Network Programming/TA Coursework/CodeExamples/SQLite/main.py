import sqlite3


def connect_to_db():
    """Returns a Connection object to the SQLite database"""
    try:
        conn = sqlite3.connect('db.sqlite')
    except sqlite3.Error as error:
        print(f"Failed to connect to the database: {error}")
        return
    return conn


def execute_query(conn: sqlite3.Connection, query: str, params=[]):
    """Executes a query against the database and return results"""
    try:
        cur = conn.cursor()
        cur.execute(query, params)
        conn.commit()
        print(f"Query results: {cur.fetchall()}")
    except sqlite3.Error as error:
        print(f"Failed to execute a query against the database: {error}")
        return


if __name__ == '__main__':
    open('db.sqlite', 'w').close()  # create or overwrite the database file
    conn = connect_to_db()
    execute_query(
        conn, f"CREATE TABLE Test (Id INTEGER NOT NULL PRIMARY KEY, Name STRING)")
    execute_query(conn, f"INSERT INTO Test VALUES (?, ?)", [1, "a"])
    execute_query(conn, f"INSERT INTO Test VALUES (?, ?)", [2, "b"])
    execute_query(conn, f"SELECT * FROM Test WHERE name = ?", ['a'])
    conn.close()
