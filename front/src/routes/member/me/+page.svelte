<script lang="ts">
    import { onMount } from 'svelte';
    import rq from '$lib/rq/rq.svelte';
    import prettyDate from '$lib/utils/ut';

    onMount(() => {
        if (rq.isLogout()) {
            rq.msgError('로그인이 필요합니다.');
			rq.goTo('/member/login');
        }
    });
</script>

<div class="flex items-center justify-center min-h-screen bg-base-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<div class="text-center">
				<div class="font-bold text-xl">내 정보</div>
				<div class="mt-3 text-gray-500">현재 로그인한 회원의 정보입니다.</div>
			</div>
			<div class="divider"></div>
			<div class="grid grid-colos-1 gap-4">
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">아이디</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.username}</div>
				</div>
				<div class="flex justify-between items-center w-full">
					<div class="font-bold">이름</div>
					<div class="text-gray-500 flex-auto text-right">{rq.member.name}</div>
				</div>
			</div>
		</div>
	</div>
</div>