<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';
	import { page } from '$app/stores';
	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/CategoryPagination.svelte';

	let categories: components['schemas']['CategoryDto'][] = [];
	let subCategories: components['schemas']['CategoryDto'][] = [];
	let posts: any = null;
	let selectedCategoryName = '';
	let isLeafCategory = false;
	let error = '';
	let currentPage = 1;
	let totalPages = 1;

	async function loadCategories() {
		try {
			const { data } = await rq.apiEndPoints().GET('/api/categories/top-level');
			categories = data?.data ?? [];
			if (categories.length === 0) {
				error = '카테고리가 없습니다.';
			}
		} catch (e) {
			console.error('Error loading categories:', e);
			error = '카테고리를 로드하는 중 오류가 발생했습니다.';
		}
	}

	async function loadSubCategories(categoryName: string) {
		try {
			const params = new URLSearchParams({ category_name: categoryName });
			const { data } = await rq.apiEndPoints().GET(`/api/categories/sub-categories?${params}`);
			subCategories = data?.data ?? [];
			isLeafCategory = subCategories.length === 0;
		} catch (e) {
			console.error('Error loading subcategories:', e);
			error = '하위 카테고리를 로드하는 중 오류가 발생했습니다.';
		}
	}

	async function loadPosts(categoryName: string, page: number = 1) {
		const params = new URLSearchParams({
			'category-name': categoryName,
			page: String(page)
		});

		const response = await rq.apiEndPoints().GET(`/api/job-posts/by-category?${params}`);

		if (response.data?.resultType === 'SUCCESS') {
			posts = response.data.data ?? [];
			currentPage = posts.number + 1;
			totalPages = posts.totalPages;
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('글 목록을 로드하는 중 오류가 발생했습니다.');
		}
	}

	async function handleCategoryClick(category) {
		selectedCategoryName = category.name;
		await loadSubCategories(category.name);
		if (isLeafCategory) {
			await loadPosts(category.name);
		}
	}

	function handlePageChange(event) {
		const newPage = event.detail;
		loadPosts(selectedCategoryName, newPage);
	}

	async function initialize() {
		try {
			await loadCategories();
			if (categories.length > 0) {
				selectedCategoryName = categories[0].name;
				await handleCategoryClick(categories[0]);
			}
		} catch (e) {
			console.error('Error during load:', e);
			error = '데이터를 로드하는 중 오류가 발생했습니다.';
		}
	}

	onMount(() => {
		initialize();
	});
</script>

<svelte:head>
	<title>구해유 - 카테고리</title>
</svelte:head>

<div class="flex items-center justify-center min-h-screen">
	{#if error}
		<p class="text-red-500">{error}</p>
	{:else}
		<div class="flex flex-col items-center p-4 space-y-4">
			<div class="menu bg-base-200 w-full rounded-box p-4 shadow-lg">
				{#if categories.length > 0}
					{#each categories as category}
						<details class="mb-2">
							<summary
								on:click={() => handleCategoryClick(category)}
								class="cursor-pointer bg-primary text-white rounded-md p-2"
							>
								{category.name}
							</summary>
							{#if subCategories.length > 0 && selectedCategoryName === category.name}
								<ul class="list-disc list-inside pl-4 space-y-2 mt-2">
									{#each subCategories as subCategory}
										<li>
											<a
												href="#"
												on:click|preventDefault={() => handleCategoryClick(subCategory)}
												class="text-gray-900 hover:underline"
											>
												{subCategory.name}
											</a>
										</li>
									{/each}
								</ul>
							{/if}
						</details>
					{/each}
				{:else}
					<p class="text-center p-4 text-red-500">카테고리가 없습니다.</p>
				{/if}
			</div>

			<!-- 글 목록이 나타나는 부분 -->
			<div class="flex justify-center min-h-screen bg-base-100 w-full">
				<div class="container mx-auto px-4">
					<!-- 글 목록의 너비 설정 -->
					<div class="w-full max-w-4xl mx-auto">
						{#if posts === null}
							<p>카테고리를 선택해주세요</p>
						{:else if isLeafCategory && posts?.content?.length === 0}
							<p class="text-center p-4 text-500">해당 카테고리에 글이 없습니다.</p>
						{:else if posts?.content?.length > 0}
							<h2 class="text-lg font-bold mb-4">카테고리 > {selectedCategoryName}</h2>
							<!-- 글 목록을 그리드 레이아웃으로 배치 -->
							<div class="grid grid-cols-1 md:grid-cols-2 gap-3">
								{#each posts.content as post}
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
													>
														<path
															d="M7,24a1,1,0,0,1-.71-.29,1,1,0,0,1,0-1.42l8.17-8.17a3,3,0,0,0,0-4.24L6.29,1.71A1,1,0,0,1,7.71.29l8.17,8.17a5,5,0,0,1,0,7.08L7.71,23.71A1,1,0,0,1,7,24Z"
														/>
													</svg>
												</div>
											</div>
											<div class="card-body pt-1">
												{#if post.mainImageUrl}
													<!-- 메인 이미지가 존재하는 경우 -->
													<div class="flex">
														<div class="flex flex-col">
															<img
																src={post.mainImageUrl}
																alt={post.title}
																class="w-20 h-20 object-cover mb-1"
															/>
															<div class="flex mb-2">
																<div class="flex flex-col items-center mr-2">
																	<div class="text-xs mx-2 flex justify-center items-center">
																		{post.incrementViewCount}
																	</div>
																	<div class="text-xs text-gray-500">봤어유</div>
																</div>
																<div class="flex flex-col items-center mr-2">
																	<div class="text-xs mx-2 flex justify-center items-center">
																		{post.commentsCount}
																	</div>
																	<div class="text-xs text-gray-500">쑥덕쑥덕</div>
																</div>
																<div class="flex flex-col items-center">
																	<div class="text-xs mx-2 flex justify-center items-center">
																		{post.interestsCount}
																	</div>
																	<div class="text-xs text-gray-500">관심있슈</div>
																</div>
															</div>
														</div>
														<div class="flex flex-col flex-1">
															<div class="text-xl font-bold line-clamp-2">
																{post.title}
															</div>
															<div class="flex items-center justify-between mt-2">
																<div class="flex flex-col items-center flex-nowrap ml-auto">
																	{#if post.closed}
																		<div class="badge badge-neutral whitespace-nowrap">
																			구했어유
																		</div>
																	{:else}
																		<div class="badge badge-primary my-1 whitespace-nowrap">
																			구해유
																		</div>
																		<div class="text-xs text-gray-500 whitespace-nowrap">
																			~ {post.deadLine}
																		</div>
																	{/if}
																</div>
															</div>
														</div>
													</div>
												{:else}
													<!-- 메인 이미지가 존재하지 않는 경우 -->
													<div class="flex items-center justify-between">
														<div
															class="flex flex-col max-40 sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl overflow-hidden"
														>
															<div class="text-xl font-bold max-w-full line-clamp-2">
																{post.title}
															</div>
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
														<div class="flex items-center justify-between min-w-[77px] ml-4">
															<div class="flex flex-col items-center flex-nowrap">
																{#if post.closed}
																	<div class="badge badge-neutral whitespace-nowrap">구했어유</div>
																{:else}
																	<div class="badge badge-primary my-1 whitespace-nowrap">
																		구해유
																	</div>
																	<div class="text-xs text-gray-500 whitespace-nowrap">
																		~ {post.deadLine}
																	</div>
																{/if}
															</div>
														</div>
													</div>
												{/if}
											</div>
										</div>
									</a>
								{/each}
							</div>
							<div class="flex justify-center mt-4">
								<Pagination {currentPage} {totalPages} on:pageChange={handlePageChange} />
							</div>
						{/if}
					</div>
				</div>
			</div>
		</div>
	{/if}
</div>

<style>
	.menu details[open] > summary {
		cursor: pointer;
	}

	.card:hover {
		background-color: rgba(245, 245, 245, 0.5);
	}

	.badge-primary {
		border-color: oklch(0.77 0.2 132.02);
		background-color: oklch(0.77 0.2 132.02);
		color: white;
	}

	.badge-neutral {
		border-color: oklch(0.57 0.02 72.86);
		background-color: oklch(0.57 0.02 72.86);
		color: white;
	}

	.btn-primary {
		border-color: oklch(0.77 0.2 132.02);
		background-color: oklch(0.77 0.2 132.02);
		color: white;
	}

	.select-bordered {
		background-color: oklch(0.98 0 0);
		border: 1px solid oklch(0.77 0.2 132.02);
	}

	.text-shadow {
		text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
	}
</style>
