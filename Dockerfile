# 1. 기본 이미지 지정 (Java 17을 예로 듭니다)
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 jar 파일을 컨테이너 안에 복사 (로컬 빌드 결과물 경로에 맞게 수정하세요)
COPY build/libs/*.jar app.jar

# 4. 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 5. 외부 접속 포트
EXPOSE 8080
