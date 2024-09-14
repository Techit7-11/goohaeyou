<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	onMount(async () => {
		// 로그인 상태를 비동기적으로 확인
		await rq.initAuth(); // 로그인 상태를 초기화
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
			return;
		}
	});

	async function loadMyNotification() {
		const { data } = await rq.apiEndPoints().GET('/api/notification', {});
		return data;
	}

	function causeTypeCodeConvertedToString(causeTypeCode) {
		switch (causeTypeCode) {
			case 'POST_MODIFICATION':
				return '에서 내용이 변경 되었습니다.';
			case 'POST_DELETED':
				return '가 삭제 되었습니다.';
			case 'POST_INTERESTED':
				return '에서 관심을 받았습니다.';
			case 'POST_DEADLINE':
				return '에서 지원서 접수가 마감 되었습니다.';
			case 'COMMENT_CREATED':
				return '에서 댓글이 작성 되었습니다.';
			case 'APPLICATION_CREATED':
				return '에서 지원서가 작성 되었습니다.';
			case 'APPLICATION_MODIFICATION':
				return '에 작성된 지원서가 수정 되었습니다.';
			case 'APPLICATION_APPROVED':
				return '에서 지원서를 승인 되었습니다.';
			case 'APPLICATION_UNAPPROVE':
				return '에서 지원서가 승인 되지 않았습니다.';
			case 'CHATROOM_CREATED':
				return '새로운 채팅방이 생성되었습니다.';
			case 'CALCULATE_PAYMENT':
				return '에 대한 정산이 되었습니다.';
		}
	}

	function resultTypeCodeConvertedToString(resultTypeCode) {
		switch (resultTypeCode) {
			case 'NOTICE':
				return '';
			case 'DELETE':
				return '삭제 되었습니다.';
			case 'APPROVE':
				return '지원서를 승인 해 주세요.';
		}
	}

	function createNotificationSentence(notification) {
		const relPostTitle = notification.relPostTitle;
		const cause = causeTypeCodeConvertedToString(notification.causeTypeCode);
		const result = resultTypeCodeConvertedToString(notification.resultTypeCode);

		return `"${relPostTitle}" 제목의 공고${cause} ${result}`;
	}

	async function deleteReadNotifications() {
		try {
			await rq.apiEndPoints().DELETE('/api/notification/read');
			alert('읽은 알림이 삭제 되었습니다.');
			location.reload(); // 페이지 새로 고침
			await loadMyNotification();
		} catch (error) {
			console.error('읽은 알림 삭제 중 오류가 발생했습니다.', error);
			alert('읽은 알림을 삭제 하는 데 실패했습니다.');
		}
	}

	async function deleteAllNotifications() {
		try {
			await rq.apiEndPoints().DELETE('/api/notification/all');
			alert('모든 알림이 삭제 되었습니다.');
			location.reload(); // 페이지 새로 고침
			await loadMyNotification();
		} catch (error) {
			console.error('모든 알림 삭제 중 오류가 발생했습니다.', error);
			alert('모든 알림을 삭제 하는 데 실패 했습니다.');
		}
	}

	async function markNotificationAsRead(notification) {
		try {
			await rq.apiEndPoints().PUT(`/api/notification/${notification.id}`);
			alert('알림이 확인 되었습니다.');
		} catch (error) {
			console.error('알림 확인 중 오류가 발생했습니다.', error);
			alert('알림을 확인 하는 데 실패 했습니다.');
		}
	}
</script>

{#await loadMyNotification()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data }}
	<div class="flex justify-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div class="w-full max-w-4xl mx-auto py-5">
				<div class="flex flex-row justify-between items-center w-full mb-3">
					<div class="invisible btn">알림</div>
					<div class="text-lg font-bold">알림</div>
					<div class="dropdown dropdown-hover dropdown-bottom dropdown-end">
						<div tabindex="0" role="button" class="btn btn-ghost m-1">
							<svg
								version="1.0"
								xmlns="http://www.w3.org/2000/svg"
								class="w-6 h-6"
								viewBox="0 0 512.000000 512.000000"
								preserveAspectRatio="xMidYMid meet"
							>
								<g
									transform="translate(0.000000,512.000000) scale(0.100000,-0.100000)"
									fill="#000000"
									stroke="none"
								>
									<path
										d="M2155 4561 c-64 -39 -67 -49 -100 -269 l-30 -207 -65 -31 c-108 -52
                        -91 -57 -258 70 -83 63 -159 119 -171 125 -25 14 -81 14 -118 1 -37 -14 -521
                        -496 -539 -537 -19 -45 -17 -102 4 -138 10 -16 67 -95 126 -173 l108 -144 -33
                        -80 c-18 -44 -40 -83 -49 -87 -8 -5 -44 -11 -80 -15 -135 -14 -314 -47 -340
                        -63 -16 -9 -38 -33 -49 -53 -20 -36 -21 -51 -21 -404 0 -396 0 -395 54 -446
                        24 -21 51 -28 234 -55 l207 -30 31 -65 c52 -108 57 -91 -70 -258 -63 -83 -119
                        -159 -125 -171 -14 -25 -14 -81 -1 -118 14 -37 496 -521 537 -539 18 -8 50
                        -14 71 -14 44 0 65 13 257 154 71 53 130 96 131 96 14 0 152 -66 160 -76 6 -8
                        15 -47 19 -87 13 -132 45 -309 62 -337 9 -16 33 -38 53 -49 36 -20 51 -21 404
                        -21 400 0 399 0 448 58 21 25 28 56 52 228 16 109 34 204 40 211 18 19 157 73
                        169 66 7 -3 76 -55 153 -114 78 -59 151 -112 162 -118 28 -15 86 -14 125 3 41
                        18 523 502 537 539 16 44 12 99 -10 135 -12 19 -68 97 -126 174 l-106 140 33
                        80 c18 44 40 83 49 87 8 5 44 11 80 15 135 14 314 47 340 63 16 9 38 33 49 54
                        21 37 22 46 19 412 l-3 374 -27 35 c-15 19 -39 40 -55 46 -15 7 -113 24 -218
                        40 l-190 27 -31 65 c-52 108 -57 91 70 258 63 83 119 159 125 171 15 28 14 85
                        -3 124 -18 41 -502 523 -539 537 -44 16 -99 12 -135 -10 -19 -12 -97 -68 -174
                        -126 l-140 -106 -80 33 c-44 18 -83 40 -87 49 -5 8 -11 44 -15 80 -14 135 -47
                        314 -63 340 -9 16 -33 38 -53 49 -36 20 -51 21 -406 21 -342 0 -371 -1 -399
                        -19z m599 -318 c2 -10 10 -58 16 -108 27 -206 51 -274 107 -306 15 -9 100 -47
                        188 -85 116 -48 176 -68 218 -72 66 -5 65 -6 253 134 l111 82 133 -133 132
                        -132 -92 -119 c-152 -197 -158 -221 -90 -380 23 -54 55 -132 72 -174 38 -97
                        68 -135 120 -154 35 -13 288 -56 327 -56 8 0 11 -56 9 -187 l-3 -187 -154 -23
                        c-165 -24 -228 -46 -260 -91 -20 -29 -107 -233 -149 -349 -34 -97 -27 -116 99
                        -288 55 -75 99 -140 97 -143 -2 -4 -60 -60 -130 -125 -84 -77 -132 -116 -140
                        -111 -7 5 -53 40 -103 78 -128 100 -191 136 -237 136 -41 0 -356 -124 -415
                        -163 -53 -35 -66 -70 -93 -247 l-25 -165 -185 0 -185 0 -23 155 c-13 85 -32
                        173 -42 196 -21 49 -64 83 -145 114 -33 13 -105 43 -160 68 -103 46 -182 61
                        -225 44 -14 -5 -86 -55 -161 -110 l-137 -100 -132 133 -132 132 87 112 c48 62
                        97 131 111 154 39 69 33 105 -52 310 -82 199 -109 238 -177 256 -36 10 -293
                        51 -318 51 -5 0 -9 83 -9 184 l0 184 63 7 c81 8 232 33 279 46 63 16 99 72
                        180 273 47 119 72 194 71 218 0 31 -19 62 -107 182 -58 80 -106 149 -106 153
                        0 5 58 66 129 137 l128 128 126 -95 c159 -122 177 -130 241 -125 54 5 344 121
                        404 161 48 33 68 86 92 252 12 83 24 153 27 158 2 4 85 7 183 7 162 0 179 -2
                        184 -17z"
									/>
									<path
										d="M2422 3389 c-335 -59 -599 -309 -689 -649 -12 -46 -17 -102 -16 -195
                        0 -111 4 -143 27 -218 78 -261 250 -456 491 -556 111 -46 196 -63 320 -64 661
                        -1 1070 720 728 1285 -148 245 -404 394 -693 403 -63 2 -139 0 -168 -6z m257
                        -324 c216 -51 381 -241 407 -466 10 -83 -12 -189 -57 -279 -132 -263 -441
                        -368 -704 -237 -186 92 -309 307 -291 509 28 319 337 545 645 473z"
									/>
								</g>
							</svg>
						</div>
						<ul
							tabindex="0"
							class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52"
						>
							<li><a on:click={deleteReadNotifications}>읽은 알림 삭제</a></li>
							<li><a on:click={deleteAllNotifications}>모든 알림 삭제</a></li>
						</ul>
					</div>
				</div>
				<ul>
					{#each data ?? [] as notification, index}
						<li>
							<a on:click={() => markNotificationAsRead(notification)} href={notification.url}></a>
							<a on:click={() => markNotificationAsRead(notification)} href={notification.url}
								>{createNotificationSentence(notification)}</a
							>
							<div class="flex justify-between mt-2">
								<a on:click={() => markNotificationAsRead(notification)} href={notification.url}
									>일 시 : {notification.createAt}</a
								>
								<a on:click={() => markNotificationAsRead(notification)} href={notification.url}>
									{#if notification.seen}
										<div class="badge bg-gray-300 text-white">확인</div>
									{:else}
										<div class="badge bg-green4 text-white">미확인</div>
									{/if}
								</a>
							</div>
							<div class="divider"></div>
						</li>
					{/each}
				</ul>
			</div>
		</div>
	</div>
{/await}

<svelte:head>
	<script>
		var global = window;
	</script>
</svelte:head>
