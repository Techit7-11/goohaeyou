<script>
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	onMount(async () => {
		var container = document.getElementById('map'); //지도 표시할 div
		var options = {
			center: new kakao.maps.LatLng(33.450701, 126.570667), //지도 중심좌표
			level: 3 //지도 확대 레벨
		};
		var map = new kakao.maps.Map(container, options); //지도 생성

		//주소-좌표 변환 객체
		var geocoder = new kakao.maps.services.Geocoder();

		// 콜백 함수 내에서 작업 제목을 받을 수 있도록 변경
		var callback = function (result, status, title, id) {
			if (status === kakao.maps.services.Status.OK) {
				var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

				// 결과값으로 받은 위치를 마커로 표시합니다
				var marker = new kakao.maps.Marker({
					map: map,
					position: coords
				});

				// 마커에 커서가 오버됐을 때 마커 위에 표시할 인포윈도우를 생성합니다
				var iwContent = `<div style="padding:5px;">${title}</div>`; // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능합니다

				// 인포윈도우를 생성합니다
				var infowindow = new kakao.maps.InfoWindow({
					content: iwContent
				});

				// 마커에 마우스오버 이벤트를 등록합니다
				kakao.maps.event.addListener(marker, 'mouseover', function () {
					// 마커에 마우스오버 이벤트가 발생하면 인포윈도우를 마커위에 표시합니다
					infowindow.open(map, marker);
				});

				// 마커에 마우스아웃 이벤트를 등록합니다
				kakao.maps.event.addListener(marker, 'mouseout', function () {
					// 마커에 마우스아웃 이벤트가 발생하면 인포윈도우를 제거합니다
					infowindow.close();
				});

				// 마커에 클릭이벤트를 등록합니다
				kakao.maps.event.addListener(marker, 'click', function () {
					window.location.href = `/job-post/${id}`;
				});
			} else {
				console.error('Geocoder failed due to: ' + status);
			}
		};

		try {
			const response = await rq.apiEndPoints().GET('/api/job-posts', {});
			if (response.data && Array.isArray(response.data.data)) {
				for (let i = 0; i < response.data.data.length; i++) {
					const job = response.data.data[i];
					const location = job.location;
					const title = job.title;
					const id = job.id;

					// 주소로 좌표 검색
					geocoder.addressSearch(location, (result, status) => {
						// 콜백 함수 내에서 작업 제목을 전달
						callback(result, status, title, id);
					});
				}
			} else {
				console.error('Invalid response data format:', response.data);
			}
		} catch (error) {
			console.error('Error fetching job posts:', error);
		}
	});
</script>

<div id="map" style="width:90%;height:400px;"></div>
