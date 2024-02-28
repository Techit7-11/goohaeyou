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
					<div class="text-xl font-bold break-words max-w-[55%]">{jobPostDetailDto?.title}</div>
					<div class="flex items-center">
						{#if !jobPostDetailDto?.closed && !jobPostDetailDto.employed && rq.isLogin && jobPostDetailDto?.author !== rq.member.username}
							<div class="flex justify-center items-center mr-4">
								{#if interested}
									<button
										class="btn btn-ghost px-1 py-1 text-xs text-gray-600"
										on:click={() => removeInterest(postId)}
									>
										<div>
											<svg
												version="1.0"
												xmlns="http://www.w3.org/2000/svg"
												class="h-6 w-6"
												viewBox="0 0 512.000000 512.000000"
												preserveAspectRatio="xMidYMid meet"
											>
												<g
													transform="translate(0.000000,512.000000) scale(0.100000,-0.100000)"
													fill="red"
													stroke="none"
												>
													<path
														d="M1292 4980 c-278 -35 -562 -165 -772 -354 -467 -420 -637 -1114 -424
                                            -1727 20 -57 71 -174 113 -259 349 -699 986 -1433 1839 -2118 193 -156 496
                                            -382 510 -382 24 0 503 369 732 564 368 314 764 711 1021 1026 330 403 620
                                            880 718 1180 138 418 107 881 -84 1260 -143 284 -386 530 -664 670 -316 158
                                            -680 185 -993 74 -118 -42 -279 -130 -390 -214 -94 -71 -261 -232 -317 -305
                                            l-22 -30 -36 45 c-64 80 -226 234 -318 302 -163 122 -376 219 -556 253 -102
                                            19 -266 26 -357 15z"
													/>
												</g>
											</svg>
										</div>
									</button>
								{:else}
									<button
										class="btn btn-ghost px-1 py-1 text-xs text-gray-600"
										on:click={() => registerInterest(postId)}
									>
										<svg
											version="1.0"
											xmlns="http://www.w3.org/2000/svg"
											class="h-6 w-6"
											viewBox="0 0 512.000000 512.000000"
											preserveAspectRatio="xMidYMid meet"
										>
											<g
												transform="translate(0.000000,512.000000) scale(0.100000,-0.100000)"
												fill="#000000"
												stroke="none"
											>
												<path
													d="M1262 4830 c-319 -40 -586 -171 -812 -399 -203 -204 -325 -420 -395
                                        -701 -124 -487 -34 -967 264 -1418 191 -289 438 -554 891 -958 288 -257 1167
                                        -1007 1210 -1032 40 -24 55 -27 140 -27 85 0 100 3 140 27 43 25 924 776 1210
                                        1032 455 406 700 670 891 958 298 451 388 931 264 1418 -70 281 -192 497 -395
                                        701 -202 203 -418 320 -701 380 -142 30 -404 33 -528 5 -346 -75 -611 -248
                                        -853 -556 l-28 -35 -27 35 c-239 302 -500 475 -833 551 -99 23 -327 33 -438
                                        19z m334 -305 c284 -50 529 -214 723 -485 33 -47 74 -103 90 -126 74 -104 228
                                        -104 302 0 16 23 57 79 90 126 265 370 634 544 1036 489 446 -61 794 -373 927
                                        -832 105 -363 59 -744 -132 -1087 -160 -287 -427 -588 -892 -1005 -225 -201
                                        -1171 -1015 -1180 -1015 -10 0 -952 811 -1180 1015 -715 641 -997 1041 -1065
                                        1510 -44 303 19 629 172 886 230 387 678 599 1109 524z"
												/>
											</g>
										</svg>
									</button>
								{/if}
							</div>
							<button class="btn btn-neutral" on:click={apply}>지원하기</button>
						{/if}
					</div>
				</div>
				<div class="mt-4">
					<div class="flex flex-col text-gray-700 text-sm">
						<div class="text-xs">등록 일자 : {jobPostDetailDto?.createdAt}</div>
						{#if jobPostDetailDto?.createdAt !== jobPostDetailDto?.modifiedAt}
							<div class="text-xs">수정 일자 : {jobPostDetailDto?.modifiedAt}</div>
						{/if}
						<div class="flex justify-end">
							{#if jobPostDetailDto?.author === rq.member.username}
								<div class="mt-4">
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
											on:click={() => goToApplicationsList(jobPostDetailDto?.id)}
											>지원서 확인</button
										>
									{/if}
								</div>
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
							<div class="text-sm text-rose-600">조기 마감</div>
						{:else}
							<div class="text-xs sm:text-sm">{jobPostDetailDto?.deadLine}</div>
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
