# Calendar API

## 1. 개요

Calendar API는 사용자가 일정을 생성하고 관리하며, 친구와 공유할 수 있도록 설계된 일정 관리 애플리케이션입니다. Spring Boot를 기반으로 개발되었으며, JWT 기반 인증을 지원하여 보안성을 강화하였습니다.

## 2. 소스 빌드 및 실행 방법

### 2.1 필수 환경

- Java 17 이상
- MySQL Database
- Gradle 7+

### 2.2 데이터베이스 설정 및 프로젝트 빌드/실행

1. 프로젝트 클론:
   ```sh
   git clone https://github.com/honggeunLee/calendar_backend
   ```
2. MySQL에 데이터베이스 생성:
   ```sql
   CREATE DATABASE calendar_db;
   ```
3. 기존 데이터 복원 (`calendar_db_backup.sql` 파일 사용, 프로젝트 루트경로에 존재합니다.):
   ```sh
   mysql -u root -p calendar_db < calendar_db_backup.sql
   ```
4. `application.yml` 설정 (아래와 같이 환경변수로 설정 되어있기 때문에 환경변수 설정을 해주어야 합니다.):
   ```yml
   datasource:
       url: ${DB_URL}
       username: ${DB_USERNAME}
       password: ${DB_PASSWORD}
   ```
   아래의 코드로 설정하거나 application.yml 파일에서 datasource에 직접 수정
   ```sh
   # PowerShell
   $env:DB_URL="jdbc:mysql://localhost:3306/calendar_db?useSSL=false&serverTimezone=UTC"
   $env:DB_USERNAME="your_db_username"
   $env:DB_PASSWORD="your_db_password"
   
   # CMD (명령 프롬프트)
   set DB_URL "jdbc:mysql://localhost:3306/calendar_db?useSSL=false&serverTimezone=UTC"
   set DB_USERNAME "your_db_username"
   set DB_PASSWORD "your_db_password"
   
   # macOS / Linux (Bash / Zsh)
   export DB_URL="jdbc:mysql://localhost:3306/calendar_db?useSSL=false&serverTimezone=UTC"
   export DB_USERNAME="your_db_username"
   export DB_PASSWORD="your_db_password"
   
   ######### your_db_username, your_db_password는 실제 mysql 계정을 사용해야합니다. ###########
   ```
4. 빌드 및 실행 (DB 환경변수 관련 에러 발생시 환경변수 설정 혹은 application.yml 파일의 datasource 수정 필수)
   ```sh
   cd [프로젝트 경로] # 실제 경로
   ./gradlew build
   ./gradlew bootRun
   ```


## 3. 주력 라이브러리 및 사용 이유

- **Spring Boot**: 빠른 개발과 설정 간소화를 위해 사용했습니다.
- **Spring Security & JWT**: 사용자 인증 및 보안을 강화하기 위해 사용했습니다.
- **Spring Data JPA**: 데이터베이스 접근을 간편하게 하기 위해 사용했습니다.
- **Lombok**: 보일러플레이트 코드를 줄이기 위해 사용했습니다. (@Getter, @Setter, @Builder 등)
- **Springdoc OpenAPI**: API 문서를 자동으로 생성하기 위해 사용했습니다.

## 4. API 명세서

프로젝트 실행 후 http://localhost:8080/swagger-ui/index.html 로 접속하여 명세서 확인 가능합니다.

## 5. 테스트 케이스 작성

아래는 API 서버에서 직접 테스트할 수 있는 케이스입니다. 각 요청을 Postman 또는 cURL을 이용하여 실행할 수 있습니다.

### 5.1 회원가입 및 로그인

1. 회원가입
2. 로그인

### 5.2 일정 관리

1. 일정 생성
2. 일정 조회 (목록)
3. 일정 조회 (상세)
4. 일정 수정
5. 일정 삭제

### 5.3 친구 관리

1. 친구 요청 보내기
2. 친구 요청 수락
3. 친구 요청 거절
4. 친구 삭제
5. 받은 친구 요청 목록 조회
6. 친구 목록 조회
7. 친구 일정 열람

