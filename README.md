# ♕ CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-ISPUgFAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
