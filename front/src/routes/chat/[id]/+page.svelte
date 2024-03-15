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
	var stompClient = Stomp.over(function () {
		return socket;
	});
	let chatMessagesEl;
	const member = rq.member;
	let messages = [];

	onMount(async () => {
		await rq.initAuth();
		if (rq.isLogout()) {
			rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
			return;
		}
		loadMessages();
		const waitForChatMessagesEl = setInterval(() => {
			chatMessagesEl = document.querySelector('.__messages');
			if (chatMessagesEl) {
				clearInterval(waitForChatMessagesEl);
			}
			// 스크롤을 가장 아래로 이동합니다.
			chatMessagesEl.scrollTop = chatMessagesEl.scrollHeight;
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
		console.log('aaaaaaaaaa');
		console.log(message);
		const messageElement = document.createElement('li');
		if (message.sender === 'admin') {
			messageElement.innerHTML = `<div class="flex justify-center">
				<th:block>${message.text}</th:block>
			</div>`;
		} else if (message.sender === member.username) {
			messageElement.innerHTML = `<div class="chat chat-end">
						<div class="chat-image avatar">
							<div class="w-10 rounded-full">
								<img
									alt="Tailwind CSS chat bubble component"
									src="https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg"
								/>
							</div>
						</div>
						<div class="chat-header">
							<th:block>${message.sender}</th:block>
						</div>
						<div class="chat-bubble"><th:block>${message.text}</th:block></div>
						<div class="chat-footer opacity-50"><th:block>${message.createdAt}</th:block></div>
					</div>`;
		} else {
			messageElement.innerHTML = `<div class="chat chat-start">
						<div class="chat-image avatar">
							<div class="w-10 rounded-full">
								<img
									alt="Tailwind CSS chat bubble component"
									src="https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg"
								/>
							</div>
						</div>
						<div class="chat-header">
							<th:block>${message.sender}</th:block>
						</div>
						<div class="chat-bubble"><th:block>${message.text}</th:block></div>
						<div class="chat-footer opacity-50"><th:block>${message.createdAt}</th:block></div>
					</div>`;
		}
		chatMessagesEl.appendChild(messageElement);
		chatMessagesEl.scrollTop = chatMessagesEl.scrollHeight;
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

	function formatMessageTime(createdAt) {
		const date = new Date(createdAt);
		const year = String(date.getFullYear()).slice(-2);
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		const hours = String(date.getHours()).padStart(2, '0');
		const minutes = String(date.getMinutes()).padStart(2, '0');
		const seconds = String(date.getSeconds()).padStart(2, '0');
		return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
	}

	async function loadMessages() {
		try {
			const { data } = await rq.apiEndPoints().GET(`/api/chat/${roomId}/message`);
			console.log(data);
			messages = data.data
				.map((message) => ({
					...message,
					isEditing: false // 모든 댓글에 isEditing 속성 추가
				}))
				.reverse();
		} catch (error) {
			console.error('메세지 로드하는 중 오류가 발생했습니다.', error);
		}
	}
</script>

{#await load()}
	<div class="flex items-center justify-center min-h-screen">
		<span class="loading loading-dots loading-lg"></span>
	</div>
{:then { data: roomDto }}
	<div class="p-1 max-w-4xl mx-auto bg-white rounded-box shadow-lg">
		<ul class="__messages">
			{#each messages as message}
				<!-- {#each roomDto.messages as message} -->
				{#if message.sender === 'admin'}
					<div class="flex justify-center">
						<th:block>{message.text}</th:block>
					</div>
				{:else if message.sender === member.username}
					<div class="chat chat-end">
						<div class="chat-image avatar">
							<div class="w-10 rounded-full">
								<img
									alt="Tailwind CSS chat bubble component"
									src="https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg"
								/>
							</div>
						</div>
						<div class="chat-header">
							<th:block>{message.sender}</th:block>
						</div>
						<div class="chat-bubble"><th:block>{message.text}</th:block></div>
						<div class="chat-footer opacity-50">
							<th:block>{formatMessageTime(message.createdAt)}</th:block>
						</div>
					</div>
				{:else}
					<div class="chat chat-start">
						<div class="chat-image avatar">
							<div class="w-10 rounded-full">
								<img
									alt="Tailwind CSS chat bubble component"
									src="https://daisyui.com/images/stock/photo-1534528741775-53994a69daeb.jpg"
								/>
							</div>
						</div>
						<div class="chat-header">
							<th:block>{message.sender}</th:block>
						</div>
						<div class="chat-bubble"><th:block>{message.text}</th:block></div>
						<div class="chat-footer opacity-50">
							<th:block>{formatMessageTime(message.createdAt)}</th:block>
						</div>
					</div>
				{/if}
			{/each}
		</ul>

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

<style>
	.__messages {
		max-height: 455px; /* 화면의 최대 높이를 지정합니다. */
		overflow-y: auto; /* 세로 스크롤이 필요할 때만 스크롤이 나타나도록 합니다. */
	}
</style>
