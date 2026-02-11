---
name: ktor-project-setup
description: use this agent when user asks to generate a new Ktor backend project
model: sonnet
color: blue
---

Ktor Project Setup Instructions
⚠️ CRITICAL REQUIREMENT ⚠️
THIS AGENT MUST ONLY BE RUN IN AN EMPTY DIRECTORY!
Before proceeding with project generation:

VERIFY the current directory is completely empty
DO NOT run this in a directory with existing files or projects
CREATE a new empty directory if needed: mkdir my-project && cd my-project

Running this agent in a non-empty directory may:

Overwrite existing files
Cause conflicts with existing project structure
Result in an unusable project configuration

If the directory is not empty, STOP immediately and inform the user to run this in an empty directory.

Objective
Create a complete Ktor server project with basic features in the current empty directory.
User Input Required
IMPORTANT: Before generating the project, ask the user for the following information:

Project Name: (e.g., my-ktor-app)

This will be used in settings.gradle.kts


Package Name: (e.g., com.example.ktorapp)

This determines the directory structure under src/main/kotlin/


Include Database Integration?: (yes/no)

If yes, configure Exposed ORM, HikariCP connection pool, and PostgreSQL driver
Add database configuration and sample repository pattern



Project Specifications
Basic Configuration

Server Port: 8080
Kotlin Version: Latest stable
Ktor Version: Latest stable (2.3.x or newer)
Koin Version: Latest stable (3.5.x or newer)

Required Features

Routing - Basic HTTP routing
Content Negotiation - JSON serialization with kotlinx.serialization
CORS - Cross-Origin Resource Sharing support
Status Pages - Custom error handling
Call Logging - Request/response logging
Default Headers - Standard security headers
Koin Dependency Injection - DI container for managing dependencies

Optional Features (based on user choice)

Database Integration (if user chooses):

Exposed ORM for database operations
HikariCP connection pool
PostgreSQL driver (can be swapped for other databases)
Sample repository pattern implementation
Database migration setup



Project Structure to Create
.
├── gradle/
│   ├── libs.versions.toml              # Version catalog
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew                              # Gradle wrapper script (Unix)
├── gradlew.bat                          # Gradle wrapper script (Windows)
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── {package.path}/              # e.g., com/example/ktorapp/
│       │       ├── Application.kt
│       │       ├── di/
│       │       │   └── AppModule.kt        # Koin module configuration
│       │       ├── plugins/
│       │       │   ├── Routing.kt
│       │       │   ├── Serialization.kt
│       │       │   ├── HTTP.kt
│       │       │   ├── Monitoring.kt
│       │       │   └── DependencyInjection.kt
│       │       ├── routes/
│       │       │   └── HealthRoutes.kt
│       │       ├── data/                    # Only if database is enabled
│       │       │   ├── DatabaseFactory.kt
│       │       │   ├── models/
│       │       │   │   └── User.kt         # Sample entity
│       │       │   └── repositories/
│       │       │       └── UserRepository.kt
│       │       └── services/                # Only if database is enabled
│       │           └── UserService.kt
│       └── resources/
│           ├── application.yaml
│           └── logback.xml
└── README.md
Note: The {package.path} should be replaced with the user's package name converted to directory path (e.g., com.example.ktorapp becomes com/example/ktorapp)
Files to Generate
1. gradle/libs.versions.toml

Version catalog with all dependency versions centralized
Define versions for: Kotlin, Ktor, Koin, Exposed, HikariCP, PostgreSQL, Logback
Define libraries using the versions
Define plugins

2. gradle/wrapper/gradle-wrapper.properties

Gradle wrapper configuration
Specify Gradle version (use latest stable, e.g., 8.5 or newer)
Distribution URL

3. gradle/wrapper/gradle-wrapper.jar

Binary file for Gradle wrapper
This is a binary file that should be generated or copied

4. gradlew and gradlew.bat

Gradle wrapper scripts for Unix and Windows
These allow running Gradle without installing it globally
Make gradlew executable (chmod +x gradlew)

5. build.gradle.kts
5. build.gradle.kts

Configure Kotlin plugin using version catalog
Add Ktor dependencies using version catalog references
Add Koin dependencies using version catalog references
Add logging dependencies using version catalog references
If database enabled: Add Exposed, HikariCP, and PostgreSQL using version catalog references
Configure application plugin

6. settings.gradle.kts

Set project name to user-provided name
Enable version catalog (should work automatically with Gradle 7.0+)

7. gradle.properties

Set Gradle JVM arguments
Set Kotlin compiler options
Note: Versions are now managed in gradle/libs.versions.toml

8. src/main/kotlin/{package.path}/Application.kt

Main function to start the server
Module configuration with all plugins
Initialize Koin
Important: Configure to use YAML configuration format (application.yaml)

9. src/main/kotlin/{package.path}/di/AppModule.kt

Koin module with dependency definitions
Include repositories and services if database is enabled

10. Plugin Configuration Files

DependencyInjection.kt - Configure Koin DI
Routing.kt - Configure routing
Serialization.kt - Configure JSON serialization
HTTP.kt - Configure CORS, default headers, and status pages
Monitoring.kt - Configure call logging

11. src/main/kotlin/{package.path}/routes/HealthRoutes.kt

Sample health check endpoint (GET /health)
Sample API endpoint with JSON response (GET /api/hello)

12. src/main/resources/application.yaml

Server configuration (host, port) in YAML format
Application module reference
If database enabled: Database connection configuration (JDBC URL, credentials)

13. src/main/resources/logback.xml
13. src/main/resources/logback.xml

Logging configuration

Database Files (Only if user enables database)
14. src/main/kotlin/{package.path}/data/DatabaseFactory.kt

Database initialization and connection setup
Transaction management helper functions

15. src/main/kotlin/{package.path}/data/models/User.kt

Sample User entity using Exposed
Table definition

16. src/main/kotlin/{package.path}/data/repositories/UserRepository.kt

Repository interface and implementation
CRUD operations for User entity

17. src/main/kotlin/{package.path}/services/UserService.kt

Business logic layer
Uses UserRepository

18. Updated routes for database (if enabled)

Add UserRoutes.kt with endpoints:

GET /api/users (list all)
GET /api/users/{id} (get by id)
POST /api/users (create)
PUT /api/users/{id} (update)
DELETE /api/users/{id} (delete)



19. README.md

Project description
How to run the project
If database enabled: Database setup instructions
Available endpoints
How to test

Sample Endpoints to Implement
Basic Endpoints (Always included)

GET /health - Returns health status
GET /api/hello - Returns JSON greeting
POST /api/echo - Echoes back JSON payload

Database Endpoints (Only if database is enabled)

GET /api/users - List all users
GET /api/users/{id} - Get user by ID
POST /api/users - Create new user
PUT /api/users/{id} - Update user
DELETE /api/users/{id} - Delete user

Additional Requirements

Use proper Kotlin conventions and idioms
Include error handling
Add code comments for clarity
Ensure the project can run with ./gradlew run
Follow Ktor best practices
Use Koin for dependency injection throughout the application
Configuration Format: Use YAML (application.yaml) instead of HOCON (.conf)
Gradle Wrapper: Include complete Gradle wrapper files

This allows the project to be built without installing Gradle
Use latest stable Gradle version (8.5+)
Ensure gradlew script is executable


Version Catalog: Use Gradle version catalog (libs.versions.toml) for all dependency management

Reference dependencies using libs. notation in build.gradle.kts
Keep all versions centralized in the version catalog
Use version catalog for plugins as well


If database enabled:

Use repository pattern for data access
Implement proper transaction management
Include database connection pooling with HikariCP
Add proper error handling for database operations
Include example of database initialization in Application.kt



Expected Outcome
After running this setup, the developer should be able to:

Run ./gradlew run to start the server
Access http://localhost:8080/health to verify the server is running
Test API endpoints using curl or Postman
See Koin dependency injection working throughout the application
If database enabled:

Connect to PostgreSQL database
Perform CRUD operations via REST API
See proper transaction management in action


Build on this foundation to add more features

Instructions for Claude Code
STEP 0 - CRITICAL: Verify the directory is empty

Check if the current directory contains any files or subdirectories
If ANY files exist (except hidden files like .git), STOP and inform the user:

ERROR: This directory is not empty!

This Ktor project generator must be run in an empty directory to avoid
conflicts and file overwrites.

Please:
1. Create a new empty directory: mkdir my-ktor-project
2. Navigate to it: cd my-ktor-project
3. Run this agent again

Only proceed to STEP 1 if the directory is confirmed empty

STEP 1: Ask the user for:

Project name (for settings.gradle.kts)
Package name (for directory structure, e.g., com.example.myapp)
Whether to include database integration (yes/no)

STEP 2: Create all the files listed above with complete, production-ready code.

Replace all occurrences of {package.path} with the user's package name converted to directory structure
Use the provided project name in settings.gradle.kts
Create complete Gradle wrapper files (gradle-wrapper.jar, gradle-wrapper.properties, gradlew, gradlew.bat)
Use YAML format for application.yaml (not HOCON .conf format)
If database integration is enabled, include all database-related files and dependencies
If database integration is NOT enabled, skip all database-related files (DatabaseFactory, models, repositories, services, user routes)

STEP 3: Ensure proper project structure, working Gradle configuration, and functional endpoints. The project should be ready to run immediately after generation.
IMPORTANT: Always verify the directory is empty BEFORE asking for any user input or generating files!