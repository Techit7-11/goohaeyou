<script lang="ts">
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import { format } from 'date-fns';

	let postId = parseInt($page.params.id);
	let comments = [];
	let newComment = '';
	let editingContent = '';
	let interested = false;

	onMount(async () => {
		const { data } = await checkInterestStatus(postId); // 해당 공고에 대해 로그인한 유저의 관심등록 여부 확인
		interested = data?.data;

		await loadComments();
	});

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

	async function checkInterestStatus() {
		const postId = parseInt($page.params.id);
		const { data } = await rq.apiEndPoints().GET(`/api/job-posts/${postId}/members/interest`);

		interested = data?.data;
	}

	async function registerInterest(postId: number) {
		const response = await rq.apiEndPoints().POST(`/api/job-posts/${postId}/interest`);

		if (response.data?.msg == 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect({ msg: response.data?.data?.message }, undefined, `/job-post/${postId}`);
		} else if (response.data?.statusCode === 204) {
			interested = true;
		} else {
			console.error('관심 등록에 실패하였습니다.');
		}
	}

	async function removeInterest(postId: number) {
		const response = await rq.apiEndPoints().DELETE(`/api/job-posts/${postId}/interest`);

		if (response.data?.msg == 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect({ msg: response.data?.data?.message }, undefined, `/job-post/${postId}`);
		}

		if (response.data?.statusCode === 204) {
			interested = false;
		} else {
			console.error('관심 취소에 실패하였습니다.');
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
			if (rq.isLogout()) {
				rq.msgError('로그인이 필요합니다.');
				rq.goTo('/member/login');
				return;
			}
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

	// 공고 조기 마감
	async function postEarlyClosing() {
		try {
			const { data } = await rq.apiEndPoints().PUT(`/api/job-posts/${postId}/closing`);
			alert('공고가 마감 되었습니다.');
		} catch (error) {
			console.error('공고 조기마감 중 오류가 발생했습니다.', error);
		}
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: jobPostDetailDto }}
	<div class="p-6 max-w-4xl mx-auto my-10 bg-white rounded-box shadow-lg">
		<div>{jobPostDetailDto?.author}</div>
		<div class="flex justify-between items-center">
			<div class="text-xl font-bold">{jobPostDetailDto?.title}</div>
			<div class="flex items-center">
				{#if jobPostDetailDto?.author === rq.member.username}
					{#if !jobPostDetailDto.closed}
						<button class="btn btn-primary btn-xs mr-2" on:click={postEarlyClosing}>조기마감</button
						>
					{/if}
					<button class="btn btn-primary btn-xs mr-2" on:click={editPost}>수정하기</button>
					<!-- 수정 -->
					<button class="btn btn-xs" on:click={deletePost}>삭제하기</button>
				{:else if !jobPostDetailDto?.closed && !jobPostDetailDto.employed && rq.isLogin}
					<!-- 지원 가능한 경우 -->
					<button class="btn btn btn-neutral" on:click={apply}>지원하기</button>
				{/if}
			</div>
		</div>
		<div class="mt-4">
			<div class="flex flex-col items-end text-gray-700 text-sm">
				<div>등록 일자 : {jobPostDetailDto?.createdAt}</div>
				{#if jobPostDetailDto?.createdAt !== jobPostDetailDto?.modifyAt}
					<div class="text-sm">수정 일자 : {jobPostDetailDto?.modifyAt}</div>
				{/if}
			</div>
			<div class="divider"></div>
			<div class="grid grid-cols-4 gap-4 my-4">
				<div class="text-sm">모집 상태 :</div>
				<div>
					{#if jobPostDetailDto?.employed}
						<span class="badge badge-outline badge-error">구인완료</span>
					{/if}
					<span
						class="badge badge-outline {jobPostDetailDto?.closed ? 'badge-error' : 'badge-success'}"
					>
						{jobPostDetailDto?.closed ? '공고마감' : '지원가능'}
					</span>
				</div>
				<div class="text-sm">공고 마감 :</div>
				{#if jobPostDetailDto?.deadLine === null}
					<span class="badge badge-outline badge-error"> 조기마감 </span>
				{:else}
					<div class="text-sm">{jobPostDetailDto?.deadLine}</div>
				{/if}
				<div class="text-sm">지원 가능 나이 :</div>
				<div class="text-sm">
					{jobPostDetailDto?.minAge === 0 ? '없음' : jobPostDetailDto?.minAge ?? '없음'}
				</div>
				<div class="text-sm">성별 구분 :</div>
				<div class="text-sm">
					{jobPostDetailDto?.gender === 'MALE'
						? '남'
						: jobPostDetailDto?.gender === 'FEMALE'
							? '여'
							: '무관'}
				</div>
			</div>
			<div class="text-sm">위치 : {jobPostDetailDto?.location}</div>
			<div class="divider"></div>
			<div class="flex justify-between text-gray-700 text-sm">
				{#if rq.isLogin() && jobPostDetailDto?.author !== rq.member.username}
					<div>
						{#if interested}
							<button class="btn btn-ghost px-1 py-1 text-xs" on:click={() => removeInterest(postId)}>관심 취소</button>
						{:else}
							<button class="btn btn-ghost px-1 py-1 text-xs" on:click={() => registerInterest(postId)}>
								<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
								관심 공고
							</button>
						{/if}
					</div>
				{/if}
				<div class="flex items-center">
					<div class="text-sm">관심 등록 :</div>
					<div class="text-sm mx-2">{jobPostDetailDto?.interestsCount}</div>
					<div class="text-sm">조회 :</div>
					<div class="text-sm mx-2">{jobPostDetailDto?.incrementViewCount}</div>
				</div>
			</div>
			<div class="p-4 mt-4 text-gray-700 bg-white rounded-lg shadow border border-gray-200">
				<div class="whitespace-pre-line">{jobPostDetailDto?.body}</div>
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
								{#if comment.author === rq.member.username}
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
								{/if}
							</div>
						</div>
						{#if comment.isEditing}
							<textarea class="textarea textarea-bordered w-full" bind:value={editingContent}
							></textarea>
						{:else}
							<div class="text-gray-700">{comment.content}</div>
						{/if}
					</div>
				{/each}
			</div>
		</div>
	</div>
{/await}
