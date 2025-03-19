"""
Main application
"""
import json
import os

from flask import Flask, render_template, jsonify
from prometheus_flask_exporter import PrometheusMetrics

from logger import init_logger

VISITS_FILE = 'volume/visits.json'

app = Flask(__name__)
metrics = PrometheusMetrics(app)
logger = init_logger()


@app.route("/")
def home():
    """
    Returns rendered home page template
    """
    logger.info('GET /')
    if not os.path.exists(VISITS_FILE):
        if not os.path.exists('volume'):
            os.mkdir('volume')
        visits = 0
    else:
        with open(VISITS_FILE, 'r', encoding='UTF-8') as file:
            visits = int(json.load(file)["visits"])

    with open(VISITS_FILE, 'w', encoding='UTF-8') as file:
        json.dump({"visits": str(visits + 1)}, file)

    return render_template('index.html')


@app.route("/visits")
def visitor_count():
    """
    Returns visitor count
    """
    if not os.path.exists(VISITS_FILE):
        return jsonify({"visits": "0"})

    with open(VISITS_FILE, 'r', encoding='UTF-8') as file:
        visits = jsonify(json.load(file))

    return visits

# When executing `python app.py` instead of `flask run`
if __name__ == "__main__":
    app.run(debug=True, host='127.0.0.1', port=5000)
