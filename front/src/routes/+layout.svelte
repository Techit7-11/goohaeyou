<script lang="ts">
    import { onMount } from 'svelte';
    import '$lib/app.css';
    import rq from '$lib/rq/rq.svelte';
  
    const { children } = $props();
  
    onMount(() => {
      rq.initAuth();
    });
</script>

<header>
    <a href="/">구해유</a>
  
    {#if rq.isLogin()}
      <a href="/member/me">마이페이지</a>
      <button on:click={() => rq.logoutAndRedirect('/')}>로그아웃</button>
    {/if}
  
    {#if rq.isLogout()}
      <a href="/member/login">로그인</a>
      <a href="/member/join">회원가입</a>
    {/if}
    <a href="/job-post/list">구인공고목록</a>
</header>
<main>{@render children()}</main>
<footer>footer</footer>
  