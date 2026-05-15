package Schedamy;

import java.time.LocalDate;
import java.time.LocalTime;

public class Lesson {

	private LocalDate lessonDate;
	private LocalTime startTime;
	private LocalTime endTime;
	
	public LocalDate getLessonDate()
	{
	    return lessonDate;
	}

	public LocalTime getStartTime()
	{
	    return startTime;
	}

	public LocalTime getEndTime()
	{
	    return endTime;
	}
}
