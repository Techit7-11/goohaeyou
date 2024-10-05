# 📢 구해유, 간편한 단기 일자리 매칭 서비스 ⚡️

<br/>

<p align="center">
  <img src="https://github.com/user-attachments/assets/a754ade8-e469-4533-8cd9-a08e806d1521" width="600px"/><br/><br/><br/>
  <a href="https://www.goohaeyou.site/">사이트 바로가기</a>
  <br/>PC/모바일 겸용<br/>
</p>

<br/><br/>

## 🎯 프로젝트 소개

구해유는 강아지 산책이나 헬스 기구 운반 등 소소한 작업부터 급한 하루 알바까지, 단기 일자리를 찾는 사람들을 위한 스팟 채용 서비스입니다.

## 주요 기능

**일반/구글 로그인** 지원

**지역별·직종별 카테고리**, **검색 및 정렬** 기능을 통해 사용자는 원하는 공고를 쉽게 찾을 수 있습니다. <br/>
**내 주변 공고 찾기** 기능을 통해 현재 위치에서 가까운 공고를 탐색할 수 있습니다.

매칭된 구인자와 구직자는 **1:1 채팅**을 통해 소통할 수 있습니다. <br/>
**수당 결제** 및 **정산** 시스템으로 빠르고 투명하게 지급 처리가 이루어집니다.

**웹 푸시 알림**을 통해 지원자 현황, 댓글, 지원서 승인 등의 정보를 실시간으로 받아볼 수 있습니다.

<br/>

### 📖 API 문서
프로젝트의 API 문서는 아래 링크를 통해 확인하실 수 있습니다. (API 호출은 불가능하며, 문서 확인만 가능합니다)
[Swagger UI API Documentation](https://a.goohaeyou.site/swagger-ui/index.html#)

<br/><br/>

## ⚒️ 기술스택

<br/>

### 백엔드

<img src="https://github.com/user-attachments/assets/a6ff76b2-40cc-444d-85f3-d4999c6442ab" width=655px alt="백엔드">

### 인프라 

<img src="https://github.com/user-attachments/assets/6f01d20b-4c25-4853-a891-3bcd365cad05" width=440px alt="인프라">

### 프론트엔드

<img src="https://github.com/user-attachments/assets/27c36404-914f-4eba-916b-2bfd5985a452" width=335px alt="프론트엔드">

<br/><br/>

## ⚙️ Infrastructure

<img src="https://github.com/user-attachments/assets/4b8379a0-4231-48c2-b198-dc4afd2319a6" width=800px alt="인프라아키텍처">

<br/><br/>

## 🚀 CI/CD Pipeline

<img src="https://github.com/user-attachments/assets/7f18d85a-e733-403f-a79b-a911c2a0a7f5" width=800px alt="cicd아키텍처">

<br/><br/>

## 📝 ERD

![erd_0929](https://github.com/user-attachments/assets/e19298dc-27dd-4dd5-a7af-7b82fd929528)

<br/><br/>

### 🔄 아키텍처 의존성 흐름

```
[Presentation] Controller
                 ⬇
[Application] Business Service (비즈니스 흐름)
              ⬇          ⬇             
[Domain] Policy     Domain Service (핵심 도메인 로직)   
                      ⬇        ⬇
[Domain]     Repository I/F   PaymentProcessor(I/F) (외부 의존성 분리) 
                                    (구현)
[Infra]                        TossPaymentUtil
```
<br/><br/>

## 🧑‍🤝‍🧑 팀원


|                                                              이민구                                                              |                                                              이재열                                                              |                                                              차재준                                                              |                                                              홍지은                                                              |                                                              황세희                                                              |
|:-----------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/e5cb563e-a100-437e-aa7f-bc6ec1bd7456" width=120px alt="민구"> | <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/2bf3093d-03b6-4b4e-9bcf-3e39ce9ec4e9" width=120px alt="재열"> | <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/374149e6-d9d1-4598-88d4-724b81dca29d" width=120px alt="재준"> | <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/8137422f-dd32-4de6-875c-9abbef5c9683" width=120px alt="지은"> | <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/8c104315-4f51-45ec-804d-154d65da47cf" width=120px alt="세희"> | 
|                                              [Yimin2](https://github.com/Yimin2)                                              |                                             [jae9380](https://github.com/jae9380)                                             |                                             [lapishi](https://github.com/lapishi)                                             |                                              [itonse](https://github.com/itonse)                                              |                                            [sehee210](https://github.com/sehee210)                                            |             
|                                 로그 인/아웃<br/>회원가입<br/>JWT token, 쿠키<br/>Oauth 2.0 인증<br/>리뷰, 댓글 CRUD                                  |                             공고, 댓글, 추천<br/> 알림 시스템<br/> 공고 자동 마감<br/> 자동 알바 완료<br/>1:1채팅<br/>FCM(웹 푸시)                              |                                                  공고/댓글 삭제<br/>검색, 회원 레벨                                                   |                 인프라, DB 관리<br/>결제, 카테고리<br/>공고, 프로필 이미지 설정<br/>급여 설정<br>수동 알바완료<br/>알바 취소 처리<br/>e-mail 본인인증                  | 검색 & 필터<br/>공고 정렬<br>공고 지원하기<br/>작성글 모아보기<br/>마이페이지, 지도
|                         [작업 내용 💻](https://github.com/Techit7-11/GooHaeYou/commits/dev/?author=Yimin2)                          |                         [작업 내용 💻](https://github.com/Techit7-11/GooHaeYou/commits/dev/?author=jae9380)                         |                         [작업 내용 💻](https://github.com/Techit7-11/GooHaeYou/commits/dev/?author=lapishi)                         |                         [작업 내용 💻](https://github.com/Techit7-11/GooHaeYou/commits/dev/?author=itonse)                          |                        [작업 내용 💻](https://github.com/Techit7-11/GooHaeYou/commits/dev/?author=sehee210)                         |

<br/><br/>

## 📱 구해유 기능 영상


| 공고에 지원하기 기능 ✏️                                                                                                               | 지원서 승인 기능 ✅                                                                                                         |
|------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/b147bd89-d873-4196-a9b1-b8769ae27fe9" width="280px"> | <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/d52edb01-05e5-4aae-8362-94634c936a41" width="280px"> |

| 공고 지도 기능 📍                                                                                                            | 알바비 결제 기능 💸                                                                                                           |
|------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/278502cd-f1f2-4835-926d-ef77a1bc399f" width="280px"> | <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/a6ba5065-54db-448f-ae99-fb2ef197c6dc" width="280px"> |

| 이메일 본인인증 기능 📧                                                                                                         |
|------------------------------------------------------------------------------------------------------------------------|
| <img src="https://github.com/Techit7-11/GooHaeYou/assets/76129297/2548bf24-eaf6-4192-b5fd-b5adf43d37ca" width="280px"> |
