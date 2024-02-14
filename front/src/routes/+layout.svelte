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
</script>

<header>
	<div class="navbar bg-base-100">
		<div class="navbar-start">
			<a class="btn btn-ghost text-xl" href="/">GooHaeYou</a>
		</div>
		<div class="navbar-center">
		</div>
		<div class="navbar-end">
			{#if rq.isLogout()}
				<a class="btn btn-ghost mx-3" href="/member/login">로그인</a>
			{/if}
			{#if rq.isLogin()}
				<button class="btn btn-ghost mx-3" on:click={() => rq.logoutAndRedirect('/')}
					>로그아웃</button
				>
			{/if}
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
		<p>© 2024 All Rights Reserved by Techit7-11번과_GooHaeYou</p>
	</aside>
</footer>
<div class="btm-nav">
<button on:click={NavHomepage}>
    <span class="btm-nav-label"></span>
    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" /></svg>
  </button>
   <button on:click={NavSearch}>
    <span class="btm-nav-label"></span>
    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
  </button>
   <button on:click={NavAlert}>
    <span class="btm-nav-label"></span>
    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
  </button>
   <button on:click={NavMyPage}>
      <span class="btm-nav-label"></span>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="w-6 h-6 opacity-70"><path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6ZM12.735 14c.618 0 1.093-.561.872-1.139a6.002 6.002 0 0 0-11.215 0c-.22.578.254 1.139.872 1.139h9.47Z" /></svg>
    </button>
</div>