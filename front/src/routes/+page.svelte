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
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: { itemPage } }}
	<div class="flex justify-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div class="py-5">
				{#each posts ?? [] as post, index}
					<a href="/job-post/{post.id}" class="block">
						<div class="card relative bg-base-100 shadow-xl my-4">
							<div class="card-body">
								<div class="flex items-center justify-between">
									<div class="flex items-center">
										<div class="flex flex-col mr-10">
											<div class="text-bold text-gray-500 mb-1">{post.author}</div>
											<div class="flex flex-col">
												<div class="font-bold">{post.title}</div>
												<div class="text-xs text-gray-500">{post.location}</div>
												<div class="flex mt-2">
													<div class="flex">
														<div class="text-xs text-gray-500">조회</div>
														<div class="text-xs mx-2">{post.incrementViewCount}</div>
													</div>
													<div class="flex">
														<div class="text-xs text-gray-500">댓글</div>
														<div class="text-xs mx-2">{post.commentsCount}</div>
													</div>
													<div class="flex">
														<div class="text-xs text-gray-500">찜</div>
														<div class="text-xs mx-2"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="flex items-center justify-between">
										<div class="flex flex-col items-center">
											{#if post.closed}
												<div class="badge badge-neutral">마감</div>
											{:else}
												<div class="badge badge-primary my-1">구인중</div>
												<div class="text-xs text-gray-500">마감기한</div>
												<div class="text-xs text-gray-500">{post.deadLine}</div>
											{/if}
										</div>
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
