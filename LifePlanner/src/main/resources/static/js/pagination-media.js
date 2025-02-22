// Global Variables
let currentPage = 1;
const itemsPerPage = 10; // Set items per page
let mediaData = [];
let totalPages = 1;

// Function to Initialize Pagination
function initializePagination() {
    const rows = document.querySelectorAll(".media-table tbody tr"); // Target the correct table
    mediaData = Array.from(rows); // Store all rows
    totalPages = Math.max(1, Math.ceil(mediaData.length / itemsPerPage)); // Ensure at least 1 page
    showPage(currentPage);
}

// Function to Show the Correct Page
function showPage(page) {
    let startIndex = (page - 1) * itemsPerPage;
    let endIndex = startIndex + itemsPerPage;

    mediaData.forEach((row, index) => {
        row.style.display = index >= startIndex && index < endIndex ? "" : "none";
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
    document.getElementById("prevPage").style.display = currentPage === 1 ? "none" : "inline-block";

    // Hide Next button if on last page
    document.getElementById("nextPage").style.display = currentPage >= totalPages ? "none" : "inline-block";
}

// Wait for DOM to Load and Initialize Pagination
document.addEventListener("DOMContentLoaded", () => {
    initializePagination();
});
