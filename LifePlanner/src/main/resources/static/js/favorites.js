// BOOK FAVORITES
document.querySelectorAll(".favorite-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const bookId = postCard.getAttribute("data-book-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".favorite-count");

        // Send POST request to toggle favorite
        fetch(`/api/books/${bookId}/favorite`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                // Expected response: { favorited: true/false, favoriteCount: number }
                const isFavorited = data.favorited;
                const newCount = data.favoriteCount;

                // Update the count display
                countSpan.textContent = newCount;

                // Update the star icon based on favorite state
                if (isFavorited) {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700";
                    img.classList.add("favorited");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000";
                    img.classList.remove("favorited");
                }
            })
            .catch(err => console.error("Error toggling favorite:", err));
    });
});
