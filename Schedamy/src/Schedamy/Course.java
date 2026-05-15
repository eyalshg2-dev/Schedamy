package Schedamy;

import java.util.ArrayList;
import java.util.List;

public class Course{
	
	private int courseID;
	private String courseName;
	private int credits;
	List<Lesson> lessons = new ArrayList<>();
	private String courseType;
	
	
	public Course(int courseID, String courseName, int credits, String courseType) {
		this.courseID = courseID;
		setCourseName(courseName);
		setCredits(credits);
		setCourseType(courseType);
	}
	
	// calculate the total hours by iterating through the lessons
	public int calculateCourseHours() {
		int totalHours = 0;
		
		for (Lesson lesson : lessons) {
			totalHours += lesson.getDurationTime().toHours();
		}
		
		return totalHours;
	}
	
	
	public int getCourseID() {
		return this.courseID;
	}
	
	
	public String getCourseName() {
		return this.courseName;
	}
	
	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	
	public int getCredits() {
		return this.credits;
	}
	
	
	public void setCredits(int credits) {
		this.credits = credits;
		
	}
	
	
	public String getCourseType() {
		return this.courseType;
	}
	
	
	public void setCourseType(String courseType) {
		if (courseType.equals("elective") || 
			courseType.equals("mandatory")) {
			this.courseType = courseType;
		} else {
			throw new IllegalArgumentException("No valid course types");
		}
	}
	
	
	public void addLesson (Lesson lesson) {
		if (lesson == null) {
			throw new IllegalArgumentException("Lesson cannot be null");
		}
		
		 lessons.add(lesson);
	}
}
