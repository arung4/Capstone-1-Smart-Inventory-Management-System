document.addEventListener('DOMContentLoaded', async () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../auth/login.html';
        return;
    }

    const userRole = getRoleFromToken();

    // Show/hide sections based on role
    if (userRole[0] === "ROLE_STAFF") {
        document.querySelector('.recent-activity').style.display = 'none';
    }

    try {
        // Load dashboard stats (only for admin)

            const statsResponse = await fetch('http://localhost:8080/api/dashboard', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (statsResponse.ok) {
                const statsData = await statsResponse.json();
                document.getElementById('totalItems').textContent = statsData.totalItems;
                document.getElementById('lowStockItems').textContent = statsData.lowStockItems;
                document.getElementById('expiringSoonItems').textContent = statsData.expiringSoonItems;
            }

        // Load activity logs (only for admin)
        if (userRole[0] === "ROLE_ADMIN") {
            const activityResponse = await fetch('http://localhost:8080/api/activities/recent', {
                method: "GET",
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (activityResponse.ok) {
                const activities = await activityResponse.json();
                const activityList = document.querySelector('#activityList tbody');

                activities.forEach(activity => {
                    const row = document.createElement('tr');

               // Add class based on activity type
                 const actionClass = activity.activityType.toLowerCase().includes('add') ? 'action-add' :
                                    activity.activityType.toLowerCase().includes('remove') ? 'action-remove' :
                                    activity.activityType.toLowerCase().includes('view') ? 'action-view' : '';


                row.innerHTML = `

                    <td class = "${actionClass}">${activity.activityType.toLowerCase()}</td>
                    <td>${activity.description}</td>
                    <td>${activity.user.email}</td>
                   <td>${new Date(activity.createdAt).toLocaleString()}</td>
                `;
                activityList.appendChild(row);

                });
            }
        }

        // Load stock movements (for both admin and staff)
        const movementsResponse = await fetch('http://localhost:8080/api/activities/stock-movements', {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (movementsResponse.ok) {
            const movements = await movementsResponse.json();
            const movementsTable = document.querySelector('#stockMovementsTable tbody');

            movements.forEach(movement => {

               // Add class based on quantity change
                 const quantityClass = movement.quantityChange > 0 ? 'quantity-positive' : 'quantity-negative';

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${new Date(movement.movementDate).toLocaleString()}</td>
                    <td>${movement.inventoryItem.name}</td>
                    <td class="${movement.movementType === 'ADD' ? 'action-add' : 'action-remove'}">${movement.movementType}</td>
                    <td class = "${quantityClass}">${movement.quantityChange}</td>
                    <td>${movement.user.email}</td>
                `;
                movementsTable.appendChild(row);
            });
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