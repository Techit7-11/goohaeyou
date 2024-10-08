<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';

	let newJobPostData = {
		title: '',
		body: '',
		minAge: '',
		gender: 'UNDEFINED',
		location: '',
		deadLine: '',
		wageType: '',
		workTime: '',
		workDays: '',
		cost: '',
		payBasis: ''
	};

	let subCategories = []; // 하위 카테고리 목록을 저장
	let selectedCategoryId = ''; // 선택한 카테고리의 ID를 저장

	onMount(async () => {
		try {
			await rq.initAuth(); // 로그인 상태를 초기화
			if (rq.isLogout()) {
				rq.msgError('로그인이 필요합니다.');
				rq.goTo('/member/login');
				return;
			} else if (rq.member.name == null) {
				rq.msgAndRedirect(
					undefined,
					{ msg: '회원정보 입력이 필요합니다.' },
					'/member/social/modify'
				);
			} else if (!rq.member.authenticated) {
				rq.msgAndRedirect(
					{ msg: '서비스를 이용하기 위해 먼저 본인 인증을 진행해주세요.' },
					undefined,
					'/member/me'
				);
			}
		} catch (error) {
			console.error('인증 초기화 중 오류 발생:', error);
			rq.msgError('인증 과정에서 오류가 발생했습니다.');
			rq.goTo('/member/login');
		}

		const categoryId = 1;

		const response = await rq.apiEndPoints().GET(`/api/categories/${categoryId}/sub-categories`);
		subCategories = response.data.data;
	});

	// 글 작성 버튼을 클릭하면 실행될 함수
	async function writeJobPost() {
		if (newJobPostData.wagePaymentMethod === '') {
			rq.msgError('대금 지금 방법 선택은 필수입니다.');
			return;
		}
		if (newJobPostData.payBasis === 'TOTAL_DAYS' && newJobPostData.workDays === '') {
			rq.msgError('총 일수를 선택해주세요.');
			return;
		}
		if (newJobPostData.payBasis === 'TOTAL_HOURS' && newJobPostData.workTime === '') {
			rq.msgError('총 시간을 입력해주세요.');
			return;
		}

		if (newJobPostData.payBasis === 'TOTAL_HOURS') {
			newJobPostData.workDays = '1'; // 총 시간 선택 시, 일수 기본값 1로 설정
		} else if (newJobPostData.payBasis === 'TOTAL_DAYS') {
			newJobPostData.workTime = '0'; // 총 일수 선택 시, 시간 기본값 0으로 설정
		} else {
			rq.msgError('대금 지급 기준을 선택해주세요.');
			return;
		}

		newJobPostData.categoryId = selectedCategoryId; // 선택된 카테고리 ID 설정

		const response = await rq.apiEndPoints().POST('/api/job-posts', { body: newJobPostData });

		if (response.data?.resultType === 'SUCCESS') {
			rq.msgAndRedirect({ msg: '글 작성 완료' }, undefined, '/');
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else if (response.data?.resultType === 'VALIDATION_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('글 작성 중 오류가 발생했습니다.');
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
					newJobPostData.location = data.roadAddress;
				}
			}).open();
		};
		document.body.appendChild(script);
	}
</script>

<div class="flex items-center justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<h2 class="text-2xl font-bold text-center text-green6 mb-10">공고 작성</h2>
			<form on:submit|preventDefault={writeJobPost}>
				<div class="flex gap-4">
					<div class="form-group flex-1">
						<label class="label" for="gender">성별</label>
						<select
							class="input input-bordered w-full"
							id="gender"
							bind:value={newJobPostData.gender}
						>
							<option value="UNDEFINED">무관</option>
							<option value="MALE">남성</option>
							<option value="FEMALE">여성</option>
						</select>
					</div>
					<div class="form-group flex-1">
						<label class="label" for="minAge">최소 나이</label>
						<input
							type="number"
							id="minAge"
							class="input input-bordered w-full"
							placeholder="나이를 입력해주세요."
							bind:value={newJobPostData.minAge}
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="label" for="deadLine">* 마감 기한</label>
					<input
						type="date"
						id="deadLine"
						class="input input-bordered w-full"
						bind:value={newJobPostData.deadLine}
					/>
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
							bind:value={newJobPostData.location}
							readonly
						/>
						<!-- 다음 주소 API 팝업 열기 버튼 -->
						<button
							class="btn btn bg-green3 text-white hover:bg-green6 ml-3"
							style="padding: 6px 7px; font-size: 14px;"
							on:click={openDaumAddressPopup}
							type="button">주소 검색</button
						>
					</div>
				</div>
				<div class="divider mt-10"></div>

				<div class="form-group">
					<label class="label" for="category">* 카테고리 선택</label>
					<select class="input input-bordered w-full" id="category" bind:value={selectedCategoryId}>
						<option value="" disabled selected>- 선택하세요 -</option>
						{#each subCategories as category}
							<option value={category.id}>{category.name}</option>
						{/each}
					</select>
				</div>

				<div class="form-group">
					<label class="label" for="title">* 제목</label>
					<input
						type="text"
						id="title"
						class="input input-bordered w-full"
						bind:value={newJobPostData.title}
					/>
				</div>
				<div class="form-group">
					<label class="label" for="body">* 내용</label>
					<textarea
						id="body"
						class="textarea textarea-bordered h-40 w-full"
						bind:value={newJobPostData.body}
						placeholder="내용을 입력해주세요."
					></textarea>
				</div>

				<div class="form-group">
					<label class="label" for="jobStartDate">* 시작 일자</label>
					<input
						type="date"
						id="jobStartDate"
						class="input input-bordered w-full"
						bind:value={newJobPostData.jobStartDate}
					/>
				</div>

				<div class="divider mt-10"></div>

				<div class="form-group">
					<span class="label">* 대금 지급 기준</span>
					<div class="flex items-center justify-start space-x-4">
						<div class="form-control">
							<label class="label cursor-pointer flex items-center space-x-2">
								<input
									type="radio"
									value="TOTAL_HOURS"
									bind:group={newJobPostData.payBasis}
									name="payBasis"
									class="radio checked:bg-blue-500"
								/>
								<span class="label-text">총 시간</span>
							</label>
						</div>
						<div class="form-control">
							<label class="label cursor-pointer flex items-center space-x-2">
								<input
									type="radio"
									value="TOTAL_DAYS"
									bind:group={newJobPostData.payBasis}
									name="payBasis"
									class="radio checked:bg-blue-500"
								/>
								<span class="label-text">총 일수</span>
							</label>
						</div>
					</div>
				</div>

				{#if newJobPostData.payBasis === 'TOTAL_HOURS'}
					<div class="form-group">
						<label class="label" for="workTime">* 총 시간 입력</label>
						<input
							type="number"
							id="workTime"
							class="input input-bordered w-full"
							min="1"
							max="24"
							placeholder="예) 3"
							bind:value={newJobPostData.workTime}
						/>
					</div>
				{:else if newJobPostData.payBasis === 'TOTAL_DAYS'}
					<div class="form-group">
						<label class="label" for="workDays">* 총 일수 선택 </label>
						<select
							class="input input-bordered w-full"
							id="workDays"
							bind:value={newJobPostData.workDays}
						>
							<option value="" disabled selected>- 선택하세요 -</option>
							{#each Array(7) as _, i}
								<option value={i + 1}>{i + 1}일</option>
							{/each}
						</select>
					</div>
				{/if}
				<div class="form-group">
					<label class="label" for="cost">* 대금</label>
					<input
						type="number"
						id="cost"
						class="input input-bordered w-full"
						min="0"
						placeholder="예) 30000"
						bind:value={newJobPostData.cost}
					/>
				</div>
				<div class="form-group">
					<label class="label" for="wagePaymentMethod">* 대금 지급 방법</label>
					<select
						class="input input-bordered w-full"
						id="wagePaymentMethod"
						bind:value={newJobPostData.wagePaymentMethod}
					>
						<option value="" disabled selected>- 선택하세요 -</option>
						<option value="SERVICE_PAYMENT">서비스 결제</option>
						<option value="INDIVIDUAL_PAYMENT">개인 지급</option>
					</select>
				</div>
				<div class="form-group">
					<button class="w-full btn btn btn bg-green3 text-white hover:bg-green6 my-3" type="submit"
						>글 작성</button
					>
				</div>
			</form>
		</div>
	</div>
</div>

<style>
	.bg-base-100 {
		background-color: #fdfffe;
	}
	.form-group {
		margin-bottom: 10px;
	}
</style>
