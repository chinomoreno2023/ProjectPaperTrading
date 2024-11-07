function openModal(id, strike, type, daysToMaturity, price, collateral, action) {
    var modal = document.getElementById("myModal");
    var optionIdField = document.getElementById("optionIdField");
    var strikeField = document.getElementById("strikeField");
    var typeField = document.getElementById("typeField");
    var daysToMaturityField = document.getElementById("daysToMaturityField");
    var priceField = document.getElementById("priceField");
    var priceFieldDisplay = document.getElementById("priceFieldDisplay");
    var collateralField = document.getElementById("collateralField");
    var collateralFieldDisplay = document.getElementById("collateralFieldDisplay");
    var buyOrWriteField = document.getElementById("buyOrWriteField");
    var volumeField = document.getElementById("volumeField");

    var errorMessageElement = document.getElementById("errorMessage");
    errorMessageElement.textContent = "";

    optionIdField.value = id;
    strikeField.value = strike;
    typeField.value = type;
    daysToMaturityField.value = daysToMaturity;
    priceField.value = price;
    priceFieldDisplay.textContent = roundToTwo(price);
    collateralField.value = collateral;
    collateralFieldDisplay.textContent = collateral;

    if (action === 'buy') {
        buyOrWriteField.value = 1;
    } else if (action === 'write') {
        buyOrWriteField.value = -1;
    }

    volumeField.value = '0';

    modal.style.display = "block";

    window.addEventListener('click', outsideClick);
    window.addEventListener('keydown', escapeKey);
}

document.getElementById("volumeField").addEventListener('input', function() {
    this.value = this.value.replace(/^0+/, '') || '0';
});

document.getElementById("volumeField").addEventListener('blur', function() {
    if (this.value === '') {
        this.value = '0';
    }
});

function outsideClick(e) {
    var modal = document.getElementById("myModal");
    if (e.target == modal) {
        modal.style.display = "none";
        window.removeEventListener('click', outsideClick);
    }
}

function escapeKey(e) {
    var modal = document.getElementById("myModal");
    if (e.key === "Escape") {
        modal.style.display = "none";
        window.removeEventListener('keydown', escapeKey);
    }
}

function closeModal() {
    var modal = document.getElementById("myModal");
    modal.style.display = "none";
}

document.addEventListener('keydown', function(event) {
    if (event.key === "Enter") {
        var modal = document.getElementById("myModal");
        if (modal.style.display === "block") {
            event.preventDefault();
            confirmOperation();
        }
    }
});

function confirmOperation() {
    var form = document.getElementById("optionForm");

    var id = form.querySelector("#optionIdField").value;
    var volume = form.querySelector("input[name='volume']").value;
    var buyOrWrite = form.querySelector("input[name='buyOrWrite']").value;
    var csrfToken = form.querySelector("input[name='_csrf']").value;

    var formData = new URLSearchParams();
    formData.append("id", id);
    formData.append("volume", volume);
    formData.append("buyOrWrite", buyOrWrite);

    const fetchRequest = fetch("/portfolio/confirmation", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': csrfToken // добавляем CSRF токен в заголовок
        },
        body: formData.toString()
    });

    const timeout = new Promise((_, reject) =>
        setTimeout(() => reject(new Error("Время ожидания больше обычного")), 3000)
    );

    Promise.race([fetchRequest, timeout])
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                return response.text().then(message => {
                    throw new Error(message);
                });
            }
        })
        .then(message => {
            alert(message);
            closeModal();
            window.location.href = "/portfolio";
        })
        .catch(error => {
            if (error.message === "Время ожидания больше обычного") {
                alert("Есть небольшая задержка с ответом. Ваша операция будет на 100% завершена, но немного позже, не повторяйте её. Все повторные действия с этим активом, кроме первого, будут отменены.");
            } else {
                var errorMessageElement = document.getElementById("errorMessage");
                errorMessageElement.textContent = error.message;
            }
        });
}

function roundToTwo(num) {
    return +(Math.round(num + "e+2")  + "e-2");
}

function checkAndReload() {
    var modal = document.getElementById("myModal");
    if (modal.style.display === "none" || modal.style.display === "") {
        location.reload();
    }
}

setInterval(checkAndReload, 300000);

function getQueryParam(param) {
    var urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

document.addEventListener("DOMContentLoaded", function() {
    var selectedValue = getQueryParam("selectedValue");
    if (selectedValue) {
        var portfolioSelector = document.getElementById("portfolioSelector");
        portfolioSelector.value = selectedValue;
    }
});