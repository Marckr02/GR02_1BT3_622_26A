(function () {
    var columns = [
        { title: "RECIBIDO",  cards: ["P-001 · Burger Lab", "P-002 · Sushi Go"] },
        { title: "EN PREP",   cards: ["P-010 · Pizza House"] },
        { title: "LISTO",     cards: ["P-020 · Burger Lab"] },
        { title: "ENTREGADO", cards: ["P-100 · Sushi Go"] }
    ];

    var board = document.getElementById("kanban-board");
    if (!board) return;

    columns.forEach(function (col) {
        var wrapper = document.createElement("div");
        wrapper.className = "kanban-column";

        var title = document.createElement("h3");
        title.textContent = col.title;
        wrapper.appendChild(title);

        col.cards.forEach(function (text) {
            var card = document.createElement("div");
            card.className = "kanban-card";
            card.textContent = text;
            wrapper.appendChild(card);
        });

        board.appendChild(wrapper);
    });
})();
