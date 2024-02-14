<script lang="ts">
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	let jobPostData = {
		title: '',
		body: '',
		minAge: '',
		gender: 'UNDEFINED',
		location: '',
		deadLine: ''
	};
	let postId;

	onMount(async () => {
            postId = parseInt($page.params.id);
            console.log('Mounted - postId:', postId);
            if (!isNaN(postId)) {
                console.log('Valid postId, loading job post detail...');
                await loadJobPostDetail(postId);
            }
        });

	async function loadJobPostDetail(postId) {
            try {
                console.log(`Loading job post detail for postId: ${postId}`);
                const { data } = await rq.apiEndPoints().GET(`/api/job-posts/${postId}`);
                console.log('Job post data loaded:', data);
                if (data) {
                    jobPostData = { ...jobPostData, ...data.data };
                    console.log('jobPostData updated:', jobPostData);
                }
            } catch (error) {
                console.error('Error loading job post detail:', error);
            }
        }
	// 공고 수정 제출 함수
	async function submitForm() {
		const response = await rq.apiEndPoints().PUT(`/api/job-posts/${postId}`, { body: jobPostData });
		if (response.data?.statusCode === 200) {
			rq.msgAndRedirect({ msg: '글 수정 완료' }, undefined, '/');
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			const customErrorMessage = response.data?.data?.message;
			rq.msgError(customErrorMessage ?? '알 수 없는 오류가 발생했습니다.');
		} else if (response.data?.msg === 'VALIDATION_EXCEPTION') {
			if (Array.isArray(response.data.data)) {
				response.data.data.forEach((msg) => rq.msgError(msg));
			}
		} else {
			rq.msgError('글 수정 중 오류가 발생했습니다.');
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
					jobPostData.location = data.roadAddress;
				}
			}).open();
		};
		document.body.appendChild(script);
	}
</script>

<div class="flex items-center justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<h2 class="text-2xl font-bold text-center mb-10">공고 수정</h2>
			<form on:submit|preventDefault={submitForm}>
				<div class="flex gap-4">
					<div class="form-group flex-1">
						<label class="label" for="gender">성별</label>
						<select class="input input-bordered w-full" id="gender" bind:value={jobPostData.gender}>
							<option value="UNDEFINED">무관</option>
							<option value="MALE">남성</option>
							<option value="FEMALE">여성</option>
						</select>
					</div>
					<div class="form-group flex-1">
						<label class="label" for="minAge">최소 나이</label>
						<input
							type="text"
							id="minAge"
							class="input input-bordered w-full"
							placeholder="나이를 입력해주세요."
							bind:value={jobPostData.minAge}
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="label" for="deadLine">* 마감 기한</label>
					<input
						type="date"
						id="deadLine"
						class="input input-bordered w-full"
						bind:value={jobPostData.deadLine}
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
							bind:value={jobPostData.location}
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
				<div class="divider mt-10"></div>
				<div class="form-group">
					<label class="label" for="title">* 제목</label>
					<input
						type="text"
						id="title"
						class="input input-bordered w-full"
						bind:value={jobPostData.title}
						placeholder="제목을 입력해주세요."
					/>
				</div>
				<div class="form-group">
					<label class="label" for="body">* 내용</label>
					<textarea
						id="body"
						class="textarea textarea-bordered h-40 w-full"
						bind:value={jobPostData.body}
						placeholder="내용을 입력해주세요."
					></textarea>
				</div>
				<div class="form-group">
					<button class="w-full btn btn-primary my-3" type="submit">글 수정</button>
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
