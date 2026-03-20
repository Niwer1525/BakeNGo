document.getElementById('restart-btn').addEventListener('click', () => {
    fetch('/restart', { method: 'POST' })
        .then(response => response.text())
        .then(data => alert(data))
        .catch(error => console.error('Error:', error));
});