FROM python:3.8

ADD entrypoint.sh /entrypoint.sh
ADD src /src

RUN pip install faraday-cli

ARG HOST
ARG USERNAME
ARG PASSWORD
ARG WORKSPACE
ARG FILES

ENTRYPOINT ./entrypoint.sh $HOST $USERNAME $PASSWORD $WORKSPACE $FILES