<script>
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';
	import { page } from '$app/stores';

	onMount(async () => {
		const container = document.getElementById('map');
		const options = {
			center: new kakao.maps.LatLng(33.450701, 126.570667),
			level: 3
		};
		var map = new kakao.maps.Map(container, options); //지도 생성

		//주소-좌표 변환 객체
		var geocoder = new kakao.maps.services.Geocoder();

		var callback = function (result, status) {
			if (status === kakao.maps.services.Status.OK) {
				var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

				// 결과값으로 받은 위치를 마커로 표시합니다
				var marker = new kakao.maps.Marker({
					map: map,
					position: coords
				});

				// 마커가 표시된 위치를 지도의 중심으로 설정합니다.
				map.setCenter(coords);
			}
		};

		try {
			const postId = $page.params.id; // 현재 페이지의 ID 가져오기
			const response = await rq.apiEndPoints().GET(`/api/job-posts/${postId}`, {}); // API 요청 보내기

			const job = response.data.data;
			const location = job.location;

			//주소로 좌표 검색
			geocoder.addressSearch(location, callback);
		} catch (error) {
			console.error('Error fetching job posts:', error);
		}

		map.setMinLevel(3);
		map.setMaxLevel(6);
	});
</script>

<div id="map" style="width:100%;height:160px;  z-index: 0;"></div>
