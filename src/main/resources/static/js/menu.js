document.addEventListener('DOMContentLoaded', function () {
    var menuToggle = document.querySelector('.menu-toggle');
    var menuContent = document.getElementById('menuContent');

    menuToggle.addEventListener('click', function () {
        if (menuContent.style.display === 'flex') {
            menuContent.style.display = 'none';
            document.removeEventListener('click', closeMenuOnClickOutside);
            document.removeEventListener('keydown', closeMenuOnEscape);
        }
        else {
            menuContent.style.display = 'flex';
            document.addEventListener('click', closeMenuOnClickOutside);
            document.addEventListener('keydown', closeMenuOnEscape);
        }
    });

    function closeMenu() {
        menuContent.style.display = 'none';
        document.removeEventListener('click', closeMenuOnClickOutside);
        document.removeEventListener('keydown', closeMenuOnEscape);
    }

    function closeMenuOnClickOutside(event) {
        if (!menuContent.contains(event.target) && !menuToggle.contains(event.target)) {
            closeMenu();
        }
    }

    function closeMenuOnEscape(event) {
        if (event.key === 'Escape') {
            closeMenu();
        }
    }
});