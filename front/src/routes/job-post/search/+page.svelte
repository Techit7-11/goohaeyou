<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { createEventDispatcher } from 'svelte';

	let kwType: string = '';
	let kw: string = '';
	let dataLoaded: boolean = false; // 데이터가 로드되었는지 여부를 저장하는 변수 추가
	const dispatch = createEventDispatcher(); // 검색 버튼 클릭 이벤트를 디스패치하기 위한 함수

	async function loading() {
		dataLoaded = false;

		load();
	}

	function handleKeyPress(event) {
		if (event.key === 'Enter') {
			loading();
		}
	}

	async function load() {
		const kwInput = document.querySelector('input[name="kw"]') as HTMLInputElement;
		kw = kwInput.value;

		const kwTypeSelect = document.querySelector('select[name="kwType"]') as HTMLSelectElement;
		kwType = kwTypeSelect.value;

		const { data } = await rq.apiEndPoints().GET(`/api/job-posts/search?${kwType}=${kw}`);

		console.log(kw, kwType);
		console.log(data);

		dispatch('search', data);
		dataLoaded = true;

		return data;
	}
</script>

<div class="flex justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="container flex justify-center items-center my-4">
			<select class="select max-w-xs text-center flex flex-1" name="kwType">
				<option value="titleOrBody">제목 + 내용</option>
				<option value="title">제목</option>
				<option value="body">내용</option>
			</select>
			<input
				name="kw"
				type="text"
				placeholder="검색어"
				class="input w-full max-w-xs"
				on:keyup={handleKeyPress}
			/>
			<button class="btn btn-ghost btn-circle" on:click={loading}>
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

		<div>
			{#if dataLoaded}
				<!-- 데이터가 로드되었는지 확인 -->
				{#await load()}
					<div class="flex items-center justify-center min-h-screen">
						<span class="loading loading-dots loading-lg"></span>
					</div>
				{:then { data: posts }}
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
				{/await}
			{/if}
		</div>
	</div>
</div>
