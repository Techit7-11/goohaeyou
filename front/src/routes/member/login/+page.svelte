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

		if (response.data?.statusCode === 200) {
			rq.msgAndRedirect({ msg: '로그인 성공' }, undefined, 'http://localhost:5173/'); // 메인페이지로 이동
			rq.setLogined(response.data?.data); // 로그인 상태로 바꾼다
		} else if (response.data?.msg === 'CUSTOM_EXCEPTION') {
			// CustomException 오류 메시지 처리
			const customErrorMessage = response.data?.data?.message;
			rq.msgError(customErrorMessage ?? '알 수 없는 오류가 발생했습니다.');
		} else if (response.data?.msg === 'VALIDATION_EXCEPTION') {
			if (Array.isArray(response.data.data)) {
				response.data.data.forEach((msg) => rq.msgError(msg));
			}
		} else {
			rq.msgError('로그인 중 오류가 발생했습니다.');
		}
	}
</script>

<style>
	body {
		background-color: #222; /* 배경색을 블랙 톤으로 설정 */
		font-family: Arial, sans-serif; /* 글꼴 설정 */
		display: flex;
		justify-content: center;
		align-items: center;
		height: 100vh; /* 화면 높이에 맞게 가운데 정렬 */
		margin: 0;
	}

	.login-container {
		background-color: #fff; /* 화이트 톤 배경색 설정 */
		border: 1px solid #333; /* 블랙 톤 테두리 설정 */
		border-radius: 10px; /* 둥근 테두리 설정 */
		padding: 20px;
		width: 400px;
		box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.2); /* 입체적인 그림자 효과 추가 */
		margin: 0 auto; /* 수평 가운데 정렬을 위한 속성 추가 */
	}

	.form-group {
		margin-bottom: 10px;
	}

	.form-group label {
		display: block;
		font-weight: bold;
		margin-bottom: 5px;
	}

	.form-group input {
		width: 100%;
		padding: 10px;
		border: 1px solid #ccc;
		border-radius: 5px;
		font-size: 16px;
	}

	.login-button {
		background-color: #333; /* 블랙 톤 배경색 설정 */
		color: #fff; /* 흰색 글자색 설정 */
		border: none;
		border-radius: 5px;
		padding: 10px 20px;
		font-size: 18px;
		cursor: pointer;
	}

	.login-button:hover {
		background-color: #444; /* 호버 시 배경색 변경 */
	}

	.button-group {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.google-login {
		margin-right: 40px; /* 구글 로그인 버튼과 로그인 버튼 사이 간격 조절 */
	}
</style>

<div class="flex items-center justify-center min-h-screen bg-gray-100">
	<div class="container mx-auto px-4">
		<div class="max-w-sm mx-auto my-10">
			<h2 class="text-2xl font-bold text-center my-3">로그인</h2>
			<form on:submit|preventDefault={submitLoginForm}>
				<div class="form-group">
					<label class="label" for="username">사용자ID</label>
					<input
						type="text"
						id="username"
						name="username"
						class="input input-bordered w-full"
						placeholder="ID를 입력해주세요."
						required
					/>
				</div>
				<div class="form-group">
					<label class="label" for="password">비밀번호</label>
					<input
						type="password"
						id="password"
						name="password"
						class="input input-bordered w-full"
						placeholder="비밀번호를 입력해주세요."
						required
					/>
				</div>
				<div class="my-5">
					<button type="submit" class="w-full btn btn-primary">들어가기</button>
				</div>
				<div>
					<a href={rq.getGoogleLoginUrl()} class="flex justify-center my-5">
						<img src="/google.png" alt="구글 로그인" />
					</a>
				</div>
				<p class="text-center text-sm text-gray-500">
					아직 회원이 아니라면?
					<a
						href="/member/join"
						class="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">회원가입</a
					>
				</p>
			</form>
		</div>
	</div>
</div>

