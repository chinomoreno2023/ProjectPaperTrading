document.addEventListener("DOMContentLoaded", function() {
    var tradePriceElements = document.querySelectorAll(".trade-price");

    tradePriceElements.forEach(function(element) {
        var tradePriceText = element.textContent.trim();
        if (tradePriceText === "-0.00") {
            tradePriceText = "0.00"; // Заменяем "-0.00" на "0.00"
        }
        var parts = tradePriceText.split(" / ");
        var formattedParts = parts.map(function(part) {
            var price = parseFloat(part);
            if (!isNaN(price)) {
                return price.toFixed(2);
            } else {
                return part;
            }
        });
        element.textContent = formattedParts.join(" / ");
    });
});