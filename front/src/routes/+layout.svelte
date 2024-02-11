<script lang="ts">
	import { onMount } from 'svelte';
	import '$lib/app.css';
	import rq from '$lib/rq/rq.svelte';
	import '../tailwind.css';

	const { children } = $props();

	onMount(() => {
		rq.initAuth();
	});
</script>

<header>
	<div class="navbar bg-base-100">
		<div class="navbar-start">
			<div class="dropdown">
				<div tabindex="0" role="button" class="btn btn-ghost btn-circle">
					<svg
						xmlns="http://www.w3.org/2000/svg"
						class="h-5 w-5"
						fill="none"
						viewBox="0 0 24 24"
						stroke="currentColor"
						><path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M4 6h16M4 12h16M4 18h7"
						/></svg
					>
				</div>
				<ul
					tabindex="0"
					class="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52"
				>
					<li><a href="/job-post/list">구인공고목록</a></li>
					<li><a href="/member/me">마이페이지</a></li>
				</ul>
			</div>
		</div>
		<div class="navbar-center">
			<a class="btn btn-ghost text-xl" href="/">구해유</a>
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
</header>
<main>{@render children()}</main>
<footer class="flex flex-col justify-center items-center p-10 footer bg-base-100 text-base-content">
  <div class="flex">
    <div class="flex flex-col items-center space-y-2">
      <span class="footer-title">Services</span>
      <a class="link link-hover">Branding</a>
      <a class="link link-hover">Design</a>
      <a class="link link-hover">Marketing</a>
      <a class="link link-hover">Advertisement</a>
    </div>
    <div class="flex flex-col items-center space-y-2">
      <span class="footer-title">Company</span>
      <a class="link link-hover">About us</a>
      <a class="link link-hover">Contact</a>
      <a class="link link-hover">Jobs</a>
      <a class="link link-hover">Press kit</a>
    </div>
    <div class="flex flex-col items-center space-y-2">
      <span class="footer-title">Social</span>
      <a class="link link-hover">Twitter</a>
      <a class="link link-hover">Instagram</a>
      <a class="link link-hover">YouTube</a>
      <a class="link link-hover">LinkedIn</a>
    </div>
    <div class="flex flex-col items-center space-y-2">
      <span class="footer-title">Legal</span>
      <a class="link link-hover">Terms of Use</a>
      <a class="link link-hover">Privacy Policy</a>
      <a class="link link-hover">Cookie Policy</a>
    </div>
  </div>
  <div>
    <span class="text-sm text-center">© 2024 All Rights Reserved by Techit7-11번과_GooHaeYou
</span>
  </div>
</footer>
