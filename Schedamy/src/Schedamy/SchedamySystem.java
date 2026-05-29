package Schedamy;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Vector;

public class SchedamySystem 
{
    private List<Course> courses;
    private List<Lecturer> lecturers;
    private List<Room> rooms;
    private List<RoomResrvation> roomReservations;
    private List<StudentGroup> studentGroups;
    private List<GroupEnrolment> groupEnrolments;
    private List<AssignedToTeach> assignedToTeachList;

    public SchedamySystem()
    {
        courses = new ArrayList<Course>();
        lecturers = new ArrayList<Lecturer>();
        rooms = new ArrayList<Room>();
        roomReservations = new ArrayList<RoomResrvation>();
        studentGroups = new ArrayList<StudentGroup>();
        groupEnrolments = new ArrayList<GroupEnrolment>();
        assignedToTeachList = new ArrayList<AssignedToTeach>();
    }

    public void addCourse(int courseID, String courseName,int credits, String courseType,int lecturerID, int groupID)
    {
    	if (courseName == null || courseName.isEmpty())
    		throw new IllegalArgumentException("Invalid course name");
    	if (credits <= 0)
    		throw new IllegalArgumentException("Invalid credits");
    	Course course = new Course(courseID,courseName,credits,courseType,new Vector<Lesson>());
    	
    	Lecturer lecturer = findLecturerById(lecturerID);
    	StudentGroup group = findStudentGroupById(groupID);
    	if (lecturer == null)
    	    throw new IllegalArgumentException("Lecturer not found");
    	if (group == null)
    	    throw new IllegalArgumentException("Student group not found");
    	courses.add(course);
    	AssignedToTeach assigned = new AssignedToTeach(lecturer, course);
    	assignedToTeachList.add(assigned);
    	GroupEnrolment enrolment = new GroupEnrolment(group, course);
    	groupEnrolments.add(enrolment);
    }

    public void addLecturer(int id, String firstName, String lastName,ArrayList<String> specializations,double teachingScore,double fte)
    {
    	// ID must be 9 digits
    	if (String.valueOf(id).length() != 9) 
    		throw new IllegalArgumentException("ID must contain 9 digits");
            	for (Lecturer l : lecturers) 
    	{
    	    if (l.getLecturerID() == id)
    	        throw new IllegalArgumentException("This ID already exists");
    	}
    	// First name only letters
    	if (!firstName.matches("[a-zA-Z]+"))
    		throw new IllegalArgumentException("First name must contain only letters");
    	// Last name only letters
    	if (!lastName.matches("[a-zA-Z]+"))
    		throw new IllegalArgumentException("Last name must contain only letters");
    	Lecturer lecturer = new Lecturer(id,firstName,lastName,specializations,teachingScore,fte);
    	// FTE Between 0-100
    	if (fte < 0 || fte > 100)
    	    throw new IllegalArgumentException("FTE must be a number between 0-100");
    	if (specializations.isEmpty())
    	    throw new IllegalArgumentException("Choose at least one specialization");
    	lecturers.add(lecturer);
    }

    public void addRoom(int roomNumber, int building,String roomType, int capacity,String equipment)
    {
    	if (roomNumber <= 0)
    		throw new IllegalArgumentException("Invalid room number");
    	if (building <= 0)
    		throw new IllegalArgumentException("Invalid building");
    	if (capacity <= 0)
    		throw new IllegalArgumentException("Invalid capacity");
    	Room room = new Room(roomNumber,building,roomType,capacity,equipment,"AVAILABLE");
    	rooms.add(room);
    }
 
    public void addRoomReservation(RoomResrvation roomReservation)
    {
        roomReservations.add(roomReservation);
    }

    public void addStudentGroup(int groupID, String department,int studyYear, int studentCount,String programName)
    {
    	if (studentCount <= 0)
    		throw new IllegalArgumentException("Invalid student count");
    	StudentGroup group = new StudentGroup(groupID,department,studyYear,studentCount,programName);
    	studentGroups.add(group);
    }
    public void addGroupEnrolment(GroupEnrolment groupEnrolment)
    {
        groupEnrolments.add(groupEnrolment);
    }

    public void addAssignedToTeach(AssignedToTeach assignedToTeach)
    {
        assignedToTeachList.add(assignedToTeach);
    }
    
    //add lessons to course
    public void addLessonToCourse(int courseID, int lessonID,LocalDate lessonDate,LocalTime startTime,LocalTime endTime,String status,String teachingMode,boolean labRoomRequired)
    {
    	Course course = findCourseById(courseID);
    	if (course == null)
    		throw new IllegalArgumentException("Course not found");
    	Lesson lesson = new Lesson(lessonID,lessonDate,startTime,endTime,status,teachingMode,labRoomRequired,new Vector<StudentGroup>());
    	for (Lesson existingLesson : course.getLessons())
    	{
    		if (existingLesson.getLessonDate().equals(lesson.getLessonDate()))
    		{
    			boolean overlap =lesson.getStartTime().isBefore(existingLesson.getEndTime()) &&lesson.getEndTime().isAfter(existingLesson.getStartTime());
    			if (overlap)
    				throw new IllegalArgumentException("This course already has a lesson at this time");
    			}
    		}
    	course.getLessons().add(lesson);
    	}

    public List<Course> getCourses()
    {
        return courses;
    }

    public List<Lecturer> getLecturers()
    {
        return lecturers;
    }

    public List<Room> getRooms()
    {
        return rooms;
    }

    public List<RoomResrvation> getRoomReservations()
    {
        return roomReservations;
    }

    public List<StudentGroup> getStudentGroups()
    {
        return studentGroups;
    }

    public List<GroupEnrolment> getGroupEnrolments()
    {
        return groupEnrolments;
    }

    public List<AssignedToTeach> getAssignedToTeachList()
    {
        return assignedToTeachList;
    }
    //find lecturer by id
    private Lecturer findLecturerById(int lecturerID) {
        for (Lecturer lecturer : lecturers) {
            if (lecturer.getLecturerID() == lecturerID)
                return lecturer;
        }

        return null;
    }
    //find student group by id
    private StudentGroup findStudentGroupById(int groupID) {
        for (StudentGroup group : studentGroups) {
            if (group.getGroupID() == groupID)
                return group;
        }
        return null;
    }
    //get lecturer name and student group for course
    public String getCourseInfo(Course course)
    {
        String info = "";

        for (AssignedToTeach assigned : assignedToTeachList)
        {
            if (assigned.getCourse().equals(course))
            {
                Lecturer lecturer = assigned.getLecturer();

                info += "Lecturer: " +
                        lecturer.getFirstName() + " " +
                        lecturer.getLastName();
            }
        }

        for (GroupEnrolment enrolment : groupEnrolments)
        {
            if (enrolment.getCourse().equals(course))
            {
                info += "\n Student Group: " +
                        enrolment.getGroup().getDepartment()+ "Year :" + enrolment.getGroup().getStudyYear();
            }
        }

        return info;
    }
    //find course by ID
    private Course findCourseById(int courseID)
    {
        for (Course course : courses)
        {
            if (course.getCourseID() == courseID)
                return course;
        }

        return null;
    }
}
