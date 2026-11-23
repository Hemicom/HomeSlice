
const choreForm = document.getElementById("choreForm");
const choreName = document.getElementById("choreName");
const assignedMember = document.getElementById("assignedMember");
const dueDate = document.getElementById("dueDate");
const choreList = document.getElementById("choreList");
const pendingChores = document.getElementById("pendingChores");

// Send requests to the Chore Management API
async function choreRequest(url, method = "GET", data = null) {
    const headers = {};

    // Spring Security requires a CSRF token for changes
    if (method !== "GET") {
        const tokenResponse = await fetch("/api/csrf");

        if (!tokenResponse.ok) {
            throw new Error("Unable to get security token");
        }

        const csrf = await tokenResponse.json();
        headers[csrf.headerName] = csrf.token;
    }

    if (data !== null) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(url, {
        method: method,
        credentials: "same-origin",
        headers: headers,
        ...(data !== null ? { body: JSON.stringify(data) } : {})
    });

    if (!response.ok) {
        throw new Error("Request failed: HTTP " + response.status);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

// Retrieve saved chores from MySQL
async function loadChores() {
    try {
        const chores = await choreRequest("/api/chores");
        displayChores(chores);
    } catch (error) {
        console.error("Could not load chores:", error);
        choreList.textContent = "Unable to load chores.";
    }
}

// Display the chores returned by Java
function displayChores(chores) {
    choreList.replaceChildren();

    const pending = chores.filter(chore => !chore.completed);
    pendingChores.textContent =
        pending.length + " pending chores";

    if (chores.length === 0) {
        const message = document.createElement("li");
        message.textContent = "No chores have been added yet.";
        choreList.appendChild(message);
        return;
    }

    chores.forEach(function(chore) {
        const li = document.createElement("li");
        li.className = "chore-item";
        li.classList.toggle("completed", chore.completed);

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.checked = chore.completed;
        const details = document.createElement("div");
        details.className = "chore-details";
        const title = document.createElement("strong");
        title.textContent = chore.name;
        const info = document.createElement("p");
        info.textContent =
            "Assigned to: " + chore.assignedTo +
            " | Due: " + (chore.dueDate || "Not set");

        details.appendChild(title);
        details.appendChild(info);

        // Save changes to MySQL
        checkbox.addEventListener("change", async function() {
            checkbox.disabled = true;

            try {
                const updatedChore = {
                    name: chore.name,
                    description: chore.description,
                    assignedTo: chore.assignedTo,
                    dueDate: chore.dueDate,
                    completed: checkbox.checked
                };

                await choreRequest(
                    "/api/chores/" + chore.id,
                    "PUT",
                    updatedChore
                );

                await loadChores();
            } catch (error) {
                checkbox.checked = !checkbox.checked;
                alert("Could not update chore: " + error.message);
            } finally {
                checkbox.disabled = false;
            }
        });

        // Delete a chore from MySQL
        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.textContent = "Delete";
        deleteButton.className = "delete-btn";
        deleteButton.addEventListener("click", async function() {
            deleteButton.disabled = true;

            try {
                await choreRequest(
                    "/api/chores/" + chore.id,
                    "DELETE"
                );

                await loadChores();
            } catch (error) {
                deleteButton.disabled = false;
                alert("Could not delete chore: " + error.message);
            }
        });
        li.appendChild(checkbox);
        li.appendChild(details);
        li.appendChild(deleteButton);

        choreList.appendChild(li);
    });
}

// Create a new chore in this should save to MySQL
choreForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const name = choreName.value.trim();
    const member = assignedMember.selectedOptions[0].textContent;
    const date = dueDate.value;

    if (!name || !assignedMember.value || !date) {
        return;
    }

    const newChore = {
        name: name,
        description: "",
        assignedTo: member,
        dueDate: date,
        completed: false
    };

    const submitButton =
        choreForm.querySelector('button[type="submit"]');
    submitButton.disabled = true;

    try {
        await choreRequest("/api/chores", "POST", newChore);

        choreForm.reset();
        await loadChores();
    } catch (error) {
        alert("Could not add chore: " + error.message);
    } finally {
        submitButton.disabled = false;
    }
});

// Load saved chores when the page opens
loadChores();

const expenseForm = document.getElementById("expenseForm");
const expenseName = document.getElementById("expenseName");
const expenseAmount = document.getElementById("expenseAmount");
const paidBy = document.getElementById("paidBy");
const expenseList = document.getElementById("expenseList");
const totalExpenses = document.getElementById("totalExpenses");
//test roommates for presentation
const roommates = ["Alex", "Jordan", "Taylor"];
let expenseTotal = 0;

// Use dollars
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

    // Divide the expense, considering the extra cents
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
