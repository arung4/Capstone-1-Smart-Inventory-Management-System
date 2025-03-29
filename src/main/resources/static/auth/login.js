document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch('http://localhost:8080/api/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        const res = await response.json();
        console.log("Full Response:", res); // Debug entire response
        
        if (response.ok) {
            // Check where your token is actually located in the response
            const token = res.token || res.data || res.accessToken;
            
            if (!token) {
                console.error("No token found in response:", res);
                alert("Login successful but no token received");
                return;
            }
            
            console.log("Token to be stored:", token);
            localStorage.setItem('token', token);
            
            // Add slight delay before redirect to see logs
            setTimeout(() => {
                window.location.href = '../dashboard/dashboard.html';
            }, 500);
            
        } else {
            const errorMsg = res.message || 'Login failed. Please check your credentials.';
            alert(errorMsg);
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login. See console for details.');
    }
});