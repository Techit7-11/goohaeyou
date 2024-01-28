<script lang="ts">
    import rq from '$lib/rq/rq.svelte';

    async function load() {
        const { data } = await rq.apiEndPoints().GET('/api/job-posts', {});
        
        return data!;
    }
</script>

{#await load()}
    <p>loading...</p>
{:then { data: jobPostDtos } }
    <h1>구인공고 목록</h1>
    <ul>
        {#each jobPostDtos ?? [] as jobPostDto}
            <li>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.title}</a>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.body}</a>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.author}</a>
                <a href="/job-post/{jobPostDto.id}">{jobPostDto.createdAt}</a>
            </li>
        {/each}
    </ul>
{/await }
