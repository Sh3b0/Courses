import os
from urllib.parse import urlencode

import jwt
import requests
from dotenv import load_dotenv
from flask import Flask, redirect, request, render_template


load_dotenv(override=True)

app = Flask(__name__)

# app.secret_key = os.getenv("APP_SECRET")
KEYCLOAK_SERVER_URL = os.getenv("KEYCLOAK_SERVER_URL")
KEYCLOAK_REALM = os.getenv("KEYCLOAK_REALM")
KEYCLOAK_CLIENT_ID = os.getenv("KEYCLOAK_CLIENT_ID")
KEYCLOAK_CLIENT_SECRET = os.getenv("KEYCLOAK_CLIENT_SECRET")
KEYCLOAK_REDIRECT_URI = os.getenv("KEYCLOAK_REDIRECT_URI")
CA_PATH = os.getenv("CA_PATH")

@app.route('/')
def index():
    return render_template('index.html', button_text="Log In", body_text="Please login to access page content!")

@app.route('/login')
def login():
    """Called when user clicks login"""
    auth_url = f"{KEYCLOAK_SERVER_URL}/realms/{KEYCLOAK_REALM}/protocol/openid-connect/auth"
    params = {
        "client_id": KEYCLOAK_CLIENT_ID,
        "response_type": "code",
        "redirect_uri": KEYCLOAK_REDIRECT_URI,
        "scope": "openid profile roles",
    }
    return redirect(f"{auth_url}?{urlencode(params)}")


@app.route('/callback')
def callback():
    """Called after a successful login"""
    code = request.args.get('code')

    # Obtain JWT
    token_url = f"{KEYCLOAK_SERVER_URL}/realms/{KEYCLOAK_REALM}/protocol/openid-connect/token"
    data = {
        "grant_type": "authorization_code",
        "code": code,
        "redirect_uri": KEYCLOAK_REDIRECT_URI,
        "client_id": KEYCLOAK_CLIENT_ID,
        "client_secret": KEYCLOAK_CLIENT_SECRET,
    }
    headers = {"Content-Type": "application/x-www-form-urlencoded"}
    token_response = requests.post(token_url, data=data, headers=headers, verify=CA_PATH)
    token_response.raise_for_status()
    tokens = token_response.json()
    app.logger.debug(f"Obtained tokens: {tokens.keys()}")
    
    # Obtain user role
    decoded_token = jwt.decode(tokens['access_token'], options={"verify_signature": False})
    role = decoded_token["resource_access"][KEYCLOAK_CLIENT_ID]["roles"][0]
    app.logger.debug(f"Decoded JWT: {decoded_token.keys()}")

    # Obtain user info
    user_info_url = f"{
        KEYCLOAK_SERVER_URL}/realms/{KEYCLOAK_REALM}/protocol/openid-connect/userinfo"
    user_info_response = requests.get(
        user_info_url,
        headers={"Authorization": f"Bearer {tokens['access_token']}"},
        verify=CA_PATH
    )
    user_info_response.raise_for_status()
    user_info = user_info_response.json()
    app.logger.debug(f"Obtained userinfo: {user_info}")
    
    return render_template('index.html', button_text="Log Out",
                           body_text=f"Logged in as {user_info["preferred_username"]} ({user_info.get("email", "")}) - role: {role}")

@app.route('/logout')
def logout():
    return redirect('/')

if __name__ == '__main__':
    app.run(debug=True)
