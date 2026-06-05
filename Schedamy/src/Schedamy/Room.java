package Schedamy;

public class Room {

	private String roomID;
	private int building;
	private String roomType; 
	private int capacity;
	private String specialEquipment;  
	private String status;

	//Constructor
	public Room(String roomID, int building, String roomType, int capacity, String specialEquipment, String status)
	{
		this.roomID = roomID;
		this.building = building;
		this.roomType = roomType;
		this.specialEquipment = specialEquipment;
		this.status = status;
		this.capacity = capacity;
	}

	//Get roomID 
	public String getRoomID()
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
	    return "Room number: " + roomID + 
	           " Building: " + building + "\n" +
	           " Room type: " + roomType +
	           "Capacity: " + capacity + 
	           " Special equipment: " + specialEquipment +
	           " Status: " + status;
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
