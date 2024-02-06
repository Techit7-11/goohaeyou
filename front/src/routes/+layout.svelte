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
              <div tabindex="0" class="btn btn-ghost btn-circle">
                  <a>메뉴</a>
              </div>
              <ul tabindex="0" class="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52">
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
              <button class="btn btn-ghost mx-3" on:click={() => rq.logoutAndRedirect('/')}>로그아웃</button>
          {/if}
      </div>
  </div>
  </header>
<main>{@render children()}</main>
<footer class="bg-blue-900 text-white">footer</footer>