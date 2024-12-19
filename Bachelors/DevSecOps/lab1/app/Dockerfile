FROM golang:1.18-alpine

WORKDIR /app

COPY go.mod ./

COPY *.go ./

RUN go build -o ./build/

EXPOSE 8080

USER 1001

CMD [ "./build/main" ]
