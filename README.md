# Powerimo MQ Libraries

A modular Java messaging framework built on top of RabbitMQ and Spring Boot. Powerimo MQ provides a structured and extensible foundation for building publish/subscribe messaging solutions using annotation-based handlers, robust message routing, and clean abstractions.

## 🧩 Modules

### `powerimo-mq-core`
Low-level message model, payload converters, message types, and processing strategies.

- Message format
- Exception resolution strategies
- Payload serialization/deserialization
- Message type resolution

### `powerimo-mq-spring`
Spring Boot integration layer for declarative configuration and lifecycle management.

- `@RabbitMessageListener` and `@RabbitMessageHandler` annotations
- Automatic bean discovery and registration
- Listener infrastructure with routing and error handling
- RabbitMQ configuration helpers

### `powerimo-mq-starter`
Auto-configuring Spring Boot starter for rapid application integration.

- Default queue/topic bindings
- JSON payload support
- Out-of-the-box retry and error strategies

## 🚀 Getting Started

### Maven

Add the dependency to your Spring Boot project:

```xml
<dependency>
  <groupId>org.powerimo</groupId>
  <artifactId>powerimo-mq-starter</artifactId>
  <version>1.0.1</version>
</dependency>
```

Examples are in the `examples` directory.