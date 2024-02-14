<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';

	// 회원 정보 데이터
	let newMemberData = {
		username: '',
		password: '',
		name: '',
		phoneNumber: '',
		gender: 'MALE',
		location: '',
		birth: ''
	};

	// 가입하기 버튼을 클릭하면 실행될 함수
	async function registerMember() {
		newMemberData.username = newMemberData.username.trim();
		newMemberData.password = newMemberData.password.trim();

		const response = await rq.apiEndPoints().POST('/api/member/join', { body: newMemberData });

		if (response.data?.statusCode === 200) {
			rq.msgAndRedirect({ msg: '회원가입 완료' }, undefined, '/');
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			const customErrorMessage = response.data?.data?.message;	
			rq.msgError(customErrorMessage);
		} else if (response.data?.msg === 'VALIDATION_EXCEPTION') {
			if (Array.isArray(response.data.data)) {
				rq.msgError(response.data.data[0])
			}
		} else {
			rq.msgError('회원가입 중 오류가 발생했습니다.');
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
					newMemberData.location = data.roadAddress;
				}
			}).open();
		};
		document.body.appendChild(script);
	}
</script>

<div class="flex items-center justify-center min-h-screen bg-gray-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<h2 class="text-2xl font-bold text-center my-3">회원가입</h2>
			<form on:submit|preventDefault={registerMember}>
				<div class="form-group">
					<label class="label" for="username">* 사용자ID</label>
					<input
						type="text"
						id="username"
						name="username"
						class="input input-bordered w-full"
						placeholder="ID를 입력해주세요."
						bind:value={newMemberData.username}
						required
					/>
				</div>
				<div class="form-group">
					<label class="label" for="password">* 비밀번호</label>
					<input
						type="password"
						id="password"
						name="password"
						class="input input-bordered w-full"
						placeholder="비밀번호를 입력해주세요."
						bind:value={newMemberData.password}
						required
					/>
				</div>
				<div class="form-group">
					<label class="label" for="name">* 이름</label>
					<input
						type="text"
						id="name"
						class="input input-bordered w-full"
						placeholder="이름을 입력해주세요."
						bind:value={newMemberData.name}
					/>
				</div>
				<div class="form-group">
					<label class="label" for="phoneNumber">* 휴대폰 번호</label>
					<input
						type="text"
						id="phoneNumber"
						class="input input-bordered w-full"
						placeholder="휴대폰 번호를 입력해주세요."
						bind:value={newMemberData.phoneNumber}
					/>
				</div>
				<div class="form-group">
					<label class="label" for="gender">성별</label>
					<select class="input input-bordered w-full" id="gender" bind:value={newMemberData.gender}>
						<option value="MALE">남성</option>
						<option value="FEMALE">여성</option>
					</select>
				</div>
				<div class="form-group">
					<label class="label" style="display: block; margin-top: 8px;" for="location">* 주소</label
					>
					<div class="flex">
						<input
							class="input input-bordered w-full"
							placeholder="주소를 입력해주세요."
							style="width: 80%;"
							type="text"
							id="location"
							bind:value={newMemberData.location}
							readonly
						/>
						<!-- 다음 주소 API 팝업 열기 버튼 -->
						<button
							class="btn btn-primary ml-3"
							style="padding: 6px 7px; font-size: 14px;"
							on:click={openDaumAddressPopup}
							type="button">주소 검색</button
						>
					</div>
				</div>
				<div class="form-group">
					<label class="label" for="birth">* 생년월일</label>
					<input
						type="date"
						id="birth"
						class="input input-bordered w-full"
						bind:value={newMemberData.birth}
					/>
				</div>
				<div class="my-5">
					<button class="w-full btn btn-primary" on:click={newMemberData}>회원가입</button>
				</div>
			</form>
		</div>
	</div>
</div>

<style>
	.form-group {
		margin-bottom: 10px;
	}
</style>
