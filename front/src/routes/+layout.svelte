<script lang="ts">
	import { onMount } from 'svelte';
	import '$lib/app.css';
	import rq from '$lib/rq/rq.svelte';
	import '../tailwind.css';

	import { getMessaging, getToken } from 'firebase/messaging';
	import { initializeApp } from 'firebase/app';

	const { children } = $props();

	function handleAuthAction() {
		if (rq.isLogin()) {
			rq.logoutAndRedirect('/');
		} else {
			window.location.href = '/member/login';
		}
	}
	function NavMyPage() {
		window.location.href = '/member/me';
	}
	function NavCategories() {
		window.location.href = '/';
	}
	function NavAlert() {
		window.location.href = '/notification/list';
	}
	function NavSearch() {
		window.location.href = '/job-post/search';
	}
	function NavMaps() {
		window.location.href = '/maps';
	}

	function JobPostWritePage() {
		rq.goTo('/job-post/write');
	}

	function backToPreviousPage() {
		window.history.back();
	}

	const firebaseConfig = {
		apiKey: 'AIzaSyC2-vB7k_IQUGn1osPjD4bLfCLaJeSYPzQ',
		authDomain: 'goohaeyou-9ba43.firebaseapp.com',
		projectId: 'goohaeyou-9ba43',
		storageBucket: 'goohaeyou-9ba43.appspot.com',
		messagingSenderId: '421577386725',
		appId: '1:421577386725:web:5c120908373765a55e020c',
		measurementId: 'G-X6YNTCZ56G'
	};

	onMount(() => {
		rq.initAuth();

		setTimeout(() => {
			if (rq.isLogin()) {
				console.log(rq.isLogin());
				const app = initializeApp(firebaseConfig);
				requestPermission();
			} else {
				console.log('Not logged in');
			}
		}, 100); // 로드가 된 후 0.1초 시간의 텀을 두고 한번만 실행
	});

	function requestPermission() {
		const messaging = getMessaging();
		void Notification.requestPermission().then((permission) => {
			if (permission === 'granted') {
				getToken(messaging, {
					vapidKey:
						'BDnzTq1rFlBlLYTM-hay6Qbrj-lXlDewXy9ShfFk3J2TF8Ndo3v70RbNYGAUg7Bc3LZOw_jX7reHjxHCYjyqC-k'
				})
					.then((currentToken) => {
						if (currentToken) {
							console.log(currentToken);
							rq.apiEndPoints().POST('/api/notification/register', {
								body: { token: currentToken }
							});
						} else {
							console.log('No registration token available. Request permission to generate one.');
						}
					})
					.then(async function name(currentToken) {})
					.catch((err) => {
						console.log('An error occurred while retrieving token. ', err);
					});
			}
		});
	}

	// 해당 부분은 읽지 않은 알림이 있을 경우 ui 변경 시도를 위해 작성했습니다.
	// 논리 타입의 a는 변경이 잘 됩니다.
	// 하지만 ui부분에서 a값 변경과 동시에 추가적인 ui변경은 안됩니다.
	// let a: boolean = false;
	// async function unreadNotification() {
	// 	let result = false;
	// 	if (rq.isLogin()) {
	// 		const response = await rq.apiEndPoints().GET(`/api/notification/new`);
	// 		console.log('데이터');
	// 		console.log(response.data?.data);
	// 		if (response.data?.data !== undefined) {
	// 			a = response.data?.data;
	// 			console.log(typeof response.data?.data);
	// 		}
	// 	}
	// }

	// function runMethodPeriodically() {
	// 	// 30초마다 메소드를 실행하기 위해 setInterval 함수 사용
	// 	setInterval(() => {
	// 		unreadNotification();
	// 		console.log('a 값, 타입');
	// 		console.log(a);
	// 		console.log(typeof a);
	// 	}, 30000); // 30초를 밀리초 단위로 표현한 값
	// }

	// runMethodPeriodically();
	function scrollToTop() {
		window.scrollTo({ top: 0, behavior: 'smooth' });
	}
</script>

<header>
	<div class="main-bar fixed-div">
		<div class="navbar bg-base-100">
			<div class="navbar-start">
				<a class="btn btn-ghost text-xl font-mono" href="/"
					><img src="/logo.png" alt="GooHaeYou Logo" class="h-10 w-auto" /></a
				>
				<!-- 뒤로가기 버튼 -->
				<button
					on:click={backToPreviousPage}
					class="fixed bottom-20 left-3 bg-gray-100 text-black rounded-full p-2 shadow-lg z-50"
					style="display: flex; align-items: center; justify-content: center; border: none; opacity: 0.8;"
					title="뒤로가기"
				>
					<svg
						xmlns="http://www.w3.org/2000/svg"
						class="h-6 w-6"
						fill="none"
						viewBox="0 0 24 24"
						stroke="currentColor"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M15 19l-7-7 7-7"
						/>
					</svg>
				</button>
			</div>
			<div class="navbar-center"></div>
			<div class="navbar-end">
				<!-- 검색 버튼 -->
				<button
					on:click={NavSearch}
					class="btn btn-ghost"
					style="margin-right: -12px;"
					title="검색"
				>
					<svg
						xmlns="http://www.w3.org/2000/svg"
						class="h-6 w-6 text-gray-700"
						fill="none"
						viewBox="0 0 24 24"
						stroke="currentColor"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
						/>
					</svg>
				</button>

				<!-- 로그인/아웃 버튼 -->
				{#if rq.isLogout()}
					<a
						class="btn btn-ghost mx-1"
						href="/member/login"
						style="font-family: 'Arial', sans-serif; color: #228B22; padding: 4px 8px; font-size: 14px;"
						>로그인</a
					>
				{/if}
				{#if rq.isLogin()}
					<button
						class="btn btn-ghost mx-1"
						on:click={() => rq.logoutAndRedirect('/')}
						style="font-family: 'Arial', sans-serif; color: #228B22; padding: 4px 8px; font-size: 14px;"
						>로그아웃</button
					>
				{/if}
			</div>
		</div>
	</div>
	<div style="border-bottom: 1px solid #dedede;"></div>
</header>
<main>{@render children()}</main>
<div style="border-bottom: 1px solid #dedede;"></div>
<footer class="footer footer-center p-10 bg-base-100 text-base-content rounded">
	<nav class="grid grid-flow-col gap-4">
		<a class="link link-hover">About us</a>
		<a class="link link-hover">Contact</a>
		<a class="link link-hover">Jobs</a>
		<a class="link link-hover" href="https://github.com/Techit7-11/GooHaeYou" target="_blank"
			>Github</a
		>
	</nav>
	<nav>
		<div class="grid grid-flow-col gap-4">
			<a
				><svg
					xmlns="http://www.w3.org/2000/svg"
					width="24"
					height="24"
					viewBox="0 0 24 24"
					class="fill-current"
					><path
						d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"
					></path></svg
				></a
			>
			<a
				><svg
					xmlns="http://www.w3.org/2000/svg"
					width="24"
					height="24"
					viewBox="0 0 24 24"
					class="fill-current"
					><path
						d="M19.615 3.184c-3.604-.246-11.631-.245-15.23 0-3.897.266-4.356 2.62-4.385 8.816.029 6.185.484 8.549 4.385 8.816 3.6.245 11.626.246 15.23 0 3.897-.266 4.356-2.62 4.385-8.816-.029-6.185-.484-8.549-4.385-8.816zm-10.615 12.816v-8l8 3.993-8 4.007z"
					></path></svg
				></a
			>
			<a
				><svg
					xmlns="http://www.w3.org/2000/svg"
					width="24"
					height="24"
					viewBox="0 0 24 24"
					class="fill-current"
					><path
						d="M9 8h-3v4h3v12h5v-12h3.642l.358-4h-4v-1.667c0-.955.192-1.333 1.115-1.333h2.885v-5h-3.808c-3.596 0-5.192 1.583-5.192 4.615v3.385z"
					></path></svg
				></a
			>
		</div>
	</nav>
	<aside>
		<p class="mb-2">© 2024 All Rights Reserved by Techit7-11번과_GooHaeYou</p>
	</aside>
</footer>
<div class="btm-nav" style="z-index: 1;">
	<button on:click={NavCategories}>
		<svg
			xmlns="http://www.w3.org/2000/svg"
			class="h-6 w-6"
			fill="none"
			viewBox="0 0 24 24"
			stroke="currentColor"
		>
			<path
				stroke-linecap="round"
				stroke-linejoin="round"
				stroke-width="2"
				d="M3 7h18M3 12h18m-9 5h9"
			/>
		</svg>
		<span class="btm-nav-label">카테고리</span>
	</button>

	<button on:click={NavMaps}>
		<svg xmlns="http://www.w3.org/2000/svg" height="24" width="24" viewBox="0 0 576 512"
			><path
				fill="#000000"
				d="M565.6 36.2C572.1 40.7 576 48.1 576 56V392c0 10-6.2 18.9-15.5 22.4l-168 64c-5.2 2-10.9 2.1-16.1 .3L192.5 417.5l-160 61c-7.4 2.8-15.7 1.8-22.2-2.7S0 463.9 0 456V120c0-10 6.1-18.9 15.5-22.4l168-64c5.2-2 10.9-2.1 16.1-.3L383.5 94.5l160-61c7.4-2.8 15.7-1.8 22.2 2.7zM48 136.5V421.2l120-45.7V90.8L48 136.5zM360 422.7V137.3l-144-48V374.7l144 48zm48-1.5l120-45.7V90.8L408 136.5V421.2z"
			/></svg
		>
		<span class="btm-nav-label">지도</span>
	</button>

	<button on:click={JobPostWritePage}>
		<svg
			xmlns="http://www.w3.org/2000/svg"
			class="h-6 w-6 text-gray-700"
			fill="none"
			viewBox="0 0 24 24"
			stroke="currentColor"
		>
			<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
		</svg>
		<span class="btm-nav-label">글 작성</span>
	</button>

	<!-- {#if a} -->
	<!-- <button on:click={NavAlert}>
			<span class="btm-nav-label"></span>
			<svg
				xmlns="http://www.w3.org/2000/svg"
				class="h-6 w-6 rounded-full bg-blue-500 p-1"
				fill="none"
				viewBox="0 0 24 24"
				stroke="currentColor"
				><path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
				/></svg
			>
		</button>
	{:else} -->
	<button on:click={NavAlert}>
		<svg
			xmlns="http://www.w3.org/2000/svg"
			class="h-6 w-6"
			fill="none"
			viewBox="0 0 24 24"
			stroke="currentColor"
			><path
				stroke-linecap="round"
				stroke-linejoin="round"
				stroke-width="2"
				d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
			/></svg
		>
		<span class="btm-nav-label">알림</span>
	</button>
	<!-- {/if} -->

	<button on:click={NavMyPage}>
		<svg
			version="1.0"
			xmlns="http://www.w3.org/2000/svg"
			class="w-6 h-6"
			viewBox="0 0 512.000000 512.000000"
			preserveAspectRatio="xMidYMid meet"
		>
			<g
				transform="translate(0.000000,512.000000) scale(0.100000,-0.100000)"
				fill="#000000"
				stroke="none"
			>
				<path
					d="M2389 4680 c-414 -75 -739 -365 -853 -762 -165 -574 178 -1170 762
        -1325 83 -22 119 -26 252 -27 167 -1 249 12 379 61 522 195 808 759 655 1291
        -105 367 -397 650 -768 743 -90 23 -344 35 -427 19z m335 -436 c265 -69 455
        -303 473 -584 19 -302 -194 -586 -492 -656 -102 -23 -256 -15 -353 19 -361
        127 -532 540 -366 882 74 152 224 283 384 334 102 32 240 34 354 5z"
				/>
				<path
					d="M2430 2344 c-802 -59 -1470 -530 -1704 -1202 -65 -188 -105 -480 -76
        -566 16 -48 63 -102 111 -127 37 -19 75 -19 1796 -19 1679 0 1760 1 1798 19
        49 22 99 77 115 127 16 46 8 209 -15 334 -136 730 -780 1301 -1597 1415 -111
        15 -339 25 -428 19z m315 -434 c374 -43 701 -195 945 -440 113 -112 199 -231
        254 -351 34 -75 86 -228 86 -255 0 -12 -193 -14 -1470 -14 -1277 0 -1470 2
        -1470 14 0 27 52 180 86 255 85 185 271 398 454 520 326 217 727 315 1115 271z"
				/>
			</g>
		</svg>
		<span class="btm-nav-label">내 정보</span>
	</button>
</div>

<!-- 페이지 최상단으로 이동 버튼 -->
<button
	on:click={scrollToTop}
	class="fixed bottom-20 right-3 bg-gray-00 text-white rounded-full p-2 shadow-lg z-50"
	style="display: flex; align-items: center; justify-content: center; background-color: rgba(0, 0, 0, 0.5); border: none; opacity: 0.6;"
	title="페이지 최상단으로 이동"
>
	<svg
		xmlns="http://www.w3.org/2000/svg"
		class="h-6 w-6"
		fill="none"
		viewBox="0 0 24 24"
		stroke="currentColor"
		style="stroke: rgba(255, 255, 255, 0.7);"
		><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7" /></svg
	>
</button>

<svelte:head>
	<script>
		var global = window;
	</script>
</svelte:head>

<style>
	.navbar {
		color: oklch(0.77 0.2 132.02);
	}

	.footer {
		background-color: #f6f7f3;
	}

	.btm-nav-label {
		font-size: 0.7rem; /* 폰트 크기를 작게 설정합니다. */
		font-weight: 300;
	}
</style>
