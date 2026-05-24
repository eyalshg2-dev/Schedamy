package Schedamy;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

public class Lesson {
	
	private int lessonID;
	private LocalDate lessonDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private String status;
	private String teachingMode;
	private boolean labRoomRequired;
	private Room room;
	private Vector<StudentGroup> students;

	
	public Lesson(int lessonID, LocalDate lessonDate, LocalTime startTime,
		  		  LocalTime endTime, String status,
		  		  String teachingMode, boolean labRoomRequired, Vector<StudentGroup> student) {
		
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
	this.labRoomRequired = labRoomRequired;
	this.students = new Vector<>();
	setStatus(status);
	setTeachingMode(teachingMode);
	}
			
	public int getLessonID() {
		return this.lessonID;
	}
	
	public synchronized LocalDate getLessonDate() {
		return this.lessonDate;
	}
	
	public synchronized void setLessonDate(LocalDate newDate) {
		if (newDate == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		this.lessonDate = newDate;
	}
	
	public LocalTime getStartTime() {
		return this.startTime;
	}
	
	public LocalTime getEndTime() {
		return this.endTime;
	}
	
	public synchronized String getStatus() {
		return this.status;
	}
	
	public synchronized void setStatus(String status) {
		
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

	
	public synchronized String getTeachingMode() {
		return this.teachingMode;
	}
	
	
	public synchronized void setTeachingMode(String teachingMode) {
		
		if (teachingMode == null) {
			throw new IllegalArgumentException ("Teaching mode cannot be null");
		}
		
		if (teachingMode.equals("FRONTAL") ||
			teachingMode.equals("ZOOM") ||
			teachingMode.equals("HYBRID")) {
			this.teachingMode = teachingMode;
		} else {
		
		throw new IllegalArgumentException("Invalid teaching mode");
		}
	}
	
	
	public boolean isLabRoomRequired() {
		
		return labRoomRequired;
	}

	
	public synchronized void changeMode(String newMode) {
		setTeachingMode(newMode);
	}	
	
	public synchronized Room getRoom() {
		return this.room;
	}
	
	public synchronized void setRoom(Room room) {
		this.room = room;
	}
	
	public synchronized void addStudent(StudentGroup student) {
		students.add(student);
	}
	
	public Vector<StudentGroup> getStudents() {
	    return students;
	}
	
	public String toString() {
		return "lessonID=" + lessonID + "lessonDate=" + lessonDate + "startTime=" + startTime +"endTime=" + endTime +
			   "status=" + status + "teachingMode=" + teachingMode + "labRoomRequired=" + labRoomRequired + "room=" + room ;
	}
}
	
