-- Pet Adoption Database Schema
-- Make sure this matches your existing database structure

USE petadoption;

-- Sample data insertion (if needed)
INSERT INTO pets (pet_name, pet_type, breed, age, date_of_rescue) VALUES
('Max', 'Dog', 'Golden Retriever', 2, '2024-01-15'),
('Luna', 'Cat', 'Persian', 1, '2024-02-20'),
('Buddy', 'Dog', 'Labrador Mix', 3, '2024-03-10'),
('Whiskers', 'Cat', 'Siamese', 2, '2024-01-25'),
('Charlie', 'Dog', 'German Shepherd', 4, '2024-02-05');

INSERT INTO products (product_name, type, used_by_pet, cost, quantity) VALUES
('Premium Dog Food', 'Food', 'Dog', 45.99, 25),
('Cat Collar', 'Accessories', 'Cat', 12.99, 15),
('Dog Toy Set', 'Toys', 'Dog', 29.99, 20),
('Pet Vitamins', 'Healthcare', 'Both', 18.50, 30),
('Bird Cage', 'Accessories', 'Bird', 89.99, 5);

INSERT INTO customers (customer_name, email_address, address, mobile_number) VALUES
('John Smith', 'john.smith@email.com', '123 Main St, Anytown, ST 12345', '555-123-4567'),
('Sarah Johnson', 'sarah.johnson@email.com', '456 Oak Ave, Other City, ST 67890', '555-234-5678'),
('Mike Chen', 'mike.chen@email.com', '789 Pine Rd, Another City, ST 11111', '555-345-6789');
