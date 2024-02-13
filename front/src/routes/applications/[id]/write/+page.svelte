<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	// 지원서 데이터
	let applicationsData = {
		applicationBody: ''
	};

	async function loadjobPostData() {
		const { data } = await rq.apiEndPoints().GET('/api/job-posts/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			}
		});

		return data!.data!;
	}

	async function writeApplications(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		const postId = parseInt($page.params.id);

		const response = await rq.apiEndPoints().POST(`/api/applications/${postId}`, {
			body: {
				body: form.applicationBody.value.trim()
			}
		});

		console.log(postId);

		if (response.data?.statusCode === 201) {
			rq.msgAndRedirect({ msg: '지원 완료' }, undefined, `/job-post/${postId}`);
		}
	}

	function calculateAge(birthDate: string): number {
		const today = new Date();
		const birth = new Date(birthDate);
		let age = today.getFullYear() - birth.getFullYear();
		const m = today.getMonth() - birth.getMonth();
		if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
			age--;
		}
		return age;
	}

	$: memberAge = rq.member && rq.member.birth ? calculateAge(rq.member.birth) : '정보 없음';
</script>

{#await loadjobPostData() then jobPostDetailDto}
	<div class="card max-w-md mx-auto bg-base-100 shadow-xl">
		<div class="card-body">
			<h2 class="card-title">{jobPostDetailDto.title}</h2>
			<div>마감일: {jobPostDetailDto.deadLine}</div>
			<div class="divider"></div>
			<p>{jobPostDetailDto.body}</p>
			<div class="divider"></div>
			<div>
				지원 가능 최소 나이:
				<span class="badge badge-outline">
					{jobPostDetailDto.minAge === 0 ? '없음' : jobPostDetailDto.minAge ?? '없음'}
				</span>
			</div>
			<div>내 나이: {memberAge}</div>
			<div>
				지원 가능 성별:
				<span
					class="badge {jobPostDetailDto.gender === 'MALE'
						? 'badge-primary'
						: jobPostDetailDto.gender === 'FEMALE'
							? 'badge-pink'
							: 'badge-neutral'}"
				>
					{jobPostDetailDto.gender === 'MALE'
						? '남'
						: jobPostDetailDto.gender === 'FEMALE'
							? '여'
							: '무관'}
				</span>
			</div>
			<div>
				내 성별: {rq.member.gender === 'MALE'
					? '남'
					: rq.member.gender === 'FEMALE'
						? '여'
						: '무관'}
			</div>

			{#if memberAge !== '정보 없음' && memberAge >= jobPostDetailDto.minAge && (jobPostDetailDto.gender === 'UNDEFINED' || jobPostDetailDto.gender === rq.member.gender)}
				<div class="badge badge-accent">지원 가능</div>
				<form on:submit|preventDefault={writeApplications}>
					<div class="form-control w-full mt-4">
						<label class="label">
							<span class="label-text text-lg">지원서 작성</span>
						</label>
						<textarea
							id="applicationBody"
							name="applicationBody"
							class="textarea textarea-bordered h-24 w-full"
							placeholder="내용을 입력하세요."
							required
							bind:value={applicationsData.applicationBody}
						></textarea>
					</div>
					<div class="form-control mt-3">
						<button type="submit" class="btn btn-primary">지원하기</button>
					</div>
				</form>
			{:else}
				<div class="badge badge-warning gap-2">
					<svg
						xmlns="http://www.w3.org/2000/svg"
						fill="none"
						viewBox="0 0 24 24"
						class="inline-block w-4 h-4 stroke-current"
						><path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M6 18L18 6M6 6l12 12"
						></path></svg
					>
					지원 불가능
				</div>
			{/if}
		</div>
	</div>
{/await}
