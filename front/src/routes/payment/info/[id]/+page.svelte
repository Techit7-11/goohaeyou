<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	const urlParams = window.location.pathname.split('/');
	let id = urlParams[urlParams.length - 1];

	async function load() {
		const response = await rq.apiEndPoints().GET(`/api/payments/${id}`);

		if (response.data?.resultType === 'SUCCESS') {
			return response.data!;
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgAndRedirect(response.data?.message, undefined, `/applications/detail/${id}`);
		} else {
			rq.msgAndRedirect(
				{ msg: '결제 정보를 불러오는 중 오류가 발생했습니다.' },
				undefined,
				`/applications/detail/${id}`
			);
		}
	}

	function formatAmount(amount: number) {
		return amount.toLocaleString('ko-KR');
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
			결제 정보
		</div>

		<hr class="border-t-2 border-gray-300 w-full max-w-md my-4" />

		<div class="space-y-4 max-w-md w-full mt-1 px-8">
			<div class="text-gray-700 font-basic">
				<span class="text-black">결제 내역: </span>
				{data.orderName}
			</div>

			<div class="text-gray-700 font-basic">
				<span class="text-black">결제수단: </span>
				{data.payMethod}
			</div>

			<hr class="my-3" />

			<div class="text-gray-700" style="margin-top: 1rem;">
				<span class="font-semibold">결제 금액:</span>
				{formatAmount(data.totalAmount)}원
			</div>
		</div>

		<hr class="border-t-2 border-gray-300 w-full max-w-md my-4" />

		<div class="mt-6">
			<button
				class="btn bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
				on:click={() => rq.goTo('/applications/detail/' + data.jobApplicationId)}
				>확인
			</button>
		</div>
	</div>
{/await}
