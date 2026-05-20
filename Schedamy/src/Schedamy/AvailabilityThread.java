package Schedamy;

import java.time.LocalDate;
import java.util.Vector;

public class AvailabilityThread implements Runnable {
	
	private Lecturer lecturer;
	private Lesson lesson;
	private LocalDate date;
	private Vector<Room> rooms;
	
	public AvailabilityThread(Lecturer lecturer, Lesson lesson, 
							  LocalDate date, Vector<Room> rooms) {
		this.lecturer = lecturer;
		this.lesson = lesson;
		this.date = date;
		this.rooms = rooms;
	}
	
	public void run() {
		
		  boolean lecturerAvailable = true;

	        for (Lesson currentLesson : lecturer.getLessons()) {
	            if (currentLesson.getLessonDate().equals(date)) {
	                lecturerAvailable = false;
	                break;
	            }
	        }

	        if (!lecturerAvailable) {
	            System.out.println("Lecturer is not available on: " + date);
	            return;
	        }

	        if ("FRONTAL".equals(lesson.getTeachingMode())) {

	            for (Room room : rooms) {
	            	if("AVAILABILITY".equals(room.getStatus()))
	            		System.out.println("Available classroom found");
	                	break;
	            }

	        } else {
	            System.out.println("Lecturer is available on: " + date + "for ZOOM");
	        }
	        

	        lesson.setStatus("UPDATED");
	    }
	}