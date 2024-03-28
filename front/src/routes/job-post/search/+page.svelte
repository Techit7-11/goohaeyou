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
		<div class="search-box container flex justify-center items-center my-4">
			<div class="join">
				<select class="join-item select max-w-xs text-center flex flex-1 mx-1" name="kwType">
					<option value="titleOrBody">제목 + 내용</option>
					<option value="title">제목</option>
					<option value="body">내용</option>
				</select>
				<input
					name="kw"
					type="text"
					placeholder="검색어"
					class="join-item input w-full max-w-xs"
					on:keyup={handleKeyPress}
				/>
				<button class="join-item-btn btn btn-ghost btn-circle mx-1" on:click={loading}>
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
		</div>

		<div class="w-full max-w-4xl mx-auto">
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
								<div class="bg-lime-500 h-60 text-white text-2xl text-shadow flex items-center justify-center">
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
				{/await}
			{/if}
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
