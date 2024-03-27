<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	// 수정할 내용을 담을 객체
	let applicationData = {
		body: ''
	};

	let promise = loadApplicationData();

	// 페이지가 로드되면 기존 데이터를 가져와 폼에 채우기
	async function loadApplicationData() {
		const applicationId = parseInt($page.params.id);
		try {
			const response = await rq.apiEndPoints().GET(`/api/applications/${applicationId}`);
			if (response.data) {
				applicationData = response.data.data;
			}
		} catch (error) {
			console.error('지원서를 로드하는 중 오류가 발생했습니다:', error.message);
			throw error;
		}
	}

	async function submitChanges() {
		const applicationId = parseInt($page.params.id);
		const response = await rq.apiEndPoints().PUT(`/api/applications/${applicationId}`, {
			body: {
				body: applicationData.body
			}
		});

		if (response.data?.resultType === 'SUCCESS') {
			rq.msgAndRedirect({ msg: '수정 완료' }, undefined, `/applications/detail/${applicationId}`);
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect(
				{ msg: response.data?.message },
				undefined,
				`/applications/detail/${applicationId}`
			);
		} else if (response.data?.resultType === 'VALIDATION_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgAndRedirect(
				undefined,
				{ msg: '지원서를 수정하는 중 오류가 발생했습니다.' },
				`/applications/detail/${applicationId}`
			);
		}
	}
</script>

{#await promise then}
	<form on:submit|preventDefault={submitChanges} style="margin-bottom: 30px;">
		<div class="flex justify-center items-center min-h-screen bg-base-100">
			<div class="container mx-auto px-4">
				<div class="w-full max-w-4xl mx-auto">
					<div class="form-control">
						<div class="flex items-center">
							<div class="flex items-center" style="padding-left: 10px;">
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
											d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L6.832 19.82a4.5 4.5 0 0 1-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 0 1 1.13-1.897L16.863 4.487Zm0 0L19.5 7.125"
										/>
									</svg>
								</div>
								<label class="label">수정 내용</label>
							</div>
						</div>
						<textarea
							class="textarea textarea-bordered"
							bind:value={applicationData.body}
							style="height: 150px;"
							required
						></textarea>
					</div>
					<div class="max-w-4xl mx-auto my-5">
						<button type="submit" class="w-full btn btn-primary my-5">수정 완료</button>
					</div>
				</div>
			</div>
		</div>
	</form>
{:catch error}
	<p>지원서를 로드하는 중 오류가 발생했습니다: {error.message}</p>
{/await}


<style>
	.btn-primary {
		border-color: oklch(0.77 0.2 132.02); /* 테두리 색상 설정 */
		background-color: oklch(0.77 0.2 132.02); /* 배경 색상 설정 */
		color: white;
	}
</style>