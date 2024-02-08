<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	// 지원서 데이터
	let applicationsData = {
		applicationBody: ''
	};

	async function load() {
		const { data } = await rq.apiEndPoints().GET('/api/job-posts/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			}
		});

		return data;
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
			rq.msgAndRedirect({ msg: '지원 완료' }, undefined, 'http://localhost:5173/job-post/list');
		}
	}
</script>

{#await load()}
	<div>Loading...</div>
{:then { data: jobPostDetailDto }}
	<div class="job-post-container">
		<div class="id-box">No.{jobPostDetailDto?.id}</div>
		<div class="title-box">{jobPostDetailDto?.title}</div>
	</div>
	<div class="content-box">{jobPostDetailDto?.body}</div>
	<form on:submit|preventDefault={writeApplications}>
		<div class="form-group">
			<input
				type="text"
				id="applicationBody"
				name="applicationBody"
				class="input input-bordered w-full"
				placeholder="내용을 입력하세요."
				required
			/>
		</div>
		<div class="apply-button" style="text-align: right;">
			<button class="btn" on:click={writeApplications}>지원하기</button>
		</div>
	</form>
{/await}
