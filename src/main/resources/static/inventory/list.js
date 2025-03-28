document.addEventListener('DOMContentLoaded', async () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../../auth/login.html';
        return;
    }

    // Load inventory data
    const loadInventory = async () => {
        try {
            const response = await fetch('/api/inventory', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            
            if (response.ok) {
                const items = await response.json();
                const tableBody = document.getElementById('inventoryTableBody');
                tableBody.innerHTML = '';
                
                items.forEach(item => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${item.name}</td>
                        <td>${item.category}</td>
                        <td>${item.price}</td>
                        <td>${item.quantity}</td>
                        <td>${new Date(item.expiryDate).toLocaleDateString()}</td>
                        <td class="${item.status === 'Low' ? 'status-low' : ''}">${item.status}</td>
                        <td>
                            <button class="btn-edit" data-id="${item.id}">Edit</button>
                            <button class="btn-delete" data-id="${item.id}">Delete</button>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });
                
                // Add event listeners to buttons
                document.querySelectorAll('.btn-edit').forEach(btn => {
                    btn.addEventListener('click', (e) => {
                        const itemId = e.target.getAttribute('data-id');
                        window.location.href = `manage.html?id=${itemId}`;
                    });
                });
                
                document.querySelectorAll('.btn-delete').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const itemId = e.target.getAttribute('data-id');
                        if (confirm('Are you sure you want to delete this item?')) {
                            try {
                                const response = await fetch(`/api/inventory/${itemId}`, {
                                    method: 'DELETE',
                                    headers: {
                                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                                    }
                                });
                                
                                if (response.ok) {
                                    loadInventory(); // Refresh the list
                                } else {
                                    throw new Error('Failed to delete item');
                                }
                            } catch (error) {
                                console.error('Delete error:', error);
                                alert('Failed to delete item');
                            }
                        }
                    });
                });
            } else {
                throw new Error('Failed to load inventory');
            }
        } catch (error) {
            console.error('Inventory error:', error);
            alert('Failed to load inventory data');
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