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
	<div>
		<div>
			<ul class="p-6 max-w-4xl mx-auto my-10 bg-white rounded-box shadow-lg">
				{#each roomListDto as room}
					<div
						class="p-6 max-w-4xl mx-auto my-3 bg-white rounded-box shadow-lg"
						on:click={() => goToRoomDetail(room.roomId)}
					>
						<th:block>{room.roomId}</th:block>
						{#if member.username === room.username1}
							<th:block>{room.username2}</th:block>
						{:else}
							<th:block>{room.username1}</th:block>
						{/if}

						<th:block>{room?.lastChat.content}</th:block>
						<th:block>{room?.lastChat.createdAtt}</th:block>
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
