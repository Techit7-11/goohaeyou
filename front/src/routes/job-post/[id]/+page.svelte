<script lang="ts">
    import { page } from '$app/stores'
    import rq from '$lib/rq/rq.svelte';

    async function load() {
        const { data } = await rq
        .apiEndPoints()
        .GET('/api/job-posts/{id}', {
            params: {
                path: {
                    id: parseInt($page.params.id)
                }
            }
        });
        
        return data!;
    }
</script>

<style>
    .job-post-container {
        background-color: #fff; /* 화이트 톤 배경색 설정 */
        border: 1px solid #000; /* 블랙 톤 테두리 설정 */
        border-radius: 10px; /* 둥근 테두리 설정 */
        padding: 20px;
        margin: 20px;
        width: 70%;
        box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.2); /* 입체적인 그림자 효과 추가 */
    }

    .id-box {
        background-color: transparent; /* 투명 배경색 설정 */
        color: #d1d1d1; /* 흰색 글자색 설정 */
        width: 40px; /* 작은 크기로 설정 */
        height: 40px; /* 작은 크기로 설정 */
        display: flex;
        justify-content: center;
        align-items: center;
        font-weight: bold;
        font-size: 16px; /* 작은 글자 크기로 설정 */
        margin-right: 10px;
        opacity: 2; /* 완전 투명으로 설정 */
    }

    .title-box {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 10px;
    }

    .content-box {
        background-color: #fff; /* 화이트 톤 배경색 설정 */
        border: 1px solid #d1d1d1; /* 그레이 톤 테두리 설정 */
        border-radius: 10px; /* 둥근 테두리 설정 */
        padding: 20px;
        margin-top: 10px;
    }

    .status-box {
        background-color: #636364; /* 블랙 톤 배경색 설정 */
        color: #fff; /* 흰색 글자색 설정 */
        border-radius: 10px; /* 둥근 테두리 설정 */
        padding: 5px 10px;
        font-weight: bold;
        font-size: 16px;
        display: inline-block;
        margin-top: 20px; /* 한 칸 아래로 배치 */
    }

    .author-createdAt {
        display: flex;
        justify-content: space-between; /* 요소 사이의 간격을 최대로 설정 */
        margin-top: 10px; /* 위로 조금 띄움 */
    }

    .info-box {
        display: grid;
        grid-template-columns: repeat(2, 1fr); /* 2열 그리드 설정 */
        gap: 10px; /* 그리드 아이템 간격 설정 */
        margin-top: 20px;
    }

    .info-item {
        background: linear-gradient(145deg, #e6e6e6, #ffffff);
        box-shadow: 5px 5px 10px #d1d1d1,
                    -5px -5px 10px #ffffff;
        border-radius: 5px;
        padding: 10px;
        font-size: 16px;
        color: #333;
        font-weight: 500;
    }
</style>

{#await load()}
    <div>Loading...</div>
{:then { data: jobPostDto }}
    <div class="job-post-container">
        <div class="id-box">No.{jobPostDto?.id}</div>
        <div class="title-box">{jobPostDto?.title}</div>
        <div class="author-createdAt">
            <p>{jobPostDto?.author}</p>
            <p>등록일시 {jobPostDto?.createdAt}</p>
        </div>
        <div class="content-box">{jobPostDto?.body}</div>
        <p>
            {#if jobPostDto?.closed}
                <span class="status-box">마감</span>
            {:else}
                <span class="status-box">구인중</span>
            {/if}
        </p>
        <div class="info-box">
            <div>위치: {jobPostDto?.location}</div>
            <div>댓글 수: {jobPostDto?.commentsCount}</div>
            <div>지원자 수: {jobPostDto?.applicationCount}</div>
            <div>관심등록 수: {jobPostDto?.interestsCount}</div>
        </div>
    </div>
{/await}
