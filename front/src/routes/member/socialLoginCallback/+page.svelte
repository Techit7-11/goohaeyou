<script lang="ts">
  import { page } from '$app/stores';
  import rq from '$lib/rq/rq.svelte';
  import { onMount } from 'svelte';

  onMount(async () => { 
    await rq.initAuth(); 
    const provierTypeCode = $page.url.searchParams.get('provierTypeCode');
    console.log(provierTypeCode);

    if (rq.member.name == null || rq.member.name == undefined) {
      rq.msgAndRedirect(undefined, { msg: '회원정보 입력이 필요합니다.' }, '/member/social/modify');
    } else {
      rq.msgAndRedirect(undefined, { msg: '로그인 성공' }, '/');
    }
  });
</script>