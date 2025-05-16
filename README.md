# ScheTook <img src="https://avatars.githubusercontent.com/u/183745605?s=200&v=4" width="110" align="left"/>
모두가 가능한 시간을 추천받고 약속을 한눈에 확인하자!

<br />
<br />

## 📅 ScheTook [👉플레이 스토어 바로가기](https://play.google.com/store/apps/details?id=com.sopt.noostak)
ScheTook은 팀원들의 가능한 시간을 모아 가장 많은 인원이 참석할 수 있는 최적의 일정을 자동으로 추천하는 서비스입니다.
<br />
<br />
<img src="https://github.com/user-attachments/assets/a9820881-54df-4df0-a18b-b418a00b27cc"/>

<br />

## 🚀 서비스 소개
<p align="left">
  <img src="https://github.com/user-attachments/assets/ba8cdc6a-e7cd-4224-967c-a2abdf4867e8" width="30%" />
  <img src="https://github.com/user-attachments/assets/79f66c19-b166-4455-85c7-52c61a008f98" width="30%" />
  <img src="https://github.com/user-attachments/assets/999042c3-7d76-45b3-8e12-25e3b64754d9" width="30%" />
</p>

<br />
<br />

## 👩🏻‍🍳 ScheTook Members
<div align="center">
<table>
    <tr>
	<th>팀원</th>
    	    <th> 💙 이가을 <a href="https://github.com/gaeulzzang"><br/></a></th>
    	    <th> 🩵 김언지 <a href="https://github.com/Eonji-sw"><br/></a></th>
    	    <th> 💙 박유진 <a href="https://github.com/youjin09222"><br/></a></th>
    	    <th> 🩵 임하늘 <a href="https://github.com/twogarlic"><br/></a></th>
    </tr>
    <tr>
        <th> 팀원 소개 </td>
	    <td><img src="https://github.com/user-attachments/assets/9bdc9d59-61a6-4446-a400-1c024a59233f"/></td>
	    <td><img src="https://github.com/user-attachments/assets/ff4f7095-6eee-4d56-94aa-eaf59771256f"/></td>
	    <td><img src="https://github.com/user-attachments/assets/d44cc116-4591-4458-81db-13c2b57bba96"/></td>
	    <td><img src="https://github.com/user-attachments/assets/c71e02dc-bb8d-4454-92ac-080648bb5f28"/></td>
    </tr>
    <tr>
	<th> 역할 </td>
	<td>
		<p align="center">ANDROID, LEAD</p>
	</td>
	<td>
		<p align="center">ANDROID</p>
	</td>
	<td>
		<p align="center">ANDROID</p>
	</td>
    <td>
		<p align="center">ANDROID</p>
	</td>
    </tr>
  </table>
</div>
<br />
<br />

## 🍴 TECH STACK
- Android app modularization
- Gradle Version Catalog
- MVI
- Jetpack Compose
- Material, Material 3
- Hilt
- Retrofit, OkHttp
- Multipart
- Coroutine & Flow
- Timber, Coil, Glide, Lottie
- Kakao Open API
- Google Open API
- Github Actions, Discord Webhook, KtLint


<br/>
<br/>

## 📁 Package
```
📦com.sopt.noostak
├─📂app
│  ├─📂di
├─📂core
│  ├─📂designsystem
│  ├─📂extension
│  ├─📂navigation
│  ├─📂state
│  ├─📂type
│  ├─📂util
├─📂data
│  ├─📂datasource
│  ├─📂datasourceimpl
│  ├─📂dto
│  ├─📂mapper
│  ├─📂repositoryimpl
│  ├─📂service
│  ├─📂util
├─📂domain
│  ├─📂entity
│  ├─📂repository
│  ├─📂usecase
└─📂presentation
```
</br>
</br>

## 📏 컨벤션
<details>
<summary > 💡 Git & Github </summary>

<br/>

### Git Flow

- **main:** 제품을 배포하는 브랜치
- **develop**: 개발 브랜치로 개발자들이 이 브랜치를 기준으로 각자 작업한 기능들을 Merge
  - `develop` 브랜치를 기준으로 `feature` 브랜치를 분기하고, 각 `feature` 브랜치를 합침
  - `develop` 브랜치에서 `main` 브랜치로 병합
- **feature:** 기능을 개발하는 브랜치로 기능 개발이 완료되면 `develop` 브랜치에 Merge
  - 반드시 `develop` 에서 분기해야 됨. 분기 된 다른 `feature` 브랜치에서 또 다른 `feature` 브랜치를 분기하면 절대 안됨.

현 프로젝트에서는 release, hotfix 브랜치는 사용하지 않음.

<br/>

### 브랜치 생성

기능 구현 전 자신이 구현할 부분을 이슈로 관리

1. `이슈템플릿` 을 활용하여 이슈 생성
2. 1에서 생성된 이슈 번호를 이용하여 브랜치 생성.
   1. 브랜치 이름은 `feature/#<issued number>/example` ex) feat/#18/common-button

<br/>

### 작업

모든 작업은 develop 에서 분기된 `feature` 브랜치에서 진행

- 커밋 메시지는 `커밋유형: <구현, 수정, 개발한 내용에 대한 커밋 메시지> (#<issued number>)`
  ex) `feat: Button 공통 컴포넌트 제작 (#18)`
  | 커밋유형 | 의미 |
  | -------- | ------------------------------------------------------------ |
  | feat | 새로운 기능 구현 |
  | fix | 버그 및 오류 발생 및 해결 |
  | docs | 문서 수정 |
  | refactor | 코드 리팩토링 |
  | chore | 버전 코드 수정, 패키지 구조 변경, 타입 및 변수명 변경 등의 작은 작업 |
  | del | 쓸모없는 코드나 파일 삭제 |
  | hotfix | 이슈나 QA에서 발생된 급하게 해결해야 될 문제 |
  | mod | 코드 수정 및 내부 파일 수정 |
  | move | 파일 이동 or 코드 이동 |
  | rename | 파일 이름 변경 |
  | setting | 기초세팅 |
  | ui | UI 작업 |

<br/>

### 브랜치 병합

`feature` 브랜치에서 `develop` 브랜치로 merge할 때는 PR을 이용함 (직접 merge ❌)

1. develop, feature 브랜치 최신화
2. develop → feature merge 하고 충돌 처리
   - feature 브랜치로 checkout 해서 `git merge develop`
3. `PR템플릿` 을 활용하여 PR 작성

   - PR 작성시 이슈번호 제대로 기입해야 이슈가 함께 닫힘(템플릿대로 하면 됨)
   - 팀원들의 review & approve(2명) 후 스쿼시 머지

   주의⚠️ - review & approve 과정에서 다른 PR 머지 등 develop에 수정사항이 생겼다면, 2번과정을 다시 해줘야 함.

4. 정상적으로 머지 되었다면 feature 브랜치 삭제

<br/>

</details>

