from flask import Flask, render_template
from prometheus_flask_exporter import PrometheusMetrics

app = Flask(__name__)
metrics = PrometheusMetrics(app)

@app.route('/')
def index():
    return render_template('index.html')


if __name__ == '__main__':
    app.run('0.0.0.0', 8080)
