# 빌드 스테이지
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# 필요한 파일만 복사
COPY ../gradlew .
COPY ../gradle gradle
COPY ../build.gradle settings.gradle ./
COPY ../src src

# 실행 권한 부여
RUN chmod +x gradlew

# 프로젝트 빌드
RUN ./gradlew clean build -x test

# 런타임 스테이지
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 결과 복사 (jar 이름 명시)
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}"]