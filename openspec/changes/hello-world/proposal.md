# Proposal: Hello World API

## Summary
Add a simple "Hello, World!" REST API endpoint to the Spring Boot application.

## Motivation
Provide a basic health-check / greeting endpoint that returns "Hello, World!" as a JSON response.

## Scope
- Add a new `HelloWorldController` with a `GET /api/hello` endpoint.
- The endpoint returns `{"message": "Hello, World!"}`.

## Out of Scope
- No database interaction.
- No authentication/authorization.
- No frontend changes.