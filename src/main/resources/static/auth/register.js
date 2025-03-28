document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phoneNumber = document.getElementById("phoneNumber").value;
    const role = document.getElementById("role").value;

    // Basic validation
    if (!name || !email || !password || !phoneNumber || !role) {
        alert("All fields are required!");
        return;
    }

    try {
        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ 
                name, 
                email, 
                password, 
                phoneNumber, 
                role 
            }),
        });

        if (response.ok) {
            alert("Registration successful! Redirecting to login...");
            window.location.href = "../auth/login.html";
        } else {
            const error = await response.json();
            alert(error.message || "Registration failed!");
        }
    } catch (err) {
        console.error("Registration error:", err);
        alert("An error occurred. Please try again.");
    }
});