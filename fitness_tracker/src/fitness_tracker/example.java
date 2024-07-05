//package fitness_tracker;
//import java.sql.*;
//
//public class example {
//
//	public static void main(String[] args) {
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/fitness_tracker",
//					"root","root");
//			Statement stmt= con.createStatement();
//			System.out.println("inserting records");
//			String sql="Insert into users values(3,'sk','123','email')";
//			stmt.executeUpdate(sql);
//		}catch(Exception e)
//		{
//			System.out.println(e);
//		}
//
//	}
//
//}

//package fitness_tracker;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//    private static final String URL = "jdbc:mysql://localhost:3306/fitness_tracker";
//    private static final String USER = "root";
//    private static final String PASSWORD = "root";
//
//    public static Connection getConnection() throws SQLException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            return DriverManager.getConnection(URL, USER, PASSWORD);
//        } catch (ClassNotFoundException e) {
//            throw new SQLException("Driver not found", e);
//        }
//    }
//}

