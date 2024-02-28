<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import { writable, get } from 'svelte/store';

	// 선택된 지원서 ID들을 저장할 writable 스토어 초기화
	const selectedApplicationIds = writable(new Set());

	// 페이지에서 postId를 가져옴
	let postId = $page.params.id;
	let canApprove = false; // '지원서 승인' 버튼과 체크박스를 표시할지 결정하는 플래그

	// 지원서 목록을 로드하는 비동기 함수
	async function loadApplications() {
		const { data } = await rq.apiEndPoints().GET('/api/employ/{postId}', {
			params: {
				path: {
					postId: parseInt($page.params.id)
				}
			}
		});

		// 지원서 목록이 존재하고, 첫 번째 지원서가 승인되지 않았을 때만 canApprove를 true로 설정
		if (data?.data?.length > 0 && data.data[0].approve !== true) {
			canApprove = true;
		} else {
			canApprove = false;
		}

		return data?.data;
	}

	// 선택된 지원서 ID들을 관리하는 함수들
	function toggleApplicationId(id: number, checked: boolean) {
		selectedApplicationIds.update((ids) => {
			if (checked) ids.add(id);
			else ids.delete(id);
			return ids;
		});
	}

	async function approveApplications() {
		const ids = Array.from(get(selectedApplicationIds));
		if (ids.length > 0) {
			const response = await rq.apiEndPoints().PATCH(`/api/employ/${postId}/${ids.join(',')}`, {});

			if (response.data?.statusCode === 204) {
				rq.msgAndRedirect({ msg: '승인 완료' }, undefined, `/applications/list/${postId}`);
				location.reload();
			} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
				const customErrorMessage = response.data?.data?.message;
				rq.msgError(customErrorMessage);
			} else {
				rq.msgError('승인 요청 중 오류가 발생했습니다.');
			}
		} else {
			rq.msgError('공고를 선택해주세요.');
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

	function summarizeBody(body) {
		return body.length > 10 ? `${body.slice(0, 10)}...` : body;
	}
</script>

{#await loadApplications()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then applications}
	{#if applications && applications.length > 0}
		<div class="flex flex-col min-h-screen items-center">
			<div class="container mx-auto px-4 mt-5">
				<ul class="w-full max-w-4xl mx-auto">
					{#each applications as application}
						<li class="card bg-base-100 shadow-xl">
							<div class="card bg-base-100 shadow-xl">
								<div class="card-body flex flex-row items-center justify-between">
									<a href={`/applications/detail/${application.id}`} class="flex-grow">
										<div class="flex">
											<div class="flex flex-col flex-grow max-w-40">
												<p class="font-bold">지원서 번호 :</p>
												<p>지원자 ID :</p>
												<p>지원 내용 :</p>
												<p>지원일 :</p>
												<p>승인여부 :</p>
											</div>
											<div class="flex-grow text-left">
												<p>{application.id}</p>
												<p>{application.author}</p>
												<p>{summarizeBody(application.body)}</p>
												<p>{formatDate(application.createdAt)}</p>
												<p>
													{#if application.approve === true}
														<span class="text-sm text-blue-900">승인</span>
													{:else if application.approve === false}
														<span class="text-sm text-rose-600">미승인</span>
													{:else}
														<span class="text-sm text-orange-500">진행중</span>
													{/if}
												</p>
											</div>
										</div>
									</a>
									<div>
										{#if canApprove}
											<input
												type="checkbox"
												id={`application-${application.id}`}
												class="checkbox"
												on:change={(event) => {
													event.stopPropagation();
													toggleApplicationId(application.id, event.target.checked);
												}}
											/>
										{/if}
									</div>
								</div>
							</div>
						</li>
					{/each}
				</ul>
				{#if canApprove}
					<div class="max-w-4xl mx-auto my-5">
						<button on:click={approveApplications} class="btn btn-primary w-full my-4"
							>지원서 승인</button
						>
					</div>
				{/if}
			</div>
		</div>
	{:else}
		<div class="flex justify-center items-center min-h-screen bg-base-100">
			<div class="container mx-auto px-4">
				<div class="font-bold text-center">해당 공고에 아직 지원자가 없습니다.</div>
			</div>
		</div>
	{/if}
{:catch error}
	<p>Error loading applications: {error.message}</p>
{/await}
