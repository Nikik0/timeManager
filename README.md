This project provides a non-blocking reactive Time Manager

Features:
1. CRUD for events, subs and users
2. Each user may have multiple subs (e.g. multiple devices)
3. Notifications about events are distributed to all subs tied to user 
4. Event may be postponed by a fixed amount of minutes if the user wants to. In case of postponing all the events after the current one will be rescheduled to keep everything in order
5. Most user cases are covered by unit tests
6. The service is built with WebFlux with R2DBC using multithreading to deal with bottlenecks

Next improvements (todo list):
* add flyway
* add blockhound
* null safety checks
* add more unit tests
* nt
* consider including kafka support for input streams
* consider further implementing multithreading
* add auth via external gateway service
* centralized exception handling
* add latest db schemas
