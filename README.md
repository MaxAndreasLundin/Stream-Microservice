# Stream Microservice Documentation

### Overview

The Stream Microservice is a Spring Boot application that serves to limit the number of simultaneous video streams that
a user can consume. The service exposes REST APIs to start and stop video streams and to fetch the currently running
streams for a user. It interacts with the https://tv4-search.a2d.tv/ API to validate video IDs.

### Setup Instructions

This section guides you on how to set up the Stream Microservice on your local machine.

#### Prerequisites

* Ensure that you have [Docker](https://docs.docker.com/get-docker/) installed on your machine.
* Ensure that you have [Docker Compose](https://docs.docker.com/compose/install/) installed on your machine.
* Ensure that you have [Maven](https://maven.apache.org/install.html) installed on your machine.

#### Steps

1. Clone the repository to your local machine.
2. Navigate to the root directory of the project.
3. Build the JAR file for your application using the following command: `mvn clean package`

This command builds a JAR file of your application using Maven. The clean command removes any previous builds, and the
package command compiles your code and packages it into a JAR file.

4. Build the Docker image for your application using the following command: `docker build -t tv4-image .`
   This command builds a Docker image named "tv4-image" using the Dockerfile in your current directory.
5. Start the database and the microservice using Docker Compose with the following command: `docker compose up`
   This command starts the services defined in your docker-compose.yml.
6. To stop the services, you can use the following command: `docker compose down`
   This command stops and removes the containers defined in your docker-compose.yml.

### Service Dependencies

The Stream Microservice relies on the following external service:

#### TV4 Search API

The Stream Microservice validates video IDs by interacting with the TV4 Search API. The base URL for this API
is https://tv4-search.a2d.tv/. Documentation for the TV4 Search API can be found at https://tv4-search.a2d.tv/docs/.

When a video stream is started, the Stream Microservice makes a GET request to the TV4 Search API at the endpoint
**`/assets/{videoId}`** where `{videoId}` is the ID of the video to be streamed. The API's response is used to validate whether
the video ID is valid. If the TV4 Search API returns a 404 Not Found status, the video ID is considered invalid, and the
stream cannot be started. However, in case of other server errors (5xx status codes), the video ID validation is
bypassed to allow the user to start the stream.

It's important to note that the availability and performance of the Stream Microservice can be impacted by the TV4
Search API. If the TV4 Search API is down or experiencing issues, the Stream Microservice may also experience problems
when trying to start a video stream, unless the API error is a server error, in which case the stream start operation
would proceed as if the video ID is valid.

### Configuration

The Stream Microservice relies on the following environment variables, which can be set in the application.yml file:

* DB_HOST: The host of the database (default is localhost).
* DB_USERNAME: The username for the database (default is postgres).
* DB_PASSWORD: The password for the database (default is 123).
  
Note: For testing, the application-test.yml file is used, which sets up an in-memory H2 database.

### Running the Service

After following the setup instructions and configuring the service, you can interact with it via the following REST
endpoints:

* `POST /v1/stream`: Start a new video stream. Requires userId and videoId parameters. If the user has already started the
  maximum number of streams (2), a 403 Forbidden response is returned. If the videoId is invalid, a 400 Bad Request
  response is returned.
* `DELETE /v1/stream`: Stop an existing video stream. Requires userId and videoId parameters. If the specified stream does
  not exist, a 404 Not Found response is returned.
* `GET /v1/stream`: Get all currently running streams for a user. Requires the userId parameter.

### Service Details

The StreamService is the core of the application. It starts, stops, and retrieves video streams for users. It ensures
that a user cannot have more than two running streams at a time and that video IDs are valid. It uses the
StreamRepository to interact with the database, and it periodically purges any streams that have been inactive for over
an hour.

The **StreamRepository** is a Spring Data JPA repository that provides methods for finding, saving, and deleting streams in
the database. It also provides a method for deleting all streams that have been inactive for over an hour.

The **StreamController** is the REST controller for the application. It maps the above endpoints to their corresponding
service methods and handles any errors that occur.

The **RestTemplateConfig** is a configuration class that provides a RestTemplate bean, which is used by StreamService to
make HTTP requests.

The **Stream** entity represents a video stream. It includes the ID of the stream, the ID of the user, the ID of the video,
and the time the stream was started.

### Testing

The StreamServiceTest and StreamControllerTest classes provide unit tests for the StreamService and StreamController
classes, respectively. To run the tests, use the following Maven command:
`mvn test`

### Deployment

The MicroserviceApplication class includes a main method that runs the application. It also enables scheduling for the
@Scheduled annotation in StreamService.

The application is packaged into a Docker image and runs as a Docker container along with a database container. The
Dockerfile and docker-compose.yml file in the project root directory provide the configuration for this.

### API Documentation

This microservice uses Swagger UI for API documentation, which provides an interactive interface for exploring the API's
endpoints. Once the microservice is running, the Swagger UI page is available at:
http://localhost:8080/swagger-ui/index.html#/
You can explore the various API endpoints, their parameters, responses, and test them directly from this page.

In addition, the OpenAPI description of the API, which provides a machine-readable specification of the API's endpoints,
parameters, responses, etc., is available in JSON format at:
http://localhost:8080/v3/api-docs

