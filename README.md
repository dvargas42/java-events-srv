<h1 align="center">
  <img alt="Logo" src="https://res.cloudinary.com/dvargas42/image/upload/v1739999864/Eventsl-logo_zuuufx.png" width="400px">
</h1>

# Events - Backend

An application built in Java - Spring Boot, focused on user registration for registered events.

## 🚀 Technologies

Technologies that I used to develop this API.

- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven 3.4](https://maven.apache.org/guides/index.html)
- [Spring Boot 3.9](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/)
- [Flyway 11](https://www.red-gate.com/products/flyway/community/)
- [MySQL 9.2](https://dev.mysql.com/doc/relnotes/mysql/9.2/en/)

## 🖨️ API Documentation

#### Returns all events

```http
  GET /event
```

#### Creates a new event

```http
  POST /event
```

| Parameter   | Type       | Description                           |
| :---------- | :--------- | :---------------------------------- |
| `title` | `String` | **Required**. Event title |
| `location` | `String` | **Required**. Event location |
| `price` | `Float` | **Required**. Event price |
| `startDate` | `LocalDate` | **Required**. Event start date |
| `endDate` | `LocalDate` | **Required**. Event end date |
| `startTime` | `LocalTime` | **Required**. Event start time |
| `endTime` | `LocalTime` | **Required**. Event end time |

#### Returns event by prettyName

```http
  GET /event/{prettyName}
```

| Parameter   | Type       | Description                           |
| :---------- | :--------- | :---------------------------------- |
| `prettyName` | `String` | **Required**. Event title without spaces |

#### Registers a user for a specific event

```http
  GET /subscription/{prettyName}
```

| Parameter   | Type       | Description                           |
| :---------- | :--------- | :---------------------------------- |
| `prettyName` | `String` | **Required**. Event title without spaces |

#### Registers a user for a specific event by referral

```http
  GET /subscription/{prettyName}/{userId}
```

| Parameter   | Type       | Description                           |
| :---------- | :--------- | :---------------------------------- |
| `prettyName` | `String` | **Required**. Event title without spaces |
| `userId` | `Integer` | **Required**. User ID |

#### Returns the general referral ranking of a specific event

```http
  GET /subscription/{prettyName}/ranking
```

| Parameter   | Type       | Description                           |
| :---------- | :--------- | :---------------------------------- |
| `prettyName` | `String` | **Required**. Event title without spaces |

#### Returns the general referral ranking of a specific event

```http
  GET /subscription/{prettyName}/ranking/{userId}
```

| Parameter   | Type       | Description                           |
| :---------- | :--------- | :---------------------------------- |
| `prettyName` | `String` | **Required**. Event title without spaces |
| `userId` | `Integer` | **Required**. User ID |

## 💻 Getting started

Clone the project

```bash
  git clone https://github.com/dvargas42/java-events-srv
```

Enter the project directory

```bash
  cd java-events-srv
```

Start the server

```bash
  mvn springboot:run
```

## 🤔 How to contribute

**Make a fork of this repository**

```bash
# Fork using GitHub official command line
# If you don't have the GitHub CLI, use the web site to do that.

$ gh repo fork dvargas42/java-events-srv
```

**Follow the steps below**

```bash
# Clone your fork
$ git clone your-fork-url && cd java-events-srv

# Create a branch with your feature
$ git checkout -b my-feature

# Make the commit with your changes
$ git commit -m 'feat: My new feature'

# Send the code to your remote branch
$ git push origin my-feature
```

After your pull request is merged, you can delete your branch

## ✅ Improvements

- [ ] Create DTOs to validate input data (I recommend trying to create custom annotations)

- [ ] Implement some type of cache for the general ranking endpoint

- [ ] Create unit and integration tests (I prefer the latter, as it allows me to scan the entire code more easily)

- [ ] Integrate the application with SonarQube (here you will be able to see reports on code smell, test coverage, etc.)

- [ ] Create documentation with Swagger (dynamic documentation for your application)

- [ ] Create a Dockerfile for building (research how to make the application images as small as possible)

- [ ] Create a docker-compose file that runs your built app and the database

- [x] Integrate some ORM tool, like Flyway (here you will write the SQLs for your application and have the source of truth for your database within your application)

- [ ] Deploy it to an external repository like GITHUB and configure CI/CD to deploy to the cloud, I recommend AWS

- [ ] Finally, to undo everything in the cloud and avoid being charged, take a look at Terraform, which allows you to control the cloud from your terminal.

## 🧰 FAQ

#### If you have issues creating migrations
```bash
mvn flyway:repair -Dflyway.url=jdbc:mysql://localhost:3306/events-db -Dflyway.user=root -Dflyway.password=mysql
```

#### If you want to revert migrations
```bash
mvn flyway:clean -Dflyway.cleanDisabled=false -Dflyway.url=jdbc:mysql://localhost:3306/events-db -Dflyway.user=root -Dflyway.password=mysql
```

#### If you want to create migrations manually
```bash
mvn flyway:migrate -Dflyway.url=jdbc:mysql://localhost:3306/events-db -Dflyway.user=root -Dflyway.password=mysql
```

## 📝 License

This project is licensed under the MIT License - see the [MIT](https://choosealicense.com/licenses/mit/) file for details.

## 💇🏼Author

Made with 💜 &nbsp;by Daniel Vargas 👋 &nbsp;[See my LinkedIn](https://www.linkedin.com/in/daniel-santos-040983ab/)
