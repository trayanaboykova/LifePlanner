// document.addEventListener("DOMContentLoaded", function() {
//     // Handle Like Button Click
//     document.querySelectorAll(".like-btn").forEach(btn => {
//         btn.addEventListener("click", function () {
//             let img = this.querySelector("img");
//             let postCard = this.closest(".post-card"); // Find parent post
//             let count = postCard.querySelector(".like-count");
//
//             if (img.classList.contains("liked")) {
//                 img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=000000"; // Default white heart (unliked)
//                 count.textContent = parseInt(count.textContent) - 1;
//             } else {
//                 img.src = "https://img.icons8.com/?size=100&id=80137&format=png&color=ff0000"; // Red heart when liked
//                 count.textContent = parseInt(count.textContent) + 1;
//             }
//             img.classList.toggle("liked");
//         });
//     });
//
//     // Handle Favorite Button Click
//     document.querySelectorAll(".favorite-btn").forEach(btn => {
//         btn.addEventListener("click", function () {
//             let img = this.querySelector("img");
//             let postCard = this.closest(".post-card"); // Find parent post
//             let count = postCard.querySelector(".favorite-count");
//
//             if (img.classList.contains("favorited")) {
//                 img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=000000"; // Default white star (unfavorited)
//                 count.textContent = parseInt(count.textContent) - 1;
//             } else {
//                 img.src = "https://img.icons8.com/?size=100&id=46336&format=png&color=FFD700"; // Gold star when favorited
//                 count.textContent = parseInt(count.textContent) + 1;
//             }
//             img.classList.toggle("favorited");
//         });
//     });
// });

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