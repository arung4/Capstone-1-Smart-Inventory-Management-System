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
                    url = 'http://localhost:8080/api/reports/daily-json';
                    break;
                case 'weekly':
                    url = 'http://localhost:8080/api/reports/weekly-json';
                    break;
                case 'custom':
                    if (!startDate || !endDate) {
                        alert('Please select both start and end dates');
                        return;
                    }
                    url = `http://localhost:8080/api/reports/custom-json?startDate=${startDate}&endDate=${endDate}`;
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
                    url = 'http://localhost:8080/api/reports/daily';
                    filename = `daily_report_${new Date().toISOString().split('T')[0]}.csv`;
                    break;
                case 'weekly':
                    url = 'http://localhost:8080/api/reports/weekly';
                    filename = `weekly_report_${new Date().toISOString().split('T')[0]}.csv`;
                    break;
                case 'custom':
                    if (!startDate || !endDate) {
                        alert('Please select both start and end dates');
                        return;
                    }
                    url = `http://localhost:8080/api/reports/custom?startDate=${startDate}&endDate=${endDate}`;
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
        window.location.href = '../../auth/login.html';
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

        data.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${item.date || ''}</td>
                <td>${item.name || ''}</td>
                <td>${item.category || ''}</td>
                <td>${item.quantity || 0}</td>
                <td>${item.price ? '$' + item.price.toFixed(2) : ''}</td>
                <td class="${getStatusClass(item.status)}">${item.status || 'OK'}</td>
            `;
            tableBody.appendChild(row);
        });
    }

    function getStatusClass(status) {
        if (!status) return '';
        return status === 'LOW_STOCK' ? 'negative' :
               status === 'NEAR_EXPIRY' ? 'warning' : '';
    }
});