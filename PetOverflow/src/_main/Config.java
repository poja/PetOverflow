package _main;

public class Config {
	
	public static String DB_NAME = "PetOvDb";
	
	public static String USER_TABLE_NAME = "PetOwner";
	public static String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE_NAME + " ("
			+ "username VARCHAR(10), "
			+ "password VARCHAR(8), "
			+ "nickname VARCHAR(20), "
			+ "description LONG VARCHAR, "
			+ "photo LONG VARCHAR, "
			+ "phone VARCHAR(20) )";
	

}
