FROM hsac/fitnesse-fixtures-test-jre8-chrome:latest
EXPOSE 9090
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "mvn", \
             "clean", \
             "compile", \
             "dependency:copy-dependencies", \
             "exec:exec"]