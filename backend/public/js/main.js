// Mobile Menu Toggle
const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
const navMenu = document.querySelector('.nav-menu');

if (mobileMenuToggle && navMenu) {
    mobileMenuToggle.addEventListener('click', () => {
        navMenu.classList.toggle('active');
        mobileMenuToggle.classList.toggle('active');
    });
}

// Smooth Scrolling for Anchor Links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Form Validation
const forms = document.querySelectorAll('form');
forms.forEach(form => {
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Basic validation
        const requiredFields = form.querySelectorAll('[required]');
        let isValid = true;
        
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                isValid = false;
                field.style.borderColor = '#dc3545';
            } else {
                field.style.borderColor = '#ff8fab';
            }
        });
        
        if (isValid) {
            // Show success message (you can replace this with actual form submission)
            alert('Thank you for your message! We will get back to you soon.');
            form.reset();
        } else {
            alert('Please fill in all required fields.');
        }
    });
});

// Add loading animation to buttons
document.querySelectorAll('.btn').forEach(button => {
    button.addEventListener('click', function() {
        if (this.type === 'submit') {
            const originalText = this.textContent;
            this.textContent = 'Sending...';
            this.disabled = true;
            
            setTimeout(() => {
                this.textContent = originalText;
                this.disabled = false;
            }, 2000);
        }
    });
});

// Intersection Observer for animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Observe elements for animation
document.querySelectorAll('.pet-card, .service-card, .blog-post, .step').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(20px)';
    el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
    observer.observe(el);
});

// Filter functionality for adoption page
const filterSelects = document.querySelectorAll('.filter-group select');
filterSelects.forEach(select => {
    select.addEventListener('change', function() {
        console.log('Filter changed:', this.value);
    });
});

// Newsletter subscription
const newsletterForm = document.querySelector('.newsletter-form');
if (newsletterForm) {
    newsletterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const email = this.querySelector('input[type="email"]').value;
        if (email) {
            alert('Thank you for subscribing to our newsletter!');
            this.reset();
        }
    });
}

// Scroll to top functionality
let scrollToTopBtn = document.createElement('button');
scrollToTopBtn.innerHTML = '↑';
scrollToTopBtn.className = 'scroll-to-top';
scrollToTopBtn.style.cssText = `
    position: fixed;
    bottom: 20px;
    right: 20px;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: linear-gradient(45deg, #ff6b9d, #ff8fab);
    color: white;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: 1000;
`;

document.body.appendChild(scrollToTopBtn);

window.addEventListener('scroll', () => {
    if (window.pageYOffset > 300) {
        scrollToTopBtn.style.opacity = '1';
    } else {
        scrollToTopBtn.style.opacity = '0';
    }
});

scrollToTopBtn.addEventListener('click', () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
});

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Fetch pets from database
async function loadPetsFromDatabase() {
    try {
        const response = await fetch(`${API_BASE_URL}/pets`);
        const pets = await response.json();
        displayPets(pets);
    } catch (error) {
        console.error('Error loading pets:', error);
        showNotification('Error loading pets from database');
    }
}

// Fetch products from database
async function loadProductsFromDatabase() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        console.error('Error loading products:', error);
        showNotification('Error loading products from database');
    }
}

// Display pets in the UI
function displayPets(pets) {
    const petsGrid = document.querySelector('.pets-grid');
    if (!petsGrid) return;
    
    petsGrid.innerHTML = pets.map(pet => `
        <div class="pet-card" data-category="${pet.pet_type}" data-age="${pet.age}">
            <img src="https://via.placeholder.com/280x200/96CEB4/FFFFFF?text=${pet.pet_name}" alt="${pet.pet_name}">
            <div class="pet-info">
                <h3>${pet.pet_name}</h3>
                <p>${pet.pet_type} • ${pet.breed}</p>
                <div class="pet-details">
                    <span>🎂 ${pet.age} years old</span>
                    <span>📅 Rescued: ${pet.date_of_rescue}</span>
                </div>
                <div class="pet-tags">
                    <span class="tag">${pet.pet_type}</span>
                    <span class="tag">${pet.breed}</span>
                </div>
                <button class="adopt-btn" onclick="adoptPet(${pet.pet_id}, '${pet.pet_name}')">Adopt ${pet.pet_name}</button>
            </div>
        </div>
    `).join('');
}

// Display products in the UI
function displayProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    if (!productsGrid) return;
    
    productsGrid.innerHTML = products.map(product => `
        <div class="product-card" data-category="${product.type}" data-price="${product.cost}">
            <img src="https://via.placeholder.com/280x250/ff6b9d/FFFFFF?text=${encodeURIComponent(product.product_name)}" alt="${product.product_name}">
            <div class="product-info">
                <h3>${product.product_name}</h3>
                <p class="product-category">${product.type}</p>
                <div class="product-price">$${product.cost}</div>
                <p class="product-description">For ${product.used_by_pet}</p>
                <p class="product-stock">Stock: ${product.quantity}</p>
                <div class="product-actions">
                    <button class="btn btn-primary add-to-cart" onclick="addToCartFromDB('${product.product_id}', '${product.product_name}', ${product.cost})">Add to Cart</button>
                    <button class="btn btn-outline view-details" onclick="viewProductDetails('${product.product_id}')">View Details</button>
                </div>
            </div>
        </div>
    `).join('');
}

// Adopt pet function
async function adoptPet(petId, petName) {
    const customerId = localStorage.getItem('customer_id');
    if (!customerId) {
        alert('Please register first to adopt a pet');
        return;
    }
    
    const adoptionDate = new Date().toISOString().split('T')[0];
    
    try {
        const response = await fetch(`${API_BASE_URL}/pets/adopt`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                pet_id: petId,
                customer_id: customerId,
                adoption_date: adoptionDate
            })
        });
        
        const result = await response.json();
        if (result.success) {
            showNotification(`Congratulations! You have successfully adopted ${petName}!`);
            loadPetsFromDatabase(); // Refresh pets list
        } else {
            showNotification('Error processing adoption. Please try again.');
        }
    } catch (error) {
        console.error('Error adopting pet:', error);
        showNotification('Error connecting to database');
    }
}

// Buy product function
async function addToCartFromDB(productId, productName, price) {
    const customerId = localStorage.getItem('customer_id');
    if (!customerId) {
        alert('Please register first to buy products');
        return;
    }
    
    // Add to local cart first
    addToCart(productId, productName, price);
    
    // Also record in database when checkout happens
    const cartItems = JSON.parse(localStorage.getItem('cart_for_db') || '[]');
    cartItems.push({
        product_id: productId,
        product_name: productName,
        price: price,
        quantity: 1,
        customer_id: customerId
    });
    localStorage.setItem('cart_for_db', JSON.stringify(cartItems));
}

// Register customer function
async function registerCustomer(customerData) {
    try {
        const response = await fetch(`${API_BASE_URL}/customers/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(customerData)
        });
        
        const result = await response.json();
        if (result.success) {
            // Store customer ID for future use
            localStorage.setItem('customer_id', result.customer_id || '1'); // Default to 1 for demo
            localStorage.setItem('customer_email', customerData.email);
            showNotification('Registration successful!');
            return true;
        } else {
            showNotification('Registration failed. Please try again.');
            return false;
        }
    } catch (error) {
        console.error('Error registering customer:', error);
        showNotification('Error connecting to database');
        return false;
    }
}

// Checkout function that saves to database
async function checkoutToDatabase() {
    const cartItems = JSON.parse(localStorage.getItem('cart_for_db') || '[]');
    const customerId = localStorage.getItem('customer_id');
    
    if (cartItems.length === 0 || !customerId) {
        alert('No items to checkout or customer not registered');
        return;
    }
    
    const orderDate = new Date().toISOString().split('T')[0];
    
    try {
        for (const item of cartItems) {
            const response = await fetch(`${API_BASE_URL}/products/buy`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    customer_id: customerId,
                    product_id: item.product_id,
                    quantity: item.quantity,
                    order_date: orderDate
                })
            });
            
            const result = await response.json();
            if (!result.success) {
                console.error('Error processing item:', item.product_name);
            }
        }
        
        // Clear cart after successful checkout
        localStorage.removeItem('cart_for_db');
        showNotification('Order placed successfully!');
        
        // Redirect to success page
        window.location.href = 'customer-dashboard.html';
        
    } catch (error) {
        console.error('Error during checkout:', error);
        showNotification('Error processing order');
    }
}

// Load data when pages load
document.addEventListener('DOMContentLoaded', function() {
    // Load pets on adoption page
    if (window.location.pathname.includes('adoption.html')) {
        loadPetsFromDatabase();
    }
    
    // Load products on shop page
    if (window.location.pathname.includes('shop.html')) {
        loadProductsFromDatabase();
    }
    
    // Check if user is logged in
    const customerId = localStorage.getItem('customer_id');
    const customerEmail = localStorage.getItem('customer_email');
    
    if (customerId && customerEmail) {
        // Update UI to show logged in state
        const accountLinks = document.querySelectorAll('a[href="customer-dashboard.html"]');
        accountLinks.forEach(link => {
            link.textContent = `Welcome, ${customerEmail.split('@')[0]}`;
        });
    }
});
