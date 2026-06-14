package Schedamy;

import java.time.Duration;
public class StudentGroup
{
	private int groupID;
	private String department;
	private int studyYear;
	private int studentCount;
	private String programName;

	//Constructor
	public StudentGroup(int groupID, String department, int studyYear,int studentCount,String programName)
	{
		this.groupID = groupID; 
		this.department = department;
		this.studyYear = studyYear;
		this.studentCount = studentCount;
		this.programName = programName;
	}

	//Get Group id
	public int getGroupID()
	{
		return this.groupID ; 
	}

	//Get department
	public String getDepartment()
	{
		return this.department;
	}

	//Get study year
	public int getStudyYear()
	{
		return this.studyYear;
	}

	//Get student count
	public int getStudentCount()
	{
		return this.studentCount;
	}

	//Get program name
	public String getProgramName()
	{
		return this.programName;
	}

	//toString
	public String toString()
	{
		return "Group ID: " + groupID + "\n" +
				"Department: " + department + "\n" +
				"Study Year: " + studyYear + "\n" +
				"Program Name: " + programName + "\n" +
				"Student Count: " + studentCount + "\n" +
				"-------------------------";
	}


	//calculate hours in course
	public double calculateTotalHours(GroupEnrolment[] enrolments)
	{
		double totalHours = 0;

		for(int i = 0; i < enrolments.length; i++)
		{
			if(enrolments[i] != null)
			{
				for(int j = 0; j < enrolments[i].getCourse().getLessons().size(); j++)
				{
					Lesson lesson = enrolments[i].getCourse().getLessons().get(j);

					if(lesson != null)
					{
						totalHours += Duration.between(
								lesson.getStartTime(),
								lesson.getEndTime()
								).toMinutes() / 45.0;
					}
				}
			}
		}

		return totalHours;
	}

	// Check if the schedule is overloaded
	public boolean isScheduleOverloaded(GroupEnrolment[] enrolments)
	{
		double totalHours = calculateTotalHours(enrolments);

		if (programName.equals("Evening"))
		{
			return totalHours > 15;
		}

		return totalHours > 30;
	}


}


