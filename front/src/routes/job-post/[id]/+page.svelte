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
		await loadComments(); // 댓글 로드
		await updateInterestStatus(); // 관심등록 여부 확인 및 상태 업데이트
	});

	async function updateInterestStatus() {
		const { data } = await checkInterestStatus(postId);
		interested = data?.data;
	}

	async function load() {
		const { data } = await rq.apiEndPoints().GET(`/api/job-posts/${postId}`);
		return data!;
	}
	async function apply() {
		try {
			if (rq.isLogout()) {
				rq.msgError('로그인이 필요합니다.');
				rq.goTo('/member/login');
				return;
			}
			const postId = parseInt($page.params.id);
			rq.goTo(`/applications/${postId}/write`);
		} catch (error) {
			console.error('애플리케이션 작성 중 오류가 발생했습니다:', error);
		}
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
		const response = await rq.apiEndPoints().PUT(`/api/job-posts/${postId}/closing`);

		if (response.data?.statusCode === 204) {
			alert('공고가 조기 마감 되었습니다.');
			location.reload();
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.data?.message);
		} else {
			console.error('조기 마감에 실패하였습니다.');
		}
	}

	// 지원서 목록으로 이동
	function goToApplicationsList(postId) {
		window.location.href = `/applications/list/${postId}`;
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: jobPostDetailDto }}
	<div class="flex justify-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div class="p-6 max-w-4xl mx-auto my-10 bg-white rounded-box shadow-lg">
				<div>{jobPostDetailDto?.author}</div>
				<div class="flex justify-between items-center">
					<div class="text-xl font-bold">{jobPostDetailDto?.title}</div>
					<div class="flex items-center">
						{#if !jobPostDetailDto?.closed && !jobPostDetailDto.employed && rq.isLogin && jobPostDetailDto?.author !== rq.member.username}
							<div class="flex justify-center items-center mr-4">
								{#if interested}
									<button
										class="btn btn-ghost px-1 py-1 text-xs text-gray-600"
										on:click={() => removeInterest(postId)}
									>
										<div class="text-red-600">
											<svg
												xmlns="http://www.w3.org/2000/svg"
												class="h-6 w-6"
												fill="none"
												viewBox="0 0 24 24"
												stroke="currentColor"
												><path
													stroke-linecap="round"
													stroke-linejoin="round"
													stroke-width="2"
													d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
												/></svg
											>
										</div>
									</button>
								{:else}
									<button
										class="btn btn-ghost px-1 py-1 text-xs text-gray-600"
										on:click={() => registerInterest(postId)}
									>
										<svg
											xmlns="http://www.w3.org/2000/svg"
											class="h-6 w-6"
											fill="none"
											viewBox="0 0 24 24"
											stroke="currentColor"
											><path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
											/></svg
										>
									</button>
								{/if}
							</div>
							<button class="btn btn-neutral" on:click={apply}>지원하기</button>
						{/if}
					</div>
				</div>
				<div class="mt-4">
					<div class="flex flex-col items-end text-gray-700 text-sm">
						<div>등록 일자 : {jobPostDetailDto?.createdAt}</div>
						{#if jobPostDetailDto?.createdAt !== jobPostDetailDto?.modifiedAt}
							<div class="text-sm">수정 일자 : {jobPostDetailDto?.modifiedAt}</div>
						{/if}
						<div class="flex justify-center mt-4">
							{#if jobPostDetailDto?.author === rq.member.username}
								<button class="btn btn-primary btn-xs mx-1" on:click={editPost}>수정하기</button>
								<button class="btn btn-xs mx-1" on:click={deletePost}>삭제하기</button>
								{#if !jobPostDetailDto.closed}
									<button class="btn btn-xs mx-1" on:click={() => postEarlyClosing()}
										>조기마감</button
									>
								{/if}
								{#if jobPostDetailDto?.author === rq.member.username}
									<button
										class="btn btn-primary btn-xs mx-1"
										on:click={() => goToApplicationsList(jobPostDetailDto?.id)}>지원서 확인</button
									>
								{/if}
							{/if}
						</div>
					</div>
					<div class="divider"></div>
					<div class="grid grid-cols-4 my-4">
						<div class="text-sm">모집 상태 :</div>
						<div>
							{#if jobPostDetailDto.employed}
								<div class="text-sm text-blue-900">구인완료</div>
							{:else if jobPostDetailDto.closed}
								<div class="text-sm text-rose-600">마감</div>
							{:else}
								<div class="text-sm text-emerald-700">구인중</div>
							{/if}
						</div>
						<div class="text-sm">공고 마감 :</div>
						{#if jobPostDetailDto?.deadLine === null}
							<span class="badge badge-outline badge-error"> 조기마감 </span>
						{:else}
							<div class="text-sm">{jobPostDetailDto?.deadLine}</div>
						{/if}
						<div class="text-sm">지원 나이 :</div>
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
					<div class="flex justify-end text-gray-700 text-sm">
						<div class="flex">
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
				<div class="w-full max-w-4xl mx-auto">
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
										<div class="avatar online placeholder">
											<div class="bg-neutral text-neutral-content rounded-full w-8">
												<span class="text-xs">{comment.author.slice(0, 3)}</span>
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
											<button
												class="btn btn-xs btn-ghost"
												on:click={() => deleteComment(comment.id)}>삭제</button
											>
										{/if}
									</div>
								</div>
								{#if comment.isEditing}
									<textarea class="textarea textarea-bordered w-full" bind:value={editingContent}
									></textarea>
								{:else}
									<div class="text-gray-700 ml-2">{comment.content}</div>
								{/if}
							</div>
						{/each}
					</div>
				</div>
			</div>
		</div>
	</div>
{/await}
