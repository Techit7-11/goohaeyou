<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/Pagination.svelte';
	let posts: components['schemas']['JobPostDto'][] = $state([]);

	function JobPostWritePage() {
		rq.goTo('/job-post');
	}

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
	<div class="flex justify-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div class="py-5">

					{#each posts ?? [] as post, index}
						<a href="/job-post/{post.id}" class="card-link">
                            <div class="card relative bg-base-100 shadow-xl my-4">
                                <div class="card-body">
                                    <div class="flex justify-between">
                                        <div>
                                            <div class="font-bold">no.{index + 1}  {post.title}</div>
                                            <div class="text-sm text-gray-500">작성자 : {post.author}</div>
                                            <div class="text-sm text-gray-500">지역 : {post.location}</div>
                                        </div>
                                        <div class="absolute right-0 top-1/2 transform -translate-y-1/2">
                                            {#if post.closed}
                                                <div class="badge badge-neutral mx-5">마감</div>
                                            {:else}
                                                <div class="badge badge-primary mx-5">구인중</div>
                                            {/if}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </a>


					{/each}

				<div class="max-w-sm mx-auto">
					<button class="w-full btn btn-primary my-5" on:click={JobPostWritePage}>
						글 작성하기
					</button>
					<Pagination page={itemPage} />
				</div>
			</div>
		</div>
	</div>
{/await}

<style>
	a:hover {
		color: #a5a5a5; /* 호버 시 색상 변경 */
	}
</style>
