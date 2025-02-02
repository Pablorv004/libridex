# Libridex

Libridex is an online management service for libraries. It provides a comprehensive solution for managing library resources, users, and transactions efficiently.
Fully deployed at [Libridex Live](https://libridex.live/)
## Features

- **User Management**: Manage library users with ease.
- **Book Management**: Keep track of all books and their statuses.
- **Transaction Management**: Handle book borrow and return transactions.
- **Security**: Secure access with Spring Security.
- **Responsive UI**: User-friendly interface built with Thymeleaf.

## Dependencies

Libridex uses the following dependencies:

- **Spring Web**: For building web applications.
- **Spring Security**: For securing the application.
- **Spring Boot DevTools**: For development tools and live reload.
- **Thymeleaf**: For server-side rendering of web pages.
- **MySQL Driver**: For connecting to MySQL databases.
- **JPA & Hibernate**: For ORM and database interactions.
- **Lombok**: For reducing boilerplate code.

## Installation

To install and run Libridex locally, follow these steps:

1. **Clone the repository**:
    ```sh
    git clone https://github.com/pablorv28/libridex.git
    cd libridex
    ```

2. **Download application.properties from trello dashboard**

3. **Run the application**:
    ```sh
    ./mvnw spring-boot:run
    ```

4. **Access the application**:
    Open your browser and go to `http://localhost:8080`.

## Authors

- **Pablorv28**
- **RafBel94**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
