<script lang="ts">
    import rq from '$lib/rq/rq.svelte';

    async function submitLoginForm(this: HTMLFormElement) {
        const form: HTMLFormElement = this;

        try {
            const response = await rq.apiEndPoints().POST('/api/member/login', {
                body: {
                    username: form.username.value.trim(),
                    password: form.password.value.trim()
                }
            });

            // 성공 응답을 올바르게 처리하는 로직
            if (response.response.ok) {
                rq.setLogined({ id: Number(response.data) });
                rq.msgAndRedirect({ msg: '로그인 성공' }, undefined, 'http://localhost:5173/');
            } else {
                // 서버가 올바른 데이터를 반환하지 않았을 때의 처리
                throw new Error('로그인 실패: 서버에서 유효한 응답을 반환하지 않음');
            }
        } catch (error) {
            const err = error as any;
            if (err.response.status >= 400 && err.response.status < 500) {
                const errorData = err.response.data;
                if (errorData.errorCode && errorData.message) {
                    rq.msgError(`${errorData.message} (오류 코드: ${errorData.errorCode})`);
                } else if (Array.isArray(errorData)) {
                    errorData.forEach(msg => rq.msgError(msg));
                } else {
                    rq.msgError('로그인 중 오류가 발생했습니다.');
                }
            } else {
                rq.msgError('서버와의 통신 중 오류가 발생했습니다.');
            }
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
        margin-bottom: 20px;
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
</style>

<div class="login-container">
    <h1>로그인</h1>
    <form on:submit|preventDefault={submitLoginForm}>
        <div class="form-group">
            <label for="username">아이디</label>
            <input type="text" id="username" name="username" placeholder="아이디" required />
        </div>
        <div class="form-group">
            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" placeholder="비밀번호" required />
        </div>
        <button class="login-button" type="submit">로그인</button>
    </form>
</div>
