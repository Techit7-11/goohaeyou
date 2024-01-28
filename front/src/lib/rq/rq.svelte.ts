import type { paths } from '$lib/types/api/v1/schema';
import createClient from 'openapi-fetch';

class Rq {
	public apiEndPoints() {
		return createClient<paths>({
			baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
			credentials: 'include'
		});
	}
}

const rq = new Rq();

export default rq;
