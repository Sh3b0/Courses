"""
Entry point to the application WSGI
"""
from app import app as application

if __name__ == '__main__':
    application.run()
