// 관리자 페이지 스크립트

document.addEventListener('DOMContentLoaded', function() {
    // 현재 페이지 URL을 가져옵니다
    const currentUrl = window.location.pathname;
    
    // 사이드바 액티브 상태 설정
    const sidebarLinks = document.querySelectorAll('.sidebar .nav-link');
    
    sidebarLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentUrl.includes(href) && href !== '/admin' || 
            (href === '/admin' && currentUrl === '/admin')) {
            link.classList.add('active');
        }
    });
    
    // 삭제 버튼에 확인 기능 추가
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        if (!button.getAttribute('data-confirm')) {
            button.addEventListener('click', function(e) {
                if (!confirm('정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
                    e.preventDefault();
                }
            });
        }
    });
    
    // 토글 사이드바 기능 (모바일)
    const sidebarToggler = document.getElementById('sidebarToggler');
    const sidebar = document.getElementById('sidebar');
    
    if (sidebarToggler && sidebar) {
        sidebarToggler.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }
});