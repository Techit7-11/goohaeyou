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

		if (response.data?.resultType === 'SUCCESS') {
			location.reload();
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('요청 중 오류가 발생했습니다.');
		}
	}

	async function cancelRequest(applicationId: number) {
		const response = await rq
			.apiEndPoints()
			.PATCH(`/api/jobs/individual/no-work/${applicationId}`, {});

		if (response.data?.resultType === 'SUCCESS') {
			rq.msgInfo('취소 완료되었습니다.');
			location.reload();
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else if (response.data?.resultType === 'VALIDATION_EXCEPTION') {
			rq.msgError(response.data?.message);
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

	async function cancelPendingPayment(applicationId: number) {
		try {
			const response = await rq
				.apiEndPoints()
				.POST(`/api/payments/cancel-pending/${applicationId}`, {});

			if (response.data?.resultType === 'SUCCESS') {
				rq.msgInfo('진행 중인 결제가 취소되었습니다.');
				location.reload();
			} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
				rq.msgError(response.data?.message);
			} else {
				rq.msgError('진행 중인 결제 취소 중 오류가 발생했습니다.');
			}
		} catch (error) {
			rq.msgError('결제 취소 요청에 실패했습니다. 다시 시도해주세요.');
		}
	}

	function goToEditPage() {
		rq.goTo(`/applications/modify/${$page.params.id}`);
	}

	function goToPaymentPage(wages: number) {
		rq.goTo(`/payment/pay/${$page.params.id}/${wages}`);
	}

	function goToPayCancelPage() {
		rq.goTo(`/payment/cancel/${$page.params.id}`);
	}
	function goToReviewPage() {
		rq.goTo(`/member/review/${$page.params.id}`);
	}
	function goToPaymentInfo(jobApplicationId: number) {
		rq.goTo(`/payment/info/${jobApplicationId}`);
	}
</script>

{#await loadApplication() then application}
	<div class="flex justify-center items-center min-h-screen bg-base-100">
		<div class="container mx-auto px-4 mt-5">
			<div class="w-full max-w-4xl mx-auto">
				<div class="card bg-base-100 shadow-lg rounded-lg p-5">
					<div class="flex items-center mb-6">
						<p class="text-2xl font-semibold">지원서 상세</p>
					</div>

					<div class="bg-white rounded-md mb-4">
						<div class="px-1 py-2 border-b border-gray-300">
							<p class="text-lg font-semibold">지원자 정보</p>
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
									<span class="badge" style="border-color: #ffe647; background-color: #ffe5477a;"
										>승인</span
									>
								{:else if application.approve === false}
									<span class="badge" style="border-color: #80776c; background-color: #80776c9c;"
										>미승인</span
									>
								{:else}
									<span class="badge" style="border-color: #82cf23; background-color: #82cf2369;"
										>진행중</span
									>
								{/if}
							</p>
						</div>
					</div>
					{#if application.wageStatus == '급여 결제 완료' || application.wageStatus == '급여 정산 신청' || application.wageStatus == '급여 정산 완료'}
						<button class="mt-6 btn btn-sm" on:click={() => goToPaymentInfo(application.id)}
							>급여 결제 정보</button
						>
					{/if}
					<div class="mx-2 my-6 mt-12">
						<div class="px-1 py-2 border-b border-gray-300">
							<p class="text-lg font-semibold">작성 내용</p>
						</div>
						<div
							class="bg-white rounded-md shadow p-3 overflow-auto mt-4 body-text"
							style="min-height: 100px; max-height: 200px;"
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
					{#if application.jobPostAuthorUsername == rq.member.username && (application.wageStatus == '급여 결제 완료' || application.wageStatus == '지원서 승인')}
						<div class="text-center mt-2 flex justify-center items-center space-x-8">
							<button
								class="btn btn-active btn-primary btn-sm"
								on:click={() => completeJobManually(application.id)}>알바 완료</button
							>
							{#if application.wageStatus == '급여 결제 완료'}
								<button class="btn btn-sm" on:click={() => goToPayCancelPage()}
									>급여 결제 취소</button
								>
							{/if}
							{#if application.wageStatus == '지원서 승인'}
								<button class="btn btn-sm" on:click={() => cancelRequest(application.id)}
									>취소 요청</button
								>
							{/if}
						</div>
					{/if}
				</div>
			</div>

			{#if application.jobPostAuthorUsername == rq.member.username && application.approve == true}
				<div class="flex justify-center">
					{#if application.wageStatus == '급여 결제 전'}
						<button
							class="btn btn-btn btn-sm mx-12 mt-6"
							on:click={() => goToPaymentPage(application.wages)}>급여 결제하기</button
						>
						<button class="btn btn-sm mt-6" on:click={() => cancelPendingPayment(application.id)}>
							진행 중인 결제 취소
						</button>
					{:else}
						<button
							class="btn btn-sm mx-12 mt-6 cursor-not-allowed"
							style="background-color: #fafafa; color: #4b5563;"
							disabled
						>
							진행 단계: {application.wageStatus}
						</button>
					{/if}
				</div>
			{/if}
			{#if application.wageStatus == '급여 정산 신청' || application.wageStatus == '급여 지급 완료'}
				<div class="text-center mt-4">
					<button class="btn btn-sm" on:click={goToReviewPage}>리뷰 작성하기</button>
				</div>
			{/if}
		</div>
	</div>
{:catch error}
	<p class="text-error">데이터를 로드하는 동안 오류가 발생했습니다: {error.message}</p>
{/await}

<style>
	.card.bg-base-100.shadow-lg.rounded-lg.p-5 {
		border: 1px solid oklch(0.77 0.2 132.02);
	}

	.body-text {
		background-color: #e3f4d2;
	}

	.btn-primary {
		border-color: oklch(0.77 0.2 132.02); /* 테두리 색상 설정 */
		background-color: oklch(0.77 0.2 132.02); /* 배경 색상 설정 */
		color: white;
	}
</style>
