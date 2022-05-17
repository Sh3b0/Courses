FROM alpine
COPY . /app
WORKDIR /app
ENV PYTHONUNBUFFERED=1
RUN apk add --update --no-cache python3 libqrencode
RUN ln -sf python3 /usr/bin/python
RUN python3 -m ensurepip
RUN pip3 install --no-cache --upgrade pip setuptools
RUN pip3 install -r requirements.txt
EXPOSE 8080
ENTRYPOINT ["gunicorn", "--bind", "0.0.0.0:8080"]
CMD ["wsgi"]
