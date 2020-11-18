# BeBetter-server

BeBetter-server is a server application for BeBetter - a habit tracker application with reward system and elements of competition.

The main purpose of BeBetter-server is to implement application logic, save and read data from a global database and to share it with mobile application with REST API.

[BeBetter-client](https://github.com/ozarychta/BeBetter-client) is an Android mobile application that consumes the API and serves as a graphical user interface for the application.

Using BeBetter users can create challenges (habits) and track progress in completing that challenges. Many users can join one challenge and have a conversation in the comments. For completing goals users are rewarded with points and achievements, which are visible on users' profile pages. Users can follow each other and see friends in ranking list. 
(More about BeBetter functionalities in [BeBetter-client](https://github.com/ozarychta/BeBetter-client) README file.)


Project is still under development - additional funcionalities will be added and refactoring will be done.

## API documentation

API documentation created with Swagger UI:
https://be-better-server.herokuapp.com/swagger-ui.html

If the link doesn't work, wait a minute and try again.
(It may happen because BeBetter-server is deployed on Heroku with free plan in which apps sleep after 30 mins of inactivity - after receiving new request app will be started again.)

## Technologies and tools
* Java
* Spring, Spring Boot
* JPA, Hibernate, PostgreSQL
* Google Sign-In
* IntelliJ IDEA
* Heroku
* Swagger UI


## To add in future
* Make API responses pageable
* Implement HATEOAS
* Add unit tests
* Add integration tests
