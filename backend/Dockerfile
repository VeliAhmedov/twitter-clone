# dockerfile builds and packages spring boot app into image

# from base image (temurin 25) with naming build, working directory in /app. this produces app.jar
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
# copy from (.mvn/) source from our project to destination (.mvn/) of path isndie image to /app/.mvn/
COPY .mvn/ .mvn/
# mvnw and pom is soruce to ./ (/app) destination copied
COPY mvnw pom.xml ./
#fix window line endings due to window-linux line ends
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B dependency:go-offline
#copy backend src dir to /app/src
COPY src/ src/
#builds jar, compile code and packages into jar under /app/target, also skip test during build for now (CI still does work)
RUN ./mvnw -B package -DskipTests

#this one create image that run app, why 2 seperate ones becuase of smaller image size
#why is why jre is used becuase runtime-only image avoid full build env therefore less on size
#ru
FROM eclipse-temurin:25-jre
WORKDIR /app
# && means exe cmd only if previous one succeseds
# Create a non-root user "app" (id 1001), create the uploads folder, give "app" ownership.
RUN useradd -r -u 1001 app \
    && mkdir -p /app/uploads \
    && chown app:app /app/uploads
# Note for above: why separate user, non-root limits the damage if the app is ever compromised.
# Copy the jar from the build stage, owned by "app".
COPY --from=build --chown=app:app /app/target/*.jar app.jar
# Run as non-root and start the app on port 8085.
USER app
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]