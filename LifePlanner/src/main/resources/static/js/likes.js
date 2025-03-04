// BOOK LIKES
document.querySelectorAll(".like-btn").forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const bookId = postCard.getAttribute("data-book-id"); // e.g. a custom attribute
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".like-count");

        // Send POST request to toggle like
        fetch(`/api/books/${bookId}/like`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                // data => { liked: true/false, likeCount: number }
                const isLiked = data.liked;
                const newCount = data.likeCount;

                countSpan.textContent = newCount;

                if (isLiked) {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000";
                    img.classList.add("liked");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000";
                    img.classList.remove("liked");
                }
            })
            .catch(err => console.error("Error toggling like:", err));
    });
});

// MEDIA LIKES
document.querySelectorAll('[data-media-id] .like-btn').forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const mediaId = postCard.getAttribute("data-media-id");
        console.log("Toggling like for media ID:", mediaId);

        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".like-count");

        fetch(`/api/media/${mediaId}/like`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                console.log("Response status:", response.status);
                return response.json();
            })
            .then(data => {
                console.log("Response data:", data);
                const isLiked = data.liked;
                const newCount = data.likeCount;

                countSpan.textContent = newCount;

                if (isLiked) {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000";
                    img.classList.add("liked");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000";
                    img.classList.remove("liked");
                }
            })
            .catch(err => console.error("Error toggling media like:", err));
    });
});

// RECIPE LIKES
document.querySelectorAll('[data-recipe-id] .like-btn').forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const recipeId = postCard.getAttribute("data-recipe-id");
        console.log("Toggling like for recipe ID:", recipeId);

        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".like-count");

        fetch(`/api/recipes/${recipeId}/like`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                console.log("Response status:", response.status);
                return response.json();
            })
            .then(data => {
                console.log("Response data:", data);
                const isLiked = data.liked;
                const newCount = data.likeCount;

                countSpan.textContent = newCount;

                if (isLiked) {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000";
                    img.classList.add("liked");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000";
                    img.classList.remove("liked");
                }
            })
            .catch(err => console.error("Error toggling recipe like:", err));
    });
});

// TRIP LIKES
document.querySelectorAll('[data-trip-id] .like-btn').forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const tripId = postCard.getAttribute("data-trip-id");
        console.log("Toggling like for trip ID:", tripId);

        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".like-count");

        fetch(`/api/trips/${tripId}/like`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                console.log("Response status:", response.status);
                return response.json();
            })
            .then(data => {
                console.log("Response data:", data);
                const isLiked = data.liked;
                const newCount = data.likeCount;
                countSpan.textContent = newCount;

                if (isLiked) {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000";
                    img.classList.add("liked");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000";
                    img.classList.remove("liked");
                }
            })
            .catch(err => console.error("Error toggling trip like:", err));
    });
});

// GOAL LIKES
document.querySelectorAll('[data-goal-id] .like-btn').forEach(btn => {
    btn.addEventListener("click", function () {
        const postCard = this.closest(".post-card");
        const goalId = postCard.getAttribute("data-goal-id");
        const img = this.querySelector("img");
        const countSpan = postCard.querySelector(".like-count");

        // Send POST request to toggle goal like
        fetch(`/api/goals/${goalId}/like`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => response.json())
            .then(data => {
                // data: { liked: true/false, likeCount: number }
                const isLiked = data.liked;
                const newCount = data.likeCount;

                countSpan.textContent = newCount;

                if (isLiked) {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000";
                    img.classList.add("liked");
                } else {
                    img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000";
                    img.classList.remove("liked");
                }
            })
            .catch(err => console.error("Error toggling goal like:", err));
    });
});