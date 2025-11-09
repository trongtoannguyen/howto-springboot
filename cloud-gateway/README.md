# Preface

Dependencies: Reactive Gateway, Resilience4J, and Contract Stub Runner.

In this guide, we route all of our requests to HTTPBin.
If Httpbin server is unavailable, you can run a local instance by following the instructions with Docker

## Run httpbin locally

To run locally, comment httpbin.uri in `application.properties` then:
1. Ensure you have Docker installed on your machine.
2. Run the following command to pull and start the Httpbin Docker container:

```shell
docker run -p 80:80 kennethreitz/httpbin
```

1. Access Httpbin at `http://localhost:80`.
2. Test methods with the following bash commands:
```shell
curl.exe -D - 'http://localhost:8080/get'
```
```shell
curl.exe -D - -H 'Host: www.circuitbreaker.com' http://localhost:8080/delay/3
```
```shell
curl.exe -D - -H 'Host: www.abc.org' http://localhost:8080/hola
```
```shell
curl.exe -D - -H 'Host: www.abc.org' http://localhost:8080/anything/png
```
```shell
curl.exe -X POST -D - -H 'Host: www.statuscode.org' -d 'code' http://localhost:8080/200
```
```shell
curl.exe -D - -H "Host: www.rewriteresponseupper.org" http://localhost:8080/xml
```

## Gateway

### Problem

In the microservices system with thousands of services, to allow them to communicate, each service needs to know ip,
port, communicate protocol then configure CORS, authentication, rate limiting, logging, monitoring, etc. a lot of things
to do. It's a security and maintenance concern when exposing critical information to the internet. What happens when
half of the services change their IP addresses or launch a DDoS attack by sending millions of requests to other
services?

### Solution

API Gateway acts as a gate guard or single entry point between clients and backend services. Client only needs to know
the only ip
address of the server, it then manages and routes requests to the appropriate backend services.

#### Routing

route requests to the appropriate backend services based on the request path, method, headers, etc.

#### Authen and autho

for instance, receive and check JWT token in the request header, validate and take the corresponding action.

#### Request aggregation

instead of calling multiple services, clients call a request to Gateway; then its responsibility is to call appropriate
services to gather data and return a single response to the client.

#### Rate limiting

limit the number of requests that a client can make in a time period.

#### Logging and monitoring

log requests and responses, monitor performance metrics, etc.

### Popular API Gateway for enterprise

- Kong: the most popular open-source API Gateway.
- AWS API Gateway: a fully managed service from Amazon Web Services.


## Websocket Sample

[install wscat](https://www.npmjs.com/package/wscat)

In one terminal, run websocket server:
```
wscat --listen 9000
``` 

In another, run a client, connecting through gateway:
```
wscat --connect ws://localhost:8080/echo
```

type away in either server and client, messages will be passed appropriately.

## Running Redis Rate Limiter Test

Make sure redis is running on localhost:6379 (using brew or apt or docker).

Then run `DemogatewayApplicationTests`. It should pass which means one of the calls received a 429 TO_MANY_REQUESTS HTTP status.