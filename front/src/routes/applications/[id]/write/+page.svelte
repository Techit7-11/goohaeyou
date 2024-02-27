<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	// 지원서 데이터
	let applicationsData = {
		applicationBody: ''
	};

	async function loadjobPostData() {
		const { data } = await rq.apiEndPoints().GET('/api/job-posts/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			}
		});

		return data!.data!;
	}

	async function writeApplications(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		const postId = parseInt($page.params.id);

		const response = await rq.apiEndPoints().POST(`/api/applications/${postId}`, {
			body: {
				body: form.applicationBody.value.trim()
			}
		});

		console.log(postId);

		if (response.data?.statusCode === 201) {
			rq.msgAndRedirect({ msg: '지원 완료' }, undefined, `/job-post/${postId}`);
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			const customErrorMessage = response.data?.data?.message;
			rq.msgError(customErrorMessage);
		} else if (response.data?.msg === 'VALIDATION_EXCEPTION') {
			if (Array.isArray(response.data.data)) {
				rq.msgError(response.data.data[0]);
			}
		} else {
			rq.msgError('지원서를 작성하는 중 오류가 발생했습니다.');
		}
	}

	function calculateAge(birthDate: string): number {
		const today = new Date();
		const birth = new Date(birthDate);
		let age = today.getFullYear() - birth.getFullYear();
		const m = today.getMonth() - birth.getMonth();
		if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
			age--;
		}
		return age;
	}

	$: memberAge = rq.member && rq.member.birth ? calculateAge(rq.member.birth) : '정보 없음';
</script>

{#await loadjobPostData() then jobPostDetailDto}
	<div class="p-6 max-w-4xl mx-auto my-10 bg-white rounded-box shadow-lg">
		<div>{jobPostDetailDto?.author}</div>
		<div class="flex justify-between items-center">
			<div class="text-xl font-bold">{jobPostDetailDto?.title}</div>
		</div>
		<div class="divider"></div>
		<div class="grid grid-cols-4 gap-4 my-4">
			<div class="text-sm">모집 상태 :</div>
			<div>
				<span class="text-sm {jobPostDetailDto?.closed ? 'text-rose-600' : 'text-emerald-700'}">
					{jobPostDetailDto?.closed ? '마감' : '구인중'}
				</span>
			</div>
			<div class="text-sm">지원 가능 여부 :</div>
			<div class="text-sm">
				{#if memberAge !== '정보 없음' && memberAge >= jobPostDetailDto.minAge && (jobPostDetailDto.gender === 'UNDEFINED' || jobPostDetailDto.gender === rq.member.gender)}
					<div class="text-sm text-emerald-700">가능</div>
				{:else}
					<div class="text-sm text-rose-600">불가능</div>
				{/if}
			</div>
			<div class="text-sm">지원 가능 나이 :</div>
			<div class="text-sm">
				{jobPostDetailDto?.minAge === 0 ? '없음' : jobPostDetailDto?.minAge ?? '없음'}
			</div>
			<div class="text-sm">성별 구분 :</div>
			<div class="text-sm">
				{jobPostDetailDto?.gender === 'MALE'
					? '남'
					: jobPostDetailDto?.gender === 'FEMALE'
						? '여'
						: '무관'}
			</div>
		</div>
		<div class="text-sm">위치 : {jobPostDetailDto?.location}</div>
		<div class="divider"></div>
		<div class="p-4 mt-4 text-gray-700 bg-white rounded-lg shadow border border-gray-200">
			<div class="whitespace-pre-line">{jobPostDetailDto?.body}</div>
		</div>
		<div class="divider"></div>
		{#if memberAge !== '정보 없음' && memberAge >= jobPostDetailDto.minAge && (jobPostDetailDto.gender === 'UNDEFINED' || jobPostDetailDto.gender === rq.member.gender)}
			<form on:submit|preventDefault={writeApplications}>
				<div class="form-control w-full mt-4">
					<div class="flex items-center">
						<div class="flex items-center" style="padding-left: 5px;">
							<div class="mr-3">
								<svg
									xmlns="http://www.w3.org/2000/svg"
									fill="none"
									viewBox="0 0 24 24"
									stroke-width="1.5"
									stroke="currentColor"
									class="w-6 h-6"
								>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
									/>
								</svg>
							</div>
							<label class="label">지원서 작성</label>
						</div>
					</div>
					<textarea
						id="applicationBody"
						name="applicationBody"
						class="textarea textarea-bordered h-24 w-full"
						placeholder="내용을 입력하세요."
						required
						bind:value={applicationsData.applicationBody}
					></textarea>
				</div>
				<div class="form-control mt-5">
					<button type="submit" class="btn btn-primary">지원하기</button>
				</div>
			</form>
		{/if}
	</div>
{/await}
