// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 테두리 제거 기능 초기화
    removeBorderOnMenuFocus();
});

// 메뉴 포커스 시 테두리 제거
function removeBorderOnMenuFocus() {
    // 메가메뉴 링크 (관광기차여행) 요소 찾기
    const megaMenuLink = document.getElementById('megaMenuLink');
    if (!megaMenuLink) return;
    
    // focus 이벤트 처리
    megaMenuLink.addEventListener('focus', function() {
        this.style.outline = 'none';
        this.style.boxShadow = 'none';
        this.style.border = 'none';
    });
    
    // blur 이벤트 처리
    megaMenuLink.addEventListener('blur', function() {
        this.style.outline = 'none';
        this.style.boxShadow = 'none';
        this.style.border = 'none';
    });
    
    // 모든 내비게이션 링크에 대해 테두리 제거
    document.querySelectorAll('.navbar .nav-link').forEach(link => {
        link.addEventListener('focus', function() {
            this.style.outline = 'none';
            this.style.boxShadow = 'none';
            this.style.border = 'none';
        });
        
        link.addEventListener('blur', function() {
            this.style.outline = 'none';
            this.style.boxShadow = 'none';
            this.style.border = 'none';
        });
    });
    
    // 드롭다운 메뉴 토글 시 테두리 제거
    megaMenuLink.addEventListener('click', function() {
        setTimeout(() => {
            this.style.outline = 'none';
            this.style.boxShadow = 'none';
            this.style.border = 'none';
        }, 0);
    });
    
    // 마우스 진입/이탈 시 테두리 제거
    const dropdown = megaMenuLink.closest('.dropdown');
    if (dropdown) {
        dropdown.addEventListener('mouseenter', function() {
            megaMenuLink.style.outline = 'none';
            megaMenuLink.style.boxShadow = 'none';
            megaMenuLink.style.border = 'none';
        });
        
        dropdown.addEventListener('mouseleave', function() {
            megaMenuLink.style.outline = 'none';
            megaMenuLink.style.boxShadow = 'none';
            megaMenuLink.style.border = 'none';
        });
    }
}
