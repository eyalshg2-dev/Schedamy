package Schedamy;

public class Room {

	private int roomID;
	private int building;
	private String roomType; 
	private int capacity;
	private String specialEquipment;  
	private String status;

	//Constructor
	public Room(int roomID, int building, String roomType, int capacity, String specialEquipment, String status)
	{
		this.roomID = roomID;
		this.building = building;
		this.roomType = roomType;
		this.specialEquipment = specialEquipment;
		this.status = status;
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

	//Get room size
	public String classifyRoomSize()
	{
		if (capacity <= 20) return "small room";
		if (capacity > 20 && capacity <= 50) return "medium room ";
		else {return "large room";}
	}

	public String getStatus() {
		return status;
	} 
	
	public void setStatus(String status) {
		this.status = status;
	}
	
}
