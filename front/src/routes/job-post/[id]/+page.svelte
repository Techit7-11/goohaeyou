<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	let postId = parseInt($page.params.id);

	async function load() {
		const { data } = await rq.apiEndPoints().GET(`/api/job-posts/${postId}`);
		return data!;
	}

	async function apply() {
		const postId = parseInt($page.params.id);
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
	}
</script>

{#await load()}
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
