<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/Pagination.svelte';
	let posts: components['schemas']['JobPostDto'][] = $state([]);

	async function load() {
		const page_ = parseInt($page.url.searchParams.get('page') ?? '1');

		const { data } = await rq.apiEndPoints().GET('/api/job-posts/sort', {
			params: {
				query: {
					page: page_
				}
			}
		});

		posts = data!.data.itemPage.content;

		return data!;
	}
</script>

{#await load()}
	<span class="loading loading-spinner loading-lg"></span>
{:then { data: { itemPage } }}
	<ul>
		{#each posts ?? [] as post, index}
			<li>
				<a href="/job-post/{post.id}">(No.{index + 1}) {post.title}</a>
				<a href="/job-post/{post.id}">작성자 : {post.author}</a>
				<a href="/job-post/{post.id}">{post.location}</a>
				<a href="/job-post/{post.id}">
					{#if post.closed}
						<div class="badge badge-neutral">마감</div>
					{:else}
						<div class="badge badge-primary">구인중</div>
					{/if}
				</a>
			</li>
		{/each}
	</ul>
	<Pagination page={itemPage} />
	{#if page.totalPages > 0}
    <div class="flex justify-center my-3">
      <ul class="flex list-none">
        <!-- 이전 페이지 버튼 -->
        <li class={`btn ${page.number === 0 ? 'btn-disabled' : ''}`}>
          <a on:click={() => goToPage(page.number - 1)}>이전</a>
        </li>

        <!-- 페이지 번호 버튼들 -->
        {#each Array(page.totalPages) as _, pageIndex}
          {#if pageIndex >= page.number - 5 && pageIndex <= page.number + 5}
          <li class={`btn ${pageIndex === page.number ? 'btn-active' : ''}`}>
            <a on:click={() => goToPage(pageIndex)}>{pageIndex + 1}</a>
          </li>
          {/if}
        {/each}

        <!-- 다음 페이지 버튼 -->
        <li class={`btn ${page.number === page.totalPages - 1 ? 'btn-disabled' : ''}`}>
          <a on:click={() => goToPage(page.number + 1)}>다음</a>
        </li>
      </ul>
    </div>
    {/if}
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
		margin: 12px 0;
		padding: 10px;
		padding-left: 20px;
		width: 80%; /* 화면 너비의 대부분을 차지 */
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
		margin-bottom: 5px; /* 요소 사이의 여백 */
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
