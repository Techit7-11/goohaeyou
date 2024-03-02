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
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: roomListDto }}
	<div class="flex items-center justify-center">
		<a class="btn btn-ghost text-xl">Chatting Room List</a>
	</div>
	<div>
		<div id="roomListWrapper" style="overflow-y: auto; height: 455px; touch-action: pan-y;">
			<ul role="list" class="divide-y divide-gray-200 dark:divide-gray-700">
				{#each roomListDto as room}
					<div
						class=" flow-root p-6 max-w-4xl mx-auto"
						on:click={() => goToRoomDetail(room.roomId)}
					>
						<li class=" sm:py-4">
							<div class="flex items-center space-x-4">
								<div class="flex-shrink-0">
									<img
										class="w-8 h-8 rounded-full"
										src="https://flowbite.com/docs/images/people/profile-picture-1.jpg"
										alt="Neil image"
									/>
								</div>
								<div class="flex-1 min-w-0">
									<p class="text-sm font-medium text-gray-900 truncate dark:text-white">
										{#if member.username === room.username1}
											<th:block>{room.username2}</th:block>
										{:else}
											<th:block>{room.username1}</th:block>
										{/if}
									</p>
									<p class="text-sm text-gray-500 truncate dark:text-gray-400">
										<th:block>{room?.lastChat?.content}</th:block>
									</p>
								</div>
								<div
									class="inline-flex items-center text-base font-semibold text-gray-900 dark:text-white"
								>
									<th:block>{room?.lastChat?.createdAtt}</th:block>
								</div>
							</div>
						</li>
						<!-- <th:block>{room.roomId}</th:block>
						{#if member.username === room.username1}
							<th:block>{room.username2}</th:block>
						{:else}
							<th:block>{room.username1}</th:block>
						{/if}

						<th:block>{room?.lastChat.content}</th:block>
						<th:block>{room?.lastChat.createdAtt}</th:block> -->
					</div>
				{/each}
			</ul>
		</div>
	</div>
{/await}
<svelte:head>
	<script>
		var global = window;
	</script>
</svelte:head>
