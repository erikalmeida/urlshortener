# URL Shortener App

## Overview

Java-based web application built with Spring Boot that allows users to shorten long URLs into shorter, more shareable links. It provides features such as URL shortening, redirection, and management.

## Features

- **URL Shortening**: Convert long URLs into short, easy-to-share links.
- **URL Redirection**: Redirect short links to their original URLs.
- **URL Management**: Enable, disable URLs.
- **URL Stadistics**: Get URLs click count data.  

## Technologies Used

- **Java**: chosen as the programming language for its robustness, platform independence, and extensive ecosystem. Java's object-oriented approach and strong community support make it an excellent choice for building scalable and maintainable applications.
- **Spring Boot**: selected as the application framework due to its simplicity, convention-over-configuration approach, and extensive features. It provides out-of-the-box support for building production-ready applications with minimal setup and configuration. Spring Boot's dependency management, auto-configuration, and embedded HTTP server streamline the development process and promote best practices.
- **MongoDB**: chosen as the database solution for its flexibility, scalability, and ease of use. As a NoSQL document-oriented database, MongoDB's schema-less architecture allows for agile development and seamless integration with Java applications. It offers high performance, horizontal scalability, and support for complex data structures, making it well-suited for storing and managing dynamic data such as URLs and their metadata.
- **Micrometer**: integrated into the application for monitoring and metrics collection. It provides a unified API for instrumenting application code and collecting metrics across different monitoring systems. Micrometer's support for various monitoring backends, including Prometheus, ensures compatibility with existing monitoring infrastructure and enables observability into the application's performance and behavior.

## Architecture

The application follows a client-server architecture:
- **Client**: Interacts with the application through HTTP requests. Clients can include web browsers, mobile apps, or any other application capable of making HTTP requests.
- **Server**: Implemented using Spring Boot, handles incoming HTTP requests, performs URL shortening and redirection, and manages data stored in MongoDB.
- **Database**: MongoDB is used as the database backend for storing shortened URLs and their metadata. It provides a flexible schema and scalable architecture, making it suitable for storing and managing large volumes of data.

Micrometer is integrated into the application to collect various metrics and monitor its performance. Metrics collected by Micrometer can include redirection counts, and response times. These metrics can be exported to monitoring systems for analysis and visualization to gain insights into the application's behavior and performance.

## Setup

1. **Clone the Repository**: Clone this repository to your local machine.
2. **Configure MongoDB**: Ensure you have a MongoDB instance available. Update connection details in the application configuration.
3. **Set Up Environment Variable**: Set an environment variable named `BASE_URL_ML` with the base URL of your application's domain. This variable will be used to construct functional links in the application. 
For example: 
```bash 
export BASE_URL_ML=https://example.com
```
You can set this environment variable in your shell configuration file (~/.bashrc for Bash or ~/.profile for Bourne Shell) to make it persistent across sessions.
4. **Compile and Run**: Use Maven to compile and run the application. 
```bash
mvn spring-boot:run
```

## Usage

- **Shorten a URL**: Make a POST request to `/` with the long URL in the request body. 
Body example:
```json
{
  "originalUrl": "https://example.com/very/long/url/that/needs/to/be/shortened"
}
```
- **Redirect a URL**: Make a GET request to `/{shortCode}`, or the url provided as a response on creation.
- **Manage URLs**: Enable, disable through a PUT request to `/{shortCode}`
Body Example:
```json
{
  "validUrl": false
}
```
- **Get Stadistics**: Make a GET request to `/{shortCode}/data`

## License

This project is licensed under the MIT License.

