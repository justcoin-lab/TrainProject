// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 국가 탭 전환 기능
    const countryTabs = document.querySelectorAll('.country-tab');
    const koreaRegionList = document.getElementById('korea-region-list');
    const japanRegionList = document.getElementById('japan-region-list');

    if (countryTabs.length > 0) {
        countryTabs.forEach(tab => {
            tab.addEventListener('click', function() {
                // 모든 탭에서 active 클래스 제거
                countryTabs.forEach(t => t.classList.remove('active'));
                
                // 클릭된 탭에 active 클래스 추가
                this.classList.add('active');
                
                // 국가에 따른 지역 목록 표시
                const country = this.getAttribute('data-country');
                if (country === 'korea') {
                    koreaRegionList.classList.remove('d-none');
                    japanRegionList.classList.add('d-none');
                } else if (country === 'japan') {
                    koreaRegionList.classList.add('d-none');
                    japanRegionList.classList.remove('d-none');
                }
            });
        });
    }

    // AOS 애니메이션 초기화 (Animation On Scroll)
    if (typeof AOS !== 'undefined') {
        AOS.init({
            duration: 800,
            easing: 'ease-in-out'
        });
    }

    // 부트스트랩 툴팁 초기화
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    if (tooltipTriggerList.length > 0) {
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl)
        });
    }

    // 네비게이션 바 스크롤 이벤트
    const navbar = document.querySelector('.navbar');
    if (navbar) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                navbar.classList.add('navbar-scrolled');
            } else {
                navbar.classList.remove('navbar-scrolled');
            }
        });
    }
});
