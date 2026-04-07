(function () {
    var columns = [
        { title: "RECIBIDO", cards: ["P-001", "P-002"] },
        { title: "EN_PREP", cards: ["P-010"] },
        { title: "LISTO", cards: ["P-020"] },
        { title: "ENTREGADO", cards: ["P-100"] }
    ];

    var board = document.getElementById("kanban-board");
    if (!board) {
        return;
    }

    columns.forEach(function (column) {
        var wrapper = document.createElement("div");
        wrapper.className = "kanban-column";

        var title = document.createElement("h3");
        title.textContent = column.title;
        wrapper.appendChild(title);

        column.cards.forEach(function (cardCode) {
            var card = document.createElement("div");
            card.className = "kanban-card";
            card.textContent = cardCode;
            wrapper.appendChild(card);
        });

        board.appendChild(wrapper);
    });
})();

