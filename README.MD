# Bake N' Go

## Overview

Bake N' Go is a comprehensive order management and fulfillment platform designed for bakery and pastry businesses. The platform streamlines customer engagement, inventory management, and order logistics through an integrated web-based application.

## Features

- **Customer Management** - Secure user authentication and account management for customers with role-based access control
- **Product Catalog** - Dynamic product listing, inventory tracking, and stock management for all bakery items
- **Order Processing** - End-to-end order management from placement to fulfillment, including order status tracking and item-level visibility
- **Pickup Scheduling** - Intelligent pickup slot management with capacity control and availability scheduling
- **Business Analytics** - Metrics and logging for business intelligence, operational monitoring, and sales insights
- **Admin Dashboard** - Comprehensive management interface for business operations and real-time monitoring

## Technology Stack

- **Backend**: Java with Gradle build system
- **Framework**: Javalin (lightweight web framework)
- **Database**: Queryon based on SQLite (embedded database)
- **Frontend**: Responsive HTML5, CSS3, and vanilla JavaScript
- **Logging**: SLF4J with Logback

## Architecture

The application is structured with a clear separation between frontend and backend:
- **Server**: RESTful endpoints for authentication, orders, products, and pickup slots
- **Client**: Single-page application with responsive dashboard and user interface
- **Data**: Database layer with table operations for products, orders, customers, and analytics

## Target Market

Independent bakeries, pastry shops, and artisanal food businesses seeking to modernize their order management and customer experience.

## How to test the application:
You have two options to test the application:
1. **Using the jar or operating system executable**:<br>
    You can run the application using the provided jar file or operating system executable. This is the recommended method for testing the application as it allows you to experience the full functionality of the platform without needing to set up it on a distant server via Docker.
2. **Using Docker**:<br>
    If you prefer to test or install the application on a distant server, you can use Docker.
    This method requires you to have Docker installed on your machine and some familiarity with Docker commands. You can find the necessary Docker configuration files in the project repository to help you set up the application in a containerized environment.

---

Note that this project can be further enhanced with additional features such as payment processing, customer reviews, and marketing tools to provide a more comprehensive solution depending on the specific needs of the target market.<br><br>
<b>For customized version or additional features, please contact me via my [website](https://niwer.dev).