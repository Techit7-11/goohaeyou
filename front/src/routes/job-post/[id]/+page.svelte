<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

<<<<<<< HEAD
	let postId = parseInt($page.params.id);

	async function load() {
		const { data } = await rq.apiEndPoints().GET(`/api/job-posts/${postId}`);
=======
	async function load() {
		const { data } = await rq.apiEndPoints().GET('/api/job-posts/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			}
		});

>>>>>>> main
		return data!;
	}

	async function apply() {
		const postId = parseInt($page.params.id);
<<<<<<< HEAD
		rq.goTo(`/applications/${postId}/write`);
	}
	function editPost() {
		rq.goTo(`/job-post/modify/${postId}`);
	}
	async function deletePost() {
		try {
			const { data } = await rq.apiEndPoints().DELETE(`/api/job-posts/${postId}`);
			alert('글이 삭제되었습니다.');
			rq.goTo(`/job-post/list`);
		} catch (error) {
			console.error('글 삭제 중 오류가 발생했습니다.', error);
			alert('글을 삭제하는 데 실패했습니다.');
		}
=======
		rq.goTo(`/applications/${postId}`);
>>>>>>> main
	}
</script>

{#await load()}
<<<<<<< HEAD
  <div class="flex justify-center items-center h-screen">
    <span class="loading loading-dots loading-md"></span>
  </div>
{:then { data: jobPostDetailDto }}
  <div class="p-6 max-w-4xl mx-auto my-10 bg-white rounded-box shadow-lg">
    <div class="flex justify-between items-center">
      <div class="text-gray-500">No.{jobPostDetailDto?.id}</div>
      <div class="text-xl font-bold">{jobPostDetailDto?.title}</div>
      <div>
        {#if jobPostDetailDto?.author === rq.member.username}
          <button class="btn btn-outline" on:click={editPost}>수정하기</button>
          <button class="btn btn-outline btn-error" on:click={deletePost}>삭제하기</button>
        {:else if !jobPostDetailDto?.closed && rq.isLogin}
          <button class="btn btn-outline btn-info" on:click={apply}>지원하기</button>
        {/if}
      </div>
    </div>
    <div class="mt-4">
      <div class="flex justify-between text-gray-700 text-sm">
        <div>{jobPostDetailDto?.author}</div>
        <div>등록일시 {jobPostDetailDto?.createdAt}</div>
      </div>
      <div class="p-4 mt-4 text-gray-700 bg-white rounded-lg shadow border border-gray-200">
		<div class="whitespace-pre-line">{jobPostDetailDto?.body}</div>
	  </div>
      <div class="mt-4">
        <span class="badge badge-outline {jobPostDetailDto?.closed ? 'badge-error' : 'badge-success'}">
          {jobPostDetailDto?.closed ? '마감' : '구인중'}
        </span>
      </div>
      <div class="grid grid-cols-2 gap-4 mt-4">
        <div>위치: {jobPostDetailDto?.location}</div>
        <div>공고 마감: {jobPostDetailDto?.deadLine}</div>
        <div>지원 가능 최소 나이: {jobPostDetailDto?.minAge === 0 ? '없음' : jobPostDetailDto?.minAge ?? '없음'}</div>
        <div>성별 구분: {jobPostDetailDto?.gender === 'MALE' ? '남' : jobPostDetailDto?.gender === 'FEMALE' ? '여' : '무관'}</div>
        <div>최종 수정일자: {jobPostDetailDto?.modifyAt}</div>
        <div>조회수: {jobPostDetailDto?.incrementViewCount}</div>
        <div>관심 등록 수: {jobPostDetailDto?.interestsCount}</div>
      </div>
    </div>
  </div>
{/await}
=======
	<div>Loading...</div>
{:then { data: jobPostDetailDto }}
	<div class="job-post-container">
		<div class="id-box">No.{jobPostDetailDto?.id}</div>
		<div class="title-box">{jobPostDetailDto?.title}</div>
		<div class="author-createdAt">
			<p>{jobPostDetailDto?.author}</p>
			<p>등록일시 {jobPostDetailDto?.createdAt}</p>
		</div>
		<div class="content-box">{jobPostDetailDto?.body}</div>
		<p>
			{#if jobPostDetailDto?.closed}
				<span class="status-box">마감</span>
			{:else}
				<span class="status-box">구인중</span>
			{/if}
		</p>
		<div class="info-box">
			<div>위치: {jobPostDetailDto?.location}</div>
			<div>공고 마감: {jobPostDetailDto?.deadLine}</div>
			<div>지원 가능 최소 나이: {jobPostDetailDto?.minAge === 0 ? '없음' : jobPostDetailDto?.minAge ?? '없음'}</div>
			<div>성별 구분: {jobPostDetailDto?.gender === 'MALE' ? '남' : jobPostDetailDto?.gender === 'FEMALE' ? '여' : '무관'}</div>
			<div>최종 수정일자: {jobPostDetailDto?.modifyAt}</div>
			<div>조회수: {jobPostDetailDto?.incrementViewCount}</div>
			<div>관심 등록 수 : {jobPostDetailDto?.interestsCount}</div>
			<div class="apply-button" style="text-align: right;">
				<button class="btn" on:click={apply}>지원하기</button>
			</div>
		</div>
	</div>
{/await}

<style>
	.job-post-container {
		background-color: #fff; /* 배경색 설정 */
		border: 1px solid #000; /* 테두리 설정 */
		border-radius: 10px; /* 둥근 테두리 설정 */
		padding: 20px;
		margin: 20px;
		width: 90%;
		box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.2); /* 입체적인 그림자 효과 추가 */
	}

	.id-box {
		background-color: transparent; /* 투명 배경색 설정 */
		color: #d1d1d1; /* 흰색 글자색 설정 */
		width: 40px; /* 작은 크기로 설정 */
		height: 40px; /* 작은 크기로 설정 */
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: bold;
		font-size: 16px; /* 작은 글자 크기로 설정 */
		margin-right: 10px;
		opacity: 2; /* 완전 투명으로 설정 */
	}

	.title-box {
		font-size: 24px;
		font-weight: bold;
		margin-bottom: 10px;
	}

	.content-box {
		background-color: #fff; /* 화이트 톤 배경색 설정 */
		border: 1px solid #d1d1d1; /* 그레이 톤 테두리 설정 */
		border-radius: 10px; /* 둥근 테두리 설정 */
		padding: 20px;
		margin-top: 10px;
	}

	.status-box {
		background-color: #636364; /* 블랙 톤 배경색 설정 */
		color: #fff; /* 흰색 글자색 설정 */
		border-radius: 10px; /* 둥근 테두리 설정 */
		padding: 5px 10px;
		font-weight: bold;
		font-size: 16px;
		display: inline-block;
		margin-top: 20px; /* 한 칸 아래로 배치 */
	}

	.author-createdAt {
		display: flex;
		justify-content: space-between; /* 요소 사이의 간격을 최대로 설정 */
		margin-top: 10px; /* 위로 조금 띄움 */
	}

	.info-box {
		display: grid;
		grid-template-columns: repeat(2, 1fr); /* 2열 그리드 설정 */
		gap: 10px; /* 그리드 아이템 간격 설정 */
		margin-top: 20px;
	}

	.info-item {
		background: linear-gradient(145deg, #e6e6e6, #ffffff);
		box-shadow:
			5px 5px 10px #d1d1d1,
			-5px -5px 10px #ffffff;
		border-radius: 5px;
		padding: 10px;
		font-size: 16px;
		color: #333;
		font-weight: 500;
	}
</style>
>>>>>>> main
