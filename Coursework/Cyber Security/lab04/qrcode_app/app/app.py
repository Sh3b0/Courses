from flask import *
import os
import hashlib
import db_handler

app = Flask(__name__)
db_handler.init_db()


@app.route("/")
def home():
    return render_template('index.html', img_path=url_for('static', filename='images/placeholder.png'))


@app.route("/generate_qr", methods=["POST"])
def generate_qr():
    data = request.form['data']
    file_hash = hashlib.md5(data.encode('utf-8')).hexdigest()
    os.system(f"qrencode -o /tmp/{file_hash}.png {data}")
    with open(f"/tmp/{file_hash}.png", "rb") as f:
        db_handler.insert_blob(file_hash=file_hash, file=f.read())
    return render_template('index.html', img_path=f"/get_qr/{file_hash}")


@app.route("/get_qr/<file_hash>", methods=["GET"])
def get_qr(file_hash):
    blob = db_handler.retrieve_blob(file_hash)
    if blob is None:
        return "Requested file was not found", 404
    return Response(blob, mimetype="image/png")


# Debug mode
if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=8080)
