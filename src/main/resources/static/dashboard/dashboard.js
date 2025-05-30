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
                console.log("Dashboard Stats: ", statsData);
                document.getElementById('totalItems').textContent = statsData.totalItems;
                document.getElementById('lowStockItems').textContent = statsData.lowStockItems;
                document.getElementById('expiringSoonItems').textContent = statsData.expiringSoonItems;



                // Handling click operation on stats

                // 1. Go to the inventory list page
                document.querySelector(".items").addEventListener("click", () => {
                      window.location.href = '../inventory/list.html';
                })

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

                console.log("Activity Logs: ", activities);
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

             console.log("Movements: ", movements);

            const movementsTable = document.querySelector('#stockMovementsTable tbody');

            movements.forEach(movement => {

               // Add class based on quantity change

                 const movementClass = movement.movementType === 'ADD' ? 'status-normal' : 'status-danger';
                 const quantityChangeText = movement.movementType === 'ADD' ? `+${movement.quantityChange}` : `-${movement.quantityChange}`;


                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${new Date(movement.movementDate).toLocaleString()}</td>
                    <td>${movement.inventoryItem.name}</td>
                    <td class="${movement.movementType === 'ADD' ? 'action-add' : 'action-remove'}">${movement.movementType}</td>
                    <td class = "${movementClass}">${quantityChangeText}</td>
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



// Function to fetch and display low stock items

// CONSTRAINTS
const Low_Stock_Value = 5;
const Expiry_Date_value = 30;
 async function showLowStockItems(){
    try{
    const response = await fetch('http://localhost:8080/api/dashboard/low-stock', {
      headers: {
        'Authorization' : `Bearer ${localStorage.getItem('token')}`
      }
    });

    if(response.ok){
           const items = await response.json();
           const tableBody = document.querySelector('#lowStockTable tbody');
          tableBody.innerHTML = ''; // CLEAR EXISTING DATA

          // Hide if expiry table is present
          document.getElementById("expiringSoonTableContainer").style.display = 'none';

          // show low stock table
            const container = document.getElementById('lowStockTableContainer');
            container.style.display = container.style.display === 'none' ? 'block' : 'none';

            if(container.style.display === 'none') return;

            items.forEach(item => {
                const row = document.createElement('tr');
                const statusClass = item.quantity < Low_Stock_Value ? 'status-low' : 'status-ok';

                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.category}</td>
                    <td class = "price-cell">₹${item.price.toFixed(2)}</td>
                    <td class = "${statusClass}">${item.quantity}</td>
                    <td>${item.expiryDate ? new Date(item.expiryDate).toLocaleDateString() : 'N/A'}</td>
                    <td>${item.supplier}</td>
                `;
                tableBody.appendChild(row);

            });
     }
    }catch(error){
           console.error('Error fetching low stock items:', error);
                alert('Failed to load low stock items');
    }
 }

 // Function to fetch and display expiring soon items
 async function showExpiringSoonItems() {
     try {
         const response = await fetch('http://localhost:8080/api/dashboard/expiring-soon', {
             headers: {
                 'Authorization': `Bearer ${localStorage.getItem('token')}`
             }
         });

         if (response.ok) {
             const items = await response.json();
             const tableBody = document.querySelector('#expiringSoonTable tbody');
             tableBody.innerHTML = ''; // Clear existing rows

             // Hide other table if visible
             document.getElementById('lowStockTableContainer').style.display = 'none';

             // Show this table
             const container = document.getElementById('expiringSoonTableContainer');
             container.style.display = container.style.display === 'none' ? 'block' : 'none';

             if (container.style.display === 'none') return;

             items.forEach(item => {
                 const row = document.createElement('tr');
                 const today = new Date();
                 const expiryDate = new Date(item.expiryDate);
                 const diffTime = expiryDate - today;
                 const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                 let status, statusClass;
                 if (diffDays <= 7) {
                     status = 'Critical';
                     statusClass = 'status-low';
                 } else if (diffDays <= Expiry_Date_value) {
                     status = 'Warning';
                     statusClass = 'status-warning';
                 } else {
                     status = 'Ok';
                     statusClass = 'status-ok';
                 }
                const stockStatus = item.quantity < Low_Stock_Value ? 'status-low' : 'status-ok';

                 row.innerHTML = `
                     <td>${item.name}</td>
                     <td>${item.category}</td>
                     <td class = "price-cell" >₹${item.price.toFixed(2)}</td>
                     <td class = "${stockStatus}">${item.quantity}</td>
                     <td>${new Date(item.expiryDate).toLocaleDateString()}</td>
                     <td class="${statusClass}">${status} (${diffDays} days)</td>
                 `;
                 tableBody.appendChild(row);
             });
         }
     } catch (error) {
         console.error('Error fetching expiring soon items:', error);
         alert('Failed to load expiring soon items');
     }
 }

 // Add event listeners to the stat cards (replace your existing click handlers)
 document.querySelector(".stock-items").addEventListener("click", showLowStockItems);
 document.querySelector(".expiry-items").addEventListener("click", showExpiringSoonItems);