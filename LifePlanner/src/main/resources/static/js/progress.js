document.addEventListener("DOMContentLoaded", function () {
    const progressCells = document.querySelectorAll(".emoji-progress");

    progressCells.forEach(cell => {
        let completed = parseInt(cell.getAttribute("data-completed")) || 0;
        let total = parseInt(cell.getAttribute("data-total")) || 0;

        if (total === 0) {
            cell.innerHTML = "No milestones";
            return;
        }

        let percentage = Math.floor((completed / total) * 100);
        let fireCount = Math.floor((completed / total) * 10);
        let windCount = 10 - fireCount;

        let progressBar = "🔥".repeat(fireCount) + "💨".repeat(windCount) + ` ${percentage}%`;
        cell.innerHTML = progressBar;
    });
});
