<script lang="ts">
	import { onMount, tick } from 'svelte';
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import { error } from '@sveltejs/kit';
	import SockJS from 'sockjs-client';
	import { Stomp } from '@stomp/stompjs';

	const roomId = parseInt($page.params.id);
	let newContent = '';
	let lastMessageId = 0;
	var socket = new SockJS(import.meta.env.VITE_CORE_API_BASE_URL + '/ws');
	var stompClient = Stomp.over(socket);
	let chatMessagesEl;

	onMount(async () => {
		await rq.initAuth();
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
			return;
		}

		const waitForChatMessagesEl = setInterval(() => {
			chatMessagesEl = document.querySelector('.__messages');
			if (chatMessagesEl) {
				clearInterval(waitForChatMessagesEl);
			}
		}, 100);
	});

	stompClient.connect({}, function (frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe(`/queue/api/chat/${roomId}/newMessage`, function (data) {
			const jsonData = JSON.parse(data.body);
			drawMoreMessage(jsonData);
		});
	});

	async function load() {
		const { data } = await rq.apiEndPoints().GET(`/api/chat/${roomId}`);
		lastMessageId = data?.data?.messages[0]?.id;
		return data!;
	}

	function drawMoreMessage(message) {
		lastMessageId = message.id;
		const messageElement = document.createElement('li');
		messageElement.innerHTML = `<li>${message.sender}</li><li>${message.text}</li>`;
		chatMessagesEl.insertBefore(messageElement, chatMessagesEl.firstChild);
	}

	async function addContent() {
		newContent = newContent.trim();
		if (newContent.length == 0) {
			alert('내용을 입력 해주세요.');
			return;
		}

		const response = await rq.apiEndPoints().POST(`/api/chat/${roomId}/message`, {
			body: { content: newContent }
		});
		newContent = '';
		lastMessageId = response.data?.data.id;
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: roomDto }}
	<div class="p-6 max-w-4xl mx-auto my-10 bg-white rounded-box shadow-lg">
		<div>
			<div>{roomDto?.username1}</div>
			<div>{roomDto?.username2}</div>
		</div>
		<hr />
		<div>
			<ul class="__messages">
				{#each roomDto.messages as message}
					<hr />
					<th:block>{message.sender}</th:block>
					<th:block>{message.content}</th:block>
				{/each}
			</ul>
		</div>

		<div class="flex justify-between items-center mb-4">
			<textarea
				class="textarea textarea-bordered w-full"
				placeholder="댓글을 입력하세요."
				bind:value={newContent}
			></textarea>
			<button class="btn btn-ghost mx-3" on:click={addContent}>보내기</button>
		</div>
	</div>
{/await}

<svelte:head>
	<script>
		var global = window;
	</script>
</svelte:head>
