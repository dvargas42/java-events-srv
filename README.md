# Events - Backend

![Coverage](.github/badges/jacoco.svg)
![GitHub language top](https://img.shields.io/github/languages/top/dvargas42/java-events-srv?color=%23177edf)
<a href="https://www.linkedin.com/in/daniel-santos-040983ab/" target="_blank" rel="noopener noreferrer">
![Made by](https://img.shields.io/badge/made%20by-daniel%20vargas-%23177edf)
</a>
![Repository size](https://img.shields.io/github/repo-size/dvargas42/java-events-srv?color=%23177edf)
![GitHub last commit](https://img.shields.io/github/last-commit/dvargas42/java-events-srv?color=%23177edf)
![Repository issues](https://img.shields.io/github/issues/dvargas42/java-events-srv?color=%23177edf)
![GitHub](https://img.shields.io/github/license/dvargas42/dvargas42?color=%23177edf)

# 

An application built in Java - Spring Boot, focused on user registration for registered events.

## 🚀 Technologies

Technologies that I used to develop this API.

- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Spring Boot 3.9](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/)
- [OpenAI](https://openai.com/)
- [Maven 3.4](https://maven.apache.org/guides/index.html)
- [Flyway 11](https://www.red-gate.com/products/flyway/community/)
- [MySQL 9.2](https://dev.mysql.com/doc/relnotes/mysql/9.2/en/)
- [Redis 7.2](https://redis.io/docs/stack/get-started/install/install-stack/)
- [Docker 24.](https://docs.docker.com/engine/install/ubuntu/)
- [Docker Compose 2](https://docs.docker.com/compose/install/)
- [Swagger](https://swagger.io/)
- [Prometheus 3.2](https://prometheus.io/docs/introduction/overview/)
- [Grafana 11.5](https://grafana.com/docs/grafana/latest/getting-started/what-is-grafana/)
- [Jacoco 0.8.12](https://www.eclemma.org/jacoco/)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 5.7](https://site.mockito.org/)

## 🌐 Endpoints

#### Return natural language search through AI

```
POST /ai-search
```

| Parameter          | Type      | Description                               |
|:-------------------|:----------|:------------------------------------------|
| `prompt`           | `String`  | **Required**. Natural language data query |
| `formatAsMarkdown` | `Boolean` | Format as markdown                        |

#### Returns all events

```
GET /event
```

#### Creates a new event

```
POST /event
```

| Parameter   | Type        | Description                    |
|:------------|:------------|:-------------------------------|
| `title`     | `String`    | **Required**. Event title      |
| `location`  | `String`    | **Required**. Event location   |
| `price`     | `Float`     | **Required**. Event price      |
| `startDate` | `LocalDate` | **Required**. Event start date |
| `endDate`   | `LocalDate` | **Required**. Event end date   |
| `startTime` | `LocalTime` | **Required**. Event start time |
| `endTime`   | `LocalTime` | **Required**. Event end time   |

#### Returns event by prettyName

```
GET /event/{prettyName}
```

| Parameter    | Type     | Description                              |
|:-------------|:---------|:-----------------------------------------|
| `prettyName` | `String` | **Required**. Event title without spaces |

#### Registers a user for a specific event

```
GET /subscription/{prettyName}
```

| Parameter    | Type     | Description                              |
|:-------------|:---------|:-----------------------------------------|
| `prettyName` | `String` | **Required**. Event title without spaces |

#### Registers a user for a specific event by referral

```
GET /subscription/{prettyName}/{userId}
```

| Parameter    | Type      | Description                              |
|:-------------|:----------|:-----------------------------------------|
| `prettyName` | `String`  | **Required**. Event title without spaces |
| `userId`     | `Integer` | **Required**. User ID                    |

#### Returns the general referral ranking of a specific event

```
GET /subscription/{prettyName}/ranking
```

| Parameter    | Type     | Description                              |
|:-------------|:---------|:-----------------------------------------|
| `prettyName` | `String` | **Required**. Event title without spaces |

#### Returns the general referral ranking of a specific event

```
GET /subscription/{prettyName}/ranking/{userId}
```

| Parameter    | Type      | Description                              |
|:-------------|:----------|:-----------------------------------------|
| `prettyName` | `String`  | **Required**. Event title without spaces |
| `userId`     | `Integer` | **Required**. User ID                    |

## 💻 Getting started

Clone the project

```bash
$ git clone https://github.com/dvargas42/java-events-srv
```

Enter the project directory

```bash
$ cd java-events-srv
```

Up all containers and start the server. 

Remember to insert the environment variables related to AI fetching and sending emails (OPENAI_API_KEY, SPRING_MAIL_USERNAME, and SPRING_MAIL_PASSWORD) into docker-compose.yml. 

If you do not have these environment variables, the project will run normally, however, these features will not work.

```bash
$ docker compose up --build -d
```

If you have issues with the build cache while uploading docker compose, run the command below. It will clear the cache and upload it again.

```bash
docker compose build --no-cache && docker compose up --force-recreate -d
```

To undo everything and delete the volumes, simply run the command below.

```bash
$ docker compose down -v
```

If you want to run the application without Docker, at least the infrastructure will have to be uploaded with Docker.

```bash
$ docker compose -f docker-compose-base.yml up -d
$ mvn springboot:run
```

To undo everything in Docker and delete the volumes, just run the command below.

```bash
docker compose -f docker-compose-base.yml down -v
```

## 🐳 How to access containers

Apos subir as aplicações pelo docker-compose

### 🐬 **SonarQube**

Here, you can see the quality of your code.

Login, username and password are: admin. Then it will ask you to change the password.

```
http://localhost:9000/
```

Within the application, configure the project with the project name, in this case: events and you will get the token **sqp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx**.

The command below must be executed in the application folder.

```bash
$ mvn clean verify sonar:sonar \
  -Dsonar.projectKey=events \
  -Dsonar.projectName='events' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

### 📊 **Grafana**

Here, you can see the healthy of your code.
To access grafana, use the url below.

```
http://localhost:3000/login
```

Login, username and password are: admin. Then it will ask you to change the password, but you can use the default password.

To configure Grafana to access Prometheus.

- Go to Connections in the side menu.
- Search for Prometheus and click on the item found.
- Then, click on the 'Add new data source' button;
- On the next screen, in the Prometheus serverURL field, type http://prometheus:9090.
- Scroll down to the end of the page and click on Save & test.

That's it, the basic configuration is ready.

For custom dashboards, use the url below.

```
https://grafana.com/grafana/dashboards/?search=spring
```

Into Grafana, 
- Go to Dashboards
- Click on New Button 
- Select Import 
- In field type 11378 (for example) and click on Load button.
- In the next screen, click in the Prometheus field 
- Select the Prometheus data source you created
- And finally click on Import.

### 🌐 **Swagger Openapi**

All ready and configured.

```
http://localhost:8080/swagger-ui/index.html
```

### 🎲 **Redis**

To view the data that has been saved in the cache

```bash
$ docker exec -it events-redis bash
$ redis-cli
$ AUTH redis
$ KEYS *
$ get "KEY"
```

## 🤝 Contributing

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

## 🧰 FAQs

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
