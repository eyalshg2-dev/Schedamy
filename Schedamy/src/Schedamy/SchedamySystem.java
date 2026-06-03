package Schedamy;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;


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

    public void addRoom(String roomNumber, int building,String roomType, int capacity,String equipment)
    {
    	if (roomNumber == null || roomNumber.isEmpty())
    	    throw new IllegalArgumentException("Invalid room number");
    	if (building <= 0)
    		throw new IllegalArgumentException("Invalid building");
    	if (capacity <= 0)
    		throw new IllegalArgumentException("Invalid capacity");
    	Room room = new Room(roomNumber,building,roomType,capacity,equipment,"AVAILABLE");
    	rooms.add(room);
    }
    private boolean isRoomAvailable(Room room, Lesson lesson)
    {
        for (RoomResrvation reservation : roomReservations) {
            if (reservation.overlaps(room, lesson)) {
                return false;
            }
        }

        return true;
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
    public void addLessonToCourse(int courseID, int lessonID, LocalDate lessonDate,
    		LocalTime startTime, LocalTime endTime, String status,
    		String teachingMode, boolean labRoomRequired, Room room)
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
    	if (room != null) {
    	    if (!isRoomAvailable(room, lesson)) {
    	        throw new IllegalArgumentException("This room is already reserved at this time");
    	    }

    	    lesson.setRoom(room);

    	    roomReservations.add(
    	        new RoomResrvation(room, lesson, lesson.getDurationTime())
    	    );
    	}
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
    
    public void saveDataToFile() throws IOException
    {
        File file = new File("schedamyData.txt");

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("LECTURERS");
        bw.newLine();

        for (Lecturer lecturer : lecturers) {
            bw.write(
                lecturer.getLecturerID() + "," +
                lecturer.getFirstName() + "," +
                lecturer.getLastName()
            );
            bw.newLine();
        }

        bw.write("STUDENT_GROUPS");
        bw.newLine();

        for (StudentGroup group : studentGroups) {
            bw.write(
                group.getGroupID() + "," +
                group.getDepartment() + "," +
                group.getStudyYear() + "," +
                group.getStudentCount() + "," +
                group.getProgramName()
            );
            bw.newLine();
        }

        bw.write("ROOMS");
        bw.newLine();

        for (Room room : rooms) {
            bw.write(
                room.getRoomID() + "," +
                room.getBuilding() + "," +
                room.getRoomType() + "," +
                room.getCapacity() + "," +
                room.getSpecialEquipment()
            );
            bw.newLine();
        }

        bw.close();
    }
    
    public void loadDataFromFile() throws IOException
    {
        File file = new File("schedamyData.txt");

        if (!file.exists()) {
            throw new IOException("File does not exist");
        }

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        lecturers.clear();
        studentGroups.clear();
        rooms.clear();

        String line;
        String currentSection = "";

        while ((line = br.readLine()) != null)
        {
            if (line.equals("LECTURERS") ||
                line.equals("STUDENT_GROUPS") ||
                line.equals("ROOMS"))
            {
                currentSection = line;
                continue;
            }

            String[] parts = line.split(",", -1);

            if (currentSection.equals("LECTURERS"))
            {
                int id = Integer.parseInt(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];

                lecturers.add(
                    new Lecturer(
                        id,
                        firstName,
                        lastName,
                        new ArrayList<String>(),
                        0,
                        100
                    )
                );
            }

            else if (currentSection.equals("STUDENT_GROUPS"))
            {
                int groupID = Integer.parseInt(parts[0]);
                String department = parts[1];
                int studyYear = Integer.parseInt(parts[2]);
                int studentCount = Integer.parseInt(parts[3]);
                String programName = parts[4];

                studentGroups.add(
                    new StudentGroup(
                        groupID,
                        department,
                        studyYear,
                        studentCount,
                        programName
                    )
                );
            }

            else if (currentSection.equals("ROOMS"))
            {
                String roomID = parts[0];
                int building = Integer.parseInt(parts[1]);
                String roomType = parts[2];
                int capacity = Integer.parseInt(parts[3]);
                String equipment = parts[4];

                rooms.add(
                    new Room(
                        roomID,
                        building,
                        roomType,
                        capacity,
                        equipment,
                        "AVAILABLE"
                    )
                );
            }
        }

        br.close();
    }
    //cancel the lesson using Availability thread
   public void cancelLesson(int courseID, int lessonID, LocalDate newDate,
    			Object roomLock) {
    	
    	//Find the course we wish to cancel
    	Course course = findCourseById(courseID);
    	if (course == null) {
    		throw new IllegalArgumentException("Course not found");
    	}
    	
    	//find the lesson inside the course
    	Lesson lessonToCancel = null;
    	for (Lesson lesson : course.getLessons()) {
    		if (lesson.getLessonID() == lessonID) {
    			lessonToCancel = lesson;
    			break;
    		}
    	}
    	
    	if (lessonToCancel == null) {
    		throw new IllegalArgumentException("Lesson not found");
    	}
    	
    	//find the lecturer for this course
    	Lecturer lecturer = null;
    	for (AssignedToTeach assigned : assignedToTeachList) {
    		if (assigned.getCourse().equals(course)) {
    			lecturer = assigned.getLecturer();
    			break;
    		}
    	}
    	
    	if (lecturer == null) {
    		throw new IllegalArgumentException("Lecturer not found for this course");
    	}
    	
    	//cancel the lesson
    	lessonToCancel.setStatus("CANCELLED");
    	
    	// start availabilityThread
    	Thread availabilityThread = new Thread(new AvailabilityThread(
    			lecturer,
    			lessonToCancel,
    			newDate,
    			(Vector <Room>) rooms,
    			roomLock,
    			(Vector <GroupEnrolment>) groupEnrolments));
    	
    	availabilityThread.start();

    }
}
