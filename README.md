# Capibaras vs Labubus 2 - Urban Incident Tracker

"**Capibaras vs Labubus 2 - Urban Incident Tracker**" is a web application that helps users register, visualize, and manage urban incidents such as potholes, broken streetlights, obstacles on public roads, among others. Our goal is to develop a collaborative tool to improve the management of all these problems that affect citizens.

## Table of Contents

1. [Built With](#built-with)
2. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
3. [Configuration](#configuration)
4. [Usage](#usage)
5. [Swagger Documentation](#swagger-documentation)

## **Built With**

We are using **Kotlin** as the main programming language, leveraging its conciseness, safety, and interoperability with Java.

Our backend is powered by **Spring Boot**, a robust framework that simplifies the development of scalable and high-performance applications.

For data persistence, we use **MongoDB**, a NoSQL database that provides flexibility and scalability, making it ideal for handling large amounts of unstructured data.

Additionally, we integrate **Docker** to containerize our application, ensuring consistency across different environments.

## Getting started

### Prerequisites

- Java 17
- Kotlin 1.9.25
- Docker (for containerization)
- MongoDB Atlas (running remotely)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/ingenieria-software-7009-2025-2/capibarasvslabubus2-project-api.git
   ```

2. Navigate to the project directory

   ```bash
    cd capibarasvslabubus2-project-api/
   ```

## Configuration

Create a .env file in the root directory of the project (if it doesn't exist already), and add the following environment variable for the MongoDB connection (MongoDB URI )

```bash
 DB_URL=<connection-string>
```

Where <connection-string> is the connection string provided by MongoDB Atlas for connecting to the database.

## Usage

To run the application locally:

```bash
 mvn spring-boot:run
```
## version 1.0.0

## Swagger Documentation

After starting the application locally, you can view the Swagger UI documentation to explore and interact with the API. Simply open your web browser and navigate to: `http://localhost:8080/swagger-ui/index.html`

You can also access the OpenAPI specification in JSON or YAML format at:

- JSON: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

For further customization of the Swagger paths, you may modify the application properties accordingly.
