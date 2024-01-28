<script lang="ts">
	import '$lib/app.css';

    const { children } = $props();
</script>

<header>
    <a href="/">홈</a>
    <a href="/member/login">로그인</a>
    <a href="/member/join">회원가입</a>
    <a href="/job-post/list">구인공고목록</a>
</header>
<main>{@render children()}</main>
<footer>footer</footer>
  