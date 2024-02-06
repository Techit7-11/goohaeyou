
<script>
    import { onMount } from 'svelte';
    import rq from '$lib/rq/rq.svelte';

    // 업데이트할 회원 정보 데이터
    let updatedMemberData = {
      name: '',
      phoneNumber: '',
      gender: 'MALE',
      location: '',
      birth: '2000-01-01',
    };
  
    // 업데이트 버튼을 클릭하면 실행될 함수
    async function updateMember() {
        const response = await rq.apiEndPoints().PUT('/api/member/social', { body: updatedMemberData });

        if (response.data?.statusCode === 200) {            
            rq.msgAndRedirect({ msg: '회원정보 수정 완료' }, undefined, 'http://localhost:5173/');   
        } else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
                const customErrorMessage = response.data?.data?.message;
                rq.msgError(customErrorMessage ?? '알 수 없는 오류가 발생했습니다.');
        } else if (response.data?.msg === 'VALIDATION_EXCEPTION') {
            if (Array.isArray(response.data.data)) {
                response.data.data.forEach(msg => rq.msgError(msg));
            }
        } else {
            rq.msgError('로그인 중 오류가 발생했습니다.');
        }
    }
  
    // 다음 주소 API 팝업 열기
    function openDaumAddressPopup() {
      // 다음 주소 API 스크립트 동적으로 가져오기
      const script = document.createElement('script');
      script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
      script.async = true;
      script.onload = function () {
        new daum.Postcode({
          oncomplete: function (data) {
            updatedMemberData.location = data.roadAddress;
          },
        }).open();
      };
      document.body.appendChild(script);
    }
  
</script>

<style>
    .container {
        background-color: #ffffff;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
        padding: 24px;
        max-width: 400px;
        margin: 0 auto;
    }

    .select {
        width: 25%;
        padding: 7px;
        border: 1px solid #dddddd;
        border-radius: 15px;
        font-size: 15px;
        margin-bottom: 12px;
        background-color: #ffffff;
    }

    .button-container {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .button {
        background-color: #5a66d8;
        color: #ffffff;
        padding: 8px 9px;
        border: none;
        border-radius: 4px;
        font-size: 16px;
        cursor: pointer;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        transition: background-color 0.3s ease;
        margin-top: 8px;
    }

    .button:hover {
        background-color: #86b3e4;
    }
</style>

<div class="flex items-center justify-center min-h-screen bg-base-100">
  <div class="container mx-auto px-4">
    <h2 class="text-2xl font-bold text-center my-3">필수 회원 정보 입력</h2>
    <div class="max-w-sm mx-auto">
<div>
    <label class="label" for="name">이름</label>
    <input class="input input-bordered w-full" placeholder="이름을 입력해주세요." type="text" id="name" bind:value={updatedMemberData.name}  />
    <label class="label" for="phoneNumber">휴대폰 번호</label>
    <input class="input input-bordered w-full" placeholder="휴대폰 번호를 입력해주세요." type="text" id="phoneNumber" bind:value={updatedMemberData.phoneNumber} />
    <label class="label" for="gender">성별</label>
    <select class="input input-bordered w-full" id="gender" bind:value={updatedMemberData.gender}>
        <option value="MALE">남성</option>
        <option value="FEMALE">여성</option>
    </select>
    <label class="label" style="display: block; margin-top: 8px;" for="location">지역</label>
    <div class="flex">
    <input class="input input-bordered w-full" placeholder="주소를 입력해주세요." style="width: 80%;" type="text" id="location" bind:value={updatedMemberData.location} readonly />
    <!-- 다음 주소 API 팝업 열기 버튼 -->
    <button class="btn btn-primary ml-3" style="padding: 6px 7px; font-size: 14px;" on:click={openDaumAddressPopup}>주소 검색</button>
    </div>
    <label class="label" for="birth">생년월일</label>
    <input class="input input-bordered w-full" type="date" id="birth" bind:value={updatedMemberData.birth} />

    <div class="mt-5">
        <button class="w-full btn btn-primary" on:click={updateMember}>회원 정보 수정</button>
    </div>
</div>
</div>
</div>
</div>
