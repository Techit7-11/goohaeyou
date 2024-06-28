# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:jdk21-graal-jammy as builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사
COPY back/gradlew ./back/gradlew
COPY back/gradlew.bat ./back/gradlew.bat
COPY back/gradle ./back/gradle
COPY back/build.gradle ./back/build.gradle
COPY settings.gradle .

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./back/gradlew

# 종속성 설치
RUN ./back/gradlew -p back dependencies --no-daemon

# 소스 코드 복사
COPY back/src /back/src

# 애플리케이션 빌드
RUN ./back/gradlew -p back build --no-daemon -x test

# 두 번째 스테이지: 실행 스테이지
FROM ghcr.io/graalvm/jdk-community:21

# 작업 디렉토리 설정
WORKDIR /app

# 첫 번째 스테이지에서 빌드된 JAR 파일 복사
COPY --from=builder /app/back/build/libs/*.jar app.jar

# 서버 시간대를 아시아/서울로 설정
ENV TZ=Asia/Seoul

# 실행할 JAR 파일 지정 및 서버 시간대 설정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "app.jar"]