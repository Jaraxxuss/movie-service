# start up

./gradlew bootBuildImage --imageName=com.setplex/movieservice

docker compose --env-file ./config/.env.docker up

# Project Overview
Your task is to build an assets manager for a movies database. The system should allow the storage and delivery of assets for movies such as posters, screenshots, etc. The system should be accessible via a REST API built using Spring Boot.
## Requirements
- Implement the API using Java 17, Spring Boot, and Hibernate.
- The API should allow the following operations:Upload an asset file (e.g. a movie poster) for a specific movie.
- Retrieve an asset file for a specific movie.
- Delete an asset file for a specific movie.
- Each asset file should be associated with a movie in the database.
- Implement appropriate error handling and validation.
- Write unit tests for the API.
- Use best practices for software development, including proper documentation, code commenting, and appropriate design patterns.
## Technical Specifications
- Use Spring Boot to create the REST API.
- Use Hibernate to interact with the database.
- Use a MySQL database to store the movie and asset data.
- Use Gradle as the build tool.
- Use JUnit for unit testing.
Submission
- Please submit your code via a GitHub repository link.
- Include a README file with instructions on how to build, run, and test the API.
- Provide any additional information that you think is relevant.
