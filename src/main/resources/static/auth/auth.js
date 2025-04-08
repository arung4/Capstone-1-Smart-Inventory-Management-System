
function getRoleFromToken() {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
        // Split the token into parts
        const parts = token.split('.');
        if (parts.length !== 3) {
            throw new Error('Invalid token format');
        }

        // Decode the payload part
        const payload = parts[1];
        const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        const parsedPayload = JSON.parse(decodedPayload);

        return parsedPayload.roles; // Should return 'ADMIN' or 'STAFF'
    } catch (error) {
        console.error('Error decoding token:', error);
        return null;
    }
}