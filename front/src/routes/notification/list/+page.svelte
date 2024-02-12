<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

<<<<<<< HEAD
	onMount(async () => {
		// 로그인 상태를 비동기적으로 확인
		await rq.initAuth(); // 로그인 상태를 초기화
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
			return;
=======
	onMount(() => {
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
>>>>>>> f42d23d (feat:프론트 부분 작성 및 엔티티 url 추가)
		}
	});

	async function loadMyNotification() {
		const { data } = await rq.apiEndPoints().GET('/api/notification', {});
		return data;
	}

	function causeTypeCodeConvertedToString(causeTypeCode) {
		switch (causeTypeCode) {
			case 'POST_MODIFICATION':
				return '에서 내용이 변경 되어';
			case 'POST_DELETED':
				return '가 삭제 되어';
			case 'POST_INTERESTED':
				return '에서 관심을 받아서';
			case 'COMMENT_CREATED':
				return '에서 댓글이 작성 되어';
			case 'APPLICATION_CREATED':
				return '에서 지원서가 작성 되어';
			case 'APPLICATION_MODIFICATION':
				return '에 작성된 지원서가 수정 되어';
			case 'APPLICATION_APPROVED':
				return '에서 지원서를 승인 되어';
			case 'APPLICATION_UNAPPROVE':
				return '에서 지원서가 승인 되지 않아서';
		}
	}

	function resultTypeCodeConvertedToString(resultTypeCode) {
		switch (resultTypeCode) {
			case 'NOTICE':
				return '알립니다.';
			case 'DELETE':
				return '삭제 되었습니다.';
			case 'MODIFY':
				return '변경 되었습니다.';
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
			alert('읽은 알림이 삭제되었습니다.');
<<<<<<< HEAD
			location.reload(); // 페이지 새로 고침
			await loadMyNotification();
=======
>>>>>>> f42d23d (feat:프론트 부분 작성 및 엔티티 url 추가)
		} catch (error) {
			console.error('읽은 알림 삭제 중 오류가 발생했습니다.', error);
			alert('읽은 알림을 삭제하는 데 실패했습니다.');
		}
	}

	async function deleteAllNotifications() {
		try {
			await rq.apiEndPoints().DELETE('/api/notification/all');
			alert('모든 알림이 삭제되었습니다.');
<<<<<<< HEAD
			location.reload(); // 페이지 새로 고침
			await loadMyNotification();
=======
>>>>>>> f42d23d (feat:프론트 부분 작성 및 엔티티 url 추가)
		} catch (error) {
			console.error('모든 알림 삭제 중 오류가 발생했습니다.', error);
			alert('모든 알림을 삭제하는 데 실패했습니다.');
		}
	}
<<<<<<< HEAD

	async function markNotificationAsRead(notification) {
		try {
			await rq.apiEndPoints().PUT(`/api/notification/${notification.id}`);
			alert('알림이 확인되었습니다.');
		} catch (error) {
			console.error('알림 확인 중 오류가 발생했습니다.', error);
			alert('알림을 확인하는 데 실패했습니다.');
		}
	}
=======
>>>>>>> f42d23d (feat:프론트 부분 작성 및 엔티티 url 추가)
</script>

{#await loadMyNotification()}
	<span class="loading loading-spinner loading-lg"></span>
{:then { data }}
	<div class="flex justify-center mt-4">
		<button class="btn btn-lg btn-primary rounded-full mx-2" on:click={deleteAllNotifications}
			>모든 알림 삭제</button
		>
		<button class="btn btn-lg btn-primary rounded-full mx-2" on:click={deleteReadNotifications}
			>읽은 알림 삭제</button
		>
	</div>
	<div class="flex justify-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4">
			<div class="py-5">
				<ul>
					{#each data ?? [] as notification, index}
						<li>
<<<<<<< HEAD
							<a on:click={() => markNotificationAsRead(notification)} href={notification.url}
								>(No.{index + 1})</a
							>
							<a on:click={() => markNotificationAsRead(notification)} href={notification.url}
								>{createNotificationSentence(notification)}</a
							>
							<a on:click={() => markNotificationAsRead(notification)} href={notification.url}
								>일 시 : {notification.createAt}</a
							>
							<a on:click={() => markNotificationAsRead(notification)} href={notification.url}>
								{#if notification.seen}
									<div class="badge badge-neutral">확인</div>
								{:else}
									<div class="badge badge-primary">미확인</div>
=======
							<a>(No.{index + 1})</a>
							<a>{createNotificationSentence(notification)}</a>
							<a>일 시 : {notification.createAt}</a>
							<a>
								{#if notification.seen}
									<div class="badge badge-neutral">확인</div>
								{:else}
									<div class="badge badge-primary">미1확인</div>
>>>>>>> f42d23d (feat:프론트 부분 작성 및 엔티티 url 추가)
								{/if}
							</a>
						</li>
					{/each}
				</ul>
			</div>
		</div>
	</div>
{/await}

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
		margin: 12px 0;
		padding: 10px;
		padding-left: 20px;
		width: 80%; /* 화면 너비의 대부분을 차지 */
		max-width: 600px; /* 최대 너비 설정 */
		box-shadow: 0 6px 10px rgba(0, 0, 0, 0.1); /* 섬세한 그림자 효과 */
		border-radius: 8px; /* 부드럽게 둥근 모서리 */
		display: flex;
		flex-direction: column; /* 세로 정렬 */
		border: 1px solid #eee; /* 미세한 경계선 */
		cursor: pointer; /* 포인터 커서 변경 */
	}

	a {
		color: #43404e;
		text-decoration: none; /* 밑줄 제거 */
		font-weight: bold; /* 글씨 굵게 */
		margin-bottom: 5px; /* 요소 사이의 여백 */
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
</style>
