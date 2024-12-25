FROM amazoncorretto:21-alpine
LABEL authors="dskroba"
WORKDIR /app
COPY ./build/libs/ExpensesBot-1.0-SNAPSHOT.jar /app
COPY ./devops/logs/prod.log4j2.xml /app
RUN mkdir -p /logs

ENTRYPOINT ["java", "-Dinstance.conf=/config/expenses-bot.properties", "-Dlog4j.configurationFile=prod.log4j2.xml", "-jar", "ExpensesBot-1.0-SNAPSHOT.jar"]