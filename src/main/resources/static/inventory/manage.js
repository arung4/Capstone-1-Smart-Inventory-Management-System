document.addEventListener("DOMContentLoaded", async () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../../auth/login.html';
        return;
    }

    // Get item ID from URL if present
    const urlParams = new URLSearchParams(window.location.search);
    const itemId = urlParams.get('id');

    // If editing existing item, load its data
    if (itemId) {
        document.querySelector(".manage-title").innerText = "Edit Item";
        await loadItemData(itemId);
    }

    // Set up form submission
    document.getElementById("inventoryForm").addEventListener("submit", handleFormSubmit);
});

async function loadItemData(itemId) {
    try {
        console.log(`Fetching item ${itemId}...`); // Debug log
        
        const response = await fetch(`http://localhost:8080/api/inventory/${itemId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            }
        });

        console.log('Response status:', response.status); // Debug log

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Server responded with ${response.status}: ${errorText}`);
        }

        const item = await response.json();
        console.log('Received item data:', item); // Debug log
          
        if (!item || typeof item !== 'object') {
            throw new Error('Invalid item data received from server');
        }
        const data = item.data;
        // Safely populate form fields with null checks
        document.getElementById("itemId").value = data.id || '';
        document.getElementById("name").value = data.name || '';
        document.getElementById("quantity").value = data.quantity ?? '';
        document.getElementById("price").value = data.price ?? '';
        
        // Handle expiryDate safely
        let expiryDateValue = '';
        if (data.expiryDate) {
            expiryDateValue = typeof data.expiryDate === 'string' 
                ? data.expiryDate.split('T')[0] 
                : '';
        }
        document.getElementById("expiryDate").value = expiryDateValue;
        
        document.getElementById("category").value = data.category || '';
        document.getElementById("supplier").value = data.supplier || '';

        // Change button text to indicate update
        const submitButton = document.querySelector('button[type="submit"]');
        if (submitButton) {
            submitButton.textContent = 'Update Item';
        }

    } catch (error) {
        console.error("Error loading item:", error);
        alert(`Failed to load item data: ${error.message}\n\nCheck console for details.`);
    }
}

async function handleFormSubmit(e) {
    e.preventDefault();
    
    const itemId = document.getElementById("itemId").value;
    const isEditMode = !!itemId;
    const url = isEditMode 
        ? `http://localhost:8080/api/inventory/${itemId}`
        : "http://localhost:8080/api/inventory/add";

    const itemData = {
        name: document.getElementById("name").value,
        quantity: parseInt(document.getElementById("quantity").value),
        price: parseFloat(document.getElementById("price").value),
        expiryDate: document.getElementById("expiryDate").value,
        category: document.getElementById("category").value,
        supplier: document.getElementById("supplier").value,
    };

    try {
        const response = await fetch(url, {
            method: isEditMode ? "PUT" : "POST",
            credentials: 'include',
            headers: { 
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(itemData),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Operation failed');
        }

        const result = await response.json();
        alert(isEditMode ? "Item updated successfully!" : "Item added successfully!");
        
        // Redirect back to inventory list
        window.location.href = 'list.html';

    } catch (error) {
        console.error("Error:", error);
        alert(`Error: ${error.message}`);
    }
}