

const choreForm = document.getElementById("choreForm");
const choreName = document.getElementById("choreName");
const assignedMember = document.getElementById("assignedMember");
const dueDate = document.getElementById("dueDate");
const choreList = document.getElementById("choreList");
const pendingChores = document.getElementById("pendingChores");

// Update the number of incomplete chores
function updatePendingCount() {
    const pending = choreList.querySelectorAll(
        "li:not(.completed)"
    ).length;

    pendingChores.textContent =
        pending + " pending chores";
}

// Create and display a chore
function addChore(title, member, date) {
    const li = document.createElement("li");
    li.className = "chore-item";

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";

    const details = document.createElement("div");
    details.className = "chore-details";

    const choreTitle = document.createElement("strong");
    choreTitle.textContent = title;

    const choreInfo = document.createElement("p");
    choreInfo.textContent =
        "Assigned to: " + member + " | Due: " + date;

    details.appendChild(choreTitle);
    details.appendChild(choreInfo);

    // Mark a chore as completed
    checkbox.addEventListener("change", function() {
        li.classList.toggle(
            "completed",
            checkbox.checked
        );

        updatePendingCount();
    });

    // Delete a chore
    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.textContent = "Delete";
    deleteButton.className = "delete-btn";

    deleteButton.addEventListener("click", function() {
        li.remove();
        updatePendingCount();
    });

    li.appendChild(checkbox);
    li.appendChild(details);
    li.appendChild(deleteButton);

    choreList.appendChild(li);
    updatePendingCount();
}

// Handle submitting the form
choreForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const title = choreName.value.trim();
    const member =
        assignedMember.selectedOptions[0].textContent;
    const date = dueDate.value;

    if (title === "" || assignedMember.value === "" ||
        date === "") {
        return;
    }

    addChore(title, member, date);

    // Clear the form for the next chore
    choreForm.reset();
});

const expenseForm = document.getElementById("expenseForm");
const expenseName = document.getElementById("expenseName");
const expenseAmount = document.getElementById("expenseAmount");
const paidBy = document.getElementById("paidBy");
const expenseList = document.getElementById("expenseList");
const totalExpenses = document.getElementById("totalExpenses");

const roommates = ["Alex", "Jordan", "Taylor"];
let expenseTotal = 0;

// Display amounts in dollars
function formatMoney(cents) {
    return "$" + (cents / 100).toFixed(2);
}

function updateExpenseTotal() {
    totalExpenses.textContent =
        "Total Expenses: " + formatMoney(expenseTotal);
}

expenseForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const name = expenseName.value.trim();
    const amount = Number(expenseAmount.value);
    const cents = Math.round(amount * 100);
    const payerIndex = Number(paidBy.value) - 1;

    if (!name || !Number.isSafeInteger(cents) ||
        cents <= 0 || payerIndex < 0 ||
        payerIndex >= roommates.length) {
        return;
    }

    const payer = roommates[payerIndex];

    // Divide the expense, accounting for extra cents
    const baseShare = Math.floor(cents / roommates.length);
    const remainder = cents % roommates.length;

    const shares = roommates.map((person, index) => {
        return baseShare + (index < remainder ? 1 : 0);
    });

    const expenseItem = document.createElement("li");

    const details = document.createElement("span");
    const amountsOwed = roommates
        .filter((person, index) => index !== payerIndex)
        .map(person => {
            const index = roommates.indexOf(person);
            return person + " owes " + payer + " " +
                formatMoney(shares[index]);
        });

    details.textContent =
        name + " — " + formatMoney(cents) +
        " | Paid by " + payer +
        " | " + amountsOwed.join("; ");

    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.textContent = "Delete";

    deleteButton.addEventListener("click", function() {
        expenseItem.remove();
        expenseTotal -= cents;
        updateExpenseTotal();
    });

    expenseItem.appendChild(details);
    expenseItem.appendChild(deleteButton);
    expenseList.appendChild(expenseItem);

    expenseTotal += cents;
    updateExpenseTotal();

    expenseForm.reset();
});
