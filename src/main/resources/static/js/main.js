// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // AOS 초기화
    AOS.init({
        duration: 800,
        once: false,
        offset: 120,
        delay: 100,
        easing: 'ease-in-out'
    });

    // 이미지 로딩 오류 처리
    setupImageErrorHandling();
    
    // 페이드 애니메이션
    setupFadeAnimations();
    
    // 탭 스크립트 초기화
    setupTabScripts();
    
    // 스크롤 이벤트 설정
    setupScrollEvents();
    
    // 호버 드롭다운 설정
    setupHoverDropdown();
    
    // 스무스 스크롤 설정
    setupSmoothScroll();
});

// 이미지 로딩 오류 처리
function setupImageErrorHandling() {
    const logoImg = document.querySelector('.navbar-brand img');
    if (!logoImg) return;

    console.log('로고 이미지 경로:', logoImg.src);
    
    // 이미지 현재 상태 체크
    if (logoImg.complete) {
        if (!logoImg.naturalWidth) {
            handleImageError(logoImg);
        }
    }
    
    // 이미지 오류 이벤트 핸들러
    logoImg.onerror = function() {
        handleImageError(this);
    };
}

// 이미지 오류 처리 함수
function handleImageError(img) {
    console.error('이미지 로드 실패:', img.src);
    // 이미지 로딩 실패시 다른 이미지로 시도
    if(img.src.includes('train_logo_test.jpg')) {
        console.log('두 번째 이미지 시도:', '/images/traintravel_logo.jpg');
        img.src = '/images/traintravel_logo.jpg';
    } else if(img.src.includes('traintravel_logo.jpg')) {
        console.log('세 번째 이미지 시도:', '/images/traintravel_logo.png');
        img.src = '/images/traintravel_logo.png';
    } else {
        console.log('모든 이미지 실패, 텍스트로 대체');
        // 모든 이미지 실패시 텍스트로 대체
        const brand = document.querySelector('.navbar-brand');
        brand.innerHTML = '<span class="fw-bold fs-4">이색 기차여행</span>';
    }
}

// Fade-up 애니메이션 설정
function setupFadeAnimations() {
    const fadeElements = document.querySelectorAll('.fade-up');
    if (fadeElements.length === 0) return;

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('inview');
            }
        });
    }, {
        threshold: 0.1
    });

    fadeElements.forEach(element => {
        observer.observe(element);
    });
}

// 드롭다운 호버 효과 (데스크톱)
function setupHoverDropdown() {
    if (window.innerWidth > 992) {
        document.querySelectorAll('.navbar .dropdown').forEach(function(dropdown) {
            dropdown.addEventListener('mouseenter', function() {
                if (!this.querySelector('.dropdown-toggle').classList.contains('show')) {
                    this.querySelector('.dropdown-toggle').click();
                }
            });
            dropdown.addEventListener('mouseleave', function() {
                if (this.querySelector('.dropdown-toggle').classList.contains('show')) {
                    this.querySelector('.dropdown-toggle').click();
                }
            });
        });
    }
}

// 윈도우 리사이즈 시 호버 드롭다운 설정 업데이트
window.addEventListener('resize', function() {
    setupHoverDropdown();
});

// 스크롤 시 헤더 스타일 변경
function setupScrollEvents() {
    window.addEventListener('scroll', function() {
        const navbar = document.querySelector('.navbar');
        if (!navbar) return;
        
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
}

// 탭 스크립트 초기화
function setupTabScripts() {
    const tabs = document.querySelectorAll('.country-tab');
    if (tabs.length === 0) return;
    
    const koreaList = document.getElementById('korea-region-list');
    const japanList = document.getElementById('japan-region-list');
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');

    // 메뉴 토글 버튼 클릭 이벤트 - 모바일에서 사용
    if (navbarToggler) {
        navbarToggler.addEventListener('click', function() {
            if (navbarCollapse.classList.contains('show')) {
                // 이미 열린 상태면 닫기
                navbarCollapse.classList.remove('show');
            }
        });
    }

    // 탭 클릭 이벤트 핸들러
    function handleTabClick(tab, initialLoad = false, evt = null) {
        // 모든 탭에서 active 클래스 제거
        tabs.forEach(t => t.classList.remove('active'));
        // 클릭한 탭에 active 클래스 추가
        tab.classList.add('active');

        // 선택된 국가에 따라 지역 목록 표시
        if (tab.dataset.country === 'korea') {
            koreaList.classList.remove('d-none');
            japanList.classList.add('d-none');
        } else if (tab.dataset.country === 'japan') {
            japanList.classList.remove('d-none');
            koreaList.classList.add('d-none');
        }
        
        // 탭 클릭 시 드롭다운 메뉴가 닫히지 않도록 이벤트 중지
        if (!initialLoad && evt) {
            evt.stopPropagation();
            evt.preventDefault();
        }
    }

    // 각 탭에 이벤트 리스너 등록
    tabs.forEach(tab => {
        tab.addEventListener('click', function(event) {
            handleTabClick(this, false, event);
        });
    });

    // 페이지 로드 시 기본 탭 상태 설정
    const defaultTab = document.querySelector('.country-tab.active');
    if (defaultTab) {
        handleTabClick(defaultTab, true);
    }
    
    // 지역 아이템 클릭 시 처리
    const regionItems = document.querySelectorAll('.region-item a');
    regionItems.forEach(item => {
        item.addEventListener('click', function(e) {
            // 타겟 ID 가져오기
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);
            
            if (targetElement) {
                e.preventDefault();
                
                // 드롭다운 메뉴 닫기
                const dropdownToggle = document.querySelector('#megaMenuLink');
                if (dropdownToggle && dropdownToggle.classList.contains('show')) {
                    dropdownToggle.click();
                }
                
                // 네비게이션 높이 고려하여 스크롤 위치 조정
                const navHeight = document.querySelector('.navbar').offsetHeight;
                
                // 약간의 지연을 두고 스크롤 (메뉴 닫힌 후)
                setTimeout(() => {
                    window.scrollTo({
                        top: targetElement.offsetTop - navHeight - 20,
                        behavior: 'smooth'
                    });
                }, 150);
            }
        });
    });
}

// 스무스 스크롤 설정
function setupSmoothScroll() {
    // 모든 # 시작 페이지 내 링크에 스무스 스크롤 처리
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;

            // 네비게이션 링크 클릭 처리
            handleNavLinkClick(targetId);
        });
    });
    
    // Tips 메뉴 클릭 이벤트 추가 연결
    const tipsLink = document.querySelector('a[href="#travel-tips"]');
    if (tipsLink) {
        tipsLink.addEventListener('click', function(e) {
            e.preventDefault();
            handleNavLinkClick('#travel-tips');
        });
    }
}

// 네비게이션 링크 클릭 처리 함수
function handleNavLinkClick(targetId) {
    const targetElement = document.querySelector(targetId);
    if (!targetElement) return;
    
    // 메뉴 닫기 - 모바일에서
    if (window.innerWidth < 992) {
        const navbarCollapse = document.querySelector('.navbar-collapse');
        if (navbarCollapse && navbarCollapse.classList.contains('show')) {
            document.querySelector('.navbar-toggler').click();
        }
    }
    
    // 네비게이션 높이 고려하여 스크롤 위치 조정
    const navHeight = document.querySelector('.navbar').offsetHeight;
    
    // 약간의 지연을 추가하여 메뉴가 닫힌 후 스크롤
    setTimeout(() => {
        window.scrollTo({
            top: targetElement.offsetTop - navHeight - 20, // 여유 공간 추가
            behavior: 'smooth'
        });
    }, 150);
}