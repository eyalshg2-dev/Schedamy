package Schedamy;

import java.time.LocalDate;
import java.util.Vector;

public class AvailabilityThread implements Runnable {
	
	//By using Runnable we want to create a thread 
	// that runs in sync by checking the availability
	// of the lecturers, students and classrooms
	
	private Lecturer lecturer;
	private Lesson lesson;
	private LocalDate date;
	private Vector<Room> rooms;
	private final Vector<GroupEnrolment> groupEnrolment;
	private final Object roomLock;
	
	public AvailabilityThread(Lecturer lecturer, Lesson lesson, 
							  LocalDate date, Vector<Room> rooms,
							  Object roomLock, Vector<GroupEnrolment> groupEnrolment) {
		this.lecturer = lecturer;
		this.lesson = lesson;
		this.date = date;
		this.rooms = rooms;
		this.roomLock = roomLock;
		this.groupEnrolment = groupEnrolment;
	}
	
	public void run() {
		
		try {
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
			
			if("FRONTAL".equals(lesson.getTeachingMode())) {
				handleFrontal();
			} else {
				handleZoom();
			}
			
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
				if(availableRoom != null) {
					availableRoom.setStatus("RESCHEDULED");
					lesson.setLessonDate(date);
					lesson.setRoom(availableRoom);
					lesson.setStatus("RESCHEDULED");
					System.out.println("[FRONTAL] Rescheduled on: " + date 
							+ " | Room: " + availableRoom.getRoomID());
				} else {
					System.out.println("[FRONTAL] No available room on : " + date);
				}
			}
		}
		
		//function to handle incase the lesson will be in zoom
		private void handleZoom() {
			synchronized (lesson) {
				if(!"RESCHEDULED".equals(lesson.getStatus())) {
					lesson.setLessonDate(date);
					lesson.setStatus("RESCHEDULED");
					System.out.println("[ZOOM] Rescheduled on: " + date +
							 " - all groups available");
				}	
			 }
		  }   
}