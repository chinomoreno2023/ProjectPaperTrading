function showOptionsList() {
    var selectedValue = document.getElementById("portfolioSelector").value;
    window.location.href = "/portfolio/options?selectedValue=" + selectedValue;
}

function logout() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken
        }
    });
    alert('Вы разлогинились');
    window.open('/')
}