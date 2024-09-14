<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';
	import { page } from '$app/stores';

	let applicationId = parseInt($page.params.id);
	let newReviewData = {
		score: 5,
		body: ''
	};

	onMount(async () => {
		try {
			await rq.initAuth(); // 로그인 상태를 초기화
			if (rq.isLogout()) {
				rq.msgError('로그인이 필요합니다.');
				rq.goTo('/member/login');
				return;
			}
		} catch (error) {
			console.error('인증 초기화 중 오류 발생:', error);
			rq.msgError('인증 과정에서 오류가 발생했습니다.');
			rq.goTo('/member/login');
		}
	});

	async function writeReview() {
		try {
			const response = await rq
				.apiEndPoints()
				.POST(`/api/member/review/${applicationId}`, { body: newReviewData });

			if (response.data?.resultType === 'SUCCESS') {
				rq.msgAndRedirect({ msg: '리뷰 작성 완료' }, undefined, '/');
			} else {
				rq.msgError(response.data?.message || '리뷰 작성 중 오류가 발생했습니다.');
			}
		} catch (error) {
			console.error('리뷰 작성 중 예외 발생:', error);
			rq.msgError('리뷰 작성 중 시스템 오류가 발생했습니다.');
		}
	}
</script>

<div class="flex items-center justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<h2 class="text-2xl font-bold text-center mb-10">리뷰 작성</h2>
			<form on:submit|preventDefault={writeReview}>
				<div>
					<div class="form-group">
						<div class="rating rating-lg rating-half flex justify-center">
							{#each Array(10) as _, i (i)}
								<input
									type="radio"
									name="rating"
									bind:group={newReviewData.score}
									value={(i + 1) / 2}
									class="bg-green-500 mask mask-star-2"
									class:mask-half-1={(i + 1) % 2 !== 0}
									class:mask-half-2={(i + 1) % 2 === 0}
								/>
							{/each}
						</div>
					</div>
					<div class="form-group">
						<label class="label" for="body">내용</label>
						<textarea
							id="body"
							class="textarea textarea-bordered h-40 w-full"
							bind:value={newReviewData.body}
							placeholder="내용을 입력해주세요."
						></textarea>
					</div>
					<div class="form-group">
						<button class="w-full btn btn-primary my-3" type="submit">리뷰 작성</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>

<style>
	.form-group {
		margin-bottom: 10px;
	}
</style>
