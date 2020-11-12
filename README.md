# BeBetter-server

BeBetter is a mobile habit tracker application with social elements and reward system.

Users can create challenges (habits) and track progress in completing that challenges. Many users can join one challenge and have a conversation in the form of comments. For completing goals users are rewarded with ranking points and achievements, which are visible on user’s profile page. 
(More about BeBetter functionalities in [BeBetter-client](https://github.com/ozarychta/BeBetter-client) README file.)

Project is still under development - additional funcionalities will be added and refactoring will be done.

## Architecture

Project was created in client-server architecture. 

The main purpose of BeBetter-server is to implement application logic, save and read data from a global database and to share it with mobile application with REST API.

[BeBetter-client](https://github.com/ozarychta/BeBetter-client) is an Android mobile application that consumes the API and serves as a graphical user interface for the application.

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
* Add unit tests
* Add integration tests
