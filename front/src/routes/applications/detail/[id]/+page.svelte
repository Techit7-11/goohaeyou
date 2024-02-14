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

	function calculateAge(birthDate) {
		const birth = new Date(birthDate);
		const today = new Date();
		let age = today.getFullYear() - birth.getFullYear();
		const m = today.getMonth() - birth.getMonth();
		if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
			age--;
		}
		return age;
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
			await rq.apiEndPoints().PATCH(`/api/employ/${jobPostId}/${applicationId}`, {
				headers: { 'Content-Type': 'application/json' }
			});

			window.location.reload(); // 승인 후 페이지 새로고침
		}
	}

	async function deleteApplication(applicationId: number) {
		try {
			const response = await rq.apiEndPoints().DELETE(`/api/applications/${applicationId}`, {
				headers: { 'Content-Type': 'application/json' }
			});

			if (response.data?.msg == 'CUSTOM_EXCEPTION') {
				alert(response.data?.data?.message);
			}

			if (response.data?.statusCode === 204) {
				alert('지원서가 삭제되었습니다.');
				rq.goTo('/member/me');
			}
		} catch (error) {
			alert('지원서 삭제에 실패했습니다. 다시 시도해주세요.');
		}
	}

	function goToEditPage(applicationId: number) {
		rq.goTo(`/applications/modify/${applicationId}`);
	}
</script>

{#await loadApplication() then application}
	<div class="container mx-auto p-4">
		<div class="card bg-base-100 shadow-xl rounded-lg p-5">
			<h2 class="card-title text-xl font-bold mb-4">지원서 상세 정보</h2>
			<p class="mb-2"><strong>지원서 번호:</strong> {application.id}</p>
			<p class="mb-2"><strong>지원자:</strong> {application.author}</p>
			<p class="mb-2"><strong>지원자 나이:</strong> {calculateAge(application.birth)}</p>
			<p class="mb-2"><strong>지원자 연락처:</strong> {application.phone}</p>
			<p class="mb-2"><strong>지원자 주소:</strong> {application.location}</p>
			<p class="mb-2"><strong>제출일:</strong> {formatDate(application.createdAt)}</p>
			<div class="mb-2">
				<strong>내용:</strong>
				<div class="p-3 bg-gray-100 rounded overflow-auto" style="max-height: 200px;">
					{application.body}
				</div>
			</div>

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

			{#if application.approve == null}
				<div class="text-center mt-2 flex justify-center items-center space-x-2">
					{#if application.author == rq.member.username}
						<button
							class="btn btn-active btn-primary btn-sm"
							on:click={() => goToEditPage(application.id)}>지원서 수정</button
						>
					{/if}

					{#if application.author == rq.member.username}
						<button class="btn btn-active btn-sm" on:click={() => deleteApplication(application.id)}
							>지원서 삭제</button
						>
					{/if}
				</div>
			{/if}

			<!-- 승인 버튼 -->
			{#if application.approve == null && application.jobPostAuthorUsername == rq.member.username}
				<div class="text-center mt-2">
					<button
						class="btn btn-active btn-accent"
						on:click={() => approve(application.jobPostId, application.id)}>승인하기</button
					>
				</div>
			{/if}

			<!-- 승인완료 버튼 -->
			{#if application.approve == true && application.jobPostAuthorUsername == rq.member.username}
				<div class="text-center mt-2">
					<button class="btn btn-disabled" disabled>승인완료</button>
				</div>
			{/if}
		</div>
	</div>
{:catch error}
	<p class="text-error">데이터를 로드하는 동안 오류가 발생했습니다: {error.message}</p>
{/await}
