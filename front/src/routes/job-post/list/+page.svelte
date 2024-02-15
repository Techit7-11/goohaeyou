<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/Pagination.svelte';
	let posts: components['schemas']['JobPostDto'][] = $state([]);

	function JobPostWritePage() {
		rq.goTo('/job-post');
	}

	let sortBy_: string = 'createdAt'; // 초기 정렬 기준
	let sortOrder_: string = 'desc'; // 초기 정렬 순서

	async function load() {
		const page_ = parseInt($page.url.searchParams.get('page') ?? '1');

		let sortByParam = sortBy_; // 정렬 기준
		let sortOrderParam = sortOrder_; // 정렬 순서

		// '지원자 많은 순'인 경우 추가로 'id'를 2차 정렬 기준으로 설정
		if (sortBy_ === 'applicationCount' && sortOrder_ === 'desc') {
			sortByParam = `${sortBy_},id`;
			sortOrderParam = `${sortOrder_},asc`;
		}

		// '댓글 많은 순'인 경우 추가로 'id'를 2차 정렬 기준으로 설정
		if (sortBy_ === 'commentsCount' && sortOrder_ === 'desc') {
			sortByParam = `${sortBy_},id`;
			sortOrderParam = `${sortOrder_},asc`;
		}

		// '마감 빠른 순'인 경우 추가로 'applicationCount'을 2차 정렬 기준으로 설정
		if (sortBy_ === 'deadline' && sortOrder_ === 'asc') {
			sortByParam = `${sortBy_},applicationCount`;
			sortOrderParam = `${sortOrder_},desc`;
		}

		const { data } = await rq.apiEndPoints().GET('/api/job-posts/sort', {
			params: {
				query: {
					page: page_,
					sortBy: sortByParam,
					sortOrder: sortOrderParam
				}
			}
		});

		posts = data!.data.itemPage.content;

		return data!;
	}

	function handleSortSelect(event) {
		const selectedValue = event.target.value;
		const [sortBy, sortOrder] = selectedValue.split(' '); // 선택된 값에서 정렬 기준과 정렬 순서를 분리

		sortBy_ = sortBy;
		sortOrder_ = sortOrder;

		// 정렬 기준과 정렬 순서를 사용하여 load 함수 호출
		load();
	}
</script>

<div class="sort mx-auto w-80 mt-5">
	<select class="select select-bordered w-full max-w-xs" on:change={handleSortSelect}>
		<option value="createdAt desc">최신 등록 순</option>
		<option value="createdAt asc">오래된 순</option>
		<option value="applicationCount desc">지원자 많은 순</option>
		<option value="commentsCount desc">댓글 많은 순</option>
		<option value="incrementViewCount desc">조회수 높은 순</option>
		<option value="interestsCount desc">관심 많은 순</option>
		<option value="deadline asc">마감 빠른 순</option>
	</select>
</div>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: { itemPage } }}
	<div class="flex justify-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div>
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
														<div class="text-xs mx-2">{post.interestsCount}</div>
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

				<div class="max-w-sm mx-auto my-5">
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
