# AuthorizationServer

Time Manager for scheduling tasks.

## Description
Features:
1. CRUD for events, subs and users
2. Each user may have multiple subs (e.g. multiple devices)
3. Notifications about events are distributed to all subs tied to user 
4. Event may be postponed by a fixed amount of minutes if the user wants to. In case of postponing all the events after the current one will be rescheduled to keep everything in order
5. Most user cases are covered by unit tests
6. The service is built with WebFlux with R2DBC using multithreading to deal with bottlenecks

Used technologies:
* Java 17
* Spring boot
* PostgreSQL
* Gradle
* Flyway
* Docker
* Postman

## Getting Started

### Prerequisites

* Docker

### Installing

* No installing needed, just run it with docker

### Executing program

* Run the compose
```
docker-compose up -d
```
### Accessing api

Api is available at localhost on port 8083 
```
http://localhost:8083/timemanager/api/v1/customers/create
```
Import postman collection to postman and test it

