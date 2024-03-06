<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';

	let newReviewData = {
		score: '',
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
		const response = await rq.apiEndPoints().POST('/api/member/review', { body: newReviewData });

		if (response.data?.statusCode === 200) {
			rq.msgAndRedirect({ msg: '리뷰 작성 완료' }, undefined, '/');
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			const customErrorMessage = response.data?.data?.message;
			rq.msgError(customErrorMessage ?? '알 수 없는 오류가 발생했습니다.');
		} else if (response.data?.msg === 'VALIDATION_EXCEPTION') {
			if (Array.isArray(response.data.data)) {
				rq.msgError(response.data.data[0]);
			}
		} else {
			rq.msgError('리뷰 작성 중 오류가 발생했습니다.');
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
							<input type="radio" name="rating-10" class="rating-hidden" value="0" checked />
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-1"
								value="0.5"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-2"
								value="1"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-1"
								value="1.5"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-2"
								value="2"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-1"
								value="2.5"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-2"
								value="3"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-1"
								value="3.5"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-2"
								value="4"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-1"
								value="4.5"
							/>
							<input
								type="radio"
								name="rating-10"
								class="bg-green-500 mask mask-star-2 mask-half-2"
								value="5"
							/>
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
