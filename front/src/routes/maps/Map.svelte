<script>
	import rq from '$lib/rq/rq.svelte';
	import { onMount } from 'svelte';

	// 현재 위치를 가져와서 지도를 보여주는 함수
	async function showCurrentLocation(map) {
		// HTML5의 geolocation을 사용할 수 있는지 확인
		if (navigator.geolocation) {
			// 현재 위치 가져오기
			navigator.geolocation.getCurrentPosition(
				async function (position) {
					const lat = position.coords.latitude; // 위도
					const lon = position.coords.longitude; // 경도

					// 현재 위치를 중심으로 지도 보여주기
					map.setCenter(new kakao.maps.LatLng(lat, lon));

					// 현재 위치에 마커 표시
					var marker = new kakao.maps.Marker({
						map: map,
						position: new kakao.maps.LatLng(lat, lon)
					});
				},
				function (error) {
					console.error('Error getting current location:', error);
				}
			);
		} else {
			console.error('Geolocation is not supported.');
		}
	}

	onMount(async () => {
		const container = document.getElementById('map');
		const options = {
			center: new kakao.maps.LatLng(33.450701, 126.570667),
			level: 3
		};
		var map = new kakao.maps.Map(container, options);

		// 현재 위치 보여주기
		await showCurrentLocation(map);

		var geocoder = new kakao.maps.services.Geocoder();

		try {
			const response = await rq.apiEndPoints().GET('/api/job-posts', {});
			if (response.data && Array.isArray(response.data.data)) {
				const markers = []; // 중복된 위치의 마커를 저장할 배열
				const infoWindows = []; // 인포윈도우를 저장할 배열

				for (let i = 0; i < response.data.data.length; i++) {
					const job = response.data.data[i];
					const location = job.location;
					const title = job.title;
					const id = job.id;

					// 주소로 좌표 검색
					geocoder.addressSearch(location, (result, status) => {
						if (status === kakao.maps.services.Status.OK) {
							var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

							// 중복된 위치의 마커인지 확인
							const duplicatedMarker = markers.find((marker) =>
								marker.getPosition().equals(coords)
							);

							// 중복된 위치의 마커가 없으면 새로운 마커를 생성하고 배열에 추가
							if (!duplicatedMarker) {
								var marker = new kakao.maps.Marker({
									map: map,
									position: coords
								});

								markers.push(marker);

								// 인포윈도우 내용을 동적으로 생성
								const infoContent = `
									<div style="padding:5px; max-height: 100px; overflow-y: auto; max-width: 170px; white-space: nowrap; text-overflow: ellipsis;">
										<ul>
											${response.data.data
												.filter((job) => job.location === location)
												.map(
													(job) =>
														`<li style="max-height: 26px; "><a href="/job-post/${job.id}" style="overflow: hidden; text-overflow: ellipsis; display: inline-block; max-width: 100%;">${job.title}</a></li>`
												)
												.join('')}
										</ul>
									</div>
								`;

								// 인포윈도우 생성
								const infoWindow = new kakao.maps.InfoWindow({
									content: infoContent,
									position: marker.getPosition()
								});

								// 마커 클릭 시 인포윈도우를 표시
								kakao.maps.event.addListener(marker, 'click', function () {
									infoWindows.forEach((window) => window.close());
									infoWindow.open(map, marker);
								});

								// 빈 지도를 클릭했을 때 인포윈도우가 닫히도록 이벤트 핸들러 추가
								kakao.maps.event.addListener(map, 'click', function () {
									infoWindows.forEach((window) => window.close());
								});

								infoWindows.push(infoWindow);
							}
						} else {
							console.error('Geocoder failed due to: ' + status);
						}
					});
				}
			} else {
				console.error('Invalid response data format:', response.data);
			}
		} catch (error) {
			console.error('Error fetching job posts:', error);
		}

		map.setMinLevel(2);
		map.setMaxLevel(5);
	});
</script>

<div id="map" style="width:90%;height:500px;"></div>
