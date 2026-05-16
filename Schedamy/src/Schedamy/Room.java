package Schedamy;

public class Room {

	private int roomID;
	private int building;
	private String roomType; 
	private int capacity;
	private String specialEquipment;  

	//Constructor
	public Room(int roomID, int building, String roomType, int capacity, String specialEquipment)
	{
		this.roomID = roomID;
		this.building = building;
		this.roomType = roomType;
		this.specialEquipment = specialEquipment;
	}

	//Get roomID 
	public int getRoomID()
	{
		return this.roomID;
	}

	//Get building 
	public int getBuilding()
	{
		return this.building;
	}

	//Get roomID 
	public String getRoomType()
	{
		return this.roomType;
	}

	//Get roomType 
	public int getCapacity()
	{
		return this.capacity;
	}

	//Get specialEquipment 
	public String getSpecialEquipment()
	{
		return this.specialEquipment;
	}

	//To String
	public String toString() 
	{
		return "		Room Number: " + building + roomID  + "	Room Type: " + roomType + "	Capacity: " + capacity + "	Special Equipment: " + specialEquipment;
	}

}
