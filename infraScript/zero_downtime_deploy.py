#!/usr/bin/env python3

import os
import requests  # HTTP 요청을 위한 모듈 추가
import subprocess
import time
from typing import Dict, Optional


class ServiceManager:
    # 초기화 함수
    def __init__(self, socat_port: int = 8080, sleep_duration: int = 3) -> None:
        self.socat_port: int = socat_port
        self.sleep_duration: int = sleep_duration
        self.services: Dict[str, int] = {
            'goohaeyou_1': 8081,
            'goohaeyou_2': 8082
        }
        self.current_name: Optional[str] = None
        self.current_port: Optional[int] = None
        self.next_name: Optional[str] = None
        self.next_port: Optional[int] = None

    # 현재 실행 중인 서비스를 찾는 함수
    def _find_current_service(self) -> None:
        cmd: str = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $NF}}'"
        current_service: str = subprocess.getoutput(cmd)
        if not current_service:
            self.current_name, self.current_port = 'goohaeyou_2', self.services['goohaeyou_2']
        else:
            self.current_port = int(current_service.split(':')[-1])
            self.current_name = next((name for name, port in self.services.items() if port == self.current_port), None)

    # 다음에 실행할 서비스를 찾는 함수
    def _find_next_service(self) -> None:
        self.next_name, self.next_port = next(
            ((name, port) for name, port in self.services.items() if name != self.current_name),
            (None, None)
        )

    # Docker 컨테이너를 제거하는 함수
    def _remove_container(self, name: str) -> None:
        os.system(f"docker stop {name} 2> /dev/null")
        os.system(f"docker rm -f {name} 2> /dev/null")

    # Docker 컨테이너를 실행하는 함수
    def _run_container(self, name: str, port: int) -> None:
        os.system(
            f"docker run --name={name} -p {port}:8080 -v /docker_projects/goohaeyou/volumes/gen:/gen --restart unless-stopped -e TZ=Asia/Seoul --pull always -d ghcr.io/techit7-11/goohaeyou")

    def _switch_port(self) -> None:
        # Socat 포트를 전환하는 함수
        cmd: str = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $2}}'"
        pid: str = subprocess.getoutput(cmd)

        if pid:
            os.system(f"kill -9 {pid} 2>/dev/null")

        time.sleep(5)

        os.system(
            f"nohup socat -t0 TCP-LISTEN:{self.socat_port},fork,reuseaddr TCP:localhost:{self.next_port} &>/dev/null &")


    # 서비스 상태를 확인하는 함수
    def _is_service_up(self, port: int) -> bool:
        ready_url = f"http://127.0.0.1:{port}/ready"  # 준비 상태를 확인하는 엔드포인트
    try:
        response = requests.get(ready_url, timeout=5)
        if response.status_code == 200 and response.json().get('status') == 'READY':
            return True
    except requests.RequestException:
        pass
    return False

    # 서비스를 업데이트하는 함수
    def update_service(self) -> None:
        self._find_current_service()
        self._find_next_service()

        self._remove_container(self.next_name)
        self._run_container(self.next_name, self.next_port)

        # 새 서비스가 'UP' 상태가 될 때까지 기다림
        while not self._is_service_up(self.next_port):
            print(f"Waiting for {self.next_name} to be 'UP'...")
            time.sleep(self.sleep_duration)

        self._switch_port()

        if self.current_name is not None:
            self._remove_container(self.current_name)

        print("Switched service successfully!")


if __name__ == "__main__":
    manager = ServiceManager()
    manager.update_service()