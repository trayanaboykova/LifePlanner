// Global Variables
let currentPage = 1;
const booksPerPage = 10;
let booksData = [];
let totalPages = 1;

// Function to Initialize Pagination
function initializePagination() {
    const rows = document.querySelectorAll(".books-table tbody tr");
    booksData = Array.from(rows); // Store all book rows

    // Calculate total pages based on number of rows
    totalPages = Math.ceil(booksData.length / booksPerPage);

    // If there are no rows, force totalPages to 1
    if (totalPages < 1) {
        totalPages = 1;
    }

    showPage(currentPage);
}

// Function to Show the Correct Page
function showPage(page) {
    let startIndex = (page - 1) * booksPerPage;
    let endIndex = startIndex + booksPerPage;

    booksData.forEach((row, index) => {
        row.style.display = (index >= startIndex && index < endIndex) ? "" : "none";
    });

    updatePaginationControls();
}

// Function to Change Pages
function changePage(direction) {
    let newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        showPage(currentPage);
    }
}

// Function to Update Pagination Controls
function updatePaginationControls() {
    document.getElementById("pageInfo").innerText = `Page ${currentPage} of ${totalPages}`;

    // Hide Previous button if on first page
    document.getElementById("prevPage").style.display = (currentPage === 1) ? "none" : "inline-block";

    // Hide Next button if on last page
    document.getElementById("nextPage").style.display = (currentPage === totalPages) ? "none" : "inline-block";
}

// Wait for DOM to Load and Initialize Pagination
document.addEventListener("DOMContentLoaded", () => {
    initializePagination();
});
