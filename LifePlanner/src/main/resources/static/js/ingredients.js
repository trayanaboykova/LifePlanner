document.getElementById("add-ingredient").addEventListener("click", function () {
    let container = document.getElementById("ingredients-container");
    let newIngredient = document.createElement("div");
    newIngredient.classList.add("ingredient-item");
    newIngredient.innerHTML = `
        <label>
            <input type="text" name="ingredient[]" placeholder="Ingredient name" required>
        </label>
        <label>
            <input type="number" name="quantity[]" placeholder="Quantity" min="1" required>
        </label>
        <label>
            <select name="unit[]">
                <option value="" disabled selected>-----Unit-----</option>
                <option value="g">g</option>
                <option value="ml">ml</option>
                <option value="cups">cups</option>
                <option value="tsp">tsp</option>
                <option value="tbsp">tbsp</option>
            </select>
        </label>
        <button type="button" class="remove-ingredient">✖</button>
    `;
    container.appendChild(newIngredient);
});

// Remove Ingredient
document.getElementById("ingredients-container").addEventListener("click", function (e) {
    if (e.target.classList.contains("remove-ingredient")) {
        e.target.parentElement.remove();
    }
});

