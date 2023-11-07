# FoodieFinder - 지리기반 맛집 추천 웹 서비스

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.1.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Data JPA-gray?style=for-the-badge&logoColor=white"/></a>
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>

<img src="https://img.shields.io/badge/Junit-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Notion-FFFFFF?style=for-the-badge&logo=Notion&logoColor=black"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>



</div>

<br>

FoodieFinder는 공공데이터를 활용하여, 지역 음식점 목록을 자동으로 업데이트 하고 이를 활용합니다. `사용자 위치` 에맞게 맛집 및 메뉴를 추천하여 더 나은 `다양한 음식 경험`을 제공하고, 음식을 좋아하는
사람들 간의 소통과
공유를 촉진하려 합니다.
<br>
<br>
<div align="center">

## ☄️ Team Q members

<table>
    <tr>
        <th>김서윤</th>
        <th>방성원</th>
        <th>장혜리</th>
        <th>정지원</th>
    </tr>
    <tr>
        <td><a href="https://github.com/seoyoon047">@seoyoon047</a></td>
        <td><a href="https://github.com/O0oO0Oo">@O0oO0Oo</a></td>
        <td><a href="https://github.com/hyerijang">@hyerijang</a></td>
        <td><a href="https://github.com/cjw9506">@cjw9506</a></td>
    </tr>
</table>
</div>

---

## 0. 목차

레이아웃 확정 이후 추가 예정

---

## 1. 개발 기간

`2023.10.31` ~ `2023.11.8` - `#9_days`

## 2. 프로젝트 요구사항

[🍣지리기반 맛집 추천 웹 서비스 요구사항](https://bow-hair-db3.notion.site/a9a2ec57b65545e4be7da370c4649007)

### 유저스토리

- 유저는 본 사이트에 들어와 회원가입 및 내 위치를 지정한다.
- **A. 내 위치 기반 맛집추천 = (`내 주변보기`)**
    - `도보` 기준 `1km` 이내의 맛집을 추천한다.
    - `교통수단` 기준 `5km` 이내의 맛집을 추천한다.
- **B. 지역명 기준 맛집추천(`특정 지역 보기`)**
    - 지정한 `지명(시군구)` 중심위치 기준 `10km` 이내의 맛집을 추천한다.
- **C. 점심 추천 서비스**
    - 점심 추천 서비스 이용을 승락한 대상에게 매일 정오, 대상의 위치를 기준으로 원하는 유형(일식, 중식 등)의 가게를 3개씩 추천 해준다.
- A, B는 다양한 검색기준 (정렬, 필터링 등)으로 조회 가능하며 (`거리순`, `평점순` , `양식`, `중식`)
- 해당 맛집의 상세정보를 확인할 수 있다.

## 3. 담당 역할

<table>
    <tr>
        <td>김서윤</td>
        <td></td>
    </tr>
    <tr>
        <td>방성원</td>
        <td></td>
    </tr>
    <tr>
        <td>장혜리</td>
        <td></td>
    </tr>
    <tr>
        <td>정지원</td>
        <td></td>
    </tr>
</table>

## 4. 프로젝트 구조

<details>
    <summary>자세히</summary>

```
├─main
│  ├─java
│  │  └─com
│  │      └─foodiefinder
│  │          ├─auth
│  │          │  ├─config
│  │          │  ├─controller
│  │          │  ├─dto
│  │          │  ├─filter
│  │          │  ├─jwt
│  │          │  └─service
│  │          ├─cities
│  │          │  ├─controller
│  │          │  │  └─response
│  │          │  ├─domain
│  │          │  ├─factory
│  │          │  └─service
│  │          ├─common
│  │          │  ├─config
│  │          │  ├─dto
│  │          │  ├─entity
│  │          │  └─exception
│  │          ├─datapipeline
│  │          │  ├─config
│  │          │  ├─enums
│  │          │  ├─job
│  │          │  ├─processor
│  │          │  │  └─dto
│  │          │  ├─reader
│  │          │  ├─step
│  │          │  ├─util
│  │          │  └─writer
│  │          │      ├─entity
│  │          │      └─repository
│  │          ├─notification
│  │          │  ├─dto
│  │          │  ├─repository
│  │          │  ├─scheduler
│  │          │  └─service
│  │          ├─settings
│  │          │  ├─controller
│  │          │  ├─dto
│  │          │  ├─entity
│  │          │  ├─service
│  │          │  └─valid
│  │          └─user
│  │              ├─controller
│  │              ├─crypto
│  │              ├─dto
│  │              ├─entity
│  │              ├─repository
│  │              └─service
│  └─resources
└─test
├─java
│  └─com
│      └─foodiefinder
│          ├─auth
│          │  ├─controller
│          │  └─service
│          ├─cities
│          ├─config
│          ├─datapipeline
│          │  ├─processor
│          │  ├─reader
│          │  ├─step
│          │  └─writer
│          │      └─repository
│          ├─notification
│          │  └─service
│          ├─settings
│          │  ├─controller
│          │  ├─service
│          │  └─valid
│          └─user
│              ├─controller
│              └─service
└─resources

```

</details>

## 5. ERD

<details>
    <summary>자세히</summary>

<img src="https://github.com/wanted-quantum-jump/FoodieFinder/assets/46921979/ff5974e4-0060-4e6d-9fcd-114e0b6eadd7" width="70%" />


</details>

## 5. API Document

## 6. 프로젝트 스케줄링

[Github Project](https://github.com/orgs/wanted-quantum-jump/projects/5)에 기능별 issue를 등록하며 프로젝트 개발 일정을 관리하였습니다.
![image](https://github.com/wanted-quantum-jump/FoodieFinder/assets/46921979/fa45837d-3362-4eff-901b-e42dc35c8319)

## 7. 협업 규칙

[Team Q Notion - 팀 규칙 및 컨벤션](https://www.notion.so/f22c8da6c7e4430a90dffc34b7b7d80c)을 참조해 주세요.
