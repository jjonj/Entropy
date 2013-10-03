package jjj.entropy.cardcreator;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Db actions are blocking! Make it threaded!

public class DatabaseManager 
{

	private static DatabaseManager instance = null ;
	
	public boolean Connected = false;
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet resultSet = null;
	
	
	protected DatabaseManager(){}
	
	public static DatabaseManager GetInstance()
	{
		if (instance == null)
			instance = new DatabaseManager();
		return instance;
	}

	
	
	public boolean Connect()
	{
		   
		try {
		    String url = "jdbc:mysql://localhost:3306/";
		    String dbName = "entropy";
		    String userName = "root"; 
		    String password = "agentaa3";//"lolipop1pandora1";
		    conn = DriverManager.getConnection(url+dbName,userName,password);
		    Connected = true;
			return true;
		} catch (SQLException e) {
			// handle any errors
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		 //   System.exit(1);
		    return false;
		}
	}
	
	public void Disconnect()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				System.out.println("FATAL ERROR: Cannot close DB Connection or sleep & retry. Close the connection manually!");
				e1.printStackTrace();
				System.exit(1);
			}
	//		Disconnect();
			   System.out.println("SQLException: " + e.getMessage());
			    System.out.println("SQLState: " + e.getSQLState());
			    System.out.println("VendorError: " + e.getErrorCode());
		}
		conn = null;
	}
	
    protected void finalize() throws SQLException
    {
		if (conn != null)
		{
			conn.close();
		}
    }

	public boolean CreateCard(String title, int cardRace, int cardType, int rarity, int raceCost, int anyCost, int strength, int intelligence, int vitality) //TODO: Prone to sql injections, care?
	{
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO cards VALUES (NULL, '" + title + "', "+cardRace+", "+cardType+", "+rarity+", "+raceCost+", "+anyCost+", "+strength+", "+intelligence+", "+vitality+");");
			return true;
		} catch (SQLException e) {
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		    return false;
		}
		
	}


	public static String GetRace(int id)
	{
		switch (id)
		{
		case 1:
			return "Orc";
		case 2:
			return "Crawnid";
		default:
			return "Other";
		}
	}
	public static int GetRaceID(String race)
	{
		switch (race)
		{
		case "Orc":
			return 1;
		case "Crawnid":
			return 2;
		default:
			return 0;
		}
	}
	
	public List<String> GenerateTextures() 
	{
		
		List<String> resultList = new ArrayList<String>();
		
		try {
			stmt = conn.createStatement();
			
			resultSet = stmt.executeQuery("select * from cards order by card_id;");
		
			while (resultSet.next())
			{
				String race = GetRace(resultSet.getInt("race"));
				resultList.add("resources/textures/templates/" + race + "/" + resultSet.getString("title").replace(" ", "") + ".png");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return resultList;
		
	}

}
