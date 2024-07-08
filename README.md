# 재능을 서로 교환하고 연결해주는 플랫폼, 재능교환소

## 🙋‍♀️ 팀원 구성
<div align="center">

<div align="center">

|                                                               **김민지**                                                               |
|:-----------------------------------------------------------------------------------------------------------------------------------:| 
| [<img src="https://avatars.githubusercontent.com/u/121842688?v=4" height=150 width=150> <br/> @10000JI](https://github.com/10000JI) |

</div>
</div>
<br>

## 📆 개발기간
- 2024년 02월 01일 ~ 2024년 07월 02일

<br>

## ✨ 서비스 소개
사용자들이 서로의 재능을 교환할 수 있는 온라인 플랫폼입니다. <br>
사용자들은 다양한 카테고리에서 재능 교환 게시물을 작성합니다. <br>
작성된 게시물을 토대로 매칭 서비스를 통해 적합한 파트너를 찾을 수 있습니다. <br>
관심 있는 상대와 실시간 채팅으로 소통하며, 자발적인 지식과 기술 공유를 촉진합니다.

<br>

## 🛠️ 개발 환경
#### &nbsp;　[ DB ]
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/MariaDB-003545?style=flat&logo=MariaDB&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=Redis&logoColor=white"/>

#### &nbsp;　[ Backend ]
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Java17-007396?style=flat&logo=java&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=springBoot&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat&logo=spring&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/QueryDSL-0769AD?style=flat&logo=java&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white"/>

#### &nbsp;　[ CI/CD ]
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=GitHub&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white"/>

#### &nbsp;　[ AWS ]
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat&logo=amazon-ec2&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/AWS RDS-527FFF?style=flat&logo=amazon-rds&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/AWS S3-569A31?style=flat&logo=amazon-s3&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/AWS ElastiCache-4053D6?style=flat&logo=amazon-aws&logoColor=white"/>

#### &nbsp;　[ Testing ]
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=JUnit5&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Mockito-C5D9C8?style=flat&logo=mock&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/SonarQube-4E9BCD?style=flat&logo=sonarqube&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Apache JMeter-D22128?style=flat&logo=apache-jmeter&logoColor=white"/>
&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white"/><br>

<br>

## 🚀 CI/CD 아키텍처

<img src="./img/CICD 아키텍처.png">

<br>

## 📺 API 명세서

<img src="./img/API명세서.png">

https://documenter.getpostman.com/view/29789417/2sA35EaNyR

<br>

## 📝 DB구조도
<img src="./img/ERD_재능교환소.png">

<br>

## 💻 수행 내용
#### [ 인증 시스템 구축 ]
- [JWT 활용 계정활성화 이메일 인증으로 안전한 회원가입](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-JWT%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8) 
- [JWT 활용 AccessToken 기반 로그인 시스템](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-JWT%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8) 
- [OAuth2 활용 소셜 로그인(Kakao, Google) 통합으로 사용자 편의성 증대](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-Spring-Boot%EC%97%90%EC%84%9C-OAuth2-JWT%EB%A5%BC-%ED%86%B5%ED%95%9C-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84-Kakao-Google) 
- [RDS에서 Redis로 RefreshToken 저장소 전환, 관리 효율성 개선](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-RDS%EC%97%90%EC%84%9C-Redis%EB%A1%9C-RefreshToken-%EC%A0%80%EC%9E%A5%EC%86%8C-%EC%A0%84%ED%99%98)

#### [ 서비스 안정성 및 성능 최적화 ]
- [예외 처리 시스템 고도화• 다형성을 활용한 중앙집중식 예외 처리 구현으로 코드 중복 감소](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-ExceptionHandler-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC)
- [JPA N+1 문제 해결, 연관관계 최적화 및 코드 리팩토링](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-JPA-Delete-%EC%9E%91%EC%97%85-%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84-SQL-%EC%A4%84%EC%9D%B4%EA%B3%A0-%EC%84%B1%EB%8A%A5-%ED%96%A5%EC%83%81%EC%8B%9C%ED%82%A4%EA%B8%B0)
  - [Jmeter를 활용해 5000건의 부하 테스트 결과 응답시간 최대 40% 개선](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-Jmeter%EB%A1%9C-%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8%ED%95%98%EA%B8%B0)
- [Redis 및 쿠키를 이용한 조회수 중복 방지 로직 구현으로 데이터 정확도 향상](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-%EC%A1%B0%ED%9A%8C%EC%88%98-%EC%A4%91%EB%B3%B5-%EB%B0%A9%EC%A7%80-Redis-%EC%BF%A0%ED%82%A4)

#### [ 인프라 구축 및 배포 프로세스 ]
- [CI/CD 파이프라인 구축으로 Nignx를 활용한 무중단 배포](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-CICD-%EB%AC%B4%EC%A4%91%EB%8B%A8-%EB%B0%B0%ED%8F%AC)
- SonarQube를 통한 정적 코드 분석 자동화

#### [ 실시간 통신 기능 구현 ]
- [Spring Boot 기반 1:1 채팅 시스템 개발](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-Spring-Boot%EB%A1%9C-Stomp-%EA%B8%B0%EB%B0%98-11-%EC%B1%84%ED%8C%85-%EA%B5%AC%ED%98%84-with-React)
- [메시지 큐 시스템(RabbitMQ) 도입으로 메시지 연속성 보장 및 안정성 확보](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-Spring-Boot%EC%99%80-RabbitMQ%EB%A1%9C-%ED%99%95%EC%9E%A5-%EA%B0%80%EB%8A%A5%ED%95%9C-11-%EC%B1%84%ED%8C%85-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0)
- 채팅방 목록 및 채팅 메시지에 무한 스크롤 구현

#### [ 보안 강화 및 사용자 데이터 관리 ]
- [JWT 블랙리스트 구현으로 AccessToken 악용 가능성 차단](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-Redis%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-JWT-%EB%B8%94%EB%9E%99%EB%A6%AC%EC%8A%A4%ED%8A%B8%EC%99%80-%EB%A1%9C%EA%B7%B8%EC%95%84%EC%9B%83-%EA%B5%AC%ED%98%84)
- [소셜 로그인 연동 로그아웃 및 회원탈퇴 프로세스 구현으로 개인정보 보호 강화](https://velog.io/@10000ji_/%EC%9E%AC%EB%8A%A5%EA%B5%90%ED%99%98%EC%86%8C-Spring-Boot-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%A1%9C%EA%B7%B8%EC%95%84%EC%9B%83-%ED%9A%8C%EC%9B%90%ED%83%88%ED%87%B4Kakao-Google)

#### [ 비즈니스 로직 구현 ]
- 재능교환 게시물 & 공지사항 CRUD 기능 구현으로 서비스의 핵심 기능 완성
- QueryDSL을 활용한 검색 기능 구현
  - 다양한 조건을 조합한 동적 쿼리 생성으로 유연한 검색 기능 제공
  - Cursor 기반 페이징 처리
- 자기 참조 관계를 활용한 계층형 댓글 시스템
  - 단일 테이블 내 부모-자식 관계 설정으로 대댓글 기능 구현
  - 재귀적 쿼리를 통한 다층 구조의 댓글 조회 최적화
  - 대댓글 깊이에 제한 없는 유연한 구조 설계
- 재능교환 게시물 스크랩 기능 & 매칭 서비스 구현
