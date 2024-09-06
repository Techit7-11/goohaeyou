<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	let tossPayments: TossPayments;
	let jobApplicationId: string = '';
	let totalAmount: number = 0;
	let orderName: string = '';

	onMount(() => {
		const script = document.createElement('script');
		script.src = 'https://js.tosspayments.com/v1/payment-widget';
		script.onload = () => {
			tossPayments = new (TossPayments as any)('test_ck_0RnYX2w532wBpgX09XNgVNeyqApQ');
		};
		document.body.appendChild(script);

		// URL 파싱하여 값 가져오기
		const urlParams = window.location.pathname.split('/');
		jobApplicationId = urlParams[urlParams.length - 2] || '';
		totalAmount = parseInt(urlParams[urlParams.length - 1] || '0', 10);

		orderName = `지원서_${jobApplicationId}_급여_결제`;

		console.log(
			`Application ID: ${jobApplicationId}, Total Amount: ${totalAmount}, Order Name: ${orderName}`
		);
	});

	async function handlePayment(event) {
		event.preventDefault(); // 폼 기본 동작 방지

		if (!tossPayments) {
			console.error('TossPayments is not initialized.');
			return;
		}

		// API 호출
		const body = {
			payStatus: 'REQUEST',
			amount: totalAmount,
			jobApplicationId,
			orderName
		};

		try {
			const response = await rq.apiEndPoints().POST(`/api/payments`, { body });
			const paymentRespDto = response.data?.data;

			// 결제창 호출
			tossPayments.requestPayment('CARD', {
				amount: totalAmount,
				orderId: paymentRespDto.orderId,
				orderName: paymentRespDto.orderName,
				successUrl: paymentRespDto.successUrl,
				failUrl: paymentRespDto.failUrl,
				payer: paymentRespDto.payer
			});
		} catch (error) {
			console.error('Error:', error);
		}
	}

	function formatAmount(amount) {
		return amount.toLocaleString('ko-KR');
	}
</script>

<form
	on:submit|preventDefault={handlePayment}
	class="max-w-md mx-auto bg-white shadow-md rounded-lg p-8"
>
	<div class="flex items-center justify-between mb-4">
		<img
			src="/tosspayments.jpg"
			alt="Toss Payments"
			class="mx-auto mb-8"
			style="width: 200px; height: auto;"
		/>
	</div>

	<div class="mb-4">
		<h2 class="text-lg font-semibold text-gray-700">결제항목</h2>

		<hr class="my-3" />
		<p class="text-gray-800 ml-1">{orderName}</p>
		<hr class="my-3" />
	</div>

	<div class="flex items-center justify-between mb-6 mt-8">
		<h1 class="text-xl font-semibold text-gray-800">총 결제금액</h1>
		<span class="text-lg font-bold text-blue-600">{formatAmount(totalAmount)}원</span>
	</div>

	<button
		type="submit"
		class="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-full w-full"
	>
		결제하기
	</button>
</form>
