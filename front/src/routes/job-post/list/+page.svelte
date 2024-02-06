<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	if (rq.member.name === null) {
		rq.goTo('http://localhost:5173/member/social/modify');
	}

	async function load() {
		const { data } = await rq.apiEndPoints().GET('/api/job-posts', {});

		return data;
	}
</script>

{#await load()}
	<p>loading...</p>
{:then { data: jobPostDtoList }}
	<ul>
		{#each jobPostDtoList ?? [] as jobPostDto, index}
			<li>
				<a href="/job-post/{jobPostDto.id}">no.{index + 1}</a>
				<a href="/job-post/{jobPostDto.id}">{jobPostDto.title}</a>
				<a href="/job-post/{jobPostDto.id}">{jobPostDto.author}</a>
				<a href="/job-post/{jobPostDto.id}">{jobPostDto.createdAt}</a>
				<a href="/job-post/{jobPostDto.id}">
					{#if jobPostDto.closed}
						마감
					{:else}
						구인중
					{/if}
				</a>
			</li>
		{/each}
	</ul>
{/await}

<style>
	ul {
		list-style-type: none;
		padding: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	li {
		background-color: #ffffff;
		margin: 20px 0;
		padding: 20px;
		width: 50%; /* 화면 너비의 대부분을 차지 */
		max-width: 600px; /* 최대 너비 설정 */
		box-shadow: 0 6px 10px rgba(0, 0, 0, 0.1); /* 섬세한 그림자 효과 */
		border-radius: 8px; /* 부드럽게 둥근 모서리 */
		display: flex;
		flex-direction: column; /* 세로 정렬 */
		border: 1px solid #eee; /* 미세한 경계선 */
	}

	a {
		color: #43404e;
		text-decoration: none; /* 밑줄 제거 */
		font-weight: bold; /* 글씨 굵게 */
		margin-bottom: 8px; /* 요소 사이의 여백 */
	}

	a:hover {
		color: #a5a5a5; /* 호버 시 색상 변경 */
	}

	footer {
		width: 100%;
		background-color: #f7f7f7; /* 밝은 회색 배경 */
		color: #6f6d70;
		text-align: center;
		padding: 20px 0;
		box-shadow: 0 -4px 6px rgba(0, 0, 0, 0.1); /* 상단으로 그림자 효과 */
		border-top: 2px solid #eee; /* 상단 경계선 */
	}
</style>
