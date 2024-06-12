<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/Pagination.svelte';
	let posts: components['schemas']['JobPostDto'][] = $state([]);

	import { createEventDispatcher } from 'svelte';

	let kwType: string = '';
	let kw: string = '';
	let dataLoaded: boolean = false; // 데이터가 로드되었는지 여부를 저장하는 변수 추가
	const dispatch = createEventDispatcher(); // 검색 버튼 클릭 이벤트를 디스패치하기 위한 함수

	async function loading() {
		dataLoaded = true;

		//load();
	}

	function handleKeyPress(event) {
		if (event.key === 'Enter') {
			load();
		}
	}

	async function load() {
		const kwInput = document.querySelector('input[name="kw"]') as HTMLInputElement;
		kw = kwInput.value;

		const kwTypeCheckboxes = document.querySelectorAll('input[name="kwType"]:checked');
		kwType = Array.from(kwTypeCheckboxes)
			.map((checkbox: HTMLInputElement) => checkbox.value)
			.join(',');

		const page_ = parseInt($page.url.searchParams.get('page') ?? '1');

		const { data } = await rq.apiEndPoints().GET('/api/job-posts/search-sort', {
			params: {
				query: {
					page: page_,
					kw,
					kwType
				}
			}
		});

		posts = data!.data.itemPage.content;

		return data!;
	}

	function all(event) {
		console.log('버튼 클릭');
	}
</script>

<div class="flex justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="search-box container flex justify-center items-center my-4 flex-col">
			<div class="join">
				<input
					name="kw"
					type="search"
					placeholder="검색어"
					class="join-item input w-full max-w-xs"
					value={$page.url.searchParams.get('kw') ?? ''}
					on:keyup={handleKeyPress}
				/>
				<button class="join-item-btn btn btn-ghost btn-circle mx-1" on:click={load}>
					<svg
						xmlns="http://www.w3.org/2000/svg"
						class="h-5 w-5"
						fill="none"
						viewBox="0 0 24 24"
						stroke="currentColor"
						><path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
						/></svg
					>
				</button>
			</div>
			<div class="filter">
				<label>
					<input type="checkbox" name="kwType" value="title" />
					<span>제목</span>
				</label>
				<label>
					<input type="checkbox" name="kwType" value="body" />
					<span>내용</span>
				</label>
				<label>
					<input type="checkbox" name="kwType" value="author" />
					<span>작성자</span>
				</label>
				<label>
					<input type="checkbox" name="kwType" value="location" />
					<span>주소</span>
				</label>
			</div>
			<div class="filter-closed">
				<select class="form-select">
					<option value="all">전체</option>
					<option value="open">공고중</option>
					<option value="closed">마감</option>
				</select>
			</div>

			<div class="filter-gender">
				<select class="form-select">
					<option value="all">무관</option>
					<option value="female">여성</option>
					<option value="male">남성</option>
				</select>
			</div>

			<div class="filter-age">
				<select class="form-select">
					<option value="all">전체</option>
					<option value="10s">10대</option>
					<option value="20s">20대</option>
					<option value="30s">30대</option>
					<option value="40s">40대</option>
					<option value="50s">50대 이상</option>
				</select>
			</div>

			<div class="filter-location">
				<button class="btn btn-filter">전국</button>
				<button class="btn btn-filter">서울</button>
				<button class="btn btn-filter">부산</button>
				<button class="btn btn-filter">대구</button>
				<button class="btn btn-filter">인천</button>
				<button class="btn btn-filter">광주</button>
				<button class="btn btn-filter">대전</button>
				<button class="btn btn-filter">울산</button>
				<button class="btn btn-filter">세종</button>
				<button class="btn btn-filter">경기</button>
				<button class="btn btn-filter">강원</button>
				<button class="btn btn-filter">충북</button>
				<button class="btn btn-filter">충남</button>
				<button class="btn btn-filter">전북</button>
				<button class="btn btn-filter">전남</button>
				<button class="btn btn-filter">경북</button>
				<button class="btn btn-filter">경남</button>
				<button class="btn btn-filter">제주</button>
			</div>
		</div>

		<div class="w-full max-w-4xl mx-auto">
			{#await load()}
				<div class="flex items-center justify-center min-h-screen">
					<span class="loading loading-dots loading-lg"></span>
				</div>
			{:then { data: { itemPage } }}
				<div class="flex justify-center min-h-screen bg-base-100">
					<div class="container mx-auto px-4">
						<div class="w-full max-w-4xl mx-auto">
							<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
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
													>
														<path
															d="M7,24a1,1,0,0,1-.71-.29,1,1,0,0,1,0-1.42l8.17-8.17a3,3,0,0,0,0-4.24L6.29,1.71A1,1,0,0,1,7.71.29l8.17,8.17a5,5,0,0,1,0,7.08L7.71,23.71A1,1,0,0,1,7,24Z"
														/>
													</svg>
												</div>
											</div>
											<div class="card-body pt-1">
												<div class="flex items-center justify-between">
													<div class="flex items-center">
														<!-- 메인 이미지가 존재하는 경우 -->
														{#if post.mainImageUrl}
															<img
																src={post.mainImageUrl}
																alt={post.title}
																class="mr-4 w-20 h-20 object-cover"
															/>
														{/if}
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
													</div>
													<div class="flex items-center justify-between min-w-[120px] ml-4">
														<!-- 여기에 min-w와 ml 추가 -->
														<div class="flex flex-col items-center flex-nowrap">
															<!-- flex-nowrap 추가 -->
															{#if post.closed}
																<div class="badge badge-neutral whitespace-nowrap">구했어유</div>
																<!-- 여기에 whitespace-nowrap 추가 -->
															{:else}
																<div class="badge badge-primary my-1 whitespace-nowrap">구해유</div>
																<!-- 여기에 whitespace-nowrap 추가 -->
																<div class="text-xs text-gray-500 whitespace-nowrap">
																	~ {post.deadLine}
																</div>
																<!-- 여기에 whitespace-nowrap 추가 -->
															{/if}
														</div>
													</div>
												</div>
											</div>
										</div>
									</a>
								{/each}
							</div>
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
		</div>
	</div>
</div>

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
