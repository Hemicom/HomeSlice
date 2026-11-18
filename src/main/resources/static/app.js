
const choreForm = document.getElementById("choreForm");
const choreName = document.getElementById("choreName");
const choreList = document.getElementById("choreList");

choreForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const name = choreName.value.trim();

    if (name === "") {
        return;
    }

    const newChore = document.createElement("li");
    newChore.textContent = name;

    choreList.appendChild(newChore);

    choreForm.reset();
});
