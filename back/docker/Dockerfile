# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:jdk21-graal-jammy as builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사
COPY ../gradlew ./gradlew
COPY ../gradle ./gradle
COPY ../build.gradle ./build.gradle
COPY ../settings.gradle ./settings.gradle

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

# 종속성 설치
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY ../src ./src

# 애플리케이션 빌드
RUN ./gradlew build --no-daemon -x test

# 두 번째 스테이지: 실행 스테이지
FROM ghcr.io/graalvm/jdk-community:21

# 작업 디렉토리 설정
WORKDIR /app

# 첫 번째 스테이지에서 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 서버 시간대 설정
ENV TZ=Asia/Seoul

# 실행할 JAR 파일 지정 및 서버 시간대 설정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "app.jar"]