package Schedamy;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Lesson {
	
	private int lessonID;
	private LocalDate lessonDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private String status;
	private String teachingMode;
	private boolean requiresLabRoom;

	
	public Lesson(int lessonID, LocalDate lessonDate, LocalTime startTime,
		  		  LocalTime endTime, String status,
		  		  String teachingMode, boolean requiresLabRoom) {
		
		if (lessonDate == null || startTime == null || endTime == null ||
			status == null) {
			throw new IllegalArgumentException("Fields cannot be null");
		}
		
		if (!endTime.isAfter(startTime)) {
			throw new IllegalArgumentException("End time must be after start time");
		}
		
	this.lessonID = lessonID;
	this.lessonDate = lessonDate;
	this.startTime = startTime;
	this.endTime = endTime;
	this.requiresLabRoom = requiresLabRoom;
	setStatus(status);
	setTeachingMode(teachingMode);
	}
			
	public int getLessonID() {
		return this.lessonID;
	}
	
	public LocalDate getLessonDate() {
		return this.lessonDate;
	}
	
	public LocalTime getStartTime() {
		return this.startTime;
	}
	
	public LocalTime getEndTime() {
		return this.endTime;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		
		if (status == null) {
			throw new IllegalArgumentException("Status cannot be null");
		}
		
		if (status.equals("SCHEDULED")||
			status.equals("RESCHEDULED")||
			status.equals("CANCELLED")) {
			
			this.status = status;
			
		} else {
			throw new IllegalArgumentException("Invalid status");
		}
	}
	
	
	public Duration getDurationTime() {
		
		return Duration.between(getStartTime(), getEndTime());
	}

	
	public String getTeachingMode() {
		return this.teachingMode;
	}
	
	
	public void setTeachingMode(String teachingMode) {
		
		if (teachingMode == null) {
			throw new IllegalArgumentException ("Teaching mode cannot be null");
		}
		
		if (teachingMode.equals("FRONTAL") ||
			teachingMode.equals("ZOOM") ||
			teachingMode.equals("HYBRID")) {
			this.teachingMode = teachingMode;
		}
	}
	
	
	public boolean getRequiresLabRoom() {
		
		return this.requiresLabRoom;
	}

	
	public void changeMode(String newMode) {
		
		if (newMode == null) {
			throw new IllegalArgumentException("Teaching mode cannot be null");
		}
		
		if (newMode.equals("FRONTAL") ||
			newMode.equals("ZOOM") ||
			newMode.equals("HYBRID")){
			
			this.teachingMode = newMode;
			
		} else {
			throw new IllegalArgumentException("Invalid teaching mode");
		}
	}		
}
	
