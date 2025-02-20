document.addEventListener("DOMContentLoaded", function() {
    // Handle Like Button Click
    document.querySelectorAll(".like-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            let img = this.querySelector("img");
            let postCard = this.closest(".post-card"); // Find parent post
            let count = postCard.querySelector(".like-count");

            if (img.classList.contains("liked")) {
                img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000"; // Default white heart (unliked)
                count.textContent = parseInt(count.textContent) - 1;
            } else {
                img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000"; // Red heart when liked
                count.textContent = parseInt(count.textContent) + 1;
            }
            img.classList.toggle("liked");
        });
    });

    // Handle Favorite Button Click
    document.querySelectorAll(".favorite-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            let img = this.querySelector("img");
            let postCard = this.closest(".post-card"); // Find parent post
            let count = postCard.querySelector(".favorite-count");

            if (img.classList.contains("favorited")) {
                img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000"; // Default white star (unfavorited)
                count.textContent = parseInt(count.textContent) - 1;
            } else {
                img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700"; // Gold star when favorited
                count.textContent = parseInt(count.textContent) + 1;
            }
            img.classList.toggle("favorited");
        });
    });
});
