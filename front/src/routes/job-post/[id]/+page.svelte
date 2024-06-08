<script lang="ts">
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import { format } from 'date-fns';
	import Map from './Map.svelte';
	let postId = parseInt($page.params.id);
	let comments = [];
	let newComment = '';
	let editingContent = '';
	let interested = false;
	let jobPostProfileImageUrl = null;
	let imageContainer;

	const baseUrl = import.meta.env.VITE_CORE_API_BASE_URL;

	onMount(async () => {
		const postId = parseInt($page.params.id);
		// 공고 작성자 프로필 이미지 URL 로드
		const response = await rq.apiEndPoints().GET(`/api/members/image/posts/${postId}`);
		jobPostProfileImageUrl = response.data?.data;

		await loadComments(); // 댓글 로드

		if (rq.isLogin()) {
			await updateInterestStatus(); // 관심등록 여부 확인 및 상태 업데이트
		}
	});
	async function updateInterestStatus() {
		const { data } = await checkInterestStatus(postId);
		interested = data?.data;
	}
	async function load() {
		const [jobPostResponse, imageUrlResponse] = await Promise.all([
			rq.apiEndPoints().GET(`/api/job-posts/${postId}`),
			rq.apiEndPoints().GET(`/api/job-post/images/${postId}`)
		]);

		const jobPostData = jobPostResponse.data;
		const imageUrls = imageUrlResponse.data?.data || [];

		return {
			...jobPostData,
			imageUrls
		};
	}
	async function apply() {
		try {
			if (rq.isLogout()) {
				rq.msgError('로그인이 필요합니다.');
				rq.goTo('/member/login');
				return;
			}

			rq.goTo(`/applications/${postId}/write`);
		} catch (error) {
			console.error('오류가 발생했습니다:', error);
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

		if (response.data?.resultType === 'SUCCESS') {
			interested = true;
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect({ msg: response.data?.message }, undefined, `/job-post/${postId}`);
		} else {
			console.error('관심 등록에 실패하였습니다.');
		}
	}
	async function removeInterest(postId: number) {
		const response = await rq.apiEndPoints().DELETE(`/api/job-posts/${postId}/interest`);

		if (response.data?.resultType === 'SUCCESS') {
			interested = false;
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect({ msg: response.data?.message }, undefined, `/job-post/${postId}`);
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
			console.log(response);
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

		if (response.data?.resultType === 'SUCCESS') {
			alert('공고가 조기 마감 되었습니다.');
			location.reload();
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			console.error('조기 마감에 실패하였습니다.');
		}
	}
	// 지원서 목록으로 이동
	function goToApplicationsList(postId) {
		window.location.href = `/applications/list/${postId}`;
	}
	// 공고에 이미지 업로드
	async function handleImageUpload(event) {
		const files = event.target.files;
		if (files.length > 3) {
			alert('이미지는 최대 3개까지 등록할 수 있습니다.');
			return;
		}

		const formData = new FormData();
		for (let i = 0; i < files.length; i++) {
			formData.append('jobPostImageFile', files[i]);
		}

		try {
			const response = await fetch(baseUrl + `/api/job-post/images?postDetailId=${postId}`, {
				method: 'POST',
				body: formData,
				credentials: 'include'
			});

			const responseData = await response.json();

			if (responseData.resultType === 'SUCCESS') {
				alert('이미지가 성공적으로 등록되었습니다.');
				// 필요한 경우 페이지 새로고침 또는 이미지 목록 업데이트
			} else {
				alert('이미지 등록에 실패했습니다.');
			}
		} catch (error) {
			console.error('이미지 등록 중 오류가 발생했습니다.', error);
			alert('이미지 등록 중 오류가 발생했습니다.');
		}
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: jobPostDetailDto, imageUrls }}
	<div class="flex justify-center items-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div class="p-6 max-w-4xl mx-auto my-5 bg-white rounded-box shadow-lg">
				<div class="flex">
					<div class="avatar placeholder">
						<div class="bg-neutral text-neutral-content rounded-full w-8">
							{#if jobPostProfileImageUrl != null}
								<img src={jobPostProfileImageUrl} alt="프로필 사진" />
							{:else}
								<span class="text-xs">{jobPostDetailDto?.author.slice(0, 3)}</span>
							{/if}
						</div>
					</div>
					<div class="text-md flex items-center mx-2">{jobPostDetailDto?.author}</div>
				</div>
				<div class="flex mt-1">
					<div class="text-xs text-gray-500">등록 :</div>
					<div class="text-xs mx-2">{jobPostDetailDto?.createdAt}</div>
					<!-- {#if jobPostDetailDto?.createdAt !== jobPostDetailDto?.modifiedAt}
					<div class="text-xs text-gray-500">수정 : </div>
					<div class="text-xs mx-2">{jobPostDetailDto?.modifiedAt}</div>
					{/if} -->
				</div>
				<div class="flex justify-center">
					{#if jobPostDetailDto?.author === rq.member.username}
						<div class="mt-4">
							<label
								for="jobPostImageFiles"
								class="btn btn-xs mx-1 bg-green4 text-white hover:bg-green6 font-thin"
							>
								이미지 등록
							</label>
							<input
								id="jobPostImageFiles"
								type="file"
								accept="image/*"
								multiple
								on:change={handleImageUpload}
								class="hidden"
							/>
							<button
								class="btn btn btn-xs mx-1 bg-green4 text-white hover:bg-green6 font-thin"
								on:click={editPost}>수정하기</button
							>
							<button
								class="btn btn-xs mx-1 bg-yellow-500 text-white hover:bg-yellow1 font-thin"
								on:click={deletePost}>삭제하기</button
							>
							{#if !jobPostDetailDto.closed}
								<button
									class="btn btn-xs mx-1 bg-green4 text-white hover:bg-green6 font-thin"
									on:click={() => postEarlyClosing()}>조기마감</button
								>
							{/if}
							{#if jobPostDetailDto?.author === rq.member.username}
								<button
									class="btn btn btn-xs mx-1 bg-green4 text-white hover:bg-green6 font-thin"
									on:click={() => goToApplicationsList(jobPostDetailDto?.id)}>지원서 확인</button
								>
							{/if}
						</div>
					{/if}
				</div>
				<div class="divider"></div>
				<div class="flex justify-between items-center">
					<div class="text-2xl font-bold break-words max-w-[55%] my-4">
						{jobPostDetailDto?.title}
					</div>
					<div class="flex">
						<div class="flex-shrink">
							<div class="text-xs mx-2 flex justify-center items-center">
								{jobPostDetailDto?.incrementViewCount}
							</div>
							<div class="text-xs text-gray-500">봤어유</div>
						</div>
						<div class="flex-shrink ml-3">
							<div class="text-xs mx-2 flex justify-center items-center">
								{jobPostDetailDto?.interestsCount}
							</div>
							<div class="text-xs text-gray-500">관심있슈</div>
						</div>
					</div>
				</div>
				<div class="mt-4">
					<div class="my-5">
						{#if imageUrls && imageUrls.length > 0}
							<div class="carousel w-full bg-white">
								{#each imageUrls as imageUrl, index}
									<div id="slide{index}" class="carousel-item relative w-full">
										<div
											class="sticky top-16 bg-white p-4 w-full max-w-screen-md mx-auto"
											bind:this={imageContainer}
										>
											<figure class="w-full h-64 flex justify-center items-center bg-white">
												<img
													src={imageUrl}
													alt="Image {index + 1}"
													class="object-contain max-w-full max-h-full"
												/>
											</figure>
										</div>
										<div
											class="absolute flex justify-between transform -translate-y-1/2 left-5 right-5 top-1/2"
										>
											<a
												href="#slide{(index - 1 + imageUrls.length) % imageUrls.length}"
												class="btn btn-circle">❮</a
											>
											<a href="#slide{(index + 1) % imageUrls.length}" class="btn btn-circle">❯</a>
										</div>
									</div>
								{/each}
							</div>
						{/if}
						<div class="bg-white p-5 rounded-lg shadow-lg">
							<h3 class="text-md font-medium text-green5 mb-3">근무 조건</h3>
							<ul class="list-disc space-y-2 text-sm">
								<li class="flex items-center text-gray-700">
									<div class="flex items-center">
										<svg
											xmlns="http://www.w3.org/2000/svg"
											id="Layer_1"
											data-name="Layer 1"
											viewBox="0 0 24 24"
											width="15"
											height="15"
											class="mr-2"
											><path
												d="M13.83,5.268c1.11-.592,2.461-1.581,3.06-3.087,.193-.486,.133-1.018-.166-1.458-.307-.453-.815-.724-1.36-.724h-6.728c-.544,0-1.053,.271-1.36,.724-.299,.44-.359,.972-.167,1.458,.599,1.506,1.95,2.495,3.06,3.087C5.238,6.62,1,13.094,1,18.25c0,3.17,2.58,5.75,5.75,5.75h10.5c3.17,0,5.75-2.58,5.75-5.75,0-5.156-4.238-11.63-9.17-12.982ZM8.039,1.812c-.094-.236,0-.432,.064-.526,.121-.179,.32-.285,.533-.285h6.728c.212,0,.412,.106,.533,.285,.064,.095,.158,.29,.064,.527-.788,1.982-3.337,2.952-3.961,3.163-.625-.21-3.169-1.174-3.96-3.164Zm9.211,21.188H6.75c-2.619,0-4.75-2.131-4.75-4.75,0-5.349,4.849-12.25,10-12.25s10,6.901,10,12.25c0,2.619-2.131,4.75-4.75,4.75Zm-1.25-5.626c0,1.448-1.178,2.626-2.626,2.626h-.874v1.5c0,.276-.224,.5-.5,.5s-.5-.224-.5-.5v-1.5h-.926c-.979,0-1.891-.526-2.381-1.374-.139-.239-.057-.545,.182-.683,.239-.14,.544-.057,.683,.182,.312,.54,.894,.875,1.516,.875h2.8c.896,0,1.626-.729,1.626-1.626,0-.803-.575-1.478-1.368-1.605l-3.422-.55c-1.28-.206-2.209-1.296-2.209-2.593,0-1.448,1.178-2.626,2.626-2.626h.874v-1.5c0-.276,.224-.5,.5-.5s.5,.224,.5,.5v1.5h.926c.978,0,1.891,.527,2.381,1.375,.139,.239,.057,.545-.182,.683-.241,.138-.544,.056-.683-.182-.312-.54-.894-.875-1.516-.875h-2.8c-.896,0-1.626,.729-1.626,1.626,0,.803,.575,1.478,1.368,1.605l3.422,.55c1.28,.206,2.209,1.296,2.209,2.593Z"
											/></svg
										>
										<span class="font-medium">급여 :</span>
										<span class="ml-2 text-gray-600">
											{#if jobPostDetailDto?.cost}
												{jobPostDetailDto?.cost.toLocaleString()}원
											{:else}
												정보 없음
											{/if}
										</span>
									</div>
								</li>
								<li class="flex items-center text-gray-700">
									<div class="flex items-center">
										<svg
											xmlns="http://www.w3.org/2000/svg"
											id="Layer_1"
											data-name="Layer 1"
											viewBox="0 0 24 24"
											width="15"
											height="15"
											class="mr-2"
											><path
												d="M17.5,11c-3.584,0-6.5,2.916-6.5,6.5s2.916,6.5,6.5,6.5,6.5-2.916,6.5-6.5-2.916-6.5-6.5-6.5Zm0,12c-3.032,0-5.5-2.467-5.5-5.5s2.468-5.5,5.5-5.5,5.5,2.467,5.5,5.5-2.468,5.5-5.5,5.5Zm1.354-4.854c.195,.195,.195,.512,0,.707-.098,.098-.226,.146-.354,.146s-.256-.049-.354-.146l-1-1c-.094-.094-.146-.221-.146-.354v-2c0-.276,.224-.5,.5-.5s.5,.224,.5,.5v1.793l.854,.854Zm.646-14.146h-2.028c-.25-2.247-2.16-4-4.472-4h-2.5c-2.312,0-4.223,1.753-4.472,4h-1.528C2.019,4,0,6.019,0,8.5v11c0,2.481,2.019,4.5,4.5,4.5h6c.276,0,.5-.224,.5-.5s-.224-.5-.5-.5H4.5c-1.93,0-3.5-1.57-3.5-3.5v-6.5H9.5c.276,0,.5-.224,.5-.5s-.224-.5-.5-.5H1v-3.5c0-1.93,1.57-3.5,3.5-3.5h15c1.93,0,3.5,1.57,3.5,3.5v2c0,.276,.224,.5,.5,.5s.5-.224,.5-.5v-2c0-2.481-2.019-4.5-4.5-4.5ZM10.5,1h2.5c1.76,0,3.221,1.306,3.464,3H7.036c.243-1.694,1.704-3,3.464-3Z"
											/></svg
										>
										<span class="font-medium">작업 시간/일수 :</span>
										<span class="ml-2 text-gray-600">
											{#if jobPostDetailDto?.payBasis === 'TOTAL_HOURS'}
												{jobPostDetailDto.workTime}시간
											{:else if jobPostDetailDto?.payBasis === 'TOTAL_DAYS'}
												{jobPostDetailDto.workDays}일
											{:else}정보 없음{/if}
										</span>
									</div>
								</li>
								<li class="flex items-center text-gray-700">
									<div class="flex items-center">
										<svg
											xmlns="http://www.w3.org/2000/svg"
											id="Layer_1"
											data-name="Layer 1"
											viewBox="0 0 24 24"
											width="15"
											height="15"
											class="mr-2"
											><path
												d="M23.18,9.65c-.5-.45-1.14-.69-1.81-.65-.67,.03-1.29,.32-1.74,.82l-3.73,4.1c-.31-1.11-1.33-1.92-2.54-1.92H3.5c-1.93,0-3.5,1.57-3.5,3.5v5c0,1.93,1.57,3.5,3.5,3.5h5.96c2.71,0,5.29-1.16,7.09-3.18l6.81-7.64c.91-1.02,.83-2.6-.18-3.53Zm-.56,2.86l-6.81,7.64c-1.61,1.81-3.92,2.85-6.35,2.85H3.5c-1.38,0-2.5-1.12-2.5-2.5v-5c0-1.38,1.12-2.5,2.5-2.5H13.36c.91,0,1.64,.74,1.64,1.64,0,.81-.61,1.51-1.4,1.62l-6.16,.74c-.27,.03-.47,.28-.44,.56,.03,.27,.27,.48,.56,.44l6.17-.74c1.02-.15,1.85-.88,2.15-1.82l4.49-4.94c.27-.3,.64-.47,1.05-.49,.4-.02,.79,.12,1.09,.39,.61,.56,.66,1.51,.11,2.12ZM5,3.24c0-1.24,1.01-2.24,2.24-2.24h.76V.5c0-.28,.22-.5,.5-.5s.5,.22,.5,.5v.5h.55c.98,0,1.89,.53,2.38,1.38,.14,.24,.06,.54-.18,.68-.24,.14-.55,.06-.68-.18-.31-.54-.89-.88-1.52-.88h-2.31c-.69,0-1.24,.56-1.24,1.24,0,.54,.34,1.01,.85,1.18l3.62,1.21c.92,.31,1.53,1.16,1.53,2.13,0,1.24-1.01,2.24-2.24,2.24h-.76v.5c0,.28-.22,.5-.5,.5s-.5-.22-.5-.5v-.5h-.55c-.98,0-1.89-.53-2.38-1.38-.14-.24-.06-.54,.18-.68,.24-.14,.55-.06,.68,.18,.31,.54,.89,.88,1.52,.88h2.31c.69,0,1.24-.56,1.24-1.24,0-.54-.34-1.01-.85-1.18l-3.62-1.21c-.92-.31-1.53-1.16-1.53-2.13Z"
											/></svg
										>
										<span class="font-medium">급여 지급 방법 :</span>
										<span class="ml-2 text-gray-600">
											{#if jobPostDetailDto?.wagePaymentMethod === 'INDIVIDUAL_PAYMENT'}개인 지급{:else if jobPostDetailDto?.wagePaymentMethod === 'SERVICE_PAYMENT'}서비스
												정산{:else}정보 없음{/if}
										</span>
									</div>
								</li>
								<div class="flex">
									<svg
										xmlns="http://www.w3.org/2000/svg"
										id="Layer_1"
										data-name="Layer 1"
										viewBox="0 0 24 24"
										width="15"
										height="15"
										class="mr-2"
										><path
											d="M22,11.5c0,.276-.224,.5-.5,.5h-2.87c-.872,0-1.692-.464-2.142-1.211,0,0-1.879-3.119-1.898-3.146l-2.799,6.067c-.084,.183-.266,.291-.455,.291-.07,0-.141-.015-.209-.046-.251-.116-.36-.413-.245-.664l2.877-6.235c-.129-.036-.263-.055-.4-.055h-2.593l-2.673,5.849c-.269,.656-.022,1.429,.594,1.818l5.061,2.899c.156,.089,.251,.255,.251,.434v5.5c0,.276-.224,.5-.5,.5s-.5-.224-.5-.5v-5.21l-4.828-2.766c-1.046-.661-1.457-1.948-.996-3.073l2.491-5.451h-2.931c-.571,0-1.085,.318-1.341,.829l-1.447,2.895c-.124,.247-.424,.346-.671,.224-.247-.124-.347-.424-.224-.671l1.447-2.895c.427-.853,1.283-1.382,2.236-1.382h6.624c.872,0,1.692,.464,2.142,1.211l1.844,3.062c.27,.448,.762,.727,1.285,.727h2.87c.276,0,.5,.224,.5,.5Zm-15.312,5.278c-.254-.103-.548,.021-.65,.279-.229,.573-.776,.943-1.393,.943H1.5c-.276,0-.5,.224-.5,.5s.224,.5,.5,.5h3.146c1.028,0,1.939-.617,2.321-1.572,.103-.256-.022-.547-.279-.65ZM11,2.5c0-1.378,1.122-2.5,2.5-2.5s2.5,1.122,2.5,2.5-1.122,2.5-2.5,2.5-2.5-1.122-2.5-2.5Zm1,0c0,.827,.673,1.5,1.5,1.5s1.5-.673,1.5-1.5-.673-1.5-1.5-1.5-1.5,.673-1.5,1.5Z"
										/></svg
									>
									<div class="font-medium">근무 시작 일자 :</div>
									<div class="text-sm ml-2">{jobPostDetailDto.jobStartDate}</div>
								</div>
							</ul>
							<div class="divider"></div>
							<h3 class="text-md font-medium text-green5 mb-3">모집 조건</h3>
							<div class="flex flex-col">
								<div class="flex mb-1">
									<div class="text-sm">모집 상태 :</div>
									<div class="ml-2">
										{#if jobPostDetailDto.employed}
											<div class="text-sm text-blue-900">구인완료</div>
										{:else if jobPostDetailDto.closed}
											<div class="text-sm text-rose-600">마감</div>
										{:else}
											<div class="text-sm text-emerald-700">구인중</div>
										{/if}
									</div>
								</div>
								<div class="flex my-1">
									<div class="text-sm">공고 마감 :</div>
									{#if jobPostDetailDto?.deadLine === null}
										<div class="text-sm text-rose-600 ml-2">조기 마감</div>
									{:else}
										<div class="text-xs sm:text-sm ml-2">{jobPostDetailDto?.deadLine}</div>
									{/if}
								</div>
								<div class="flex my-1">
									<div class="text-sm">지원 나이 :</div>
									<div class="text-sm ml-2">
										{jobPostDetailDto?.minAge === 0 ? '없음' : jobPostDetailDto?.minAge ?? '없음'}
									</div>
								</div>
								<div class="flex my-1">
									<div class="text-sm">성별 구분 :</div>
									<div class="text-sm ml-2">
										{jobPostDetailDto?.gender === 'MALE'
											? '남'
											: jobPostDetailDto?.gender === 'FEMALE'
												? '여'
												: '무관'}
									</div>
								</div>
							</div>
						</div>
					</div>
					<svg
						xmlns="http://www.w3.org/2000/svg"
						id="Layer_1"
						data-name="Layer 1"
						viewBox="0 0 24 24"
						width="15"
						height="15"
						class="inline-block mr-1"
					>
						<path
							d="M12,7c-2.757,0-5,2.243-5,5s2.243,5,5,5,5-2.243,5-5-2.243-5-5-5Zm0,9c-2.206,0-4-1.794-4-4s1.794-4,4-4,4,1.794,4,4-1.794,4-4,4Zm9.987-4.5c-.253-5.117-4.37-9.234-9.487-9.487V0h-1V2.013C6.383,2.266,2.266,6.383,2.013,11.5H0v1H2.013c.253,5.117,4.37,9.234,9.487,9.487v2.013h1v-2.013c5.117-.253,9.234-4.371,9.487-9.487h2.013v-1h-2.013Zm-9.987,9.5c-4.962,0-9-4.037-9-9S7.038,3,12,3s9,4.037,9,9-4.038,9-9,9Z"
						/></svg
					>
					<span class="text-sm">{jobPostDetailDto?.location ?? '정보 없음'}</span>
					<!-- 지도 표시를 위한 섹션 -->
					<section>
						<Map />
					</section>
					<div class="p-4 mt-4 text-gray-700 bg-white rounded-lg shadow border border-gray-300">
						<div class="whitespace-pre-line h-48">{jobPostDetailDto?.body}</div>
					</div>
					<div class="flex justify-center mt-5 gap-2 w-full">
						{#if !jobPostDetailDto?.closed && !jobPostDetailDto.employed && rq.isLogin && jobPostDetailDto?.author !== rq.member.username}
							<div class="join flex w-full">
								{#if interested}
									<button
										class="btn btn-neutral join-item flex-grow"
										on:click={() => removeInterest(postId)}>관심 없슈</button
									>
								{:else}
									<button class="btn flex-grow join-item" on:click={() => registerInterest(postId)}
										>관심 있슈</button
									>
								{/if}
								<button
									class="btn flex-grow join-item btn-green"
									on:click={apply}
									style="background-color: hsl(140, 60%, 50%) !important; color: white"
									>지원해유</button
								>
							</div>
						{/if}
					</div>
				</div>
			</div>
			<div class="container mx-auto px-4 py-8">
				<div class="w-full max-w-4xl mx-auto">
					<!-- 댓글 입력 폼 -->
					<div class="flex justify-between items-center mb-4">
						<textarea
							class="textarea textarea-bordered border-green4 w-full"
							placeholder="댓글을 입력하세요."
							bind:value={newComment}
						></textarea>
						<button class="btn btn-ghost mx-3 bg-white text-green-500" on:click={addComment}
							>댓글 달기</button
						>
					</div>
					<!-- 댓글 목록 -->
					<div class="space-y-4">
						{#each comments as comment}
							<div class="p-4 rounded-lg bg-base-100 shadow">
								<div class="flex items-center justify-between mb-2">
									<div class="flex items-center space-x-2">
										<div class="avatar placeholder">
											<div class="bg-neutral text-neutral-content rounded-full w-8">
												{#if comment.authorProfileImageUrl != null}
													<img src={comment.authorProfileImageUrl} alt={comment.author} />
												{:else}
													<span class="text-xs">{comment.author.slice(0, 3)}</span>
												{/if}
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
												<button
													class="btn btn-xs btn-ghost text-gray-500"
													on:click={() => submitEdit(comment.id)}>수정 완료</button
												>
											{:else}
												<button
													class="btn btn-xs btn-ghost text-gray-500"
													on:click={() => startEdit(comment.id)}>수정</button
												>
											{/if}
											<button
												class="btn btn-xs btn-ghost text-gray-500"
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

<style>
	section {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		flex: 0.6;
		margin-top: 6px; /* 6px의 상단 여백 추가 */
	}
</style>
