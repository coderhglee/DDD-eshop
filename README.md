# DDD-eshop

# Description

Java, Spring Boot, JPA, H2

### build

```shell
./gradlew clean build
```

### Run Test

```shell
./gradlew clean test --info
```

### Run

```shell
java -jar build/libs/homework-0.0.1-SNAPSHOT.jar
```

# Architecture & Design Pattern & Test

-   The architecture follows the principles of `Clean Architecture`.
    -   Application layer
        -   Command
        -   UseCase(adapter)
        -   Service
    -   Domain layer
        -   Entity, VO
        -   Event
        -   Repository(interface)
    -   Infrastructure layer
        -   JPA Repository(persistence)

The main reason for applying Clean Architecture is to prevent changes in low-level modules (such as the UI) from propagating to high-level modules (core business logic). The Adapter Pattern reverses the direction of control between each module and removes dependencies, allowing each implementation to be injected through an external framework. This achieves low coupling and high cohesion, making it easier to write testable code.

## Domain Design Pattern

Each Aggregate has been separated into three domains: Product, Cart, and Order. The Product domain is responsible for managing products and has the responsibility for quantity management.

The Cart domain is responsible for managing the products and quantities to be ordered before the actual ordering process. It plays a role in providing the order domain with the products and quantities the user wishes to order.

The Order domain is responsible for validating and creating orders based on the CartLineItems stored in the cart. It also handles the payment request to the payment service.

The sub-entities belonging to each Aggregate are accessible through the Aggregate Root, and domain rules are defined as much as possible within the domain entities to facilitate the management of core business requirements.
