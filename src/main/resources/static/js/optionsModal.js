// Функция для открытия модального окна с данными о выбранном option
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

    // Очистка текста ошибки
    var errorMessageElement = document.getElementById("errorMessage");
    errorMessageElement.textContent = "";

    // Заполняем скрытые поля значениями из объекта option
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

    // Очищаем поле ввода объема
    volumeField.value = '0';

    modal.style.display = "block";

    // Добавляем обработчик события клика за пределами модального окна
    window.addEventListener('click', outsideClick);
    // Добавляем обработчик события нажатия на клавишу ESC
    window.addEventListener('keydown', escapeKey);
}

// Добавляем обработчик ввода для удаления ведущих нулей
document.getElementById("volumeField").addEventListener('input', function() {
    // Превращаем введенное значение в строку, удаляем ведущие нули и возвращаем обратно
    this.value = this.value.replace(/^0+/, '') || '0';
});

// Удаление ведущих нулей на потерю фокуса (если пользователь удаляет все цифры, возвращаем пустую строку)
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
            event.preventDefault(); // предотвращает стандартное поведение клавиши Enter, если нужно
            confirmOperation();
        }
    }
});

function confirmOperation() {
    var form = document.getElementById("optionForm");

    // Получаем значения полей id, volume и buyOrWrite
    var id = form.querySelector("#optionIdField").value;
    var volume = form.querySelector("input[name='volume']").value;
    var buyOrWrite = form.querySelector("input[name='buyOrWrite']").value;
    var csrfToken = form.querySelector("input[name='_csrf']").value;

    // Создаем объект для URL-кодирования данных
    var formData = new URLSearchParams();
    formData.append("id", id);
    formData.append("volume", volume);
    formData.append("buyOrWrite", buyOrWrite);

    // Отправляем данные на сервер
    fetch("/portfolio/confirmation", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': csrfToken // добавляем CSRF токен в заголовок
        },
        body: formData.toString()
    })
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
            // Отобразить сообщение об успешном завершении торговли
            alert(message);
            closeModal();
            window.location.href = "/portfolio";
        })
        .catch(error => {
            // Отобразить сообщение об ошибке в модальном окне
            var errorMessageElement = document.getElementById("errorMessage");
            errorMessageElement.textContent = error.message;
        });
}

// Функция для округления числа до двух знаков после запятой
function roundToTwo(num) {
    return +(Math.round(num + "e+2")  + "e-2");
}

function checkAndReload() {
    // Проверяем, открыто ли модальное окно
    var modal = document.getElementById("myModal");
    if (modal.style.display === "none" || modal.style.display === "") {
        // Если модальное окно закрыто, обновляем страницу
        location.reload();
    }
}

setInterval(checkAndReload, 300000);

// Функция для получения значения параметра из URL
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