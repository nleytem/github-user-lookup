# GitHub User Lookup

A Spring Boot application that retrieves and aggregates GitHub user profile information along with a list of their repositories using the public GitHub REST API.

## Features

- **User Lookup Endpoint:** Exposes a simple REST endpoint to fetch a GitHub user's details by their username.
- **Combined Data:** Aggregates data from both the GitHub User API (`/users/{username}`) and the User Repositories API (`/users/{username}/repos`) into a single, comprehensive response.
- **HTTP Caching:** Implements intelligent caching using Apache HTTP Client to reduce the number of direct calls to the GitHub API, handling rate limits more gracefully and improving response times.
- **OpenAPI / Swagger:** Includes automated API documentation accessible via a web UI.

## Technologies Used

- Java 25
- Spring Boot 4.0.5
    - Spring WebMVC
    - Spring Boot Actuator
- Apache HTTP Client 5 (with caching)
- Springdoc OpenAPI (Swagger UI)
- Maven

## Prerequisites

- JDK 25 or higher
- Maven 3.6+

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd GitHubUserLookup
```

### Build the Project

You can build the project and run tests using the Maven wrapper:

```bash
./mvnw clean install
```

### Run the Application

Run the Spring Boot application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

By default, the server will start on port `8000`. This can be configured in `src/main/resources/application.properties`.

## API Endpoints

### Get User Information

Retrieves aggregated information about a GitHub user.

**Request:**

```http
GET /users/{username}
```

**Example:**

```http
GET http://localhost:8000/users/octocat
```

**Successful Response (200 OK):**

```json
{
  "userName": "octocat",
  "displayName": "The Octocat",
  "avatar": "https://avatars.githubusercontent.com/u/583231?v=4",
  "geoLocation": "San Francisco",
  "email": null,
  "url": "https://api.github.com/users/octocat",
  "createdAt": "Tue, 25 Jan 2011 21:16:19 +0000",
  "repos": [
    {
      "url": "https://api.github.com/repos/octocat/boysenberry-repo-1",
      "name": "boysenberry-repo-1"
    },
    ...
  ]
}
```

## API Documentation

Once the application is running, you can access the Swagger UI for interactive API documentation:

- **Swagger UI:** `http://localhost:8000/swagger-ui.html`
- **OpenAPI Spec:** `http://localhost:8000/v3/api-docs`

## Configuration

Key application properties are found in `src/main/resources/application.properties`:

- `server.port`: The port on which the application runs (default: `8000`).
- `http-client.cache-max-entries`: Maximum number of entries in the HTTP client cache.
- `http-client.cache-max-object-size`: Maximum size (in bytes) of cached objects.

## Architecture & Error Handling

- **`UserController`**: Exposes the REST API endpoints.
- **`GitHubUserService`**: Contains business logic, orchestrating calls to fetch both user data and repositories, and formatting data like the `createdAt` date.
- **`GitHubUserRepository`**: Handles HTTP requests to the GitHub API, managing the Apache HTTP Client and caching strategies to minimize redundant network calls.

The application includes custom exceptions `UserNotFoundException` (returns a 404 status when a GitHub user does not exist) and `InternalServerError` for API communication issues.