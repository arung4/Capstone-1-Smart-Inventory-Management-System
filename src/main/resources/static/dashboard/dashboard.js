document.addEventListener('DOMContentLoaded', async () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../auth/login.html';
        return;
    }

     const userRole = getRoleFromToken();
     console.log("role: ", userRole)
   // Hide stats if user if from staff
   if(userRole[0] === "ROLE_STAFF"){
        document.querySelector('.dashboard-content').style.display = 'none';
   }
    // Load dashboard data
    try {
        const response = await fetch('/api/dashboard', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            document.getElementById('totalItems').textContent = data.totalItems;
            document.getElementById('lowStockItems').textContent = data.lowStockItems;
            document.getElementById('expiringItems').textContent = data.expiringItems;

            // Populate activity list
            const activityList = document.getElementById('activityList');
            data.recentActivities.forEach(activity => {
                const li = document.createElement('li');
                li.textContent = `${activity.action} - ${activity.itemName} (${activity.timestamp})`;
                activityList.appendChild(li);
            });
        } else {
            throw new Error('Failed to load dashboard data');
        }
    } catch (error) {
        console.error('Dashboard error:', error);
        alert('Failed to load dashboard data');
    }

    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = '../auth/login.html';
    });
});