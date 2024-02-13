<script lang="ts">
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import { format } from 'date-fns';

	let postId = parseInt($page.params.id);
	let comments = [];
	let newComment = '';
	let editingContent = '';

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

	// 댓글
	async function loadComments() {
		try {
			const { data } = await rq.apiEndPoints().GET(`/api/post-comment/${postId}`);
			comments = data.data
				.map((comment) => ({
					...comment,
					isEditing: false // 모든 댓글에 isEditing 속성 추가
				}))
				.reverse();
		} catch (error) {
			console.error('댓글을 로드하는 중 오류가 발생했습니다.', error);
		}
	}

	async function addComment() {
		try {
			const response = await rq.apiEndPoints().POST(`/api/post-comment/${postId}/comment`, {
				body: { content: newComment }
			});
			newComment = ''; // 입력 필드 초기화
			loadComments(); // 댓글 목록 새로고침
		} catch (error) {
			console.error('댓글 추가 중 오류가 발생했습니다.', error);
		}
	}
	// 댓글 수정 시작
	function startEdit(commentId) {
		comments = comments.map((comment) => ({
			...comment,
			isEditing: comment.id === commentId
		}));
		const currentComment = comments.find((comment) => comment.id === commentId);
		editingContent = currentComment ? currentComment.content : '';
	}
	// 댓글 수정 제출
	async function submitEdit(commentId) {
		try {
			await rq.apiEndPoints().PUT(`/api/post-comment/${postId}/comment/${commentId}`, {
				body: { content: editingContent }
			});
			await loadComments(); // 댓글 목록 새로고침
		} catch (error) {
			console.error('댓글 수정 중 오류가 발생했습니다.', error);
		}
	}

	async function deleteComment(commentId) {
		try {
			await rq.apiEndPoints().DELETE(`/api/post-comment/${postId}/comment/${commentId}`);
			loadComments();
		} catch (error) {
			console.error('댓글 삭제 중 오류가 발생했습니다.', error);
		}
	}
	function formatDateTime(dateTimeString) {
		return format(new Date(dateTimeString), 'yyyy-MM-dd HH:mm');
	}

	onMount(async () => {
		await loadComments();
	});
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
				<span
					class="badge badge-outline {jobPostDetailDto?.closed ? 'badge-error' : 'badge-success'}"
				>
					{jobPostDetailDto?.closed ? '마감' : '구인중'}
				</span>
			</div>
			<div class="grid grid-cols-2 gap-4 mt-4">
				<div>위치: {jobPostDetailDto?.location}</div>
				<div>공고 마감: {jobPostDetailDto?.deadLine}</div>
				<div>
					지원 가능 최소 나이: {jobPostDetailDto?.minAge === 0
						? '없음'
						: jobPostDetailDto?.minAge ?? '없음'}
				</div>
				<div>
					성별 구분: {jobPostDetailDto?.gender === 'MALE'
						? '남'
						: jobPostDetailDto?.gender === 'FEMALE'
							? '여'
							: '무관'}
				</div>
				<div>최종 수정일자: {jobPostDetailDto?.modifyAt}</div>
				<div>조회수: {jobPostDetailDto?.incrementViewCount}</div>
				<div>관심 등록 수: {jobPostDetailDto?.interestsCount}</div>
			</div>
		</div>
	</div>

	<div class="container mx-auto px-4 py-8">
		<div class="w-full max-w-xl mx-auto">
			<!-- 댓글 입력 폼 -->
			<div class="flex justify-between items-center mb-4">
				<textarea
					class="textarea textarea-bordered w-full"
					placeholder="댓글을 입력하세요."
					bind:value={newComment}
				></textarea>
				<button class="btn btn-ghost mx-3" on:click={addComment}>댓글 달기</button>
			</div>
			<div class="divider"></div>
			<!-- 댓글 목록 -->
			<div class="space-y-4">
				{#each comments as comment}
					<div class="p-4 rounded-lg bg-base-100 shadow">
						<div class="flex items-center justify-between mb-2">
							<div class="flex items-center space-x-2">
								<div class="avatar">
									<div class="w-8 rounded-full">
										<img src="https://placeimg.com/64/64/people" />
									</div>
								</div>
								<div>
									<div class="font-bold">{comment.author}</div>
									<div class="text-xs text-gray-500">
										작성 일자 : {formatDateTime(comment.createAt)}
									</div>
									{#if comment.createAt !== comment.modifyAt}
										<div class="text-xs text-gray-500">
											수정 일자 : {formatDateTime(comment.modifyAt)}
										</div>
									{/if}
								</div>
							</div>
							<div>
								{#if comment.isEditing}
									<button class="btn btn-xs btn-ghost" on:click={() => submitEdit(comment.id)}
										>수정 완료</button
									>
								{:else}
									<button class="btn btn-xs btn-ghost" on:click={() => startEdit(comment.id)}
										>수정</button
									>
								{/if}
								<button class="btn btn-xs btn-ghost" on:click={() => deleteComment(comment.id)}
									>삭제</button
								>
							</div>
						</div>
						{#if comment.isEditing}
							<textarea class="textarea textarea-bordered w-full" bind:value={editingContent}></textarea>
						{:else}
							<div class="text-gray-700">{comment.content}</div>
						{/if}
					</div>
				{/each}
			</div>
		</div>
	</div>
{/await}
