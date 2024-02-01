<script lang="ts">
    import rq from '$lib/rq/rq.svelte';

    async function load() {
        const { data } = await rq.apiEndPoints().GET('/api/job-posts', {});
        
        return data!;
    }
</script>

<style>
    ul {
        list-style-type: none; /* 목록 스타일 제거 */
        padding: 0;
    }

    li {
        border: 1px solid #000; /* 테두리 추가 */
        margin: 10px 0;
        padding: 10px;
        background-color: #fff; /* 배경색을 흰색으로 설정 */
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 그림자 효과 추가 */
        border-radius: 10px; /* 동그란 테두리 반지름 설정 */
        width: 55%; /* 3/4 크기 설정 */
        margin: 10px 10px 10px; /* 오른쪽으로 살짝 이동 */
    }

    a {
        text-decoration: none; /* 링크 텍스트 밑줄 제거 */
        color: #000; /* 텍스트 색상을 검은색으로 설정 */
        font-weight: bold;
    }

    a:hover {
        color: #666; /* 링크에 호버 효과를 주어 회색으로 변경 */
    }

    /* 텍스트 위치 조정 */
    li a:nth-child(2) {
        margin-left: 2em; /* 제목과의 간격 조정 */
    }

    li a:nth-child(3) {
        margin-left: 6em; /* 작성자와의 간격 조정 */
    }

    li a:nth-child(4) {
        margin-left: 2em; /* 작성일시와의 간격 조정 */
    }

    li a:nth-child(5) {
        margin-left: 1em; /* closed 와의 간격 조정 */
    }

    footer {
        clear: both; /* float 요소 뒤에 오는 요소에 영향을 주지 않도록 설정 */
        text-align: center; /* 텍스트 가운데 정렬 */
        background-color: #fff; /* 배경색을 흰색으로 설정 */
        box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1); /* 그림자 효과 추가 */
        border-top: 1px solid #000; /* 상단 테두리 추가 */
        padding: 10px;
    }
</style>

{#await load()}
    <p>loading...</p>
{:then { data : jobPostDtoList } }
    <ul>
        {#each jobPostDtoList ?? [] as jobPostDto, index}
            <li>
                <a href="/job-post/{jobPostDto.id}">no.{index + 1}</a>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.title}</a>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.author}</a>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.createdAt}</a>
                <a href="/job-post/{jobPostDto.id}">
                    {#if jobPostDto.closed}
                        마감
                    {:else}
                        구인중
                    {/if}
                </a>
            </li>
        {/each}
    </ul>
{/await }
