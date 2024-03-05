<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function load() {
		const searchParams = new URLSearchParams(window.location.search);
		const code = searchParams.get('code');
		const message = searchParams.get('message');
		const orderId = searchParams.get('orderId');

		const response = await rq
			.apiEndPoints()
			.GET(`/api/payments/fail?code=${code}&message=${message}&orderId=${orderId}`);

		if (response.data?.msg === 'OK') {
			return response.data!;
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.data.message);
			rq.goTo('/'); // 추후에 경로 재설정
		} else {
			rq.msgError('결제 실패 정보를 불러오는 중 오류가 발생했습니다.');
			rq.goTo('/');
		}
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen bg-white">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data }}
	<div class="flex flex-col items-center justify-center min-h-screen bg-white px-4 py-5">
		<div class="text-2xl font-semibold text-center text-gray-600 mt-3" style="margin-bottom: 2rem;">
			결제에 실패하였습니다.
		</div>

		<div class="mt-4 text-center">
			<div class="text-left w-full max-w-md px-8">
				<p class="text-md mb-2"></p>
				<p class="text-md mb-2">
					결제실패 사유:
					<span class="font-semibold text-gray-700">{data.errorMessage}</span>
				</p>
				<p class="text-md mb-2 mt-5">오류 코드: {data.errorCode}</p>
				<p class="text-md mb-2">주문 ID: {data.orderId}</p>
			</div>
		</div>
		<div class="mt-6">
			<!-- 추후 리다이렉트 url 변경 필요 -->
			<button
				class="btn bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
				on:click={() => rq.goTo('/')}
				>확인
			</button>
		</div>
	</div>
{/await}
