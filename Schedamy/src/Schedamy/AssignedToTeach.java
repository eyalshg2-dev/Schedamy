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

// calculate lecturer's weekly hours
public double calculateLecturerWeeklyHours()
{
    int totalHours = 0;

    for (Lesson lesson : course.getLessons())
    {
        if (lesson.getStatus().equals("SCHEDULED") ||
            lesson.getStatus().equals("RESCHEDULED"))
        {
            totalHours += lesson.getDurationTime().toMinutes() / 45;
        }
    }

    return totalHours;
}
}
