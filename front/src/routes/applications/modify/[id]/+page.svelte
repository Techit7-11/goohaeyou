<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	// 수정할 내용을 담을 객체
	let applicationData = {
		body: ''
	};

	// 페이지가 로드되면 기존 데이터를 가져와 폼에 채운다
	async function loadApplicationData() {
		try {
			const applicationId = parseInt($page.params.id); // 수정할 지원서의 ID
			const { response } = await rq.apiEndPoints().GET(`/api/applications/${applicationId}`); // 지원서의 내용

			// 가져온 데이터를 applicationData 객체에 할당
			if (response.data) {
				applicationData = response.data;
			} else {
				console.error('지원서 데이터를 가져올 수 없습니다.');
			}
		} catch (error) {
			console.error('지원서를 로드하는 중 오류가 발생했습니다:', error.message);
		}
	}

	async function submitChanges() {
		const applicationId = parseInt($page.params.id);
		const response = await rq.apiEndPoints().PUT(`/api/applications/${applicationId}`, {
			body: {
				body: applicationData.body
			}
		});

		if (response.data?.msg == 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect(
				{ msg: response.data?.data?.message },
				undefined,
				`/applications/detail/${applicationId}`
			);
		}
		if (response.data?.statusCode === 204) {
			rq.msgAndRedirect({ msg: '수정 완료' }, undefined, `/applications/detail/${applicationId}`);
		} else {
			rq.msgAndRedirect(
				undefined,
				{ msg: '지원서를 수정하는 중 오류가 발생했습니다.' },
				`/applications/detail/${applicationId}`
			);
		}
	}
</script>

{#await loadApplicationData() then}
	<form on:submit|preventDefault={submitChanges} style="margin-bottom: 30px;">
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
			<textarea class="textarea" bind:value={applicationData.body} style="height: 150px;" required
			></textarea>
		</div>
		<div class="form-control mt-5">
			<button type="submit" class="btn btn-primary">수정 완료</button>
		</div>
	</form>
{:catch error}
	<p>지원서를 로드하는 중 오류가 발생했습니다: {error.message}</p>
{/await}
