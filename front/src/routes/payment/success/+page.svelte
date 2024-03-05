<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function load() {
		const searchParams = new URLSearchParams(window.location.search);
		const orderId = searchParams.get('orderId');
		const paymentKey = searchParams.get('paymentKey');
		const amount = searchParams.get('amount');

		const response = await rq
			.apiEndPoints()
			.GET(`/api/payments/success?orderId=${orderId}&paymentKey=${paymentKey}&amount=${amount}`);

		if (response.data?.msg === 'OK') {
			return response.data!;
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.data.message);
			rq.goTo('/'); // 추후에 경로 재설정
		} else {
			rq.msgError('결제 정보를 불러오는 중 오류가 발생했습니다.');
			rq.goTo('/');
		}
	}

	function formatAmount(amount) {
		return amount.toLocaleString('ko-KR');
	}

	function formatDateTime(dateTimeString) {
		const date = new Date(dateTimeString);
		return date.toLocaleString('ko-KR', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
			second: '2-digit'
		});
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen bg-white">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data }}
	<div
		class="flex flex-col items-center justify-center bg-white px-4"
		style="padding-top: 5vh; padding-bottom: 5vh;"
	>
		<div class="text-2xl font-semibold text-center text-blue-500 mt-3" style="margin-bottom: 2rem;">
			결제가 완료되었습니다.
		</div>

		<hr class="border-t-2 border-gray-300 w-full max-w-md my-4" />

		<div class="space-y-4 max-w-md w-full mt-1 px-8">
			<div class="text-gray-700 font-basic">
				<span class="text-black">주문 ID: </span>
				<span class="text-sm">{data.orderId}</span>
			</div>

			<div class="text-gray-700 font-basic">
				<span class="text-black">결제 내역: </span>{data.orderName}
			</div>

			{#if data.method == '카드'}
				<div class="text-gray-700 font-basic">
					<span class="text-black">결제 수단: </span>{data.card.cardType} 카드
				</div>
			{/if}
			{#if data.method == '간편결제'}
				<div class="text-gray-700 font-basic">
					<span class="text-black">결제 수단: </span>{data.easyPay.provider}
				</div>
			{/if}
			<div class="text-gray-700 font-basic">
				<span class="text-black">결제 일시: </span>{formatDateTime(data.approvedAt)}
			</div>
			<hr class="my-3" />
			<div class="text-gray-700">
				<span class="font-semibold">결제 금액:</span>
				{formatAmount(data.totalAmount)}원
			</div>
			<div class="text-gray-700 text-sm">
				(VAT:{formatAmount(data.vat)}원)
			</div>
		</div>

		<hr class="border-t-2 border-gray-300 w-full max-w-md my-4" />

		<div class="mt-6">
			<button class="btn bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded">
				확인
			</button>
		</div>
	</div>
{/await}
