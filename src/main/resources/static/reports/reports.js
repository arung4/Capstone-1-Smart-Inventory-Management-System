document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../../auth/login.html';
        return;
    }

    // Set default dates
    const today = new Date();
    document.getElementById('startDate').valueAsDate = today;
    document.getElementById('endDate').valueAsDate = today;
    
    // Generate report
    document.getElementById('generateReportBtn').addEventListener('click', async () => {
        const reportType = document.querySelector('input[name="reportType"]:checked').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        
        try {
            let url = '/api/reports';
            if (reportType === 'daily') {
                url += `/daily?date=${startDate}`;
            } else {
                url += `/weekly?start=${startDate}&end=${endDate}`;
            }
            
            const response = await fetch(url, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            
            if (response.ok) {
                const reportData = await response.json();
                const tableBody = document.getElementById('reportTableBody');
                tableBody.innerHTML = '';
                
                reportData.forEach(item => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${item.name}</td>
                        <td>${item.category}</td>
                        <td>${item.startingQuantity}</td>
                        <td>${item.added}</td>
                        <td>${item.used}</td>
                        <td>${item.endingQuantity}</td>
                    `;
                    tableBody.appendChild(row);
                });
            } else {
                throw new Error('Failed to generate report');
            }
        } catch (error) {
            console.error('Report error:', error);
            alert('Failed to generate report');
        }
    });
    
    // Export to CSV
    document.getElementById('exportReportBtn').addEventListener('click', () => {
        // This would be implemented to convert the table data to CSV
        alert('Export to CSV functionality would be implemented here');
    });
    
    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = '../../auth/login.html';
    });
});