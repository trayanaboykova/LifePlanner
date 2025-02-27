function searchRecipes() {
    let input = document.getElementById("searchInput").value.toLowerCase();
    let table = document.getElementById("mediaTable");
    let rows = table.getElementsByTagName("tr");

    // Loop through all rows, skipping the header (index 0)
    for (let i = 1; i < rows.length; i++) {
        let cells = rows[i].getElementsByTagName("td");
        let match = false;

        // Search only the first 7 columns (indices 0 to 6)
        for (let j = 0; j < 7; j++) {
            if (cells[j] && cells[j].innerText.toLowerCase().includes(input)) {
                match = true;
                break;
            }
        }
        rows[i].style.display = match ? "" : "none";
    }
}
