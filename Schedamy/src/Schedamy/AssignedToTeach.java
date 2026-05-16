package Schedamy;

import java.util.Vector;

public class AssignedToTeach 
{
	
private Lecturer lecturer;
private Vector<Course> courses;

//Constructor
public AssignedToTeach (Lecturer lecturer, Vector<Course> courses)
{
	this.lecturer = lecturer;
	this.courses = courses;
	
}

// get Lecturer
public Lecturer getLecturer()
{
	return this.lecturer;
}

// get Course
public Vector<Course> getCourses()
{
	return this.courses;
}

// calculate lecturer's weekly hours
public double calculateLecturerWeeklyHours()
{
    double totalHours = 0;
    for (Course course : courses)
    {
        for (Lesson lesson : course.getLessons()) 
        {
            if (lesson.getStatus().equals("SCHEDULED") ||
                lesson.getStatus().equals("RESCHEDULED")) 
                {
                totalHours += lesson.getDurationTime();
                }
        }
    }

    return totalHours;
}
}
