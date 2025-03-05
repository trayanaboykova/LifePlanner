// BOOK FAVORITES
document.querySelectorAll("[data-book-id] .favorite-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const bookId = postCard.getAttribute("data-book-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".favorite-count");

        // Send POST request to toggle favorite for books
        fetch(`/api/books/${bookId}/favorite`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                // data => { favorited: true/false, favoriteCount: number }
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

// MEDIA FAVORITES
document.querySelectorAll("[data-media-id] .favorite-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const mediaId = postCard.getAttribute("data-media-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".favorite-count");

        // Send POST request to toggle favorite for media
        fetch(`/api/media/${mediaId}/favorite`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                // data => { favorited: true/false, favoriteCount: number }
                const isFavorited = data.favorited;
                const newCount = data.favoriteCount;

                // Update the count display
                countSpan.textContent = newCount;

                // Update the star icon
                if (isFavorited) {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700";
                    img.classList.add("favorited");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000";
                    img.classList.remove("favorited");
                }
            })
            .catch(err => console.error("Error toggling media favorite:", err));
    });
});

// RECIPE FAVORITES
document.querySelectorAll("[data-recipe-id] .favorite-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const recipeId = postCard.getAttribute("data-recipe-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".favorite-count");

        // Send POST request to toggle favorite for recipes
        fetch(`/api/recipes/${recipeId}/favorite`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                const isFavorited = data.favorited;
                const newCount = data.favoriteCount;

                countSpan.textContent = newCount;
                if (isFavorited) {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700";
                    img.classList.add("favorited");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000";
                    img.classList.remove("favorited");
                }
            })
            .catch(err => console.error("Error toggling recipe favorite:", err));
    });
});

// TRIP FAVORITES
document.querySelectorAll("[data-trip-id] .favorite-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const tripId = postCard.getAttribute("data-trip-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".favorite-count");

        // Send POST request to toggle favorite for trips
        fetch(`/api/trips/${tripId}/favorite`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                const isFavorited = data.favorited;
                const newCount = data.favoriteCount;

                countSpan.textContent = newCount;
                if (isFavorited) {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700";
                    img.classList.add("favorited");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000";
                    img.classList.remove("favorited");
                }
            })
            .catch(err => console.error("Error toggling trip favorite:", err));
    });
});

// GOAL FAVORITES
document.querySelectorAll("[data-goal-id] .favorite-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const goalId = postCard.getAttribute("data-goal-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".favorite-count");

        // Send POST request to toggle favorite for goals
        fetch(`/api/goals/${goalId}/favorite`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                const isFavorited = data.favorited;
                const newCount = data.favoriteCount;

                // Update the count display
                countSpan.textContent = newCount;

                // Update the star icon based on favorite state
                if (isFavorited) {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700"; // Gold star for favorited
                    img.classList.add("favorited");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000"; // Default star for unfavorited
                    img.classList.remove("favorited");
                }
            })
            .catch(err => console.error("Error toggling goal favorite:", err));
    });
});
