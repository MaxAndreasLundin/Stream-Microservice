Stream Microservice Documentation

Overview
The Stream Microservice is a Spring Boot application that serves to limit the number of simultaneous video streams that a user can consume. The service exposes REST APIs to start and stop video streams and to fetch the currently running streams for a user. It interacts with the https://tv4-search.a2d.tv/ API to validate video IDs.
Setup Instructions 
This section guides you on how to set up the Stream Microservice on your local machine.
Prerequisites
Ensure that you have Docker installed on your machine.
Ensure that you have Docker Compose installed on your machine.
Ensure that you have Maven installed on your machine.
Steps
Clone the repository to your local machine.
Navigate to the root directory of the project.
Build the JAR file for your application using the following command:

mvn clean package
This command builds a JAR file of your application using Maven. The clean command removes any previous builds, and the package command compiles your code and packages it into a JAR file.
     4. Build the Docker image for your application using the following command:
    docker build -t tv4-image .
         This command builds a Docker image named "tv4-image" using the Dockerfile in your current directory.	
     5. Start the database and the microservice using Docker Compose with the following command:
    docker compose up
        This command starts the services defined in your docker-compose.yml in detached mode. 
     6. To stop the services, you can use the following command:
   docker-compose down
       This command stops and removes the containers defined in your docker-compose.yml.

Service Dependencies
The Stream Microservice relies on the following external service:
TV4 Search API
The Stream Microservice validates video IDs by interacting with the TV4 Search API. The base URL for this API is https://tv4-search.a2d.tv/. Documentation for the TV4 Search API can be found at https://tv4-search.a2d.tv/docs/.
When a video stream is started, the Stream Microservice makes a GET request to the TV4 Search API with the video ID. The API's response is used to validate whether the video ID is valid. If the TV4 Search API returns an error or if the video ID is not found in the response, the video ID is considered invalid, and the stream cannot be started.
It's important to note that the availability and performance of the Stream Microservice can be impacted by the TV4 Search API. If the TV4 Search API is down or experiencing issues, the Stream Microservice may also experience problems when trying to start a video stream.

Codebase Structure
The Stream Microservice follows a typical Spring Boot project structure organized by feature. Here is a high-level overview:
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── microservice
│   │   │               ├── controller
│   │   │               │   └── StreamController.java
│   │   │               ├── dto
│   │   │               │   ├── StreamRequest.java
│   │   │               │   ├── StreamResponse.java
│   │   │               │   └── UserIdRequest.java
│   │   │               ├── entity
│   │   │               │   └── Stream.java
│   │   │               ├── repository
│   │   │               │   └── StreamRepository.java
│   │   │               └── service
│   │   │                   └── StreamService.java
│   │   └── resources
│   │       └── application.yml
│   └── test
├── .gitignore
├── docker-compose.yml
├── pom.xml
└── README.md

controller - This package contains the StreamController class which handles HTTP requests and responses.
dto - This package contains Data Transfer Object classes which are used to send and receive data from clients.
entity - This package contains the Stream entity class which maps to the Stream table in the database.
repository - This package contains the StreamRepository interface which is used to interact with the database.
service - This package contains the StreamService class which contains the business logic for starting, stopping, and getting video streams.
resources - This directory contains the application.properties file which holds configuration settings for the application.
test - This directory will contain all test files and resources.

Endpoints
POST /startstream
This endpoint starts a new video stream for a user.
Request Body
{
    "userId": "string",
    "videoId": "string"
}
Responses
200 OK: Stream started successfully.
400 BAD REQUEST: Invalid video ID.
403 FORBIDDEN: User has reached the maximum number of allowed running streams.

POST /stopstream
This endpoint stops a currently running video stream for a user.
Request Body
{
    "userId": "string",
    "videoId": "string"
}
Responses
200 OK: Stream stopped successfully.
400 BAD REQUEST: Invalid request.
404 NOT FOUND: Stream not found.


POST /runningstreams
This endpoint retrieves the currently running streams for a user.
Request Body
{
    "userId": "string"
}
Responses
200 OK: Returns a list of the currently running streams for the user.

DTOs
StreamRequest: Contains the userId and videoId.
UserIdRequest: Contains the userId.
StreamResponse: Contains a list of Stream objects.
Stream: Contains a streamId.

Entity
Stream: Represents a video stream. Contains id, userId, videoId, startTime, and endTime.

Repository
StreamRepository: Repository for performing operations on the Stream entity. Contains methods to find a running stream and to find all running streams for a user.

Service
StreamService: Contains the business logic for starting and stopping streams, validating a stream, and checking if a user has reached the maximum number of running streams.

Error Handling
Errors are handled using standard HTTP status codes. For instance, if a user tries to start a stream with an invalid video ID, a 400 BAD REQUEST status is returned. If a user tries to start more than the maximum allowed number of streams, a 403 FORBIDDEN status is returned. If a user tries to stop a non-existing stream, a 404 NOT FOUND status is returned.

Testing
The application includes unit tests to ensure the correctness of the business logic and the controllers.

