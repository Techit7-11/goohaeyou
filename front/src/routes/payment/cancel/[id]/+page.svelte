<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	let jobApplicationId: string = '';
	let paymentDto: PaymentDto | null = null;

	interface PaymentDto {
		paymentKey: string;
		totalAmount: number;
		orderName: string;
		paid: boolean;
		canceled: boolean;
		jobApplicationId: number;
		payMethod: string;
	}

	onMount(async () => {
		// 현재 URL에서 jobApplicationId 파싱
		const urlParams = window.location.pathname.split('/');
		jobApplicationId = urlParams[urlParams.length - 1];

		const response = await rq.apiEndPoints().GET(`/api/payments/${jobApplicationId}`);

		if (response.data?.resultType === 'SUCCESS') {
			paymentDto = response.data?.data;
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect(
				response.data?.data.message,
				undefined,
				'/applications/detail/' + jobApplicationId
			);
		} else {
			rq.msgAndRedirect(
				'오류가 발생했습니다.',
				undefined,
				'/applications/detail/' + jobApplicationId
			);
		}
	});

	async function handlePaymentCancellation() {
		if (!paymentDto) return;

		const params = new URLSearchParams({
			paymentKey: paymentDto.paymentKey,
			cancelReason: '사용자 요청'
		}).toString();

		const response = await rq.apiEndPoints().POST(`/api/payments/cancel?${params}`);

		if (response.data?.resultType === 'SUCCESS') {
			rq.msgInfo('결제가 취소되었습니다.');
			rq.goTo('/applications/detail/' + jobApplicationId);
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('결제 취소 중 오류가 발생했습니다.');
		}
	}
</script>

<form
	on:submit|preventDefault={handlePaymentCancellation}
	class="max-w-md mx-auto bg-white shadow-md rounded-lg p-8"
>
	<div class="flex items-center justify-between mb-8">
		<h2 class="text-xl font-semibold text-gray-700">결제 취소 요청</h2>
	</div>

	{#if paymentDto}
		<div class="mb-6">
			<p>결제 항목: {paymentDto.orderName}</p>
			<p>결제 수단: {paymentDto.payMethod}</p>
		</div>

		<hr class="border-t-2 border-gray-300 w-full max-w-md my-4" />

		<div class="flex items-center justify-between mb-6 mt-6">
			<h1 class="text-lg font-semibold text-gray-800">취소 요청 금액</h1>
			<span class="text-lg font-bold text-blue-600"
				>{paymentDto.totalAmount.toLocaleString('ko-KR')}원</span
			>
		</div>

		<button
			type="submit"
			class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-full w-full"
		>
			결제 취소
		</button>
	{:else}
		<div class="flex items-center justify-center min-h-screen">
			<span class="loading loading-dots loading-lg"></span>
		</div>
	{/if}
</form>
