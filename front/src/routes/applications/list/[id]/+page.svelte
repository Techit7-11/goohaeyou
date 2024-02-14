<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	async function loadApplications() {
		const { data } = await rq.apiEndPoints().GET('/api/employ/{postId}', {
			params: {
				path: {
					postId: parseInt($page.params.id)
				}
			}
		});

		return data;
	}

	function formatDate(dateString) {
		const options = {
			year: '2-digit',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit'
		};
		return new Date(dateString).toLocaleString('ko-KR', options);
	}

	function summarizeBody(body) {
		return body.length > 10 ? `${body.slice(0, 10)}...` : body;
	}
</script>

{#await loadApplications()}
	<p class="text-center">Loading...</p>
{:then { data: applications }}
	{#if applications.length > 0}
		<div class="flex flex-col items-center">
			<ul class="w-full max-w-4xl">
				{#each applications ?? [] as application, index}
					<li class="card bg-base-100 shadow-xl m-4">
						<div class="card-body">
							<h2 class="card-title">
								<a href={`/applications/detail/${application.id}`} class="link link-hover">
									지원서 번호 #{index + 1}
								</a>
							</h2>
							<p>지원자 ID: {application.author}</p>
							<p>지원내용: {summarizeBody(application.body)}</p>
							<p>지원일: {formatDate(application.createdAt)}</p>
							<p>
								승인 여부:
								{#if application.approve === true}
									<span class="badge badge-success">승인</span>
								{:else if application.approve === false}
									<span class="badge badge-error">미승인</span>
								{:else}
									<span class="badge badge-warning">진행중</span>
								{/if}
							</p>
						</div>
					</li>
				{/each}
			</ul>
		</div>
	{:else}
		<div class="text-center">해당 공고에 아직 지원자가 없습니다.</div>
	{/if}
{:catch error}
	<p>Error loading applications: {error.message}</p>
{/await}