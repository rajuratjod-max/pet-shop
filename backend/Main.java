//package DBMSProject;
import java.util.*;

import java.sql.*;
import java.sql.Date;
public class Main {
static final String URL = "jdbc:mysql://localhost:3306/miniproject?useSSL=false";
static final String USER = "root"; 
static final String PASSWORD = "raju";
static Scanner in = new Scanner(System.in);
static int customer_id;
static int userType;
static void BuyAProduct() {

    String query1, query2;

    Scanner in = new Scanner(System.in);

    query1 = "select * from product";

    query2 = "Insert into orders(customer_id, product_id, order_date, quantity) values (?,?,?,?)";

    

    try (Connection conn = connect(); 

         PreparedStatement ps = conn.prepareStatement(query1); 

         PreparedStatement ps1 = conn.prepareStatement(query2)) {

        

        ResultSet r = ps.executeQuery();

        

        // Display products

        while (r.next()) {

            System.out.println("Product id: " + r.getInt("product_id"));

            System.out.println("Product Name: " + r.getString("product_name"));

            System.out.println("Quantity: " + r.getInt("quantity"));

            System.out.println("Cost: " + r.getInt("cost"));

            System.out.println("Type: " + r.getString("type"));

            System.out.println("Used by pet: " + r.getString("used_by_pet"));

            System.out.println("----------------------------------------------------");

        }

        

        // Get user input for purchase

        System.out.println("Enter the productID of the product you want to buy: ");

        int p_id = in.nextInt();

        System.out.println("Enter the quantity of the product to buy: ");

        int p_quantity = in.nextInt();
        
        System.out.println("Enter today's date: ");
        
        String todaysdate=in.next();

       

        // Insert order into the database

        ps1.setInt(1, customer_id);  // Make sure customerID is defined

        ps1.setInt(2, p_id);

        ps1.setDate(3, Date.valueOf(todaysdate)); // Use the current date

        ps1.setInt(4, p_quantity);

        

        // Execute the insert query

        ps1.executeUpdate();

        

    } catch (SQLException e) {

        e.printStackTrace();

    }

}


static void totalsales() {
String query,startdate,enddate;
	System.out.println("Enter start date (yyyy-MM-dd)");
	startdate=in.next();
	System.out.println("Enter end date (yyyy-MM-dd)");
	enddate=in.next();
	query="call totalsales(?,?)";
	try(Connection conn=connect();PreparedStatement ps= conn.prepareStatement(query);){
		ps.setDate(1, Date.valueOf(startdate));
		ps.setDate(2, Date.valueOf(enddate));
		ResultSet r=ps.executeQuery();
		 if (r.next()) {
             // Assuming total_sales is returned as a single column in the result set
             double totalSales = r.getDouble("total_sales");
             System.out.println("Total sales: " + totalSales);
         } else {
            System.out.println("No sales found for the given date range.");

         }
	
	}
	catch(SQLException e) {
		e.printStackTrace();
	}

}
static void changeItemPrice() {
    Scanner in = new Scanner(System.in);

    System.out.print("Enter Product ID to change price: ");
    int productId = in.nextInt();

    System.out.print("Enter New Cost: ");
    double newCost = in.nextDouble();

    // Directly embed user input into query string
    String query = "UPDATE Product SET cost = " + newCost + " WHERE product_id = " + productId;

    Connection conn = null;
    Statement stmt = null;

    try {
        conn = DriverManager.getConnection(Main.URL, Main.USER, Main.PASSWORD);
        stmt = conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Item price updated successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 
static void changeItemQuantity() {
    Scanner in = new Scanner(System.in);

    System.out.print("Enter Product ID to change quantity: ");
    int productId = in.nextInt();

    System.out.print("Enter New Quantity: ");
    int newQuantity = in.nextInt();

    // Directly embed user input into query string
    String query = "UPDATE Product SET quantity = " + newQuantity + " WHERE product_id = " + productId;

    Connection conn = null;
    Statement stmt = null;

    try {
        conn = DriverManager.getConnection(Main.URL, Main.USER, Main.PASSWORD);
        stmt = conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Item quantity updated successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
static void insertnewpet() {

	String query="Insert into Pet(pet_id,pet_name,pet_type,date_of_rescue,breed) values (?,?,?,?,?)";

	String petname,dateofrescue,breed,type;

	int petid;

	Scanner in=new Scanner(System.in);

	System.out.println("Enter pet id");

	petid=in.nextInt();

	System.out.println("Enter pet name");

	petname=in.next();

	System.out.println("Enter pet type");

	type=in.next();
	System.out.println("Enter date of rescue (yyyy-mm-dd) format");

	dateofrescue=in.next();

	System.out.println("Enter pet breed");
	in.nextLine();
	breed=in.nextLine();

	

	try(Connection conn=connect();PreparedStatement ps=conn.prepareStatement(query);)

	{

		ps.setInt(1, petid);

		ps.setString(2, petname);

		ps.setString(3, type);

		ps.setDate(4,Date.valueOf(dateofrescue));

		ps.setString(5, breed);

		ps.executeUpdate();

		System.out.println("Pet inserted succesfully");

		conn.close();

		

	}

	catch(SQLException e) {

		e.printStackTrace();

		}

}
static void adoptedpets() {
	String query;
	query="Select pet_id,pet_name,customer_id from  AdopteesJoin";
	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);)
	{
ResultSet r=ps.executeQuery();
		while(r.next()) {
			System.out.println("Pet id: "+r.getInt("pet_id"));
			System.out.println("Pet Name: "+r.getString("pet_name"));
			System.out.println("Customer id: "+r.getInt("customer_id"));
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
}
static void insertcustomer() {

	String query;

	String customer_name,address,emailid;

	long mobileno;

	query="Insert into customer(customer_name,address,email_address,mobile_number) values (?,?,?,?)";

	Scanner in=new Scanner(System.in);

	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);){

		System.out.println("Enter customer name");

		customer_name=in.next();

		System.out.println("Enter address");
		in.nextLine();

		address=in.nextLine();

		System.out.println("Enter email id");

		emailid=in.next();

		System.out.println("Enter mobile number");

		mobileno=in.nextLong();

		ps.setString(1,customer_name);

		ps.setString(2,address);

		ps.setString(3, emailid);

		ps.setLong(4, mobileno);

		ps.executeUpdate();

		System.out.println("Customer inserted successfully");

		

		

	}

	catch(SQLException e) {

		e.printStackTrace();

		}

}
static void customerdetails() {

	String query;

	query="Select * from customer";

	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);)

	{

		ResultSet r=ps.executeQuery();

		while(r.next()) {

			System.out.println("Customer id: "+r.getInt("customer_id"));

			System.out.println("Customer Name: "+r.getString("customer_name"));

			System.out.println("Email address: "+r.getString("email_address"));

			System.out.println("Address: "+r.getString("address"));

			System.out.println("Mobile NumberL "+r.getLong("mobile_number"));

			System.out.println("------------------------------------------------------");

			

		}

	

}

	catch(SQLException e) {

		e.printStackTrace();

	}

}
static void adoptAPet() {
    Scanner in = new Scanner(System.in);
    String query1, query2, currentDate = null;
    query1 = "SELECT * from pet";
    System.out.println("Please enter the pet id of the pet that you would like to adopt");
    int p_id = in.nextInt();
   System.out.println("Enter todays date (YYYY-MM-DD)"); // Specify date format
   String date = in.next();

    query2 = "INSERT INTO adopts (pet_id, customer_id,date_of_adoption) VALUES (?, ?,?)";

    try (Connection conn = DriverManager.getConnection(Main.URL, Main.USER, Main.PASSWORD)) {
        PreparedStatement ps = conn.prepareStatement(query2);
        ps.setInt(1, p_id);
        ps.setInt(2, customer_id);
        ps.setDate(3, Date.valueOf(date));

        ResultSet r1 = conn.createStatement().executeQuery(query1);

        // ... (rest of the code for displaying pet list and adopted pet list)

        ps.executeUpdate(); // Execute the prepared statement for adoption
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void petadopthistory() {
	String query;
	int cno;
	cno=customer_id;
	query="Select * from pet natural left outer join adopts where customer_id=?";
	try(Connection conn=connect();PreparedStatement ps= conn.prepareStatement(query);){
		ps.setInt(1, cno);
		ResultSet r=ps.executeQuery();
		while(r.next()) {   
			System.out.println("pet _id: "+ r.getInt("pet_id"));
			System.out.println("pet name: "+ r.getString("pet_name"));
			System.out.println("adoption date: "+ r.getDate("date_of_adoption"));
			System.out.println("---------------------");

		}
}
	catch(SQLException e) {
		e.printStackTrace();
}
}

private static Connection connect() throws SQLException {
	return DriverManager.getConnection(URL,USER,PASSWORD);
}
static void petproductlikebyusedbypet() {
    System.out.println("Enter the animal type:");
    in.nextLine();
    String used_by_pet = in.nextLine();
    String query = "select * from product where used_by_pet like '%" + used_by_pet + "%'";

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);
        while (r.next()) {
            System.out.println("product name: " + r.getString("product_name"));
            System.out.println("product type: " + r.getString("type"));
            System.out.println("Used by pet type: " + r.getString("used_by_pet"));
            System.out.println("cost: " + r.getString("cost"));
            System.out.println("---------------------");
        }
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void orderByProductCost() {
    int choice;
    System.out.println("Enter 1 to display ascending and 2 to display descending order:");
    choice = in.nextInt();
    String query;

    if (choice == 1) {
        query = "SELECT * FROM product ORDER BY cost";
    } else {
        query = "SELECT * FROM product ORDER BY cost DESC";
    }

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);

        while (r.next()) {
            System.out.println("product id: " + r.getInt("product_id"));
            System.out.println("product name: " + r.getString("product_name"));
            System.out.println("product type: " + r.getString("type"));
            System.out.println("Used by pet type: " + r.getString("used_by_pet"));
            System.out.println("cost: " + r.getInt("cost"));
            System.out.println("---------------------");
        }
        
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void seeProducts(){

    String query;
        query = "SELECT * FROM Product";
    
    try {
        Connection conn = DriverManager.getConnection(Main.URL, Main.USER, Main.PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);

        while (r.next()) {
            System.out.println("product id: " + r.getInt("product_id"));
            System.out.println("product name: " + r.getString("product_name"));
            System.out.println("type: " + r.getString("type"));
            System.out.println("used by pet: " + r.getString("used_by_pet"));
            System.out.println("cost: " + r.getInt("cost"));
            System.out.println("quantity: " + r.getInt("quantity"));
            System.out.println("---------------------");
        }
        
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void inventory() {

	String query;

	query="Select * from product";

	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);)

	{

		ResultSet r=ps.executeQuery();

		while(r.next()) {

			System.out.println("Product id: "+r.getInt("product_id"));

			System.out.println("Product Name: "+r.getString("product_name"));

			System.out.println("Quantity: "+r.getInt("quantity"));

			System.out.println("Cost :"+r.getInt("cost"));

			System.out.println("Type: "+r.getString("type"));

			System.out.println("Used by pet: "+r.getString("used_by_pet"));

			System.out.println("----------------------------------------------------");

			

		}

	}

	catch(SQLException e) {

		e.printStackTrace();

	}

}
static void ageOfPetBetween() {
    System.out.println("Enter the age range of pets (from and to):");
    int age1 = in.nextInt();
    int age2 = in.nextInt();
    in.nextLine(); // Consume the newline

    String query = "SELECT * FROM pet WHERE age BETWEEN " + age1 + " AND " + age2;

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);
        while (r.next()) {
            System.out.println("Pet name: " + r.getString("pet_name"));
            System.out.println("Pet type: " + r.getString("pet_type"));
            System.out.println("Pet breed: " + r.getString("breed"));
            System.out.println("Pet age: " + r.getInt("age"));
            System.out.println("---------------------");
        }
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void petbreedlike() {
    System.out.println("Enter the breed of pet you want");
    in.nextLine();
    String breed = in.nextLine();
    String query = "select * from pet where breed like '%" + breed + "%'";

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);
        while (r.next()) {
            System.out.println("pet name: " + r.getString("pet_name"));
            System.out.println("pet type: " + r.getString("pet_type"));
            System.out.println("pet breed: " + r.getString("breed"));
            System.out.println("---------------------");
        }
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static  void toseethepetsrescuedonadate() {
    System.out.println("Enter the date of rescue (yyyy-MM-dd) whose data you want to see");
    in.nextLine();
    String date = in.nextLine();
    String query = "select * from pet where date_of_rescue like '%" + date + "%'"; // Ensure date format matches

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);
        while (r.next()) {
            System.out.println("pet id: " + r.getInt("pet_id"));
            System.out.println("pet name: " + r.getString("pet_name"));
            System.out.println("pet type: " + r.getString("pet_type"));
            System.out.println("pet breed: " + r.getString("breed"));
            System.out.println("---------------------");
        }
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void getcustomerproducthistory() {
	String query,query1;
	int cno;
	cno=customer_id;
	query="select*from orders where customer_id=?";
	
	try(Connection conn=connect();PreparedStatement ps= conn.prepareStatement(query);){
		ps.setInt(1, cno);
		ResultSet r=ps.executeQuery(); 
		while(r.next()) {   
			System.out.println("order id: "+ r.getInt("order_id"));
			System.out.println("order date: "+ r.getDate("order_date"));
			System.out.println("product id: "+ r.getInt("product_id"));
			System.out.println("---------------------");


		}
}
	catch(SQLException e) {
	e.printStackTrace();
	}
}
static void seePets() {
    String query;
    query = "SELECT * FROM Pet";

try {
    Connection conn = DriverManager.getConnection(Main.URL, Main.USER, Main.PASSWORD);
    Statement stmt = conn.createStatement();
    ResultSet r = stmt.executeQuery(query);

    while (r.next()) {
        System.out.println("pet id: " + r.getInt("pet_id"));
        System.out.println("pet name: " + r.getString("pet_name"));
        System.out.println("pet type: " + r.getString("pet_type"));
        System.out.println("date of rescue: " + r.getString("date_of_rescue"));
        System.out.println("breed: " + r.getString("breed"));
        System.out.println("age: " + r.getString("age"));
        System.out.println("---------------------");
    }
    
    stmt.close();
    conn.close();
} catch (SQLException e) {
    e.printStackTrace();
}
}
static void specificpet() {
	String query,type;
	System.out.println("Enter specific pet type");
	type=in.next();
	query="select * from pet where pet_type=?";
	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);){
		ps.setString(1, type);
		ResultSet r= ps.executeQuery();
		while(r.next()) {
			System.out.println("pet id: " + r.getInt("pet_id"));
            System.out.println("pet name: " + r.getString("pet_name"));
            System.out.println("pet type: " + r.getString("pet_type"));
            System.out.println("pet breed: " + r.getString("breed"));
            System.out.println("---------------------");         

			
		}
		ps.close();
        conn.close();
	}
	catch (SQLException e) {
	    e.printStackTrace();
	}
}
static void petproducttypelike() {
    System.out.println("Enter the type of product you want");
    in.nextLine();
    String type = in.nextLine();
    String query = "select * from product where type like '%" + type + "%'";

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);
        while (r.next()) {
            System.out.println("product name: " + r.getString("product_name"));
            System.out.println("Used by pet type: " + r.getString("used_by_pet"));
            System.out.println("cost: " + r.getString("cost"));
            System.out.println("---------------------");
        }
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void filterForProductUsingBetween() {
    System.out.println("Enter the price range (from and to) to display the products:");
    int from = in.nextInt();
    int to = in.nextInt();
    in.nextLine(); // Consume the newline
    boolean flag=false;

    String query = "SELECT * FROM product WHERE cost BETWEEN " + from + " AND " + to;

    try {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet r = stmt.executeQuery(query);
        while (r.next()) {
            System.out.println("Product id: " + r.getInt("product_id"));
            System.out.println("Product name: " + r.getString("product_name"));
            System.out.println("Product Price: " + r.getFloat("cost"));
            System.out.println("---------------------");
            flag=true;
        }
        if(flag==false) {
        	System.out.println("No pet products between " +from+" and"+to);
        }
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static void insertproduct() {

	String query,productname,type,usebypet;

	int productid,quantity;

	double cost;

	query="Insert into product(product_id,product_name,type,used_by_pet,cost,quantity) values (?,?,?,?,?,?)";

	Scanner in=new Scanner(System.in);

	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);){

		System.out.println("Enter product id");

		productid=in.nextInt();

		System.out.println("Enter product name");

		in.nextLine();

		productname=in.nextLine();

		System.out.println("Enter type");

		type=in.next();

		System.out.println("Enter used by pet");

		usebypet=in.next();

		System.out.println("Enter cost");

		cost=in.nextDouble();

		System.out.println("Enter quantity");

		quantity=in.nextInt();

		ps.setInt(1,productid);

		ps.setString(2,productname);

		ps.setString(3, type);

		ps.setString(4, usebypet);

		ps.setDouble(5, cost);

		ps.setInt(6, quantity);

		ps.executeUpdate();

		System.out.println("Product inserted");

	}

	catch (SQLException e) {

        e.printStackTrace();

    }



}
public static void main(String[] args) {
	int option=0;
try {
Class.forName("com.mysql.cj.jdbc.Driver");
}
catch(Exception e) {
e.printStackTrace();
return;
}
try {
String email, query;

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Enter your email_id");
        email = in.next();
        email = email.replace("'", "''");  // Escape single quotes if any
        query = "SELECT check_email_status('" + email + "')";  // Direct concatenation with escaped email
        Statement st = connection.createStatement();
        ResultSet resultset = st.executeQuery(query);
        while(resultset.next()) {
        	userType = resultset.getInt(1);
        }
       
        	query = "CALL GetCustomerIdByEmail(?, @customer_id)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);
        ps.executeUpdate();
        query = "SELECT @customer_id"; 
        resultset = st.executeQuery(query);
            while(resultset.next()) {
            customer_id = resultset.getInt(1);
            }	
        
      
        connection.close();
    } catch(SQLException e) {
        e.printStackTrace();
    }
if(userType==0) {
System.out.println("Do you want \n1: Customer View\n2: Admin VIew");	
userType=in.nextInt();
}
do {
switch(userType) {
//Customer View
case 1:{
int opt=0;
do {
System.out.println("Enter\n1: View pets  to be adopted\n2: View products\n3: Adopt a pet\n4: Buy a product\n5: See adoption history\n6: See purchase history\n0: Exit");
option = in.nextInt();
switch(option) {
case 1: {
do {
System.out.println("Enter \n1: View all\n2: Specific pet type\n3: Pets by age\n4: pets by rescue date\n5: Pet by breed");	
opt = in.nextInt();
switch(opt) {
case 1:{
seePets();	
break;
}
case 2:{
specificpet();
break;
}
case 3:{
ageOfPetBetween();
break;	
}
case 4:{
toseethepetsrescuedonadate();
break;
}
case 5:{
petbreedlike();
break;
}
}
}while(opt!=0);
break;
}
case 2:
{
do {
System.out.println("Enter \n1: View all\n2: Specific pet product\n3: Order by Cost\n4: Product type\n5: Filter using price");
opt = in.nextInt();
switch(opt) {
case 1:{
seeProducts();
break;	
}
case 2:{
petproductlikebyusedbypet();
break;	
}
case 3:{
orderByProductCost();
break;	
}
case 4:{
petproducttypelike();
break;	
}
case 5:
{
filterForProductUsingBetween();
break;
}
}
}while(opt!=0);
break;	
}
case 3:
{
adoptAPet();
break;
}
case 4:{
BuyAProduct();
break;
}
case 5:{
petadopthistory();
break;
}
case 6:{
getcustomerproducthistory();
break;	
}
}
}while(option!=0);
break;
}
//Administrator View
case 2:{
do {
System.out.println("Enter\n1: View adoption details\n2: Pets to be adopted\n3: View inventory\n4: View customers\n5: See rescued pets by date\n6: Add new pet\n7: Add new items\n8: Change item price\n9: Change item quantity\n10: View total sales\n0: Exit");
option = in.nextInt();	
switch(option) {
case 1:{
adoptedpets();
break;	
}
case 2:{
seePets();	
break;	
}
case 3:{
inventory();
break;	
}
case 4:{
customerdetails();
break;	
}
case 5:{
toseethepetsrescuedonadate();
break;	
}
case 6:{
insertnewpet();
break;	
}
case 7:{
insertproduct();
break;	
}
case 8:{
changeItemPrice();
break;	
}
case 9:{
changeItemQuantity();
break;	
}
case 10:{
totalsales();
break;	
}
}
}while(option!=0);
break;	
}
//View without login 
case 3:{
boolean flag = false;
do {
if(flag==true) {
	flag = true;
	userType = 1;
	break;	
}
System.out.println("Enter \1: Register\n2: View Products\n3: View Pets\n0: Exit");
option = in.nextInt();	
switch(option) {
case 1:{
	flag=true;
	String query;

	String customer_name,address,emailid;

	long mobileno;

	query="Insert into customer(customer_name,address,email_address,mobile_number) values (?,?,?,?)";

	Scanner in=new Scanner(System.in);

	try(Connection conn=connect(); PreparedStatement ps=conn.prepareStatement(query);){

		System.out.println("Enter customer name");

		customer_name=in.next();

		System.out.println("Enter address");
		in.nextLine();

		address=in.nextLine();

		System.out.println("Enter email id");

		emailid=in.next();

		System.out.println("Enter mobile number");

		mobileno=in.nextLong();

		ps.setString(1,customer_name);

		ps.setString(2,address);

		ps.setString(3, emailid);

		ps.setLong(4, mobileno);

		ps.executeUpdate();

		System.out.println("Customer inserted successfully");

	
	}

	catch(SQLException e) {

		e.printStackTrace();

		}

}
case 2:{
	int opt;
	do {
		System.out.println("Enter \n1: View all\n2: Specific pet product\n3: Order by Cost\n4: Product type");
		opt = in.nextInt();
		switch(opt) {
		case 1:{
		seeProducts();
		break;	
		}
		case 2:{
		petproductlikebyusedbypet();
		break;	
		}
		case 3:{
		orderByProductCost();
		break;	
		}
		case 4:{
		petproducttypelike();
		break;	
		}
		}
		}while(opt!=0);
break;	
}
case 3:{
	int opt;
	do {
		System.out.println("Enter \n1: View all\n2: Specific pet type\n3: Pets by age\n4: pets by rescue date\n5: Pet by breed");	
		opt = in.nextInt();
		switch(opt) {
		case 1:{
		seePets();	
		break;
		}
		case 2:{
		specificpet();
		break;
		}
		case 3:{
		ageOfPetBetween();
		break;	
		}
		case 4:{
		toseethepetsrescuedonadate();
		break;
		}
		case 5:{
		petbreedlike();
		break;
		}
		}
		}while(opt!=0);
break;	
}
}
}while(option!=0);
break;	
}
}
}while(option!=0);
}

// Add these methods to your Main.java file

public void seeCustomers() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/miniproject", "root", "your_password");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM customer");
        
        while (rs.next()) {
            System.out.println("customer id: " + rs.getInt("customer_id"));
            System.out.println("customer name: " + rs.getString("customer_name"));
            System.out.println("email address: " + rs.getString("email_address"));
            System.out.println("address: " + rs.getString("address"));
            System.out.println("mobile number: " + rs.getLong("mobile_number"));
            System.out.println("---");
        }
        con.close();
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

public void totalSales() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/miniproject", "root", "your_password");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT SUM(p.cost * o.quantity) AS total_sales FROM orders o JOIN product p ON o.product_id = p.product_id");
        
        if (rs.next()) {
            System.out.println("total sales: " + rs.getDouble("total_sales"));
        }
        con.close();
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

public void adoptedPets() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/miniproject", "root", "your_password");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM adopts");
        
        while (rs.next()) {
            System.out.println("pet id: " + rs.getInt("pet_id"));
            System.out.println("customer id: " + rs.getInt("customer_id"));
            System.out.println("adoption date: " + rs.getDate("date_of_adoption"));
            System.out.println("---");
        }
        con.close();
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

}
