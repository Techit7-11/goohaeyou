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
						<div tabindex="0" role="button" class="btn btn-ghost m-1">설정</div>
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
										<div class="badge badge-neutral">확인</div>
									{:else}
										<div class="badge badge-primary">미확인</div>
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
