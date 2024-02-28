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
							<button class="btn btn-xs mx-1" on:click={() => postEarlyClosing()}>조기마감</button>
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
			<div class="my-5">
				<div class="bg-white p-5 rounded-lg shadow-lg">
					<h3 class="text-md font-medium text-gray-800 mb-3">근무 조건</h3>
					<ul class="list-disc space-y-2 text-sm">
						<li class="flex items-center text-gray-700">
							<div class="flex items-center">
								<svg xmlns="http://www.w3.org/2000/svg" id="Layer_1" data-name="Layer 1" viewBox="0 0 24 24" width="15" height="15" class="mr-2"><path d="M13.83,5.268c1.11-.592,2.461-1.581,3.06-3.087,.193-.486,.133-1.018-.166-1.458-.307-.453-.815-.724-1.36-.724h-6.728c-.544,0-1.053,.271-1.36,.724-.299,.44-.359,.972-.167,1.458,.599,1.506,1.95,2.495,3.06,3.087C5.238,6.62,1,13.094,1,18.25c0,3.17,2.58,5.75,5.75,5.75h10.5c3.17,0,5.75-2.58,5.75-5.75,0-5.156-4.238-11.63-9.17-12.982ZM8.039,1.812c-.094-.236,0-.432,.064-.526,.121-.179,.32-.285,.533-.285h6.728c.212,0,.412,.106,.533,.285,.064,.095,.158,.29,.064,.527-.788,1.982-3.337,2.952-3.961,3.163-.625-.21-3.169-1.174-3.96-3.164Zm9.211,21.188H6.75c-2.619,0-4.75-2.131-4.75-4.75,0-5.349,4.849-12.25,10-12.25s10,6.901,10,12.25c0,2.619-2.131,4.75-4.75,4.75Zm-1.25-5.626c0,1.448-1.178,2.626-2.626,2.626h-.874v1.5c0,.276-.224,.5-.5,.5s-.5-.224-.5-.5v-1.5h-.926c-.979,0-1.891-.526-2.381-1.374-.139-.239-.057-.545,.182-.683,.239-.14,.544-.057,.683,.182,.312,.54,.894,.875,1.516,.875h2.8c.896,0,1.626-.729,1.626-1.626,0-.803-.575-1.478-1.368-1.605l-3.422-.55c-1.28-.206-2.209-1.296-2.209-2.593,0-1.448,1.178-2.626,2.626-2.626h.874v-1.5c0-.276,.224-.5,.5-.5s.5,.224,.5,.5v1.5h.926c.978,0,1.891,.527,2.381,1.375,.139,.239,.057,.545-.182,.683-.241,.138-.544,.056-.683-.182-.312-.54-.894-.875-1.516-.875h-2.8c-.896,0-1.626,.729-1.626,1.626,0,.803,.575,1.478,1.368,1.605l3.422,.55c1.28,.206,2.209,1.296,2.209,2.593Z"/></svg>
								<span class="font-medium">급여:</span>
								<span class="ml-2 text-gray-600">
									{#if jobPostDetailDto?.cost}
										{jobPostDetailDto?.cost.toLocaleString()}원
									{:else}
										정보 없음
									{/if}
								</span>
						</li>
						<li class="flex items-center text-gray-700">
							<div class="flex items-center">
								<svg xmlns="http://www.w3.org/2000/svg" id="Layer_1" data-name="Layer 1" viewBox="0 0 24 24" width="15" height="15" class="mr-2"><path d="M17.5,11c-3.584,0-6.5,2.916-6.5,6.5s2.916,6.5,6.5,6.5,6.5-2.916,6.5-6.5-2.916-6.5-6.5-6.5Zm0,12c-3.032,0-5.5-2.467-5.5-5.5s2.468-5.5,5.5-5.5,5.5,2.467,5.5,5.5-2.468,5.5-5.5,5.5Zm1.354-4.854c.195,.195,.195,.512,0,.707-.098,.098-.226,.146-.354,.146s-.256-.049-.354-.146l-1-1c-.094-.094-.146-.221-.146-.354v-2c0-.276,.224-.5,.5-.5s.5,.224,.5,.5v1.793l.854,.854Zm.646-14.146h-2.028c-.25-2.247-2.16-4-4.472-4h-2.5c-2.312,0-4.223,1.753-4.472,4h-1.528C2.019,4,0,6.019,0,8.5v11c0,2.481,2.019,4.5,4.5,4.5h6c.276,0,.5-.224,.5-.5s-.224-.5-.5-.5H4.5c-1.93,0-3.5-1.57-3.5-3.5v-6.5H9.5c.276,0,.5-.224,.5-.5s-.224-.5-.5-.5H1v-3.5c0-1.93,1.57-3.5,3.5-3.5h15c1.93,0,3.5,1.57,3.5,3.5v2c0,.276,.224,.5,.5,.5s.5-.224,.5-.5v-2c0-2.481-2.019-4.5-4.5-4.5ZM10.5,1h2.5c1.76,0,3.221,1.306,3.464,3H7.036c.243-1.694,1.704-3,3.464-3Z"/></svg>
								<span class="font-medium">급여 타입:</span>
								<span class="ml-2 text-gray-600">
									{#if jobPostDetailDto?.wageType === 'HOURLY'}시급{:else if jobPostDetailDto?.wageType === 'SALARY'}일급{:else if jobPostDetailDto?.wageType === 'PER_PROJECT'}건당{:else if jobPostDetailDto?.wageType === 'UNDEFINED'}미정{:else}'정보 없음'{/if}
								</span>
						</li>
						<li class="flex items-center text-gray-700">
							<div class="flex items-center">
								<svg xmlns="http://www.w3.org/2000/svg" id="arrow-circle-down" viewBox="0 0 24 24" width="15" height="15" class="mr-2"><path d="M12,24C5.383,24,0,18.617,0,12S5.383,0,12,0s12,5.383,12,12-5.383,12-12,12ZM12,1C5.935,1,1,5.935,1,12s4.935,11,11,11,11-4.935,11-11S18.065,1,12,1Zm1.854,16.26l-4.877-4.876c-.211-.212-.211-.557,0-.768l4.873-4.872-.707-.707-4.873,4.873c-.601,.601-.601,1.58,0,2.181l4.877,4.877,.707-.707Z"/></svg>
								<span class="font-medium">도움시간 또는 일수:</span>
							<span class="ml-2 text-gray-600">{jobPostDetailDto?.workTime ?? '정보 없음'}</span>
						</li>
					</ul>
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
			<svg xmlns="http://www.w3.org/2000/svg" id="Layer_1" data-name="Layer 1" viewBox="0 0 24 24" width="15" height="15" class="inline-block mr-1">
				<path d="M12,7c-2.757,0-5,2.243-5,5s2.243,5,5,5,5-2.243,5-5-2.243-5-5-5Zm0,9c-2.206,0-4-1.794-4-4s1.794-4,4-4,4,1.794,4,4-1.794,4-4,4Zm9.987-4.5c-.253-5.117-4.37-9.234-9.487-9.487V0h-1V2.013C6.383,2.266,2.266,6.383,2.013,11.5H0v1H2.013c.253,5.117,4.37,9.234,9.487,9.487v2.013h1v-2.013c5.117-.253,9.234-4.371,9.487-9.487h2.013v-1h-2.013Zm-9.987,9.5c-4.962,0-9-4.037-9-9S7.038,3,12,3s9,4.037,9,9-4.038,9-9,9Z"/></svg>
			<span class="text-sm">{jobPostDetailDto?.location ?? '정보 없음'}</span>

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
							<div class="text-gray-700 ml-2">{comment.content}</div>
						{/if}
					</div>
				{/each}
			</div>
		</div>
	</div>
{/await}
