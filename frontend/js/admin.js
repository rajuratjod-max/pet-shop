// Admin Dashboard Functionality

// Pet Management Functions
function openAddPetModal() {
    document.getElementById('addPetModal').style.display = 'block';
}

function closeAddPetModal() {
    document.getElementById('addPetModal').style.display = 'none';
    document.getElementById('addPetForm').reset();
}

function editPet(id) {
    alert(`Edit pet with ID: ${id}`);
    // Here you would open an edit modal with pre-filled data
}

function deletePet(id) {
    if (confirm('Are you sure you want to delete this pet?')) {
        alert(`Pet with ID: ${id} deleted`);
        // Here you would make an API call to delete the pet
    }
}

// Product Management Functions
function openAddProductModal() {
    document.getElementById('addProductModal').style.display = 'block';
}

function closeAddProductModal() {
    document.getElementById('addProductModal').style.display = 'none';
    document.getElementById('addProductForm').reset();
}

function editProduct(id) {
    alert(`Edit product with ID: ${id}`);
    // Here you would open an edit modal with pre-filled data
}

function deleteProduct(id) {
    if (confirm('Are you sure you want to delete this product?')) {
        alert(`Product with ID: ${id} deleted`);
        // Here you would make an API call to delete the product
    }
}

// Form Submissions
document.addEventListener('DOMContentLoaded', function() {
    // Add Pet Form
    const addPetForm = document.getElementById('addPetForm');
    if (addPetForm) {
        addPetForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            
            // Here you would send the data to your backend
            alert('Pet added successfully!');
            closeAddPetModal();
            
            // Reload the pets table
            // loadPetsTable();
        });
    }
    
    // Add Product Form
    const addProductForm = document.getElementById('addProductForm');
    if (addProductForm) {
        addProductForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            
            // Here you would send the data to your backend
            alert('Product added successfully!');
            closeAddProductModal();
            
            // Reload the products table
            // loadProductsTable();
        });
    }
});

// Filter Functions
function filterBySpecies() {
    const species = document.getElementById('speciesFilter').value;
    // Filter pets table by species
    console.log('Filtering by species:', species);
}

function filterByBreed() {
    const breed = document.getElementById('breedFilter').value;
    // Filter pets table by breed
    console.log('Filtering by breed:', breed);
}

function filterByCategory() {
    const category = document.getElementById('categoryFilter').value;
    // Filter products table by category
    console.log('Filtering by category:', category);
}

// Search Functions
function searchPets() {
    const searchTerm = document.getElementById('searchPets').value;
    // Search pets by name or other criteria
    console.log('Searching pets:', searchTerm);
}

function searchProducts() {
    const searchTerm = document.getElementById('searchProducts').value;
    // Search products by name or other criteria
    console.log('Searching products:', searchTerm);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const addPetModal = document.getElementById('addPetModal');
    const addProductModal = document.getElementById('addProductModal');
    
    if (event.target === addPetModal) {
        closeAddPetModal();
    }
    if (event.target === addProductModal) {
        closeAddProductModal();
    }
}

// Generate Reports Function
function generateSalesReport() {
    alert('Generating sales report...');
    // Here you would generate and download a sales report
}

// Data visualization functions (you can expand these)
function loadDashboardStats() {
    // Load real-time statistics for the dashboard
    // This would typically fetch data from your backend API
}

function updateOrderStatus(orderId, newStatus) {
    // Update order status in the database
    alert(`Order ${orderId} status updated to: ${newStatus}`);
}

// Initialize admin dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Load dashboard statistics
    loadDashboardStats();
    
    // Set up event listeners for filters and search
    const speciesFilter = document.getElementById('speciesFilter');
    if (speciesFilter) {
        speciesFilter.addEventListener('change', filterBySpecies);
    }
    
    const breedFilter = document.getElementById('breedFilter');
    if (breedFilter) {
        breedFilter.addEventListener('change', filterByBreed);
    }
    
    const categoryFilter = document.getElementById('categoryFilter');
    if (categoryFilter) {
        categoryFilter.addEventListener('change', filterByCategory);
    }
    
    const searchPetsInput = document.getElementById('searchPets');
    if (searchPetsInput) {
        searchPetsInput.addEventListener('input', searchPets);
    }
    
    const searchProductsInput = document.getElementById('searchProducts');
    if (searchProductsInput) {
        searchProductsInput.addEventListener('input', searchProducts);
    }
});
