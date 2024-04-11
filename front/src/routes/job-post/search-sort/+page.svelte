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

		const kwTypeSelect = document.querySelector('select[name="kwType"]') as HTMLSelectElement;
		kwType = kwTypeSelect.value;

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
        console.log(kw, kwType);
        console.log(data);

		posts = data!.data.itemPage.content;

		return data!;
    }

</script>

<div class="flex justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="search-box container flex justify-center items-center my-4 flex-col">
			<div class="join">
				<div class="filter">
					<select
						name="kwType"
						class="select select-bordered"
						value={$page.url.searchParams.get('kwType') ?? 'ALL'}
					>
						<option value="ALL">전체</option>
						<option value="title">제목</option>
						<option value="body">내용</option>
						<option value="AUTHOR">작성자</option>
						<option value="LOCATION">주소</option>
					</select>
				</div>
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
            <div class="filter-closed">
				<button class="btn btn-filter">전체</button>
				<button class="btn btn-filter">공고중</button>
				<button class="btn btn-filter">마감</button>
			</div>
			<div class="filter-gender">
				<button class="btn btn-filter">무관</button>
				<button class="btn btn-filter">여성</button>
				<button class="btn btn-filter">남성</button>
			</div>
			<div class="filter-age">
				<button class="btn btn-filter">전체</button>
				<button class="btn btn-filter">19세 이하</button>
				<button class="btn btn-filter">20대</button>
				<button class="btn btn-filter">30대</button>
				<button class="btn btn-filter">40대 이상</button>
			</div>
			<div class="filter-location">
				<button class="btn btn-filter">전국</button>
				<button class="btn btn-filter">서울</button>
				<button class="btn btn-filter">경기</button>
				<button class="btn btn-filter">충북</button>
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
		<div class="container mx-auto mx-4">
			<div class="w-full max-w-4xl mx-auto">
				{#each posts ?? [] as post, index}
					<a href="/job-post/{post.id}" class="block">
						<div class="card relative bg-base-100 shadow-xl my-4">
							<div class="card-body">
								<div class="flex items-center justify-between">
									<div class="flex items-center">
										<div class="flex flex-col mr-10">
											<div class="text-bold text-gray-500 mb-1">{post.author}</div>
											<div
												class="flex flex-col max-w-40 sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl overflow-hidden"
											>
												<div class="font-bold truncate">{post.title}</div>
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
														<div class="text-xs text-gray-500">관심등록</div>
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

				<div class="max-w-4xl mx-auto my-5">
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
	.join {
		border: 1px solid oklch(0.77 0.2 132.02); /* 테두리를 1px 두께로 설정하고 검은색(#000)으로 색상을 지정합니다. */
	}

	.card:hover {
		background-color: oklch(0.95 0.05 128.99 / 0.3);
	}

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
	.text-shadow {
		text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
	}

</style>
