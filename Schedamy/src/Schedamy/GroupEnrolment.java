package Schedamy;

public class GroupEnrolment
{
    private int priorityLevel;
    private boolean attendanceRequired;
    private StudentGroup group;
    private Course course;

  //Constructor
    public GroupEnrolment(int priorityLevel, boolean attendanceRequired,StudentGroup group, Course course)
    {
        this.priorityLevel = priorityLevel;
        this.attendanceRequired = attendanceRequired;
        this.group = group;
        this.course = course;
    }
    
    //Get PriorityLevel
    public int getPriorityLevel()
    {
        return priorityLevel;
    }
    
    //Get group
    public StudentGroup getGroup()
    {
        return group;
    }
    
    //Get course
    public Course getCourse()
    {
        return course;
    }

    //To string
    public String toString()
    {
        return "Priority Level: " + priorityLevel + "Attendance Required: " + attendanceRequired + "Group: " + group.getGroupID() +"Course: " + course.getCourseName();
    }
    
    //Calculation if is mandatory
    public boolean isAttendanceRequired()
    {
        if(course.getCourseType().equals("mandatory"))
        {
            attendanceRequired = true;
        }
        else
        {
            attendanceRequired = false;
        }
        return attendanceRequired;
    }
    
    // calculation Priority
    public int calculationPriority()
    {
        int finalPriority = priorityLevel;
        if(course.getCourseType().equals("mandatory"))
        {
            finalPriority += 10;
        }
        if(group.getProgramName().equals("Morning"))
        {
            finalPriority += 5;
        }
        if(group.getStudyYear() == 3 ||
           group.getStudyYear() == 4)
        {
            finalPriority += 5;
        }
        priorityLevel = finalPriority;
        return priorityLevel;
    }

}
