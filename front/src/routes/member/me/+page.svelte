<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	function navigateToModifyPage() {
		rq.goTo('/member/social/modify');
	}

	onMount(async () => {
		// 로그인 상태를 비동기적으로 확인
		await rq.initAuth(); // 로그인 상태를 초기화
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
			return;
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

	function summarizeBody(body) {
		return body.length > 10 ? `${body.slice(0, 10)}...` : body;
	}

	function goToApplicationsList(postId) {
        window.location.href = `/applications/list/${postId}`;
    }
</script>

<div class="flex items-center justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<div class="text-center">
				<div class="font-bold text-xl">내 정보</div>
				<div class="mt-3 text-gray-500">현재 로그인한 회원의 정보입니다.</div>
			</div>
			<div class="divider"></div>
			<div class="grid grid-colos-1 gap-4">
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">아이디</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.username}</div>
				</div>
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">이름</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.name}</div>
				</div>
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">휴대폰 번호</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.phoneNumber}</div>
				</div>
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">성별</div>
					<div class="text-gray-500 flex-auto text-right">
						{#if rq.member.gender === 'MALE'}
							남자
						{:else if rq.member.gender === 'FEMALE'}
							여자
						{/if}
					</div>
				</div>
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">주소</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.location}</div>
				</div>
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">생년월일</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.birth}</div>
				</div>
				<div class="pb-10">
					<button class="w-full btn btn-ghost" on:click={navigateToModifyPage}
						>회원 정보 수정</button
					>
				</div>
			</div>
			<div class="w-full">
				<div class="container mx-auto px-4">
					<div class="max-w-sm mx-auto">
						<div role="tablist" class="tabs tabs-bordered">
							<input
								type="radio"
								name="my_tabs_2"
								role="tab"
								class="tab"
								aria-label="내 공고"
								checked
							/>
							<div role="tabpanel" class="tab-content p-5">
								{#await loadMyPosts()}
									<p>loading...</p>
								{:then { data: posts }}
										{#each posts ?? [] as post, index}
										<a href="/job-post/{post.id}" class="card-link">
                                                                                            <div class="card">
                                                                                            <div class="text-sm text-gray-500">no.{index + 1}</div>
                                                                                            <div class="text-xl font-bold">{post.title}</div>
                                                                                            <button class="btn btn-primary my-3" on:click={() => goToApplicationsList(post.id)}>지원서 확인</button>
                                                                                            <div class="divider"></div>
                                                                                            </div>
                                                                                        </a>
										{/each}
								{/await}
							</div>

							<input
								type="radio"
								name="my_tabs_2"
								role="tab"
								class="tab"
								aria-label="나의 지원"
								checked
							/>
							<div role="tabpanel" class="tab-content p-5">
								{#await loadMyApplications()}
									<p>loading...</p>
								{:then { data: applicationDtoList }}
										{#each applicationDtoList ?? [] as applicationDto}
												<a href="/applications/detail/{applicationDto.id}" class="card-link">
                                                                                                    <div class="card">
                                                                                                    <div class="text-sm text-gray-500">{applicationDto.jobPostName}</div>
                                                                                                    <div class="text-xl font-bold">{summarizeBody(applicationDto.body)}</div>
                                                                                                    <div class="divider"></div>
                                                                                                    </div>
                                                                                                </a>
										{/each}

								{/await}
							</div>

							<input
								type="radio"
								name="my_tabs_2"
								role="tab"
								class="tab"
								aria-label="내가 쓴 댓글"
							/>
							<div role="tabpanel" class="tab-content p-7">
								{#await loadMyComments()}
									<p>loading...</p>
								{:then { data: commentsDtoList }}
										{#each commentsDtoList ?? [] as commentsDto}
												<a href="/job-post/{commentsDto.jobPostId}" class="card-link">
                                                    <div class="card">
                                                    <div class="text-sm text-gray-500">{commentsDto.jobPostId}번 공고</div>
                                                    <div class="text-xl ont-bold">{commentsDto.content}</div>
                                                    <div class="divider"></div>
                                                    </div>
                                                </a>
										{/each}
								{/await}
							</div>

							<input type="radio" name="my_tabs_2" role="tab" class="tab" aria-label="관심 공고" />
							<div role="tabpanel" class="tab-content p-5">
								{#await loadMyInterest()}
									<p>loading...</p>
								{:then { data: interestDtoList }}
									<ul>
										{#each interestDtoList ?? [] as interestDto}
											<li>
												<a href="/job-post/{interestDto.id}"
													>{interestDto.id}
													번 공고</a
												>
												<a href="/job-post/{interestDto.id}">{interestDto.title}</a>
											</li>
										{/each}
									</ul>
								{/await}
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>