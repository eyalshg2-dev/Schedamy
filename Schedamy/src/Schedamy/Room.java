package Schedamy;

public class Room {

	private String roomID;
	private int building;
	private String roomType; 
	private int capacity;
	private String status;

	//Constructor
	public Room(String roomID, int building, String roomType, int capacity,String status)
	{
		this.roomID = roomID;
		this.building = building;
		this.roomType = roomType;
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

	//To String
	public String toString()
	{
		return "Room Number: " + roomID + "\n" +
				"Building: " + building + "\n" +
				"Room Type: " + roomType + "\n" +
				"Capacity: " + capacity + "\n" +
				"Room Size: " + classifyRoomSize() + "\n" +
				"Special Equipment: " +"-------------------------";
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
