<script>
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';
	import { page } from '$app/stores';
	const KAKAO_MAPS_SDK_URL =
		'//dapi.kakao.com/v2/maps/sdk.js?appkey=dbec7e19bbbe4a9e21a7b64b16dd537c&autoload=false&libraries=services';
	onMount(async () => {
		if (!window.kakao || !window.kakao.maps) {
			loadKakaoMapsScript(KAKAO_MAPS_SDK_URL)
				.then(() => {
					kakao.maps.load(() => initializeMap());
				})
				.catch((error) => console.error('Failed to load the Kakao Maps script:', error));
		} else {
			kakao.maps.load(() => initializeMap());
		}
	});

	async function initializeMap() {
		const container = document.getElementById('map');
		const options = {
			center: new kakao.maps.LatLng(33.450701, 126.570667),
			level: 3
		};
		const map = new kakao.maps.Map(container, options);

		map.setMinLevel(3);
		map.setMaxLevel(6);

		await setMarkerFromApi(map);
	}

	async function setMarkerFromApi(map) {
		try {
			const postId = $page.params.id;
			const response = await rq.apiEndPoints().GET(`/api/job-posts/${postId}`);
			if (response.data && response.data.data && response.data.data.location) {
				addMarkerByAddress(map, response.data.data.location);
			} else {
				throw new Error('Location data is missing');
			}
		} catch (error) {
			console.error('Error fetching job post location:', error);
		}
	}

	function addMarkerByAddress(map, address) {
		const geocoder = new kakao.maps.services.Geocoder();
		geocoder.addressSearch(address, (result, status) => {
			if (status === kakao.maps.services.Status.OK) {
				const coords = new kakao.maps.LatLng(result[0].y, result[0].x);
				new kakao.maps.Marker({
					map: map,
					position: coords
				});
				map.setCenter(coords);
			} else {
				console.error('Failed to get location from address:', status);
			}
		});
	}
	function loadKakaoMapsScript(src) {
		return new Promise((resolve, reject) => {
			const script = document.createElement('script');
			script.src = src;
			document.head.appendChild(script);
			script.onload = resolve;
			script.onerror = reject;
		});
	}
</script>

<div id="map" style="width:100%; height:160px;"></div>
