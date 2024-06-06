<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	let reviews = [];
	let authCode = '';
	let showSendEmailModal = false;
	let showVerifyCodeModal = false;
	let profileImageFile: File | null = null;
	let imagePreviewUrl = '';
	let currentProfileImageUrl = '';

	const baseUrl = import.meta.env.VITE_CORE_API_BASE_URL;

	function navigateToChatRoomListPage() {
		rq.goTo('/chat/list');
	}

	onMount(async () => {
		try {
			await rq.initAuth(); // 로그인 상태를 초기화
			if (rq.isLogout()) {
				rq.msgError('로그인이 필요합니다.');
				rq.goTo('/member/login');
				return;
			}

			currentProfileImageUrl = rq.member.profileImageUrl || '';
			loadMyReview();
		} catch (error) {
			console.error('인증 초기화 중 오류 발생:', error);
			rq.msgError('인증 과정에서 오류가 발생했습니다.');
			rq.goTo('/member/login');
		}
	});

	async function loadMyPosts() {
		const { data } = await rq.apiEndPoints().GET('/api/member/myposts', {});
		return data;
	}

	async function loadMyApplications() {
		const { data } = await rq.apiEndPoints().GET('/api/member/myapplications', {});

		return data;
	}

	async function loadMyComments() {
		const { data } = await rq.apiEndPoints().GET('/api/member/mycomments', {});
		return data;
	}

	async function loadMyInterest() {
		const { data } = await rq.apiEndPoints().GET('/api/member/myinterest', {});
		return data;
	}

	async function loadMyReview() {
		try {
			const { data } = await rq.apiEndPoints().GET('/api/member/review', {});
			reviews = data.data;
			console.log('로드된 리뷰 데이터:', reviews);
		} catch (error) {
			console.error('리뷰 로드 중 오류 발생:', error);
		}
	}
	async function deleteReview(reviewId) {
		try {
			const confirmation = confirm('리뷰를 삭제하시겠습니까?');
			if (!confirmation) {
				return;
			}
			await rq.apiEndPoints().DELETE(`/api/member/review/${reviewId}`);
			console.log(`리뷰 삭제됨: ${reviewId}`);
			await loadMyReview();
			alert('리뷰가 삭제되었습니다.');
		} catch (error) {
			console.error('리뷰 삭제 중 오류 발생:', error);
			alert('리뷰 삭제 중 오류가 발생했습니다.');
		}
	}

	function summarizeBody(body) {
		return body.length > 10 ? `${body.slice(0, 10)}...` : body;
	}

	function goToApplicationsList(postId) {
		window.location.href = `/applications/list/${postId}`;
	}

	function navigateToModifyPage() {
		rq.goTo('/member/social/modify');
	}

	function generateStars(score) {
		const totalStars = 10;
		let stars = [];
		for (let i = 1; i <= totalStars; i++) {
			const starValue = i * 0.5;
			stars.push({ value: starValue, checked: starValue <= score });
		}
		return stars;
	}

	// 본인 인증하기 버튼 클릭 시 호출
	function openSendEmailModal() {
		showSendEmailModal = true;
	}

	// 이메일로 인증 코드 발송
	async function sendAuthEmail() {
		const response = await rq.apiEndPoints().POST('/api/auth/email');
		if (response.data?.resultType === 'SUCCESS') {
			showSendEmailModal = false;
			showVerifyCodeModal = true;
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('이메일 발송 중 오류가 발생했습니다.');
		}
	}

	// 사용자가 입력한 인증 코드 검증
	async function verifyAuthCode() {
		const response = await rq.apiEndPoints().PATCH(`/api/members/verify/${authCode}`);
		if (response.data?.resultType === 'SUCCESS') {
			rq.member.authenticated = true;
			showVerifyCodeModal = false;
			alert('인증이 완료되었습니다.');
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('인증 코드 검증 중 오류가 발생했습니다.');
		}
	}

	function closeModal() {
		showSendEmailModal = false;
		showVerifyCodeModal = false;
	}

	function handleImageChange(event: Event) {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files[0]) {
			profileImageFile = input.files[0];
			imagePreviewUrl = URL.createObjectURL(profileImageFile);
		}
	}

	async function updateProfileImage() {
		if (!profileImageFile) {
			alert('프로필 사진을 선택해주세요.');
			return;
		}

		// FormData 객체 생성 및 파일 추가
		const formData = new FormData();
		formData.append('profileImageFile', profileImageFile);

		console.log('Uploading file:', profileImageFile); // 파일 객체 로그 찍기
		console.log('FormData entries:', Array.from(formData.entries())); // FormData 내용 확인

		try {
			// rq.apiEndPoints() 대신 직접 fetch 사용
			const response = await fetch(baseUrl + '/api/members/image', {
				method: 'PUT',
				body: formData,
				credentials: 'include' // 쿠키가 필요한 경우
			});

			const responseData = await response.json(); // 응답을 JSON 형태로 파싱
			console.log('Server response:', responseData); // 서버 응답 로그 찍기

			if (responseData.resultType === 'SUCCESS') {
				rq.msgInfo('프로필 이미지가 성공적으로 업데이트되었습니다.');
				location.reload();
			} else if (responseData.resultType === 'CUSTOM_EXCEPTION') {
				rq.msgError(responseData.message);
			} else {
				rq.msgError('프로필 이미지 업데이트를 실패하였습니다.');
			}
		} catch (error) {
			console.error('Error during profile image update:', error); // 에러 로그 찍기
			rq.msgError('에러가 발생하였습니다.');
		}
	}
</script>

<div class="flex justify-center items-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="w-full max-w-4xl mx-auto my-10">
			<div class="text-center">
				<div class="text-xl font-semibold text-gray-800">내 정보</div>
				<div class="mt-3 text-green-600">현재 로그인한 회원의 정보입니다.</div>
				<div class="flex justify-center items-center mt-8 space-x-4">
					<button
						class="text-xs px-4 py-1 rounded-md bg-green3 text-white hover:bg-green6 transition-colors duration-150 ease-in-out"
						on:click={navigateToChatRoomListPage}>채팅방 이동</button
					>
					{#if !rq.member.authenticated && rq.member.email != null}
						<button
							class="text-xs px-4 py-1 rounded-md bg-green3 text-white hover:bg-green6 transition-colors duration-150 ease-in-out"
							on:click={openSendEmailModal}>본인 인증하기</button
						>
					{/if}
				</div>
			</div>
			<div class="border-t-2 border-gray-200 my-6"></div>
			<div class="grid grid-cols-1 gap-2 pl-2 pr-8">
				<div class="flex items-center justify-between bg-gray-50 px-4 py-3">
					<div class="flex items-center space-x-4">
						{#if currentProfileImageUrl == ''}
							<div class="avatar">
								<button
									type="button"
									class="w-12 h-12 rounded-full bg-gray-200 flex items-center justify-center cursor-pointer"
									on:click={() => document.getElementById('profileImageFile').click()}
									on:keydown={(e) => {
										if (e.key === 'Enter' || e.key === ' ') {
											document.getElementById('profileImageFile').click();
										}
									}}
									role="button"
									tabindex="0"
								>
									<svg
										xmlns="http://www.w3.org/2000/svg"
										class="h-6 w-6 text-gray-600"
										fill="none"
										viewBox="0 0 24 24"
										stroke="currentColor"
									>
										<path
											stroke-linecap="round"
											stroke-linejoin="round"
											stroke-width="2"
											d="M12 6v6m0 0v6m0-6h6m-6 0H6"
										/>
									</svg>
								</button>
							</div>
							<input
								id="profileImageFile"
								type="file"
								accept="image/*"
								class="hidden"
								on:change={handleImageChange}
							/>
						{:else}
							<div class="avatar">
								<div class="w-12 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
									<img src={currentProfileImageUrl} alt="프로필 사진" />
								</div>
							</div>
						{/if}
					</div>
					<div class="flex items-center space-x-6">
						<div class="text-sm font-medium text-gray-600">ID :</div>
						<div class="text-sm font-bold text-gray-500">{rq.member.username}</div>
					</div>
				</div>
				<div class="flex items-center justify-between bg-gray-50 px-4 py-3">
					<div class="text-xs font-medium text-gray-500">이름</div>
					<div class="ml-4 text-xs text-gray-900">{rq.member.name}</div>
				</div>
				<div class="flex items-center justify-between bg-gray-50 px-4 py-3">
					<div class="text-xs font-medium text-gray-500">휴대폰 번호</div>
					<div class="ml-4 text-xs text-gray-900">{rq.member.phoneNumber}</div>
				</div>
				{#if rq.member.email != null}
					<div class="px-4 py-2 flex items-center justify-between">
						<div class="text-xs font-medium text-gray-500">이메일</div>
						<div class="text-xs text-gray-900">{rq.member.email}</div>
					</div>
				{/if}
				<div class="px-4 py-2 flex items-center justify-between">
					<div class="text-xs font-medium text-gray-500">주소</div>
					<div class="text-xs text-gray-900">{rq.member.location}</div>
				</div>
				<div class="px-4 py-2 flex items-center justify-between">
					<div class="text-xs font-medium text-gray-500">성별</div>
					<div class="text-xs text-gray-900">
						{#if rq.member.gender === 'MALE'}남자{:else if rq.member.gender === 'FEMALE'}여자{/if}
					</div>
				</div>
				<div class="px-4 py-2 flex items-center justify-between">
					<div class="text-xs font-medium text-gray-500">생년월일</div>
					<div class="text-xs text-gray-900">{rq.member.birth}</div>
				</div>
				<div>
					<button class="w-full btn btn-ghost" on:click={navigateToModifyPage}
						>회원 정보 수정</button
					>
				</div>
			</div>

			<!-- 프로필 사진 미리보기 모달 -->
			{#if imagePreviewUrl}
				<div class="fixed z-10 inset-0 overflow-y-auto">
					<div
						class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0"
					>
						<div class="fixed inset-0 transition-opacity" aria-hidden="true">
							<div class="absolute inset-0 bg-gray-500 opacity-75"></div>
						</div>
						<span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true"
							>​</span
						>
						<div
							class="inline-block align-middle bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:max-w-lg sm:w-full"
							role="dialog"
							aria-modal="true"
							aria-labelledby="modal-headline"
						>
							<div class="bg-white px-6 pt-5 pb-4 sm:p-6 sm:pb-4">
								<div class="sm:flex sm:items-center">
									<div
										class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-green-100 sm:mx-0 sm:h-10 sm:w-10"
									>
										<svg
											class="h-6 w-6 text-green-600"
											xmlns="http://www.w3.org/2000/svg"
											fill="none"
											viewBox="0 0 24 24"
											stroke="currentColor"
											aria-hidden="true"
										>
											<path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M5 13l4 4L19 7"
											/>
										</svg>
									</div>
									<div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
										<h3 class="text-md leading-6 font-medium text-gray-800" id="modal-headline">
											프로필 사진 미리보기
										</h3>
										<div class="mt-2">
											<div class="flex justify-center">
												<div class="avatar">
													<div class="rounded-full w-24 h-24">
														<img src={imagePreviewUrl} alt="프로필 사진 미리보기" />
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="bg-gray-50 px-6 py-3 sm:px-6 flex justify-between">
								<button
									type="button"
									class="inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-green-600 text-base font-medium text-white hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 w-auto text-sm"
									on:click={updateProfileImage}
								>
									업로드
								</button>
								<button
									type="button"
									class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 w-auto text-sm"
									on:click={() => (imagePreviewUrl = '')}
								>
									취소
								</button>
							</div>
						</div>
					</div>
				</div>
			{/if}

			<div class="border-t-2 border-gray-200 my-6"></div>
			<div class="flex justify-center">
				<div class="join flex flex-wrap gap-4 justify-around">
					<label for="my_modal_1" class="btn btn-ghost join-item">작성 공고</label>
					<input type="checkbox" id="my_modal_1" class="modal-toggle" />
					<div class="modal" role="dialog">
						<div class="modal-box">
							{#await loadMyPosts()}
								<div class="flex items-center justify-center min-h-screen">
									<span class="loading loading-dots loading-lg"></span>
								</div>
							{:then { data: posts }}
								{#each posts ?? [] as post, index}
									<a href="/job-post/{post.id}" class="card-link">
										<div class="card">
											<div class="text-sm text-gray-500">no.{index + 1}</div>
											<div class="text-lg font-bold truncate">{post.title}</div>
										</div>
									</a>
									<button
										class="btn btn bg-green4 text-white hover:bg-green6 my-3 w-full"
										on:click={() => goToApplicationsList(post.id)}>지원서 확인</button
									>
									<div class="divider"></div>
								{/each}
							{/await}
						</div>
						<label class="modal-backdrop" for="my_modal_1">Close</label>
					</div>
					<label for="my_modal_2" class="btn btn-ghost join-item">지원 현황</label>
					<input type="checkbox" id="my_modal_2" class="modal-toggle" />
					<div class="modal" role="dialog">
						<div class="modal-box">
							{#await loadMyApplications()}
								<p>loading...</p>
							{:then { data: applicationDtoList }}
								{#each applicationDtoList ?? [] as applicationDto}
									<a href="/applications/detail/{applicationDto.id}" class="card-link">
										<div class="card">
											<div class="text-sm text-gray-500">{applicationDto.jobPostName}</div>
											<div class="text-lg font-bold truncate">
												{summarizeBody(applicationDto.body)}
											</div>
											<div class="divider"></div>
										</div>
									</a>
								{/each}
							{/await}
						</div>
						<label class="modal-backdrop" for="my_modal_2">Close</label>
					</div>
					<label for="my_modal_3" class="btn btn-ghost join-item">작성 댓글</label>
					<input type="checkbox" id="my_modal_3" class="modal-toggle" />
					<div class="modal" role="dialog">
						<div class="modal-box">
							{#await loadMyComments()}
								<p>loading...</p>
							{:then { data: commentsDtoList }}
								{#each commentsDtoList ?? [] as commentsDto}
									<a href="/job-post/{commentsDto.jobPostId}" class="card-link">
										<div class="card">
											<div class="text-sm text-gray-500">{commentsDto.jobPostId}번 공고</div>
											<div class="text-lg font-bold truncate">{commentsDto.content}</div>
											<div class="divider"></div>
										</div>
									</a>
								{/each}
							{/await}
						</div>
						<label class="modal-backdrop" for="my_modal_3">Close</label>
					</div>
					<label for="my_modal_4" class="btn btn-ghost join-item">관심 공고</label>
					<input type="checkbox" id="my_modal_4" class="modal-toggle" />
					<div class="modal" role="dialog">
						<div class="modal-box">
							{#await loadMyInterest()}
								<p>loading...</p>
							{:then { data: interestDtoList }}
								{#each interestDtoList ?? [] as interestDto}
									<a href="/job-post/{interestDto.id}" class="card-link">
										<div class="card">
											<div class="text-sm text-gray-500">{interestDto.id}번 공고</div>
											<div class="text-lg font-bold truncate">{interestDto.title}</div>
											<div class="divider"></div>
										</div>
									</a>
								{/each}
							{/await}
						</div>
						<label class="modal-backdrop" for="my_modal_4">Close</label>
					</div>
					<label for="my_modal_5" class="btn btn-ghost join-item">작성 리뷰</label>
					<input type="checkbox" id="my_modal_5" class="modal-toggle" />
					<div class="modal" role="dialog">
						<div class="modal-box">
							{#each reviews ?? [] as ReviewDto}
								<div class="card flex flex-row justify-between items-center">
									<a href="/applications/detail/{ReviewDto.jobPostingId}" class="card-link">
										<div>
											<div class="flex items-center">
												<div class="text-sm text-gray-500">{ReviewDto.jobPostingId}번 지원서</div>
												<div class="rating rating-md rating-half mx-2 mb-2">
													{#each generateStars(ReviewDto.score) as star (star.value)}
														<input
															class={`mask mask-star-2 ${star.checked ? 'bg-green-500' : 'bg-gray-300'} ${star.value % 1 === 0.5 ? 'mask-half-1' : 'mask-half-2'}`}
															disabled
														/>
													{/each}
												</div>
											</div>
											<div class="text-lg font-bold truncate">{ReviewDto.body}</div>
										</div>
									</a>
									<div>
										<button class="btn btn-ghost" on:click={() => deleteReview(ReviewDto.id)}
											>삭제</button
										>
									</div>
								</div>
								<div class="divider"></div>
							{/each}
						</div>
						<label class="modal-backdrop" for="my_modal_5">Close</label>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

{#if showSendEmailModal}
	<div class="modal modal-open">
		<div class="modal-box">
			<h3 class="font-bold text-lg mt-1">이메일 본인인증</h3>
			<p class="py-4">계속하기 위해, 아래 버튼으로 인증 코드를 요청해주세요.</p>
			<div class="modal-action">
				<button class="btn bg-green-500 hover:bg-green-600 text-white" on:click={sendAuthEmail}
					>인증 코드 발송</button
				>
				<button class="btn" on:click={closeModal}>닫기</button>
			</div>
		</div>
	</div>
{/if}

{#if showVerifyCodeModal}
	<div class="modal modal-open">
		<div class="modal-box">
			<h3 class="font-bold text-lg mt-1 mb-4">인증 코드 입력</h3>
			<p class="py-4">인증 코드를 발송했습니다. 이메일을 확인해주세요.</p>
			<input
				type="text"
				bind:value={authCode}
				placeholder="인증 코드"
				class="input input-bordered w-full max-w-xs"
			/>
			<p class="py-4 mt-2">인증 코드를 입력하고 본인 인증을 완료해주세요.</p>
			<p class="text-sm text-gray-600">인증 코드는 <strong>30분</strong> 동안 유효합니다.</p>
			<div class="modal-action">
				<button class="btn bg-green-500 hover:bg-green-600 text-white" on:click={verifyAuthCode}
					>인증 코드 확인</button
				>
				<button class="btn" on:click={closeModal}>닫기</button>
			</div>
		</div>
	</div>
{/if}
