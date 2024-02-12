<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import Swal from 'sweetalert2'; // SweetAlert2를 사용하여 예/아니오 확인 메시지 표시

	async function loadApplication() {
		const { data } = await rq.apiEndPoints().GET('/api/applications/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			}
		});

		return data!.data!;
	}

	function formatDate(dateString) {
		const options = {
			year: '2-digit',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit'
		};
		return new Date(dateString).toLocaleString('ko-KR', options);
	}

	async function approve(jobPostId, applicationId) {
		// SweetAlert2를 사용하여 사용자에게 승인 확인 요청
		const result = await Swal.fire({
			title: '승인하시겠습니까?',
			text: '이 작업은 다시 되돌릴 수 없습니다.',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'OK'
		});

		if (result.isConfirmed) {
			await rq.apiEndPoints().PUT(`/api/employ/${jobPostId}/${applicationId}`, {
				headers: { 'Content-Type': 'application/json' }
			});
			Swal.fire('해당 지원서가 승인되었습니다.', '완료');
			window.location.reload(); // 승인 후 페이지 새로고침
		}
	}
</script>

{#await loadApplication() then application}
	<div class="container mx-auto p-4">
		<div class="card bg-base-100 shadow-xl rounded-lg p-5">
			<h2 class="card-title text-xl font-bold mb-4">지원서 상세 정보</h2>
			<p class="mb-2"><strong>지원서 번호:</strong> {application.id}</p>
			<p class="mb-2"><strong>지원자:</strong> {application.author}</p>
			<p class="mb-2"><strong>내용:</strong> {application.body}</p>
			<p class="mb-2"><strong>제출일:</strong> {formatDate(application.createdAt)}</p>
			<p class="mb-2">
				<strong>승인 상태:</strong>
				{#if application.approve === true}
					<span class="badge badge-success">승인</span>
				{:else if application.approve === false}
					<span class="badge badge-error">미승인</span>
				{:else}
					<span class="badge badge-warning">진행중</span>
				{/if}
			</p>
			<!-- 승인 버튼 -->
			{#if application.approve == null}
				<div class="text-center mt-2">
					<button
						class="btn btn-outline btn-info"
						on:click={() => approve(application.jobPostId, application.id)}>승인하기</button
					>
				</div>
			{/if}
			<!-- 승인완료 버튼 -->
			{#if application.approve == true}
				<div class="text-center mt-2">
					<button class="btn btn-disabled" disabled>승인완료</button>
				</div>
			{/if}
		</div>
	</div>
{:catch error}
	<p class="text-error">데이터를 로드하는 동안 오류가 발생했습니다: {error.message}</p>
{/await}
