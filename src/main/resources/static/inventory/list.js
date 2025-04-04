document.addEventListener('DOMContentLoaded', async () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../../auth/login.html';
        return;
    }

    const userRole = getRoleFromToken();

       if (userRole[0] === "ROLE_STAFF") {
           // Hide Add Item button
           document.getElementById("addItemBtn").style.display = 'none';

           // Remove Actions header
           const headers = document.querySelectorAll('th');
           headers[headers.length - 1].remove(); // Remove last header (Actions)

       }

    console.log("Role: ", userRole[0]);
    // Load inventory data
    const loadInventory = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/inventory', {
              headers: {
                      'Authorization': `Bearer ${localStorage.getItem('token')}`
                     }
            
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            
            // Check if data exists in response
            if (!result.data || !Array.isArray(result.data)) {
                throw new Error('Invalid data format from server');
            }

            console.log("API Response:", result);
            
            const tableBody = document.getElementById('inventoryTableBody');
            tableBody.innerHTML = '';
            
            result.data.forEach(item => {
                const row = document.createElement('tr');
                
                 // Determine quantity class
                      const quantityClass = item.quantity < 5 ? 'quantity-low' : 'quantity-normal';

                      // Determine expiry status
                      const expiryDate = new Date(item.expiryDate);
                      const today = new Date();
                      const daysToExpiry = Math.floor((expiryDate - today) / (1000 * 60 * 60 * 24));


                      // expiry status logic
                      let expiryStatusClass = "status-normal";
                      let expiryStatusText = "Normal";

                     if (daysToExpiry < 0) {
                                        expiryStatusClass = 'status-danger';
                                        expiryStatusText = 'Expired';
                                    } else if (daysToExpiry <= 10) {
                                        expiryStatusClass = 'status-warning';
                                        expiryStatusText = 'Expiring Soon';
                                    }

                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.category || 'N/A'}</td>
                    <td class = "price-cell">₹${item.price.toFixed(2)}</td>
                    <td class= "${quantityClass}" >${item.quantity}</td>
                    <td>${item.expiryDate ? new Date(item.expiryDate).toLocaleDateString() : 'N/A'}</td>
                    <td><span class="status-badge ${expiryStatusClass}">${expiryStatusText}</span></td>
                    <td>
                                        <div class="action-btns">
                                            <button class="action-btn edit-btn" data-id="${item.id}">
                                                <i class="fas fa-edit"></i> Edit
                                            </button>
                                            <button class="action-btn delete-btn" data-id="${item.id}">
                                                <i class="fas fa-trash"></i> Delete
                                            </button>
                                        </div>

                    </td>
                `;

                 if (userRole[0] === 'ROLE_STAFF') {
                                row.querySelector('.edit-btn').style.display = 'none';
                                row.querySelector('.delete-btn').style.display = 'none';
                        }

                tableBody.appendChild(row);
            });
            
            // Add event listeners to buttons
            document.querySelectorAll('.edit-btn').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    const itemId = e.target.getAttribute('data-id');
                    window.location.href = `manage.html?id=${itemId}`;
                });
            });
            
            document.querySelectorAll('.delete-btn').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    const itemId = e.target.getAttribute('data-id');
                    if (confirm('Are you sure you want to delete this item?')) {
                        try {
                            const deleteResponse = await fetch(`http://localhost:8080/api/inventory/${itemId}`, {
                                method: 'DELETE',
                                headers: {
                                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                                }
                            });
                            
                            if (deleteResponse.ok) {
                                loadInventory(); // Refresh the list
                            } else {
                                const errorData = await deleteResponse.json();
                                throw new Error(errorData.message || 'Failed to delete item');
                            }
                        } catch (error) {
                            console.error('Delete error:', error);
                            alert(error.message);
                        }
                    }
                });
            });
            
        } catch (error) {
            console.error('Inventory load error:', error);
            alert(`Error: ${error.message}`);
        }
    };

    // Initial load
    await loadInventory();
    
    // Add item button
    document.getElementById('addItemBtn').addEventListener('click', () => {
        window.location.href = 'manage.html';
    });
    
    // Search functionality
    document.getElementById('searchInput').addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        const rows = document.querySelectorAll('#inventoryTableBody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm) ? '' : 'none';
        });
    });
    
    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = '../auth/login.html';
    });
});