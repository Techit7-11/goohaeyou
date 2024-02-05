<!-- <script lang="ts">
    import { page } from '$app/stores';
    import rq from '$lib/rq/rq.svelte';
    import { onMount } from 'svelte';
    onMount(() => {
      const provierTypeCode = $page.url.searchParams.get('provierTypeCode');
      console.log(provierTypeCode);
      rq.msgInfo('구글 로그인 완료');
      rq.replace(`localhost:5173/`);    // 메인페이지로 이동
    });
  </script> -->