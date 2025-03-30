// api.js - For direct browser use (without modules)
const API_BASE_URL = 'http://localhost:8080/api';

async function makeApiCall(endpoint, method = 'GET', body = null) {
    // ... (keep existing makeApiCall implementation)
}

// Define functions as global variables instead of exporting
window.api = {
    login: async function(email, password) {
        return makeApiCall('/users/login', 'POST', { email, password });
    },
    
    register: async function(userData) {
        return makeApiCall('/users/register', 'POST', userData);
    },
    
    getInventory: async function() {
        return makeApiCall('/inventory');
    },
    
    getDashboardData: async function() {
        return makeApiCall('/dashboard');
    },
    
    generateReport: async function(type, startDate, endDate) {
        if (type === 'daily') {
            return makeApiCall(`/reports/daily?date=${startDate}`);
        } else {
            return makeApiCall(`/reports/weekly?start=${startDate}&end=${endDate}`);
        }
    }
};


// api.js - Add this new function
window.auth = {
  getUserRole: function() {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role; // Returns "ADMIN" or "STAFF"
    } catch (e) {
      console.error("Token decode error:", e);
      return null;
    }
  }
};