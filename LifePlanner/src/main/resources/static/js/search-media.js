function searchMedia() {
    let input = document.getElementById("searchInput").value.toLowerCase();
    let table = document.getElementById("mediaTable");
    let rows = table.getElementsByTagName("tr");

    for (let i = 1; i < rows.length; i++) {
        let cells = rows[i].getElementsByTagName("td");
        let match = false;

        for (let j = 1; j < cells.length - 1; j++) { // Skip Status and Edit columns
            if (cells[j] && cells[j].innerText.toLowerCase().includes(input)) {
                match = true;
                break;
            }
        }

        rows[i].style.display = match ? "" : "none";
    }
}