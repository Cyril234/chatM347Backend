# ── Stage 1: Build ─────────────────────────────────────────────────────────────
# Maven mit Java 21 (passe die Version an euer pom.xml <java.version> an)
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Erst nur pom.xml kopieren -> Docker Layer Cache nutzt das für Dependencies,
# d.h. bei reinen Code-Änderungen müssen Dependencies nicht neu geladen werden
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Jetzt den restlichen Quellcode kopieren und bauen
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
# JRE statt JDK reicht zur Laufzeit -> deutlich kleineres Image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Eigenen, nicht-root User anlegen (Sicherheit)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Das gebaute JAR aus Stage 1 übernehmen
COPY --from=build /app/target/*.jar app.jar

EXPOSE 3001

ENTRYPOINT ["java", "-jar", "app.jar"]
