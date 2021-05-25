# Kalah API

## Run this project

### Prerequisites

- Java 11

### Compilation

```shell
./mvnw clean package -DskipTests
```

### Execution

```shell
java -jar target/kalah-0.0.1-SNAPSHOT.jar
```

## TODOs

1. Do not return entities in controllers! Use something like ModelMapper, and create DTOs

2. More consistent usage of `final`
