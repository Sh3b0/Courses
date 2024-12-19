class Session:
    def __init__(self, session_id):
        self.session_id = session_id
        self.data = {}

    def set_data(self, key, value):
        self.data[key] = value

    def get_data(self, key):
        return self.data.get(key)

    def clear_data(self):
        self.data = {}

# Create a new session
session = Session(session_id='123')

# Set some data in the session
session.set_data(key='username', value='johndoe')
session.set_data(key='logged_in', value=True)

# Get some data from the session
username = session.get_data(key='username')
is_logged_in = session.get_data(key='logged_in')

print(f"{username} active status: {is_logged_in}")

# Clear the session data
session.clear_data()
