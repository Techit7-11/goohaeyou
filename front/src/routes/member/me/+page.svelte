<script lang="ts">
<<<<<<< HEAD
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
									<ul>
										{#each posts ?? [] as post, index}
											<li>
												<a href="/job-post/{post.id}">no.{index + 1}</a>
												<a href="/job-post/{post.id}">{post.title}</a>
												<button class="btn btn-outline btn-primary btn-sm ml-2 w-auto px-2" on:click={() => goToApplicationsList(post.id)}>지원서 확인</button>
											</li>
										{/each}
									</ul>
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
									<ul>
										{#each applicationDtoList ?? [] as applicationDto}
											<li>
												<a href="/applications/detail/{applicationDto.id}"
													>{applicationDto.jobPostName}</a
												>
												<a href="/applications/detail/{applicationDto.id}"
													>{summarizeBody(applicationDto.body)}</a
												>
											</li>
										{/each}
									</ul>
								{/await}
							</div>

							<input
								type="radio"
								name="my_tabs_2"
								role="tab"
								class="tab"
								aria-label="내가 쓴 댓글"
							/>
							<div role="tabpanel" class="tab-content p-5">
								{#await loadMyComments()}
									<p>loading...</p>
								{:then { data: commentsDtoList }}
									<ul>
										{#each commentsDtoList ?? [] as commentsDto}
											<li>
												<a href="/job-post/{commentsDto.id}"
													>{commentsDto.jobPostId}
													번 공고</a
												>
												<a href="/job-post/{commentsDto.id}"
													>[{commentsDto.content}
													]</a
												>
											</li>
										{/each}
									</ul>
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

<style>
	ul {
		list-style-type: none;
		padding: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	li {
		background-color: #ffffff;
		margin: 6px 0;
		padding: 10px;
		width: 100%; /* 화면 너비의 대부분을 차지 */
		max-width: 600px; /* 최대 너비 설정 */
		box-shadow: 0 6px 10px rgba(0, 0, 0, 0.1); /* 섬세한 그림자 효과 */
		border-radius: 8px; /* 부드럽게 둥근 모서리 */
		display: flex;
		flex-direction: column; /* 세로 정렬 */
		border: 1px solid #eee; /* 미세한 경계선 */
	}

	a {
		color: #43404e;
		text-decoration: none; /* 밑줄 제거 */
		font-weight: bold; /* 글씨 굵게 */
		margin-bottom: 8px; /* 요소 사이의 여백 */
	}

	a:hover {
		color: #a5a5a5; /* 호버 시 색상 변경 */
	}

	footer {
		width: 100%;
		background-color: #f7f7f7; /* 밝은 회색 배경 */
		color: #6f6d70;
		text-align: center;
		padding: 20px 0;
		box-shadow: 0 -4px 6px rgba(0, 0, 0, 0.1); /* 상단으로 그림자 효과 */
		border-top: 2px solid #eee; /* 상단 경계선 */
	}
=======
    import rq from '$lib/rq/rq.svelte';
    import {onMount} from 'svelte';

    onMount(() => {
        if (rq.isLogout()) {
            rq.msgError('로그인이 필요합니다.');
            rq.goTo('/member/login');
        }
    });

    async function loadMyPosts() {
        const {data} = await rq.apiEndPoints().GET('/api/member/myposts', {});
        return data;
    }

    async function loadMyApplications() {
        const {data} = await rq.apiEndPoints().GET('/api/member/myapplications', {});

        return data;
    }

    async function loadMyComments() {
        const {data} = await rq.apiEndPoints().GET('/api/member/mycomments', {});
        return data;
    }

    async function loadMyInterest() {
        const {data} = await rq.apiEndPoints().GET('/api/member/myinterest', {});
        return data;
    }
</script>

<div class="flex flex-col items-center min-h-screen bg-base-100">
    <div class="container mx-auto px-4">
        <div class="max-w-sm mx-auto my-5">
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
            </div>
        </div>
    </div>
    <div class="w-full">
        <div class="container mx-auto px-4">
            <div class="max-w-sm mx-auto my-5">
                <div role="tablist" class="tabs tabs-lifted">
                    <input
                            type="radio"
                            name="my_tabs_2"
                            role="tab"
                            class="tab"
                            aria-label="내 공고"
                            checked
                    />
                    <div role="tabpanel"
                         class="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        {#await loadMyPosts()}
                            <p>loading...</p>
                        {:then {data: jobPostDtoList}}
                            <ul>
                                {#each jobPostDtoList ?? [] as jobPostDto, index}
                                    <li>
                                        <a href="/job-post/{jobPostDto.id}">no.{index + 1}</a>
                                        <a href="/job-post/{jobPostDto.id}">{jobPostDto.title}</a>
                                    </li>
                                {/each}
                            </ul>
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
                    <div role="tabpanel"
                         class="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        {#await loadMyApplications()}
                            <p>loading...</p>
                        {:then {data: applicationDtoList}}
                            <ul>
                                {#each applicationDtoList ?? [] as applicationDto}
                                    <li>
                                        <a href="/job-post/{applicationDto.postId}">공고{applicationDto.postId}</a>
                                        <a href="/job-post/{applicationDto.postId}">{applicationDto.body}</a>
                                    </li>
                                {/each}
                            </ul>
                        {/await}
                    </div>

                    <input type="radio" name="my_tabs_2" role="tab" class="tab"
                           aria-label="내가 쓴 댓글"/>
                    <div role="tabpanel"
                         class="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        {#await loadMyComments()}
                            <p>loading...</p>
                        {:then {data: commentsDtoList}}
                            <ul>
                                {#each commentsDtoList ?? [] as commentsDto}
                                    <li>
                                        <a href="/job-post/{commentsDto.id}">{commentsDto.jobPostId}
                                            번 공고</a>
                                        <a href="/job-post/{commentsDto.id}">[{commentsDto.content}
                                            ]</a>
                                    </li>
                                {/each}
                            </ul>
                        {/await}
                    </div>

                    <input type="radio" name="my_tabs_2" role="tab" class="tab"
                           aria-label="관심 공고"/>
                    <div role="tabpanel"
                         class="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        {#await loadMyInterest()}
                            <p>loading...</p>
                        {:then {data: interestDtoList}}
                            <ul>
                                {#each interestDtoList ?? [] as interestDto}
                                    <li>
                                        <a href="/job-post/{interestDto.id}">{interestDto.id}
                                            번 공고</a>
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

<style>
    ul {
        list-style-type: none;
        padding: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    li {
        background-color: #ffffff;
        margin: 6px 0;
        padding: 10px;
        width: 100%; /* 화면 너비의 대부분을 차지 */
        max-width: 600px; /* 최대 너비 설정 */
        box-shadow: 0 6px 10px rgba(0, 0, 0, 0.1); /* 섬세한 그림자 효과 */
        border-radius: 8px; /* 부드럽게 둥근 모서리 */
        display: flex;
        flex-direction: column; /* 세로 정렬 */
        border: 1px solid #eee; /* 미세한 경계선 */
    }

    a {
        color: #43404e;
        text-decoration: none; /* 밑줄 제거 */
        font-weight: bold; /* 글씨 굵게 */
        margin-bottom: 8px; /* 요소 사이의 여백 */
    }

    a:hover {
        color: #a5a5a5; /* 호버 시 색상 변경 */
    }

    footer {
        width: 100%;
        background-color: #f7f7f7; /* 밝은 회색 배경 */
        color: #6f6d70;
        text-align: center;
        padding: 20px 0;
        box-shadow: 0 -4px 6px rgba(0, 0, 0, 0.1); /* 상단으로 그림자 효과 */
        border-top: 2px solid #eee; /* 상단 경계선 */
    }
>>>>>>> main
</style>
