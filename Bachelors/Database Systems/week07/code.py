import psycopg2
from geopy.geocoders import Nominatim

# connecting to the database server
con = psycopg2.connect(database="dvdrental", user="postgres",
                       password="postgres", host="127.0.0.1", port="5432")
print("Database opened successfully")

cur = con.cursor()

# call function
cur.execute('''select get_addr();''')
res = cur.fetchall()

locator = Nominatim(user_agent="ahmed_123456")
print(res)

# Execute once to add the columns

# cur.execute('''alter table address add column longitude float;''')
# cur.execute('''alter table address add column latitude float;''')

for i in res:
    location = locator.geocode(i[0])
    lat = 0
    lon = 0
    if location is not None:
        lat = location.latitude
        lon = location.longitude

    query = '''UPDATE address
    SET longitude = ''' + str(lon) + ''', latitude = ''' + str(lat) + '''
    WHERE address = \'''' + i[0] + '''\';'''
    # print(query)
    cur.execute(query)
    con.commit()
