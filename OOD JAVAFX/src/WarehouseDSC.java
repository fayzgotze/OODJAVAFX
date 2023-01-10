import java.sql.*;

import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

//MUMINOV FAYZ 19897760 2022-CSE3OAD(SY-2) 

public class WarehouseDSC {

	// the date format we will be using across the application
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	/*
		FREEZER, // freezing cold
		MEAT, // MEAT cold
		COOLING, // general Warehousearea
		CRISPER // veg and fruits section

		note: Enums are implicitly public static final
	*/
	public enum SECTION {
		FREEZER,
		MEAT,
		COOLING,
		CRISPER
	};

	private static Connection connection;
	private static Statement statement;
	private static PreparedStatement preparedStatement;

	public static void connect() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");


			/* TODO 1-01 - TO COMPLETE ****************************************
			 * change the value of the string for the following 3 lines:
			 * - url
			 * - user
			 * - password
			 */			
			String url = "jdbc:mysql://localhost:3306/warehousedb";
			String user = "root";
			String password = "1234";

			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
  		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
	}

	public static void disconnect() throws SQLException {
		if(preparedStatement != null) preparedStatement.close();
		if(statement != null) statement.close();
		if(connection != null) connection.close();
	}



	public Item searchItem(String name) throws Exception {
		String queryString = "SELECT * FROM item WHERE name = ?";
		preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1,name);
		ResultSet rs = preparedStatement.executeQuery();
		/* 
		  preparedStatement to add argument name to the queryStringresultSet to execute the preparedStatement queryiterate through the resultSet result
		 */	


		Item item = null;

		if (rs.next()) { // i.e. the item exists
			
			String name1 = rs.getString(1);
            Integer expires = Integer.parseInt(rs.getString(2));
            boolean expiry= false;
            if(expires ==1)
			  {
			  	expiry = true;
			  }

            item  = new Item(name1,expiry);
			// if resultSet has result, get data and create an Item instance
				

		}	


		return item;
	}

	public Product searchProduct(int id) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM product WHERE id = ?";
		preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1,id+"");
		ResultSet rs = preparedStatement.executeQuery();
		/*
		  preparedStatement to add argument name to the queryString
		  resultSet to execute the preparedStatement query
		  iterate through the resultSet result
		 */	


		Product product = null;

		if (rs.next()) { // i.e. the product exists
			
			int id1 = Integer.parseInt(rs.getString(1));
			String itemName = rs.getString(2);
			LocalDate date = LocalDate.parse(rs.getString(3),dtf);
            int quantity = Integer.parseInt(rs.getString(4));
            String section = rs.getString(5);
            Item item = searchItem(itemName);

            product = new Product(id1,item,date,quantity,SECTION.valueOf(section));

			/* 
			  if resultSet has result, get data and create a product instance
			  making sure that the item name from product exists in 
			  item table (use searchItem method)
			  pay attention about parsing the date string to LocalDate
			 */	

		}

		return product;
	}

	public List<Item> getAllItems() throws Exception {
		String queryString = "SELECT * FROM item";
		statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(queryString);

		//  resultSet to execute the statement query
		 	

		List<Item> items = new ArrayList<Item>();
		Item item =null;
		while (rs.next())
		{
			String name1 = rs.getString(1);
			Integer expires = Integer.parseInt(rs.getString(2));
			boolean expiry= false;
			if(expires ==1)
			{
				expiry = true;
			}

			item  = new Item(name1,expiry);
			items.add(item);

		}

		// iterate through the resultSet result, create intance of Item and add to list items
		 

		return items;
	}

	public List<Product> getAllProducts() throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM product";
		statement = connection.createStatement();
		ResultSet product = statement.executeQuery(queryString);

		// resultSet to execute the statement query
		 

		List<Product> products = new ArrayList<Product>();
		Product product =null;
		while(rs.next())
		{
			int id = Integer.parseInt(rs.getString(1));
			String itemName = rs.getString(2);
			LocalDate date = LocalDate.parse(rs.getString(3),dtf);
			int quantity = Integer.parseInt(rs.getString(4));
			String section = rs.getString(5);
			Item item = searchItem(itemName);

			product = new Product(id,item,date,quantity,SECTION.valueOf(section));
			products.add(product);
		}

		//iterate through the resultSet result, create intance of Itemand add to list itemsmaking sure that the item name from each product exists in  item table (use searchItem method) pay attention about parsing the date string to LocalDate
		return products;
	}


	public int addProduct(String name, int quantity, SECTION section) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate date = LocalDate.now();
		String dateStr = date.format(dtf);
		
		// NOTE: should we check if itemName (argument name) exists in item table?
		//		--> adding a product with a non-existing item name should through an exception

		String command = "INSERT INTO Product VALUES(?, ?, ?, ?, ?)";
		PreparedStatement p = connection.prepareStatement(command);
		p.setString(1,name);
		p.setString(2,dateStr);
		p.setString(3,Integer.toString(quantity));
		p.setString(4,section.toString());
        p.executeUpdate();
		//  preparedStatement to add arguments to the queryString resultSet to executeUpdate the preparedStatement query
		// retrieving & returning last inserted record id
		ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		int newId = rs.getInt(1);

		return newId;		
	}

	public Product useProduct(int id) throws Exception {
//search product by id check if has quantity is greater one; if not throw exception with adequate error message
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		Product p = searchProduct(id);
        if (p.getQuantity() <2)
		{
			throw new Exception("There is only 1 " + p.getItemName() + " brought on " + p.getDate().format(dtf)+ " -Use Delete Instead");
		}	


		String queryString = 
			"UPDATE product " +
			"SET quantity = quantity - 1 " +
			"WHERE quantity > 1 " + 
			"AND id = " + id + ";";

		//statement execute update on queryStringshould the update affect a row search product by id and return it; else throw exception with adequate error message
		statement = connection.createStatement();
        statement.executeUpdate(queryString);
		 
		return searchProduct(id);//NOTE: method should return instance of product
	}

	public int removeProduct(int id) throws Exception {
		String queryString = "DELETE FROM product WHERE id = " + id + ";";
		Product p = searchProduct(id);
		if(p==null)
		{
			throw new Exception("There is no such product!!!");

		}

		statement = connection.createStatement();
		return statement.executeUpdate(queryString);

		//search product by id if product exists, statement execute update on queryString return the value value of that statement execute update if product does not exist, throw exception with adequate error messageNOTE: method should return int: the return value of astetement.executeUpdate(...) on a DELETE query
		 

	}

	// STATIC HELPERS -------------------------------------------------------

	public static long calcDaysAgo(LocalDate date) {
    	return Math.abs(Duration.between(LocalDate.now().atStartOfDay(), date.atStartOfDay()).toDays());
	}

	public static String calcDaysAgoStr(LocalDate date) {
    	String formattedDaysAgo;
    	long diff = calcDaysAgo(date);

    	if (diff == 0)
    		formattedDaysAgo = "today";
    	else if (diff == 1)
    		formattedDaysAgo = "yesterday";
    	else formattedDaysAgo = diff + " days ago";	

    	return formattedDaysAgo;			
	}

	// To perform some quick tests	
	public static void main(String[] args) throws Exception {
		WarehouseDSC myWarehouseDSC = new WarehouseDSC();

		myWarehouseDSC.connect();

		System.out.println("\nSYSTEM:\n");

		System.out.println("\n\nshowing all of each:");
		System.out.println(myWarehouseDSC.getAllItems());
		System.out.println(myWarehouseDSC.getAllProducts());

		int addedId = myWarehouseDSC.addProduct("Milk", 40, SECTION.COOLING);
		System.out.println("added: " + addedId);
		System.out.println("deleting " + (addedId - 1) + ": " + (myWarehouseDSC.removeProduct(addedId - 1) > 0 ? "DONE" : "FAILED"));
		System.out.println("using " + (addedId) + ": " + myWarehouseDSC.useProduct(addedId));
		System.out.println(myWarehouseDSC.searchProduct(addedId));

		myWarehouseDSC.disconnect();
	}
}