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

<div class="container">
	<select class="select w-full max-w-xs" name="kwType">
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
	<button class="btn" on:click={loading}>검색</button>
</div>

<div>
	{#if dataLoaded}
		<!-- 데이터가 로드되었는지 확인 -->
		{#await load()}
			<p>loading...</p>
		{:then { data: jobPostDtoList }}
			<ul>
				{#each jobPostDtoList ?? [] as jobPostDto, index}
					<li>
						<a href="/job-post/{jobPostDto.id}">(No.{index + 1}) {jobPostDto.title}</a>
					</li>
				{/each}
			</ul>
		{/await}
	{/if}
</div>

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

	.container {
		display: flex;
		width: 100%;
		max-width: 600px; /* 최대 너비를 조절할 수 있습니다 */
		margin: 0 auto; /* 화면 중앙 정렬 */
	}

	.select {
		width: 35%; /* 40% 너비 */
	}

	.input {
		width: 45%; /* 40% 너비 */
	}

	.btn {
		width: 20%; /* 20% 너비 */
	}
</style>
