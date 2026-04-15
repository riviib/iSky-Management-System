# iSky Management System

A full-stack Java application that simulates a secure management system with role-based access control, user authentication, and persistent data storage.

## Overview

The iSky system manages user accounts across multiple roles while enforcing secure authentication and access control. It supports a complete user lifecycle, from initial system setup to role-based interaction within the platform.

## Features

* Role-based access control (RBAC) with multiple user roles
* Secure authentication with hashed passwords
* First-time system initialization with master account setup
* Forced password reset on first login
* Persistent data storage using H2 database and Spring Data JPA
* Dynamic JavaFX interface with role-specific views and menus

## How It Works

* On first launch, the system checks for an existing master account and prompts initialization if none exists
* User credentials are securely stored using hashed passwords in a persistent database
* After login, users are granted access based on their assigned role
* The interface dynamically updates to display features relevant to each role
* User data is managed using JPA/Hibernate for object-relational mapping

## Technologies Used

* Java
* JavaFX
* Spring Boot
* Spring Data JPA
* Hibernate
* H2 Database

## How to Run

1. Clone the repository
2. Open the project in any Java IDE
3. Run the application
4. On first launch, create a master account to initialize the system

## Notes

This project demonstrates concepts in authentication, database integration, and layered software design.
