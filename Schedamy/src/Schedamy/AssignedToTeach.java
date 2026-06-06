package Schedamy;


public class AssignedToTeach 
{
	
private Lecturer lecturer;
private Course course;

//Constructor 
public AssignedToTeach (Lecturer lecturer,Course course)
{
	this.lecturer = lecturer;
	this.course = course;
	
}

// get Lecturer
public Lecturer getLecturer()
{
	return this.lecturer;
}

// get Course
public Course getCourse()
{
	return this.course;
}

// calculate lecturer's  hours
public double calculateLecturerHours()
{
    return course.calculateCourseHours();
}
}
