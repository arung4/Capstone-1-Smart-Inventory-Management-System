// Utility functions for API calls
const API_BASE_URL = 'http://localhost:8080/api';

async function makeApiCall(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    const token = localStorage.getItem('token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options = {
        method,
        headers
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        
        if (!response.ok) {
            throw new Error(`API request failed with status ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API call error:', error);
        throw error;
    }
}

// Example API functions
export async function login(email, password) {
    return makeApiCall('/auth/login', 'POST', { email, password });
}

export async function register(userData) {
    return makeApiCall('/auth/register', 'POST', userData);
}

export async function getInventory() {
    return makeApiCall('/inventory');
}

export async function getDashboardData() {
    return makeApiCall('/dashboard');
}

export async function generateReport(type, startDate, endDate) {
    if (type === 'daily') {
        return makeApiCall(`/reports/daily?date=${startDate}`);
    } else {
        return makeApiCall(`/reports/weekly?start=${startDate}&end=${endDate}`);
    }
}