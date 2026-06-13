package Schedamy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

public class AvailabilityThread implements Runnable {
	
	//By using Runnable we want to create a thread 
	// that runs in sync by checking the availability
	// of the lecturers, students and classrooms
	private static final java.time.format.DateTimeFormatter DATE_FORMAT = 
		    java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	private Lecturer lecturer;
	private Lesson lesson;
	private LocalDate date;
	private Vector<Room> rooms;
	private final Vector<GroupEnrolment> groupEnrolment;
	private final Object roomLock;
	private boolean isCancellation;
	private LocalTime startTime;
	private LocalTime endTime;
	private String suggestion = "";
	private Room suggestedRoom = null;
	private LocalDate suggestedDate = null;
	
	public AvailabilityThread(Lecturer lecturer, Lesson lesson, 
							  LocalDate date, Vector<Room> rooms,
							  Object roomLock, Vector<GroupEnrolment> groupEnrolment,
							  boolean isCancellation) {
		this.lecturer = lecturer;
		this.lesson = lesson;
		this.date = date;
		this.rooms = rooms;
		this.roomLock = roomLock;
		this.groupEnrolment = groupEnrolment;
		this.isCancellation = isCancellation;
		this.startTime = lesson.getStartTime();
		this.endTime = lesson.getEndTime();
	}
	
	public void run() {
		
		try {
			
			if (isCancellation) {
				System.out.println("Lesson Cancelled!");
				return;
			}
			//Check the availability of the lecturer
			for (Lesson current : lecturer.getLessons()) {
				if(current.getLessonDate().equals(date)) {
					System.out.println("Lecturer unavilable on : " + date);
					return;
				}
			}
			
			//Check the student groups availability
			for(StudentGroup group : lesson.getStudents()) {
				for(GroupEnrolment enrolment : groupEnrolment) {
					if(enrolment.getGroup().getGroupID()== group.getGroupID()) {
						for(Lesson groupLesson : enrolment.getCourse().getLessons()){
							if(groupLesson.getLessonDate().equals(date) &&
									!groupLesson.getStatus().equals("CANCELLED")) {
								System.out.println("Group: " +
									group.getGroupID() + ("unavailable on: " + date));
								return;
							}
						}
					}
				 }	
			 }
			
			Thread.sleep(1000);
			
			if("FRONTAL".equals(lesson.getTeachingMode())) {
				System.out.println("Searching for available room...");
				handleFrontal();
			} else {
				handleZoom();
			}
			
			System.out.println("Search Complete!");
			
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
		
		//function to handle incase the class is frontal
		private void handleFrontal() {
			synchronized (roomLock) {
				Room availableRoom = null;
				
				for(Room room : rooms) {
					if("AVAILABLE".equals(room.getStatus())) {
						availableRoom = room;
						break;
					}
				}
				if (availableRoom != null) {
		            if (!isCancellation) {
		                // Store suggestion instead of applying
		                suggestedRoom = availableRoom;
		                suggestedDate = date;
		                suggestion = "Date: " + date.format(DATE_FORMAT) + 
		                             "\nTime: " + startTime + " - " + endTime + 
		                             "\nRoom: " + availableRoom.getRoomID();
		                System.out.println("[FRONTAL] Suggestion found: " + suggestion);
		            }
		        } else {
		            System.out.println("[FRONTAL] No available room on: " + date);
		        }
		    }
		}
		
		//function to handle incase the lesson will be in zoom
		private void handleZoom() {
			synchronized (lesson) {
				if (!isCancellation) {
		            suggestedDate = date;
		            suggestion = "Date: " + date.format(DATE_FORMAT) + 
		                         "\nTime: " + startTime + " - " + endTime + 
		                         "\nMode: ZOOM (no room needed)";
		            System.out.println("[ZOOM] Suggestion found: " + suggestion);
		        }
		    }
		}

		public String getSuggestion() {
			return suggestion;
		}   
		
		public Room getSuggestedRoom() {
			return suggestedRoom;
		}
		
		public LocalDate getSuggestedDate() {
			return suggestedDate;
		}
}