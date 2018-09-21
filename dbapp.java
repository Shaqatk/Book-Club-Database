import java.awt.List;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Scanner;

public class dbapp {

	private Connection conDB;   // Connection to the database system.
    private String url;         // URL: Which database?
	private int customer_id;         //customers cid
    private String customer_name;    //customers name
    private String customer_city;    //customers city 
    private String in_category;    // category customer has chosen
    private String chosen_title;    // title of book customer has chosen
    private int chosen_title_year;  //year of book customer has chosen
    private String chosen_title_lang;   //language of book customer has chosen
    private double chosen_title_weight; //weight of book customer has chosen
    private String customer_club;   //club of customer the book was chosen in
    private int title_qnty;     //quantity of book customer has chosen 
    private double minprice;    //cheapest price of book chosen in club
    private double total_price; //total price of book/books purchased 
    private  ArrayList<String> categories = new ArrayList<String>(); //array of types of categories
    private ArrayList<book> books = new ArrayList<book>(); //array of books in category
    private Scanner scanner;    // scanner for user input
   
//Constructor
	 public dbapp(){
	// Register the db2 driver with DriverManager, catch exceptions that might occur	
		 try{
			    Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
			    }catch(ClassNotFoundException e) {
              e.printStackTrace();
              System.exit(0);
          } catch (InstantiationException e) {
              e.printStackTrace();
             System.exit(0);
         } catch (IllegalAccessException e) {
              e.printStackTrace();
              System.exit(0);
          }
         
			url = "jdbc:db2:c3421a";    // URL for the database.

		 try{
		    conDB = DriverManager.getConnection(url);
		    }catch(SQLException e){
		    	System.out.println("\nSQL: database connection error.\n");
		    } 
         
		    scanner = new Scanner(System.in); //initialize scanner
	 }
	
    
//Class to store book information once found 
	 public class book{
		 String title; int year; String language; double weight;
		 public book(String title, int year, String language, double weight){
			 this.title = title;
			 this.year = year;
			 this.language = language;
			 this.weight = weight;
		 }
	 }
    
    
//PROCEDURES
	 
    
// Checks id cid exists in yrb_customer table
	 public boolean idcheck(int cid) throws SQLException{
		 String            queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        int num_of_customers = 0;
	        queryText =
		            "SELECT COUNT(cid) as customer"
		          + "    FROM yrb_customer "
		          +"where cid = "+cid;     
	        
	        try {
				querySt = conDB.prepareStatement(queryText);
			} catch (SQLException ex) {
				ex.printStackTrace();// TODO Auto-generated catch block
			}
		    
		    try {
				answers = querySt.executeQuery();
				answers.next();
				num_of_customers = answers.getInt("customer");
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		    
		    if (num_of_customers > 0) {
	            return true;
	        } else {
	            return false;
	        }
	 }
    
    
// finds cid 
	 public void find_cid() throws SQLException{
		 System.out.print("Enter your cid \n");
		 this.customer_id = scanner.nextInt();
		 boolean check = idcheck(customer_id);
		 while(!check){
			 System.out.print("Not a valid cid, Please enter your cid \n");
			  customer_id = scanner.nextInt();
			 check = idcheck(customer_id); 
		 } 
	 }
    
    
// finds customer assosiated with cid
	 public void find_customer(int cid) throws SQLException{
		 String queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        queryText ="SELECT cid, name, city "
		          + " FROM yrb_customer "
		          +" where cid = "+cid;     // the sql query
	        querySt = conDB.prepareStatement(queryText);
	        answers = querySt.executeQuery();
	        answers.next();
	        this.customer_name = answers.getString("name");
	        this.customer_city = answers.getString("city");
	        System.out.println("Cid: "+this.customer_id+",  Name: "+this.customer_name+", City: "+this.customer_city);
	 }
    
    
//prompt to check if customer want to update name and city
	 public boolean update_check(){
		 System.out.print("If you want to UPDATE customer Name and City press y for YES, if NOT press n for NO  \n");
		 String input = scanner.next();
         //if anything other than Y or N is pressed
		 while(!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")){	
			 System.out.print("Please only enter y or n \n");
			 input = scanner.next();
		 }
		 
		 if(input.equalsIgnoreCase("y")){
			 return true;
		 }else{
			 System.out.print("No update made \n");
			 return false;
		 }
	 }
	
    
    
// Makes updates to name and city of customer
	 public void update_customer(int cid) throws SQLException{
		 String upname ;
		 String upcity ;
		 scanner.nextLine();			 

		 System.out.print("Please enter new name \n");
		 upname = scanner.nextLine();
		 while(upname.length()>20){
			 System.out.print("You exceeded 20 character limit, Enter a name with 20 or less characters \n");
			 upname = scanner.next(); 
		 }
		 System.out.print("Please enter new city  \n");
		 upcity = scanner.nextLine();
		 
		 while(upcity.length()>15){
			 System.out.print("You exceeded 15 character limit, Enter a city with 15 or less characters \n");
			 upcity = scanner.next(); 
		 }
		 
		 String queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        queryText =
		            "update yrb_customer "
		          + " set name = ?, city = ?"
		          +"  where cid = "+cid;     // the sql query
	        querySt = conDB.prepareStatement(queryText);
	        querySt.setString(1, upname);
	        querySt.setString(2, upcity);
	         int ans = querySt.executeUpdate();
	         this.customer_name = upname;
	         this.customer_city = upcity;
			 System.out.print("Customer updated \n");
	 }
    
    
//lists  all categories
	 public void fetch_categories() throws SQLException{
		 String queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        queryText =
		            "SELECT cat "
		          + " FROM yrb_category ";     // the sql query
	        querySt = conDB.prepareStatement(queryText);
	        answers = querySt.executeQuery();
	        int count = 0;
	        int chosen_cat;
	        String cat_name;
	        System.out.println("Enter the number of CATEGORY you want to choose");
	        while(answers.next()){
	        	cat_name = answers.getString("cat");
	        	count+=1;
	        	this.categories.add(cat_name);
	        	System.out.println(count+"   "+cat_name);
	        }
	       chosen_cat = scanner.nextInt();
	       while(chosen_cat<=0 || chosen_cat> count){
	    	   System.out.println("Invalid Entry, Please choose a number listed above");
	    	   chosen_cat = scanner.nextInt();
	       }
	       this.in_category = this.categories.get(chosen_cat - 1);
	       System.out.println(this.in_category+" category was chosen ");
	       this.categories.clear();
	 }
    
    
// checks if book exists
	public boolean book_check(String book_title) throws SQLException{	
		String queryText = "";     // The SQL text.
        PreparedStatement querySt   = null;   // The query handle.
        ResultSet         answers   = null;   // A cursor.
        queryText =
	            "SELECT * "
	          + "  FROM yrb_book "
	           + "  where title = ? and cat = ?";     // the sql query
        querySt = conDB.prepareStatement(queryText);
        querySt.setString(1, book_title);
        querySt.setString(2, this.in_category);
        answers = querySt.executeQuery();
        if(answers.next()){
       	return true;
       }else{
       	System.out.println("Book entered does NOT exist");
       	return false;
       }
		
	}
    
    
// Finds all books with chosen title
	 public void find_book(String chosen_cat) throws SQLException{
		 String queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        queryText =
		            "SELECT title, year, language, weight "
		          + " FROM yrb_book "
		           + "  where title = ? and cat = ? ";     // the sql query
	        querySt = conDB.prepareStatement(queryText);
	        querySt.setString(1, chosen_cat);
	        querySt.setString(2, this.in_category);
	        answers = querySt.executeQuery();
	        int count = 0;
	        
	        while(answers.next()){
	        	count += 1;
	        	this.books.add(new book(answers.getString("title"), 
	        			                 answers.getInt("year"), 
	        			                  answers.getString("language"),
	        			                   answers.getDouble("weight")));
	        	
	        System.out.println(count+") title: "+answers.getString("title")+
	        		           ",  year: "+answers.getInt("year")+
	        		           ",  language: "+answers.getString("language")+
	        		           ",  weight: "+answers.getDouble("weight")+"\n");
	        }
	        System.out.println("Enter the number of the BOOK you want to choose ");
	        int book_number = scanner.nextInt();
	        while(book_number<=0 || book_number> count){
		    	   System.out.println("Invalid Entry, Please choose a number listed above");
		    	   book_number = scanner.nextInt();
		       }
	        book b_in_arr = this.books.get(book_number - 1);
	        this.chosen_title = b_in_arr.title;
	        this.chosen_title_year = b_in_arr.year;
	        this.chosen_title_lang = b_in_arr.language;
	        this.chosen_title_weight = b_in_arr.weight;
	        books.clear();
	        System.out.println("you chose  title: "+this.chosen_title+
	        		            ",  year: "+this.chosen_title_year+
	        		            ",  language: "+this.chosen_title_lang+
	        		            ",  weight: "+this.chosen_title_weight+"\n");
	 }
    
    
//finds min price of chosen book for according to club customer belongs too	 
	 public void min_price() throws SQLException{
		 String queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        queryText =
	        		"with clb(club) as (select club  from yrb_member m  where m.cid = ?)  " 
	        	+	"select min(o.price) as price  "
	        	+   "from yrb_offer o  "	 
	        	+   " where o.title = ? and o.year = ? and o.club in ( select * from clb C )";	   		     
	        querySt = conDB.prepareStatement(queryText);
	        querySt.setInt(1, this.customer_id);
	        querySt.setString(2, this.chosen_title);
	        querySt.setInt(3, this.chosen_title_year);
	        answers = querySt.executeQuery(); 
	        answers.next();
	        this.minprice = answers.getDouble("price");
	        System.out.println("The cheapest price of "+this.chosen_title+" available is $"+this.minprice);
	 }
    
    
//finds total price of books to be purchased by minprice*quantity
	 public void total_price(){
		 System.out.println("Please enter the quantity of the book you want");
		 int qnty = scanner.nextInt();
		 while(qnty < 1 ){
			 System.out.println("Invalid Entry, Choose a valid entry");
			 qnty = scanner.nextInt(); 
		 }
		 this.title_qnty = qnty;
		 this.total_price = this.title_qnty*this.minprice;
		 System.out.println("Your total will be  $"+this.minprice+" x" +qnty+" = $"+this.total_price);
	 }
    
    
// check if user wants to go through with purchase
	public boolean checkout(){
		System.out.print("Do you want to continue with purchase \n");
		 System.out.print("Enter y for YES and n for NO  \n");
		 String input = scanner.next();
		 while(!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")){	
			 System.out.print("Invalid Entry, Please only enter y or n \n");
			 input = scanner.next();
		 }
		 
		 if(input.equalsIgnoreCase("y")){
			 System.out.print("Proceed to purchase \n");
			 return true;
		 }else{
			 return false;
		 }
	}
    
    
//finds clubs 
	public void find_clb() throws SQLException{
		 String queryText = "";     // The SQL text.
	        PreparedStatement querySt   = null;   // The query handle.
	        ResultSet         answers   = null;   // A cursor.
	        queryText =
	        		"with clb(club) as (select club  from yrb_member m  where m.cid = ?)  " 
	        	+	"select o.club as club  "
	        	+   "from yrb_offer o, clb C  "	 
	        	+   " where o.title = ? and o.year = ? and o.price = ? and o.club = C.club";	   		     
	        querySt = conDB.prepareStatement(queryText);
	        querySt.setInt(1, this.customer_id);
	        querySt.setString(2, this.chosen_title);
	        querySt.setInt(3, this.chosen_title_year);
	        querySt.setDouble(4, this.minprice);
	        answers = querySt.executeQuery(); 
	        answers.next();
	        this.customer_club = answers.getString("club");
	}
    
    
// Updates purchse info in yrb_purcahase
	public void insert_purchase() throws SQLException{
	Timestamp t = new Timestamp(System.currentTimeMillis());
	String queryText = "";     // The SQL text.
    PreparedStatement querySt   = null;   // The query handle.
    ResultSet         answers   = null;   // A cursor.
    queryText ="insert into yrb_purchase  "
    	+   "values (?, ?, ?, ?, ?, ?)  "	 ;	   		     
    querySt = conDB.prepareStatement(queryText);
    querySt.setInt(1, this.customer_id);
    querySt.setString(2, this.customer_club);
    querySt.setString(3, this.chosen_title);
    querySt.setInt(4, this.chosen_title_year);
    querySt.setTimestamp(5, t);
    querySt.setInt(6, this.title_qnty);
    int confirmed = querySt.executeUpdate(); 
    
	}
    
    
    
    
// MAIN METHOD
	 public static void main(String[]args) throws SQLException{
		 Scanner scn = new Scanner(System.in);
		 dbapp srt = new dbapp(); 
		 srt.find_cid();
		 srt.find_customer(srt.customer_id);
		 if(srt.update_check()){
			srt.update_customer(srt.customer_id); 
		 }
         
		 srt.fetch_categories();
		
		 System.out.println("Please enter BOOK TITLE ");
			String book_title = scn.nextLine();
		 while(!srt.book_check(book_title)){
			 System.out.println("Please enter a valid title of your book ");
				book_title = scn.nextLine();
		 }
		 srt.find_book(book_title);
		 srt.min_price();
		 srt.total_price();
		 if(srt.checkout()){
             srt.find_clb();
			 srt.insert_purchase();
             System.out.println("Purchase Confirmed"); 
		 }else{
			System.out.println("No Purchases made"); 
		 }
		 
	 }
}
