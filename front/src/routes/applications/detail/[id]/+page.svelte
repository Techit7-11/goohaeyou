<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

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

	async function completeJobManually(applicationId: number) {
		const response = await rq
			.apiEndPoints()
			.PATCH(`/api/jobs/complete/${applicationId}/manually`, {});

		if (response.data?.statusCode === 204) {
			location.reload();
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			const customErrorMessage = response.data?.data?.message;
			rq.msgError(customErrorMessage);
		} else {
			rq.msgError('요청 중 오류가 발생했습니다.');
		}
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

	function goToEditPage(applicationId: number) {
		rq.goTo(`/applications/modify/${applicationId}`);
	}

	function goToPaymentPage(applicationId: number, wages: number) {
		rq.goTo(`/payment/pay/${$page.params.id}/${wages}`);
	}
</script>

{#await loadApplication() then application}
	<div class="flex justify-center items-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4 mt-5">
			<div class="w-full max-w-4xl mx-auto">
				<div class="card bg-base-100 shadow-lg rounded-lg p-5">
					<div class="flex items-center mb-4">
						<h2 class="px-1 card-title text-xl font-bold">지원서 상세</h2>
					</div>

					<div class="bg-white rounded-md mb-4">
						<div class="px-2 py-2 border-b border-gray-200">
							<p class="font-semibold">지원자 정보</p>
						</div>
						<div class="flex">
							<div class="mx-2 mt-4 flex-grow max-w-40">
								<p class="text-gray-700">ID</p>
								<p class="text-gray-700">이름</p>
								<p class="text-gray-700">나이</p>
								<p class="text-gray-700">연락처</p>
								<p class="text-gray-700">주소</p>
							</div>
							<div class="mx-2 mt-4">
								<p>{application.author}</p>
								<p>{application.name}</p>
								<p>{calculateAge(application.birth)}</p>
								<p>{application.phone}</p>
								<p>{application.location}</p>
							</div>
						</div>
					</div>
					<hr class="border-t border-gray-300 opacity-50" />
					<div class="flex">
						<div class="mx-2 mt-4 flex-grow max-w-40">
							<p class="text-gray-700">지원서 번호</p>
							<p class="text-gray-700">제출일</p>
							<p class="text-gray-700">승인 상태</p>
						</div>
						<div class="mx-2 mt-4">
							<p>{application.id}</p>
							<p>{formatDate(application.createdAt)}</p>
							<p>
								{#if application.approve === true}
									<span class="text-sm text-emerald-700">승인</span>
								{:else if application.approve === false}
									<span class="text-sm text-rose-600">미승인</span>
								{:else}
									<span class="text-sm text-orange-500">진행중</span>
								{/if}
							</p>
						</div>
					</div>
					<div class="mx-2 my-6 mt-12">
						<p class="font-semibold">작성 내용</p>
						<div
							class="bg-white rounded-md shadow p-3 overflow-auto mt-1"
							style="max-height: 200px;"
						>
							<p>{application.body}</p>
						</div>
					</div>

					{#if application.approve == null}
						<div class="text-center mt-2 flex justify-center items-center space-x-2">
							{#if application.author == rq.member.username}
								<button
									class="btn btn-active btn-primary btn-sm"
									on:click={() => goToEditPage(application.id)}>지원서 수정</button
								>
							{/if}

							{#if application.author == rq.member.username}
								<button
									class="btn btn-active btn-sm"
									on:click={() => deleteApplication(application.id)}>지원서 삭제</button
								>
							{/if}
						</div>
					{/if}
					{#if rq.member.username == application.jobPostAuthorUsername && (application.wageStatus == '지원서 승인' || application.wageStatus == '급여 결제 완료')}
						<div class="text-center mt-2 flex justify-center items-center space-x-2">
							<button
								class="btn btn-active btn-primary btn-sm"
								on:click={() => completeJobManually(application.id)}>알바 완료</button
							>
						</div>
					{/if}
				</div>
			</div>

			{#if application.jobPostAuthorUsername == rq.member.username && application.approve == true}
				<div class="flex justify-center">
					{#if application.wageStatus == '급여 결제 전'}
						<button
							class="btn btn-btn btn-sm mx-12 mt-6"
							on:click={() => goToPaymentPage(application.id, application.wages)}
							>급여 결제하기</button
						>
					{:else}
						<button
							class="btn btn-sm mx-12 mt-6 cursor-not-allowed"
							style="background-color: #f7f7f7; color: #4b5563;"
							disabled
						>
							{application.wageStatus}
						</button>
					{/if}
				</div>
			{/if}
		</div>
	</div>
{:catch error}
	<p class="text-error">데이터를 로드하는 동안 오류가 발생했습니다: {error.message}</p>
{/await}
