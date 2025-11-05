# Stage 1: Build with Maven
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app

# pom.xml 먼저 복사 (캐시 활용)
COPY pom.xml .
# src 폴더 복사
COPY src ./src

# 패키지 빌드 (테스트는 건너뜀)
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Stage 1에서 빌드한 jar 파일 복사
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]