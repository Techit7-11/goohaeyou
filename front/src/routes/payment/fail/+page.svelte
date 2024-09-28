<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	let code: string | null = null;
	let message: string | null = null;

	async function load() {
		const searchParams = new URLSearchParams(window.location.search);

		code = searchParams.get('code');
		message = searchParams.get('message');
	}

	onMount(() => {
		load();
	});
</script>

<div class="flex flex-col items-center justify-center min-h-screen bg-white px-4 py-5">
	<div class="text-2xl font-semibold text-center text-gray-600 mt-3" style="margin-bottom: 2rem;">
		결제에 실패하였습니다.
	</div>

	<div class="mt-4 text-center">
		<div class="text-left w-full max-w-md px-8">
			<p class="text-md mb-2"></p>
			<p class="text-md mb-2">
				결제실패 사유:
				<span class="font-semibold text-gray-700">{message}</span>
			</p>
			<p class="text-md mb-2 mt-5">오류 코드: {code}</p>
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
