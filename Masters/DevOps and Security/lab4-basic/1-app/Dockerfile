FROM python:alpine

WORKDIR /app

COPY requirements.txt .

ENV PYTHONUNBUFFERED=1

RUN pip install --no-cache-dir -r requirements.txt \ 
    && addgroup -S app \
    && adduser -S app -G app \
    && chown -R app:app .

COPY . .

EXPOSE 8080

USER app

CMD ["gunicorn", "--bind", "0.0.0.0:8080", "wsgi"]
