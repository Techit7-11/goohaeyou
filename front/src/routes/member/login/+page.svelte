<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function submitLoginForm(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		// 로그인 요청 (이 때 쿠키에 저장)
		const response = await rq.apiEndPoints().POST('/api/member/login', {
			body: {
				username: form.username.value.trim(),
				password: form.password.value.trim()
			}
		});

		if (response.data?.resultType === 'SUCCESS') {
			rq.msgAndRedirect({ msg: '로그인 성공' }, undefined, '/'); // 메인페이지로 이동
			rq.setLogined(response.data?.data); // 로그인 상태로 바꾼다
		} else if (response.data?.resultType === 'CUSTOM_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else if (response.data?.resultType === 'VALIDATION_EXCEPTION') {
			rq.msgError(response.data?.message);
		} else {
			rq.msgError('로그인 중 오류가 발생했습니다.');
		}
	}
</script>

<link href="https://unpkg.com/boxicons@2.0.7/css/boxicons.min.css" rel="stylesheet" />
<link
	rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css"
/>
<link href="https://fonts.googleapis.com/css2?family=Jua&display=swap" rel="stylesheet" />

<div class="flex items-center justify-center min-h-screen bg-light-green">
	<div class="container mx-auto px-4 text-center">
		<div class="max-w-sm mx-auto my-10">
			<h2 class="pont mb-4">GooHaeYou</h2>
			<form on:submit|preventDefault={submitLoginForm}>
				<div class="form-group">
					<label class="label" for="username">사용자ID</label>
					<div class="input-group">
						<i class="bx bx-user"></i>
						<input
							type="text"
							id="username"
							name="username"
							class="input input-bordered w-full"
							placeholder="ID를 입력해주세요."
							required
						/>
					</div>
				</div>
				<div class="form-group">
					<label class="label" for="password">비밀번호</label>
					<div class="input-group">
						<i class="bx bx-lock"></i>
						<input
							type="password"
							id="password"
							name="password"
							class="input input-bordered w-full"
							placeholder="비밀번호를 입력해주세요."
							required
						/>
					</div>
				</div>
				<div class="my-5">
					<button type="submit" class="w-full btn btn-green">로그인</button>
				</div>
				<div class="my-5 flex items-center">
					<a href={rq.getGoogleLoginUrl()} class="social"><i class="fab fa-google"></i></a>
					<span class="social-text">Google로 시작</span>
				</div>
				<p class="text-center text-sm text-gray-600">
					아직 회원이 아니라면?
					<a href="/member/join" class="font-semibold leading-6 text-green-600 hover:text-green-700"
						>회원가입</a
					>
				</p>
			</form>
		</div>
	</div>
</div>

<style>
	.bg-light-green {
		background-color: #fcfffd;
	}
	.container {
		background-color: #ffffff;
		border: none;
		border-radius: 10px;
		padding: 40px;
		box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.2);
		margin: 0 auto;
		width: auto;
		max-width: 85%;
		transform: translateY(-30px);
	}

	.form-group label {
		color: #3c9e41;
	}

	.input {
		border: 1px solid #bcdbbd;
		background-color: #ffffff;
		border-radius: 5px;
	}

	.input:focus {
		border-color: #66bb6a;
	}

	.input-group {
		display: flex;
		align-items: center;
		position: relative;
	}

	.input-group i {
		position: absolute;
		left: 10px;
		color: #5f695f;
	}

	.input-group .input {
		padding-left: 35px;
	}

	.btn-green {
		background-color: #69ca6e; /* 로그인 버튼 배경색 */
		color: #fff;
		font-size: 15px;
		transition: background-color 0.3s ease;
	}

	.btn-green:hover {
		background-color: #66bb6a; /* 로그인 버튼 호버 */
	}

	.social .fab {
		background-color: #80c283; /* 구글 아이콘 배경색 */
		color: white;
		border-radius: 50%;
		padding: 8px;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
	}

	.social {
		display: inline-flex;
		justify-content: center;
		align-items: center;
		text-decoration: none;
	}
	.social-text {
		margin-left: 10px; /* 아이콘과 텍스트 사이 간격 */
		color: #8f8f8f;
		font-size: 0.875rem; /* 텍스트 크기 조정 */
		align-self: center;
	}
	.my-5.flex.items-center {
		justify-content: center; /* 가운데 정렬 */
	}
	.pont {
		font-family: 'Jua', sans-serif;
		font-size: 2rem;
		color: #37b637;
	}
</style>
