import time
import secrets
from flask import Flask, session, redirect, jsonify, request, make_response
from utils.db_connection import Connector
from utils.renders import render_template
import jwt
from waitress import serve

app = Flask(__name__)
conn = Connector()
cursor = conn.initMySQL()
token_jwt = secrets.token_bytes(32)


@app.before_request
def checkBefore():
    global cursor
    global session
    # print(session)
    cursor = conn.initMySQL()
    if session and "time" in session and time.time() - session['time'] > 7000: #2-hours
        session.pop("time", None)
        return redirect("/")


def verifyAndDecodeToken(encoded):
    decodedJWT = ""
    error = False
    try:
        decodedJWT = jwt.decode(encoded, token_jwt, algorithms=["HS256"])
    except Exception:
        error = True

    return decodedJWT if not error else False



@app.route('/addToCart', methods=["POST"])
def addToCart():
    if session and "encodedJWT" in request.cookies:
        decodedJWT = verifyAndDecodeToken(request.cookies.get('encodedJWT'))
        if decodedJWT and decodedJWT['authorized'] == "true":
            productid = request.form["productid"]
            productInCart = conn.addProductToCart(cursor, productid, decodedJWT['userId'])

            if productInCart:
                return jsonify({"success":"Product added to the cart:  " + decodedJWT['username'] })
    return redirect('/')


@app.route('/shop', methods=["GET", "POST"])
def shop():
    # print(session)
    # print(request.cookies)
    if session and "encodedJWT" in request.cookies:
        decodedJWT = verifyAndDecodeToken(request.cookies.get('encodedJWT'))
        print(decodedJWT)
        if decodedJWT and decodedJWT['authorized'] == "true":
            productsList = conn.getProductsFromDb(cursor)
            return render_template('shop.html', products = productsList)

    return redirect('/')


@app.route('/showCart', methods=["GET"])
def showCart():
    if session and "encodedJWT" in request.cookies:
        decodedJWT = verifyAndDecodeToken(request.cookies.get('encodedJWT'))
        if decodedJWT and decodedJWT['authorized'] == "true":
            productInCart = conn.getProductFromCart(cursor, decodedJWT['userId'])
            message = 'No stuffs found yet in your cart'
            if len(productInCart)> 0:
                message = decodedJWT['username'] + " This is your cart"
            return render_template('cart.html', products = productInCart, message=message, display="block")

    return redirect('/')


@app.route('/refreshTime', methods=['HEAD'])
def refresh():
    # print("refresh called")
    # print(session.items())
    if "time" not in session:
        session['time'] = time.time()
    return redirect('/')


@app.route('/', methods=["GET", "POST"])
def login():
    session["test"] = "true"
    if request.method == "POST" and "identifier" in request.cookies:
        username = request.form["username"]
        password = request.form["password"]
        identifier = request.cookies['identifier']
        loginResult = conn.loginUserCheck(username, password, identifier, cursor)

        if (loginResult):
            #set jwt token to allow the access to the shop
            encodedJwt = jwt.encode({"authorized": "true", "username":username, \
                                     "identifier":identifier, "userId":loginResult}, token_jwt, algorithm="HS256")
            resp = make_response(redirect('/shop'))
            resp.set_cookie('encodedJWT', encodedJwt)
            return resp

        return jsonify({"error":"Invalid credentials"})

    return render_template('login.html')



@app.route('/register', methods=["GET", "POST"])
def register():
    # print(session)
    if session and "authorized" in session and session['authorized'] == True:
        if request.method == "POST":
            username = request.form["username"]
            password = request.form["password"]
            print(password)

            if len(password) < 8:
                return jsonify({"error":"Password must have at least 8 characters"})

            registerResult = conn.registerUser(username, password, cursor)

            if not registerResult:
                return jsonify({"error":"Registration was not succesfull"})

            resp = make_response(redirect('/'))
            resp.set_cookie('identifier', registerResult)

            return resp

        return render_template('register.html')

    return jsonify({"error":"Sign Up is not allowed yet."})


if __name__ == "__main__":
    app.secret_key = secrets.token_hex(16)
    print(app.secret_key)
    serve(app, host='0.0.0.0', port=1999)
