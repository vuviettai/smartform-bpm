
# Maven build
# FROM arm64v8/maven:3.8.7-openjdk-18-slim AS SMARTFORM_BUIDLER
FROM arm64v8/maven:3.8.1-openjdk-17-slim   AS SMARTFORM_BUIDLER
COPY pom.xml /smartform/
COPY docker/settings-docker.xml /usr/share/maven/ref/
WORKDIR /smartform/
# This allows Docker to cache most of the maven dependencies
RUN mvn -s /usr/share/maven/ref/settings-docker.xml dependency:resolve-plugins dependency:resolve dependency:go-offline -B
COPY ./src /smartform/src
RUN mvn -s /usr/share/maven/ref/settings-docker.xml package -P default -DskipTests

# Final custom slim java image (for apk command see 17-jdk-alpine-slim)
FROM arm64v8/openjdk:17-ea-16-jdk
# FROM arm64v8/openjdk:18-ea-35-jdk-slim
# set label for image
LABEL Name="smartform"

ENV JAVA_VERSION=17-ea+14
ENV JAVA_HOME=/opt/java/openjdk-17\
    PATH="/opt/java/openjdk-17/bin:$PATH"

EXPOSE 8080
RUN mkdir -p /deployments
# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=SMARTFORM_BUIDLER --chown=185 /smartform/target/quarkus-app/lib/ /deployments/lib/
COPY --from=SMARTFORM_BUIDLER --chown=185 /smartform/target/quarkus-app/*.jar /deployments/
COPY --from=SMARTFORM_BUIDLER --chown=185 /smartform/target/quarkus-app/app/ /deployments/app/
COPY --from=SMARTFORM_BUIDLER --chown=185 /smartform/target/quarkus-app/quarkus/ /deployments/quarkus/

RUN chmod a+rwx -R /deployments
WORKDIR /deployments
VOLUME /deployments
ENTRYPOINT ["java","-jar","/deployments/quarkus-run.jar"]
CMD [ "-Dquarkus.http.host=0.0.0.0", "-Djava.util.logging.manager=org.jboss.logmanager.LogManager" ]