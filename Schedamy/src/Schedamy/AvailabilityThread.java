package Schedamy;

import java.time.LocalDate;
import java.util.Vector;

public class AvailabilityThread implements Runnable {
	
	private Lecturer lecturer;
	private Lesson lesson;
	private LocalDate date;
	private Vector<Room> rooms;
	private final Object roomLock;
	
	public AvailabilityThread(Lecturer lecturer, Lesson lesson, 
							  LocalDate date, Vector<Room> rooms, Object roomLock) {
		this.lecturer = lecturer;
		this.lesson = lesson;
		this.date = date;
		this.rooms = rooms;
		this.roomLock = roomLock;
	}
	
	public void run() {
		
		try {
			for (Lesson current : lecturer.getLessons()) {
				if(current.getLessonDate().equals(date)) {
					System.out.println("Thread lecturer unavilable on : " + date);
					return;
				}
			}
		} catch(Exception e) {
			System.out.println("[AvailabilityThread] Error: " + e.getMessage());
		}
	        

	        lesson.setStatus("RESCHEDULED");
	    }
	}