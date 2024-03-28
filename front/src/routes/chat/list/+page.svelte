<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';

	const member = rq.member;

	onMount(async () => {
		await rq.initAuth();
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
			return;
		}
	});

	async function load() {
		const { data } = await rq.apiEndPoints().GET(`/api/chat`);
		return data!;
	}

	function goToRoomDetail(roomId: Long) {
		rq.goTo(`/chat/${roomId}`);
	}

	async function exitChatRoom(roomId: Long) {
		const isConfirmed = confirm('정말 나가시겠습니까?');
		if (isConfirmed) {
			console.log(roomId);
			console.log(rq.apiEndPoints());
			const response = await rq.apiEndPoints().PUT(`/api/chat/${roomId}`);
			location.reload();
		} else {
		}
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: roomListDto }}
	{#if roomListDto.length > 0}
		<div class="flex justify-center min-h-screen bg-base-100">
			<div class="container mx-auto px-4">
				<div class="w-full max-w-4xl mx-auto">
					<h2 class="text-xl font-extrabold mx-4 mt-10">채팅</h2>
					<div>
						<div id="roomListWrapper" style="overflow-y: auto; height: 455px; touch-action: pan-y;">
							<ul role="list">
								{#each roomListDto as room}
									<div
										class=" flow-root p-6 max-w-4xl mx-auto hover: cursor-pointer"
										on:click={() => goToRoomDetail(room.roomId)}
									>
										<li class=" sm:py-4">
											<div class="flex items-center space-x-4">
												<div class="flex-shrink-0">
													<div class="avatar online placeholder">
														<div class="bg-neutral text-neutral-content rounded-full w-8">
															<span class="text-xs">{room.username1.slice(0, 3)}</span>
														</div>
													</div>
												</div>
												<div class="flex-1 min-w-0">
													<p class="text-sm font-medium text-gray-900 truncate dark:text-white">
														{#if member.username === room.username1}
															<th:block>{room.username2}</th:block>
														{:else}
															<th:block>{room.username1}</th:block>
														{/if}
													</p>
													<p
														class="inline-flex items-center text-base font-semibold dark:text-gray-400 text-gray-900 dark:text-white"
													>
														<th:block>{room?.lastChat}</th:block>
													</p>
												</div>
												<div class="text-sm text-gray-500 truncate">
													<th:block>{room?.lastChatDate}</th:block>
												</div>
											</div>
										</li>
									</div>
									<div class="flex items-center justify-end">
										<button
											class="text-sm text-gray-500"
											type="button"
											on:click={() => exitChatRoom(room?.roomId)}
											>나가기
										</button>
									</div>
									<div class="divider mt-3"></div>
								{/each}
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	{:else}
		<div class="flex items-center justify-center min-h-screen">
			<h2>채팅방이 존재하지 않습니다</h2>
		</div>
	{/if}
{/await}
