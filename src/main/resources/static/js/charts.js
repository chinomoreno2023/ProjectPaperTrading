function showChart() {
    var selectedValue = document.getElementById("portfolioSelector").value;
    var url;

    switch (selectedValue) {
        case "SF":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3ASF1%21";
            break;
        case "BR":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3ABR1%21";
            break;
        case "SR":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3ASR1%21";
            break;
        case "Si":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3ASI1%21";
            break;
        case "MX":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3AMX1%21";
            break;
        case "RI":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3ARI1%21";
            break;
        case "GZ":
            url = "https://ru.tradingview.com/chart/rITqKTLL/?symbol=MOEX%3AGZ1%21";
            break;
        default:
            url = "http://options-paper-trading.ru/portfolio";
    }

    window.open(url, "_blank");
}