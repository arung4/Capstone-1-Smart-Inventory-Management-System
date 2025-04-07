document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    if (!localStorage.getItem('token')) {
        window.location.href = '../../auth/login.html';
        return;
    }

    // Set default dates
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('startDate').value = today;
    document.getElementById('endDate').value = today;

    // Show/hide custom date range based on report type
    document.querySelectorAll('input[name="reportType"]').forEach(radio => {
        radio.addEventListener('change', (e) => {
            const customDateRange = document.getElementById('customDateRange');
            customDateRange.style.display = e.target.value === 'custom' ? 'block' : 'none';
        });
    });

    // Store the last fetched report data
    let currentReportData = null;

    // Generate report (JSON format)
    document.getElementById('generateReportBtn').addEventListener('click', async () => {
        const reportType = document.querySelector('input[name="reportType"]:checked').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        try {
            let url;
            switch(reportType) {
                case 'daily':
                    url = 'http://localhost:8080/api/reports/stock-movement/daily-json';
                    break;
                case 'weekly':
                    url = 'http://localhost:8080/api/reports/stock-movement/weekly-json';
                    break;
                case 'custom':
                    if (!startDate || !endDate) {
                        alert('Please select both start and end dates');
                        return;
                    }
                    url = `http://localhost:8080/api/reports/stock-movement/custom-json?startDate=${startDate}&endDate=${endDate}`;
                    break;
            }

            const response = await fetch(url, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Failed to generate report');
            }

            currentReportData = await response.json();
            populateReportTable(currentReportData);

        } catch (error) {
            console.error('Report error:', error);
            alert('Failed to generate report: ' + error.message);
        }
    });

    // Export to CSV
    document.getElementById('exportReportBtn').addEventListener('click', async () => {
        if (!currentReportData || currentReportData.length === 0) {
            alert('No report data available to export. Please generate a report first.');
            return;
        }

        try {
            const reportType = document.querySelector('input[name="reportType"]:checked').value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            let url;
            let filename;

            switch(reportType) {
                case 'daily':
                    url = 'http://localhost:8080/api/reports/stock-movement/daily/csv';
                    filename = `daily_report_${new Date().toISOString().split('T')[0]}.csv`;
                    break;
                case 'weekly':
                    url = 'http://localhost:8080/api/reports/stock-movement/weekly/csv';
                    filename = `weekly_report_${new Date().toISOString().split('T')[0]}.csv`;
                    break;
                case 'custom':
                    if (!startDate || !endDate) {
                        alert('Please select both start and end dates');
                        return;
                    }
                    url = `http://localhost:8080/api/reports/stock-movement/custom/csv?startDate=${startDate}&endDate=${endDate}`;
                    filename = `custom_report_${startDate}_to_${endDate}.csv`;
                    break;
            }

            const response = await fetch(url, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Failed to export report');
            }

            const blob = await response.blob();
            const downloadUrl = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = downloadUrl;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(downloadUrl);

        } catch (error) {
            console.error('Export error:', error);
            alert('Failed to export report: ' + error.message);
        }
    });

    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = '../auth/login.html';
    });

    function populateReportTable(data) {
        const tableBody = document.getElementById('reportTableBody');
        tableBody.innerHTML = '';

        if (!data || data.length === 0) {
            const row = document.createElement('tr');
            row.innerHTML = '<td colspan="6" style="text-align: center;">No data available for the selected period</td>';
            tableBody.appendChild(row);
            return;
        }

         console.log("Data: ", data);


        data.forEach(item => {
            const row = document.createElement('tr');

            // Adding CSS class
              const quantityClass = item.newQuantity < 5 ? 'quantity-low' : 'quantity-normal';
              const movementClass = item.movementType === 'ADD' ? 'status-normal' : 'status-danger';

              const quantityChangeText = item.movementType === 'ADD' ? `+${item.quantityChange}` : `-${item.quantityChange}`;

            row.innerHTML = `
                <td>${new Date(item.date).toLocaleString() || ''}</td>
                <td>${item.itemName || ''}</td>
                <td class = "${movementClass}">${item.movementType || ''}</td>
                <td class = "${movementClass}">${quantityChangeText}</td>
                <td>${item.previousQuantity || ''}</td>
                <td class = "${quantityClass}">${item.newQuantity || ''}</td>
                <td>${item.user || ''}</td>
                <td>${item.notes || ''}</td>
            `;
            tableBody.appendChild(row);
        });
    }

});