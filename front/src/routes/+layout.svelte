<script lang="ts">
	import { onMount } from 'svelte';
	import '$lib/app.css';
	import rq from '$lib/rq/rq.svelte';
	import '../tailwind.css';

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
	function NavHomepage() {
		window.location.href = '/';
	}
	function NavAlert() {
		window.location.href = '/notification/list';
	}
	function NavSearch() {
		window.location.href = '/job-post/search';
	}

	onMount(() => {
		rq.initAuth();
	});

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
</script>

<header>
	<div class="fixed-div">
		<div class="navbar bg-base-100">
			<div class="navbar-start">
				<a class="btn btn-ghost text-xl" href="/">GooHaeYou</a>
			</div>
			<div class="navbar-center"></div>
			<div class="navbar-end">
				{#if rq.isLogout()}
					<a class="btn btn-ghost mx-3" href="/member/login">Login</a>
				{/if}
				{#if rq.isLogin()}
					<button class="btn btn-ghost mx-3" on:click={() => rq.logoutAndRedirect('/')}
						>Logout</button
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
		<a class="link link-hover">Press kit</a>
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
		<p class="mb-10">© 2024 All Rights Reserved by Techit7-11번과_GooHaeYou</p>
	</aside>
</footer>
<div class="btm-nav">
	<button on:click={NavSearch}>
		<span class="btm-nav-label"></span>
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
				d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
			/></svg
		>
	</button>
	<button on:click={NavHomepage}>
		<span class="btm-nav-label"></span>
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
				d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
			/></svg
		>
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
		<span class="btm-nav-label"></span>
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
	</button>
	<!-- {/if} -->

	<button on:click={NavMyPage}>
		<span class="btm-nav-label"></span>
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
	</button>
</div>
