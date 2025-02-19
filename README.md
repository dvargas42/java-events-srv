mvn flyway:clean -Dflyway.cleanDisabled=false \
  -Dflyway.url=jdbc:mysql://localhost:3306/events-db \
  -Dflyway.user=root \
  -Dflyway.password=mysql

mvn flyway:migrate -Dflyway.url=jdbc:mysql://localhost:3306/events-db -Dflyway.user=root -Dflyway.password=mysql

mvn flyway:repair -Dflyway.url=jdbc:mysql://localhost:3306/events-db -Dflyway.user=root -Dflyway.password=mysql

