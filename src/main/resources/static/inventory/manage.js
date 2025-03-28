document.addEventListener("DOMContentLoaded", async () => {
    await loadInventory();
    document.getElementById("inventoryForm").addEventListener("submit", handleFormSubmit);
});

async function loadInventory() {
    const response = await fetch("/api/inventory");
    const items = await response.json();
    const tableBody = document.querySelector("#inventoryTable tbody");
    tableBody.innerHTML = "";

    items.forEach(item => {
        const row = document.createElement("tr");
        if (item.quantity < 10) row.classList.add("low-stock");
        if (new Date(item.expiryDate) < new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)) {
            row.classList.add("expiry-alert");
        }

        row.innerHTML = `
            <td>${item.name}</td>
            <td>${item.quantity}</td>
            <td>${item.price}</td>
            <td>${item.expiryDate}</td>
            <td>${item.category}</td>
            <td>${item.supplier}</td>
            <td>
                <button onclick="editItem('${item.id}')">Edit</button>
                <button onclick="deleteItem('${item.id}')">Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

async function handleFormSubmit(e) {
    e.preventDefault();
    const itemId = document.getElementById("itemId").value;
    const method = itemId ? "PUT" : "POST";
    const url = itemId ? `/api/inventory/${itemId}` : "/api/inventory";

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
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(itemData),
        });
        if (response.ok) {
            await loadInventory();
            document.getElementById("inventoryForm").reset();
        }
    } catch (err) {
        console.error("Error:", err);
    }
}

function editItem(id) {
    // Fetch item by ID and populate form
}

function deleteItem(id) {
    if (confirm("Delete this item?")) {
        fetch(`/api/inventory/${id}`, { method: "DELETE" })
            .then(() => loadInventory());
    }
}