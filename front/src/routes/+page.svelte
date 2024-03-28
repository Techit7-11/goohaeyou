<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/Pagination.svelte';
	let posts: components['schemas']['JobPostDto'][] = $state([]);

	function JobPostWritePage() {
		rq.goTo('/job-post/write');
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

<svelte:head>
	<title>구해유</title>
</svelte:head>

<div class="sort mx-auto w-80 mt-5">
	<select class="select select-ghost w-full max-w-xs" on:change={handleSortSelect}>
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
			<div class="w-full max-w-4xl mx-auto">
				{#each posts ?? [] as post, index}
					<a href="/job-post/{post.id}" class="block">
						<div class="card relative bg-base-100 shadow-xl my-4">
							<div class="card-body flex flex-row justify-between">
								<div>
									<div class="text-bold text-gray-500 mb-1">{post.author}</div>
									<div class="text-xs text-gray-500">{post.location}</div>
								</div>
								<div class="flex justify-center items-center">
									<svg
										xmlns="http://www.w3.org/2000/svg"
										id="Outline"
										viewBox="0 0 24 24"
										width="12"
										height="12"
										><path
											d="M7,24a1,1,0,0,1-.71-.29,1,1,0,0,1,0-1.42l8.17-8.17a3,3,0,0,0,0-4.24L6.29,1.71A1,1,0,0,1,7.71.29l8.17,8.17a5,5,0,0,1,0,7.08L7.71,23.71A1,1,0,0,1,7,24Z"
										/></svg
									>
								</div>
							</div>
							<div
								class="bg-lime-500 h-60 text-white text-2xl text-shadow flex items-center justify-center"
							>
								<p>{post.title}</p>
							</div>
							<div class="card-body">
								<div class="flex items-center justify-between">
									<div class="flex items-center">
										<div class="flex flex-col mr-10">
											<div
												class="flex flex-col max-w-40 sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl overflow-hidden"
											>
												<div class="text-xl font-bold truncate">{post.title}</div>
												<div class="flex mt-2">
													<div class="flex-shrink">
														<div class="text-xs mx-2 flex justify-center items-center">
															{post.incrementViewCount}
														</div>
														<div class="text-xs text-gray-500">봤어유</div>
													</div>
													<div class="flex-shrink ml-3">
														<div class="text-xs mx-2 flex justify-center items-center">
															{post.commentsCount}
														</div>
														<div class="text-xs text-gray-500">쑥덕쑥덕</div>
													</div>
													<div class="flex-shrink ml-3">
														<div class="text-xs mx-2 flex justify-center items-center">
															{post.interestsCount}
														</div>
														<div class="text-xs text-gray-500">관심있슈</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="flex items-center justify-between">
										<div class="flex flex-col items-center">
											{#if post.closed}
												<div class="badge badge-neutral">구했어유</div>
											{:else}
												<div class="badge badge-primary my-1">구해유</div>
												<div class="text-xs text-gray-500">~ {post.deadLine}</div>
											{/if}
										</div>
									</div>
								</div>
							</div>
						</div>
					</a>
				{/each}
				<div class="max-w-4xl mx-auto my-5">
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
	.card:hover {
		background-color: oklch(0.95 0.05 128.99 / 0.3);
	}

	/* 제목 형광펜 스타일 */
	/*.card:hover .truncate {
		background: linear-gradient(to top, oklch(0.77 0.2 132.02 / 0.72) 50%, transparent 50%);
	}*/

	.badge-primary {
		border-color: oklch(0.77 0.2 132.02); /* 테두리 색상 설정 */
		background-color: oklch(0.77 0.2 132.02); /* 배경 색상 설정 */
		color: white;
	}

	.badge-neutral {
		border-color: oklch(0.57 0.02 72.86); /* 테두리 색상 설정 */
		background-color: oklch(0.57 0.02 72.86); /* 배경 색상 설정 */
		color: white;
	}

	.btn-primary {
		border-color: oklch(0.77 0.2 132.02); /* 테두리 색상 설정 */
		background-color: oklch(0.77 0.2 132.02); /* 배경 색상 설정 */
		color: white;
	}

	.select-bordered {
		background-color: oklch(0.98 0 0); /* 배경색 설정 */
		border: 1px solid oklch(0.77 0.2 132.02); /* 테두리를 1px 두께로 설정하고 검은색(#000)으로 색상을 지정합니다. */
	}
	.text-shadow {
		text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
	}
</style>
