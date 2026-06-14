package Schedamy;

import java.util.Vector;

public class Course{

	private int courseID;
	private String courseName;
	private int credits;
	private Vector<Lesson> lessons;
	private String courseType;

	public Course(int courseID, String courseName, int credits,
			String courseType, Vector<Lesson> lessons) {
		this.courseID = courseID;
		this.lessons = new Vector<Lesson>();
		// Create an empty lessons vector for the course.
		// If a lessons list was provided, copy all lessons into the vector.
		if (lessons != null) {
			this.lessons.addAll(lessons);
		}

		setCourseName(courseName);
		setCredits(credits);
		setCourseType(courseType);
	}

	// calculate the total hours by iterating through the lessons
	public synchronized double calculateCourseHours() {
		double totalHours = 0;

		for (Lesson lesson : lessons) {
			if (lesson.getStatus().equals("SCHEDULED") ||
					lesson.getStatus().equals("RESCHEDULED")) {
				totalHours += lesson.getDurationTime().toMinutes() / 45;
			}
		}

		return totalHours;
	}


	public int getCourseID() {
		return this.courseID;
	}


	public synchronized String getCourseName() {
		return this.courseName;
	}


	public synchronized void setCourseName(String courseName) {
		this.courseName = courseName;
	}


	public synchronized int getCredits() {
		return this.credits;
	}


	public synchronized void setCredits(int credits) {
		if (credits <= 0) {
			throw new IllegalArgumentException("Credits must be positive");
		}
		this.credits = credits;

	}


	public synchronized String getCourseType() {
		return this.courseType;
	}


	public synchronized Vector<Lesson> getLessons(){
		return this.lessons;
	}

	public synchronized void setCourseType(String courseType) {
		if (courseType.equals("elective") || 
				courseType.equals("mandatory")) {
			this.courseType = courseType;
		} else {
			throw new IllegalArgumentException("No valid course types");
		}
	}


	public synchronized void addLesson (Lesson lesson) {
		if (lesson == null) {
			throw new IllegalArgumentException("Lesson cannot be null");
		}

		lessons.add(lesson);
	}

	public synchronized void removeLesson(Lesson lesson) {

		if (lesson == null) {
			throw new IllegalArgumentException("Lesson cannot be null");
		}

		if (!lessons.remove(lesson)) {
			throw new IllegalArgumentException("Lesson not found");
		}
	}

	public String toString() 
	{
		return "Course ID: " + courseID + "\n" +
				"Course Name: " + courseName + "\n" +
				"Credits: " + credits + "\n" +
				"Course Type: " + courseType + "\n" +
				"Number of Lessons: " + lessons.size() + "\n";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Course other = (Course) obj;
		return this.courseID == other.courseID;
	}

}
