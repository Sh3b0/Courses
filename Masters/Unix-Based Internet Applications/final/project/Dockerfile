FROM python:3-alpine

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["gunicorn", "-b", "0.0.0.0:5000", "--certfile", "/app/certs/tls.crt", "--keyfile", "/app/certs/tls.key", "wsgi"]
