package Schedamy;

import java.time.LocalDate;
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
    
    //To string
    public String toString()
    {
        return "Group ID: " + groupID + "Department: " + department + "Study Year: " + studyYear + "Student Count: " + studentCount + "Program Name: " + programName;
    }

  //calculate hours in course
    public int calculateTotalHours(GroupEnrolment[] enrolments)
    {
        int totalHours = 0;

        for(int i = 0; i < enrolments.length; i++)
        {
            if(enrolments[i] != null)
            {
                for(int j = 0; j < enrolments[i].getCourse().getLessons().size(); j++)
                {
                    if(enrolments[i].getCourse().getLessons().get(j) != null)
                    {
                        totalHours += Duration.between(
                                enrolments[i].getCourse().getLessons().get(j).getStartTime(),
                                enrolments[i].getCourse().getLessons().get(j).getEndTime()
                        ).toMinutes() / 45;
                    }
                }
            }
        }
   
        return totalHours;
    }
    
    //calculate if the schedule over loaded
    public boolean isScheduleOverloaded(GroupEnrolment[] enrolments)
    {
    	int totalHours = calculateTotalHours(enrolments);
    	if(programName.equals("Evening"))
    	{
    		if(totalHours > 15)
    		{
    			System.out.println("The group schedule is overloaded");
    			return true;
    		}
    		System.out.println("The group schedule is not overloaded");
    		return false;
    		}
    	if(totalHours > 30)
    	{
    		System.out.println("The group schedule is overloaded");
    		return true;
    		}
    	System.out.println("The group schedule is not overloaded");
    	 return false;
    }

}

	
