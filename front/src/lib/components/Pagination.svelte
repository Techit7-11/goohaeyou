<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import type { PageDto } from '$lib/types';

	const { page, pageDelta = 1 } = $props<{ page: PageDto; pageDelta?: number }>();

	// 페이지네이션 범위 계산 함수
	function calculatePaginationRange(current: number, total: number, delta = 4) {
		const left = current - delta;
		const right = current + delta;
		const range = [] as { no: number; text: string }[];

		if (total <= 1) return [];

		for (let i = 1; i <= total; i++) {
			if (i === 1) {
				range.push({ no: i, text: `${i}` });
			} else if (i == left - 1) {
				range.push({ no: i, text: `...` });
			} else if (i >= left && i <= right) {
				range.push({ no: i, text: `${i}` });
			} else if (i === total) {
				range.push({ no: i, text: `${i}` });
			} else if (i == right + 1) {
				range.push({ no: i, text: `...` });
			}
		}
		return range;
	}
</script>

<div class="flex justify-center">
	<div class="join">
		{#each calculatePaginationRange(page.number, page.totalPagesCount, pageDelta) as pageNumber}
			<button
				class={`join-item btn ${pageNumber.no == page.number ? 'text-green5' : ''}`}
				on:click={() => rq.goToCurrentPageWithNewParam('page', `${pageNumber.no}`)}
			>
				{pageNumber.text}
			</button>
		{/each}
	</div>
</div>
