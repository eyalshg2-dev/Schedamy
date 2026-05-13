package Schedamy;

import java.time.Duration;
import java.time.LocalDateTime;

public class RoomResrvation {

	private Room room;
	private Lesson lesson;
	private Duration occupancyTime;

	//Constructor
	public RoomResrvation(Room room, Lesson lesson, Duration occupancyTime)
	{
		this.room = room;
		this.lesson = lesson;
		this.occupancyTime = occupancyTime;
	}

	//Get Room
	public Room getRoom()
	{
		return room;
	}

	//Get Lesson
	public Lesson getLesson()
	{
		return lesson;
	}

	//Get occupancyTime
	public Duration getOccupancyTime()
	{
		return occupancyTime;
	}

	//Set room
	public void setRoom(Room newRoom)
	{
		this.room = newRoom;
	}

	//Set lesson
	public void setLesson(Lesson newLesson)
	{
		this.lesson = newLesson;
	}

	//To String 
	public String toString()
	{
		return room.toString() + lesson.toString() + "	Occupancy Time: " + occupancyTime;
	}

	//Calculate the occupancy time
	public void calculationOccupancyTime(LocalDateTime start,LocalDateTime end)
	{
		this.occupancyTime = Duration.between(start, end);
	}
}
