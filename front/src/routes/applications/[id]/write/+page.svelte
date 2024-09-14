<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	// 지원서 데이터
	let applicationsData = {
		applicationBody: ''
	};

	async function loadjobPostData() {
		if (rq.member.name == null) {
			rq.msgAndRedirect(undefined, { msg: '회원정보 입력이 필요합니다.' }, '/member/social/modify');
		} else if (!rq.member.authenticated) {
			rq.msgAndRedirect(
				{ msg: '서비스를 이용하기 위해 먼저 본인 인증을 진행해주세요.' },
				undefined,
				'/member/me'
			);
		}

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

		if (response.data?.resultType === 'SUCCESS') {
			rq.msgAndRedirect({ msg: '지원 완료' }, undefined, `/job-post/${postId}`);
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else if (response.data?.resultType === 'VALIDATION_EXCEPTION') {
			rq.msgError(response.data?.message);
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
	<div class="flex justify-center items-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4 mt-5">
			<div class="p-6 max-w-4xl w-full mx-auto my-10 bg-white rounded-box shadow-lg">
				<div>{jobPostDetailDto?.author}</div>
				<div class="flex justify-between items-center">
					<div class="title-text text-xl font-bold">{jobPostDetailDto?.title}</div>
				</div>
				<div class="divider"></div>
				<div class="grid grid-cols-4 gap-3 my-4">
					<div class="text-sm">모집 상태 :</div>
					<div>
						<div class="text-sm {jobPostDetailDto?.closed ? 'text-rose-600' : 'text-emerald-700'}">
							{jobPostDetailDto?.closed ? '마감' : '구인중'}
						</div>
					</div>
					<div class="text-sm">지원 가능 여부 :</div>
					<div class="text-sm">
						{#if memberAge !== '정보 없음' && memberAge >= jobPostDetailDto.minAge && (jobPostDetailDto.gender === 'UNDEFINED' || jobPostDetailDto.gender === rq.member.gender)}
							<div class="text-sm text-emerald-700">가능</div>
						{:else}
							<div class="text-sm text-rose-600">불가능</div>
						{/if}
					</div>
					<div class="text-sm">성별 구분 :</div>
					<div class="text-sm">
						{jobPostDetailDto?.gender === 'MALE'
							? '남'
							: jobPostDetailDto?.gender === 'FEMALE'
								? '여'
								: '무관'}
					</div>
					<div class="text-sm">지원 가능 나이 :</div>
					<div class="text-sm">
						{jobPostDetailDto?.minAge === 0 ? '없음' : jobPostDetailDto?.minAge ?? '없음'}
					</div>
				</div>
				<div class="text-sm">위치 : {jobPostDetailDto?.location}</div>
				<div class="divider"></div>
				<div
					class="p-4 mt-4 text-gray-700 bg-white rounded-lg shadow border border-gray-200 body-text"
				>
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
		</div>
	</div>
{/await}

<style>
	.p-6.max-w-4xl.w-full.mx-auto.my-10.bg-white.rounded-box.shadow-lg {
		border: 1px solid oklch(0.77 0.2 132.02);
	}

	/* 제목 형광펜 스타일 */
	.title-text {
		background: linear-gradient(to top, oklch(0.77 0.2 132.02 / 0.62) 50%, transparent 60%);
	}

	.btn-primary {
		border-color: oklch(0.77 0.2 132.02); /* 테두리 색상 설정 */
		background-color: oklch(0.77 0.2 132.02); /* 배경 색상 설정 */
		color: white;
	}

	.body-text {
		background-color: #e3f4d2;
	}

	.grid-cols-4 {
		display: grid;
		grid-template-columns: 1.5fr 1fr 2fr 1fr; /* 두 번째 열을 첫 번째 열보다 2배 넓게 설정 */
	}
</style>
