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
	    return "Room Reservation\n" +
	           "Room: " + room.getRoomID() + "\n" +
	           "Lesson ID: " + lesson.getLessonID() + "\n" +
	           "Date: " + lesson.getLessonDate() + "\n" +
	           "Time: " + lesson.getStartTime() + " - " + lesson.getEndTime() + "\n" +
	           "Occupancy Time: " + occupancyTime.toMinutes() + " minutes\n" +
	           "-------------------------";
	}
	

	//Calculate the occupancy time
	public void calculationOccupancyTime(LocalDateTime start,LocalDateTime end)
	{
		this.occupancyTime = Duration.between(start, end);
	}
	
	public boolean overlaps(Room otherRoom, Lesson otherLesson)
	{
		if (!room.getRoomID().equals(otherRoom.getRoomID()) ||
			    room.getBuilding() != otherRoom.getBuilding()) {
			    return false;
			}

	    if (!lesson.getLessonDate().equals(otherLesson.getLessonDate())) {
	        return false;
	    }

	    return otherLesson.getStartTime().isBefore(lesson.getEndTime()) &&
	           otherLesson.getEndTime().isAfter(lesson.getStartTime());
	}
}
