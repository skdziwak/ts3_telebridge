# Telebridge

Telebridge is a Java-based application that integrates Telegram with TeamSpeak, allowing users to manage TeamSpeak servers through Telegram commands. The application is built using Spring Boot and leverages various libraries for functionality such as JWT authentication, language translation, and command processing.

## Features

- **Telegram Command Processing**: Execute commands in Telegram to interact with TeamSpeak servers.
- **TeamSpeak Integration**: Manage TeamSpeak clients and bridges directly from Telegram.
- **Language Support**: Dynamic language translation and customization.
- **Role-Based Access Control**: Different permission levels for commands.
- **Token-Based Authentication**: Secure operations with JWT tokens.

## Getting Started

### Prerequisites

- Java 17
- Maven
- A Telegram Bot Token
- A TeamSpeak server

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/telebridge.git
   cd telebridge
   ```

2. Configure the application properties:
   - Update `src/main/resources/application.properties` with your database and TeamSpeak configurations.

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   java -jar target/telebridge-0.0.2-SNAPSHOT.jar
   ```

### Configuration

- **Telegram Bot Token**: Set in `TelebridgeConfiguration.java` using the `telegram.bot.token` property.
- **JWT Secret**: Set in `TokenService.java` using the `jwt.secret` property.

### Usage

- **Register a Bridge**: Use the `/registerBridge` command with the required parameters to connect a TeamSpeak server.
- **Manage Users**: Commands like `/addModerator`, `/alias`, and `/hide` allow managing user roles and visibility.
- **Translation**: Use `/setTranslation` to customize language keys for different contexts.

## Code Structure

- **Commands**: Implemented in the `commands.implementations` package, each command extends `AbstractCommand`.
- **Services**: Core logic is encapsulated in services like `TeamspeakBridgeService` and `LanguageService`.
- **Repositories**: Data access is managed through Spring Data JPA repositories.
- **Configuration**: Spring Boot configuration is handled in `TelebridgeConfiguration.java`.

## Design Patterns

- **Dependency Injection**: Utilized throughout the application for managing dependencies.
- **Command Pattern**: Commands are defined as classes with a common interface, allowing for flexible command processing.
- **Singleton Pattern**: Used for the `TelegramBot` instance to ensure a single bot instance is used throughout the application.
- **Scope Management**: Custom scope `CommandScope` is used for managing command execution contexts.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [TeamSpeak 3 API](https://www.teamspeak.com/en/integrations/)
