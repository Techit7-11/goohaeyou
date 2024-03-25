import { goto } from '$app/navigation';
import { get } from 'svelte/store'; // Import 'get' from Svelte store to access reactive variables.
import type { paths } from '$lib/types/api/v1/schema';
import createClient from 'openapi-fetch';
import type { components } from '$lib/types/api/v1/schema';

class Rq {
	public member: components['schemas']['MemberDto'];

	constructor() {
		this.member = this.makeReactivityMember();
	}

	// URL
	public goTo(url: string) {
		goto(url);
	}

	public replace(url: string) {
		goto(url, { replaceState: true });
	}

	// 인증
	// member 의 값이 바뀌면, member 를 사용하는 모든 곳에서 자동으로 즉각 반영된다.
	public makeReactivityMember() {
		let id = $state<number>(0); // 로그아웃 상태
		let username = $state<string>('');
		let gender = $state<'UNDEFINED' | 'MALE' | 'FEMALE' | undefined>('UNDEFINED');
		let location = $state<string>('');
		let birth = $state<string>('');
		let name = $state<string>('');
		let phoneNumber = $state<string>('');
		let email = $state<string>('');
		let authenticated = $state<boolean>(false);

		return {
			get id() {
				return id;
			},
			set id(value: number) {
				id = value;
			},
			get username() {
				return username;
			},
			set username(value: string) {
				username = value;
			},
			get name() {
				return name;
			},
			set name(value: string) {
				name = value;
			},
			get phoneNumber() {
				return phoneNumber;
			},
			set phoneNumber(value: string) {
				phoneNumber = value;
			},
			get email() {
				return email;
			},
			set email(value: string) {
				email = value;
			},
			get gender() {
				return gender;
			},
			set gender(value: 'UNDEFINED' | 'MALE' | 'FEMALE' | undefined) {
				gender = value;
			},
			get location() {
				return location;
			},
			set location(value: string) {
				location = value;
			},
			get birth() {
				return birth;
			},
			set birth(value: string) {
				birth = value;
			},
			get authenticated() {
				return authenticated;
			},
			set authenticated(value: boolean) {
				authenticated = value;
			}
		};
	}

	public setLogined(member: components['schemas']['MemberDto']) {
		Object.assign(this.member, member);
	}

	// API END POINTS
	public apiEndPoints() {
		return createClient<paths>({
			baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
			credentials: 'include',
			headers: {
				'Content-Type': 'application/json'
			}
		});
	}

	// MSG, REDIRECT
	public msgAndRedirect(
		data: { msg: string } | undefined,
		error: { msg: string } | undefined,
		url: string,
		callback?: () => void
	) {
		if (data) this.msgInfo(data.msg);
		if (error) this.msgError(error.msg);

		this.replace(url);

		if (callback) window.setTimeout(callback, 100);
	}

	public msgInfo(msg: string) {
		window.alert(msg);
	}

	public msgError(msg: string) {
		window.alert(msg);
	}

	public setLogout() {
		this.member.id = 0;
		this.member.username = '';
		this.member.gender = 'UNDEFINED';
		this.member.location = '';
		this.member.birth = '';
		this.member.name = '';
		this.member.phoneNumber = '';
		(this.member.email = ''), (this.member.authenticated = false);
	}

	public isLogin() {
		return this.member.id !== 0;
	}

	public isLogout() {
		return !this.isLogin();
	}

	public async initAuth() {
		const { data } = await this.apiEndPoints().GET('/api/member');

		if (data) {
			this.setLogined(data.data); // MemberDto 넘기기
		}
	}

	public async logoutAndRedirect(url: string) {
		try {
			await this.apiEndPoints().POST('/api/member/logout', {
				credentials: 'include'
			});
			this.setLogout();
			this.replace(url);
		} catch (error) {
			console.error('Logout failed:', error);
		}
	}

	public getGoogleLoginUrl() {
		return `${
			import.meta.env.VITE_CORE_API_BASE_URL
		}/member/socialLogin/google?redirectUrl=${encodeURIComponent(
			import.meta.env.VITE_CORE_FRONT_BASE_URL
		)}/member/socialLoginCallback?provierTypeCode=google`;
	}

	public goToCurrentPageWithNewParam(name: string, value: string) {
		// 현재 URL 객체 생성
		const currentUrl = new URL(window.location.href);

		// 쿼리 매개변수를 수정하기 위한 URLSearchParams 객체 생성
		const searchParams = currentUrl.searchParams;

		// 'page' 매개변수를 새 페이지 번호로 설정
		searchParams.set(name, value);

		this.goToCurrentPageWithNewQueryStr(searchParams.toString());
	}

	public goToCurrentPageWithNewQueryStr(query: string) {
		this.goTo(window.location.pathname + '?' + query);
	}
}

const rq = new Rq();

export default rq;
