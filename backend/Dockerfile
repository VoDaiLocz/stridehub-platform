FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src src
RUN ./mvnw -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN addgroup --system stridehub && adduser --system --ingroup stridehub stridehub
COPY --from=build /workspace/target/*.jar /app/stridehub.jar

USER stridehub
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/stridehub.jar"]
