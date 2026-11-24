
const loginMessage = document.getElementById("loginMessage");
const signInButton = document.getElementById("signInButton");
// Check if Spring Security redirected the user
// back to the login page after bad attempt
if (new URLSearchParams(window.location.search).has("error")) {
    loginMessage.hidden = false;
    loginMessage.textContent = "Invalid username or password.";
}
// Retrieve CSRF token before login
async function prepareLogin() {
    try {
        const response = await fetch("/api/csrf", {
            credentials: "same-origin"
        });
        // Stop if the security token could not be retrieved
        if (!response.ok) {
            throw new Error("Unable to retrieve CSRF token");
        }

        const csrf = await response.json();
        const csrfField = document.getElementById("csrfField");
        // Include token in the form submitted to Spring Security
        csrfField.name = csrf.parameterName;
        csrfField.value = csrf.token;
        // Enable the button once the token is ready
        signInButton.disabled = false;

    } catch (error) {
        console.error(error);
        // Show a message if the login page wont initialize
        loginMessage.hidden = false;
        loginMessage.textContent =
            "Unable to prepare sign-in. Please refresh the page.";
    }
}
// Prepare the form when the login page loads
prepareLogin();
