document.addEventListener("DOMContentLoaded", function () {
    const submenus = document.querySelectorAll('.dropdown-submenu');
    submenus.forEach(function (submenu) {
        submenu.addEventListener('mouseenter', function () {
            let dropdown = this.querySelector('.dropdown-menu');
            if (dropdown) dropdown.classList.add('show');
        });
        submenu.addEventListener('mouseleave', function () {
            let dropdown = this.querySelector('.dropdown-menu');
            if (dropdown) dropdown.classList.remove('show');
        });
    });
});
