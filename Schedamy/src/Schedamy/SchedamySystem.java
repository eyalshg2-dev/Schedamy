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
    private Lecturer findLecturerById(String lecturerID) {
        for (Lecturer lecturer : lecturers) {
        	if (lecturer.getLecturerID().equals(lecturerID))
                return lecturer;
        }

        return null;
    }
    //add course
    public void addCourse(int courseID, String courseName,int credits, String courseType,String lecturerID, int groupID)
    {	
    	for (Course existing : courses) 
    	{
    	    if (existing.getCourseName().equalsIgnoreCase(courseName)) 
    	    {
    	        Lecturer existingLecturer = getLecturerForCourse(existing);
    	        StudentGroup existingGroup = getGroupForCourse(existing);

    	        if (existingLecturer != null &&
    	            existingGroup != null &&
    	            existingLecturer.getLecturerID().equals(lecturerID) &&
    	            existingGroup.getGroupID() == groupID)
    	        {
    	            throw new IllegalArgumentException(
    	                "This course already exists for this lecturer and student group");
    	        }
    	    }
    	}
    	if (courseName == null || courseName.trim().isEmpty())
    	    throw new IllegalArgumentException("Course name cannot be empty");

    	if (courseName.matches("\\d+"))
    	    throw new IllegalArgumentException("Course name cannot contain only numbers");

    	if (credits <= 0)
    	    throw new IllegalArgumentException("Credits must be greater than 0");
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

    //add lecturer
    public void addLecturer(String id, String firstName, String lastName,ArrayList<String> specializations,double teachingScore,double fte)
    {
    	// ID must be 9 digits
    	if (String.valueOf(id).length() != 9) 
    		throw new IllegalArgumentException("ID must contain 9 digits");
            	for (Lecturer l : lecturers) 
    	{
            		if (l.getLecturerID().equals(id))
    	        throw new IllegalArgumentException("This ID already exists");
    	}
    	// First name only letters
    	if (!firstName.matches("[a-zA-Z ]+"))
    		throw new IllegalArgumentException("First name must contain only letters");
    	// Last name only letters
    	if (!lastName.matches("[a-zA-Z ]+"))
    		throw new IllegalArgumentException("Last name must contain only letters");
    	Lecturer lecturer = new Lecturer(id,firstName,lastName,specializations,teachingScore,fte);
    	// FTE Between 0-100
    	if (fte < 0 || fte > 100)
    	    throw new IllegalArgumentException("FTE must be a number between 0-100");
    	if (specializations.isEmpty())
    	    throw new IllegalArgumentException("Choose at least one specialization");
    	lecturers.add(lecturer);
    }

    public void addRoom(String roomNumber, int building,String roomType, int capacity)
    {
    	if (roomNumber == null || roomNumber.isEmpty())
    	    throw new IllegalArgumentException("Invalid room number");
    	if (building <= 0)
    		throw new IllegalArgumentException("Invalid building");
    	if (capacity <= 0)
    		throw new IllegalArgumentException("Invalid capacity");
    	  for (Room existingRoom : rooms)
    	    {
    	        if (existingRoom.getRoomID().equals(roomNumber) &&
    	            existingRoom.getBuilding() == building)
    	        {
    	            throw new IllegalArgumentException("This room already exists in this building");
    	        }
    	    }
    	Room room = new Room(roomNumber,building,roomType,capacity,"AVAILABLE");
    	rooms.add(room);
    }
    //room available
    private boolean isRoomAvailable(Room room, Lesson lesson)
    {
        for (RoomResrvation reservation : roomReservations) {
            if (reservation.overlaps(room, lesson)) {
                return false;
            }
        }

        return true;
    }
    //add room reservation
    public void addRoomReservation(RoomResrvation roomReservation)
    {
        roomReservations.add(roomReservation);
    }
    
     //add student group
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
    		LocalTime localDate, LocalTime localDate2, String status,
    		String teachingMode, boolean labRoomRequired, Room room)
    {
    	
    	Course course = findCourseById(courseID);
    	if (course == null)
    		throw new IllegalArgumentException("Course not found");
    	Lesson lesson = new Lesson(lessonID,lessonDate,localDate,localDate2,status,teachingMode,labRoomRequired,new Vector<StudentGroup>());
    	StudentGroup group = getGroupForCourse(course);
    	if (room != null &&group != null &&room.getCapacity()< group.getStudentCount())
    	{
    	    throw new IllegalArgumentException("Room is too small for this student group");
    	    
    	}
    	for (Lesson existingLesson : course.getLessons())
    	{
    		if (existingLesson.getLessonDate().equals(lesson.getLessonDate()))
    		{
    			boolean overlap = !lesson.getStartTime().isAfter(existingLesson.getEndTime()) && !lesson.getEndTime().isBefore(existingLesson.getStartTime());
    			if (overlap)
    				throw new IllegalArgumentException("Time slot unavailable");
    			}
    		}
    	
    	if (room != null)
    	{
    	    if (!isRoomAvailable(room, lesson))
    	    {
    	        throw new IllegalArgumentException("This room is already reserved at this time");
    	    }
    	   
    	    lesson.setRoom(room);
    	    room.setStatus("SCHEDULED");
    	    roomReservations.add(new RoomResrvation(room, lesson, lesson.getDurationTime()));
    	    }
    	
    	StudentGroup group1 = getGroupForCourse(course);
    	if (group1 != null) {
    	    for (GroupEnrolment enrolment : groupEnrolments) {
    	        if (enrolment.getGroup().equals(group1) &&
    	            !enrolment.getCourse().equals(course)) {
    	            for (Lesson existingLesson : enrolment.getCourse().getLessons()) {
    	                if (existingLesson.getStatus().equalsIgnoreCase("CANCELLED"))
    	                    continue;
    	                if (existingLesson.getLessonDate().equals(lesson.getLessonDate())) {
    	                    boolean overlap = !lesson.getStartTime().isAfter(existingLesson.getEndTime()) &&
    	                                     !lesson.getEndTime().isBefore(existingLesson.getStartTime());
    	                    if (overlap) {
    	                        throw new IllegalArgumentException(
    	                            "Student group already has a lesson at this time in another course"
    	                        );
    	                    }
    	                }
    	            }
    	        }
    	    }
    	}
    	
    	// Check conflicts for the same lecturer
    	for (AssignedToTeach assigned : assignedToTeachList) {
    	    if (assigned.getLecturer().equals(getLecturerForCourse(course)) &&
    	        !assigned.getCourse().equals(course)) {
    	        for (Lesson existingLesson : assigned.getCourse().getLessons()) {
    	            if (existingLesson.getStatus().equalsIgnoreCase("CANCELLED"))
    	                continue;
    	            if (existingLesson.getLessonDate().equals(lesson.getLessonDate())) {
    	                boolean overlap = !lesson.getStartTime().isAfter(existingLesson.getEndTime()) &&
    	                                 !lesson.getEndTime().isBefore(existingLesson.getStartTime());
    	                if (overlap) {
    	                    throw new IllegalArgumentException(
    	                        "Lecturer already has a lesson at this time in another course"
    	                    );
    	                }
    	            }
    	        }
    	    }
    	}
    	
    	course.getLessons().add(lesson);
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
                        lecturer.getLastName() + "\n";
                info += "\n";
            }
        }

        for (GroupEnrolment enrolment : groupEnrolments)
        {
            if (enrolment.getCourse().equals(course))
            {
                StudentGroup group = enrolment.getGroup();

                info += "Student Group:\n" +
                        group.getDepartment() +
                        " (Year " + group.getStudyYear() +
                        ", " + group.getProgramName() + ")\n";
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
    //get student group for course
    public StudentGroup getGroupForCourse(Course course)
    {
        for (GroupEnrolment enrolment : groupEnrolments)
        {
            if (enrolment.getCourse().equals(course))
            {
                return enrolment.getGroup();
            }
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
        		    lecturer.getLastName() + "," +
        		    lecturer.getTeachingScore() + "," +
        		    lecturer.getFTE() + "," +
        		    String.join(";", lecturer.getSpecializations()));
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
                room.getCapacity() + "," + room.getStatus());
            bw.newLine();
        }

        bw.write("COURSES");
        bw.newLine();

        for (Course course : courses) {
            bw.write(
                course.getCourseID() + "," +
                course.getCourseName() + "," +
                course.getCredits() + "," +
                course.getCourseType()
            );
            bw.newLine();
        }

        bw.write("ASSIGNED_TO_TEACH");
        bw.newLine();

        for (AssignedToTeach assigned : assignedToTeachList) {
            bw.write(
                assigned.getLecturer().getLecturerID() + "," +
                assigned.getCourse().getCourseID()
            );
            bw.newLine();
        }

        bw.write("GROUP_ENROLMENTS");
        bw.newLine();

        for (GroupEnrolment enrolment : groupEnrolments) {
            bw.write(
                enrolment.getGroup().getGroupID() + "," +
                enrolment.getCourse().getCourseID()
            );
            bw.newLine();
        }

        bw.write("LESSONS");
        bw.newLine();

        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                String roomID = "NONE";

                if (lesson.getRoom() != null) {
                    roomID = lesson.getRoom().getRoomID();
                }

                bw.write(
                    course.getCourseID() + "," +
                    lesson.getLessonID() + "," +
                    lesson.getLessonDate() + "," +
                    lesson.getStartTime() + "," +
                    lesson.getEndTime() + "," +
                    lesson.getStatus() + "," +
                    lesson.getTeachingMode() + "," +
                    lesson.isLabRoomRequired() + "," +
                    roomID
                );
                bw.newLine();
            }
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
        courses.clear();
        assignedToTeachList.clear();
        groupEnrolments.clear();
        roomReservations.clear();

        String line;
        String currentSection = "";

        while ((line = br.readLine()) != null)
        {
            if (line.equals("LECTURERS") ||
                line.equals("STUDENT_GROUPS") ||
                line.equals("ROOMS") ||
                line.equals("COURSES") ||
                line.equals("ASSIGNED_TO_TEACH") ||
                line.equals("GROUP_ENROLMENTS") ||
                line.equals("LESSONS"))
            {
                currentSection = line;
                continue;
            }

            String[] parts = line.split(",", -1);

            if (currentSection.equals("LECTURERS"))
            {
            	String id = parts[0];
                String firstName = parts[1];
                String lastName = parts[2];
                double teachingScore = Double.parseDouble(parts[3]);
                double fte = Double.parseDouble(parts[4]);
                ArrayList<String> specializations = new ArrayList<String>();

                String[] specs = parts[5].split(";");

                for (String spec : specs)
                {
                    specializations.add(spec);
                }

                lecturers.add(new Lecturer(id,firstName,lastName,specializations,teachingScore,fte));
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
                String status = parts[4];

                rooms.add(
                    new Room(roomID,building,roomType,capacity,status));
            }

            else if (currentSection.equals("COURSES"))
            {
                int courseID = Integer.parseInt(parts[0]);
                String courseName = parts[1];
                int credits = Integer.parseInt(parts[2]);
                String courseType = parts[3];

                courses.add(
                    new Course(
                        courseID,
                        courseName,
                        credits,
                        courseType,
                        new Vector<Lesson>()
                    )
                );
            }

            else if (currentSection.equals("ASSIGNED_TO_TEACH"))
            {
                String lecturerID = parts[0];
                int courseID = Integer.parseInt(parts[1]);

                Lecturer lecturer = findLecturerById(lecturerID);
                Course course = findCourseById(courseID);

                if (lecturer != null && course != null) {
                    assignedToTeachList.add(
                        new AssignedToTeach(lecturer, course)
                    );
                }
            }

            else if (currentSection.equals("GROUP_ENROLMENTS"))
            {
                int groupID = Integer.parseInt(parts[0]);
                int courseID = Integer.parseInt(parts[1]);

                StudentGroup group = findStudentGroupById(groupID);
                Course course = findCourseById(courseID);

                if (group != null && course != null) {
                    groupEnrolments.add(
                        new GroupEnrolment(group, course)
                    );
                }
            }

            else if (currentSection.equals("LESSONS"))
            {
                int courseID = Integer.parseInt(parts[0]);
                int lessonID = Integer.parseInt(parts[1]);
                LocalDate date = LocalDate.parse(parts[2]);
                LocalTime start = LocalTime.parse(parts[3]);
                LocalTime end = LocalTime.parse(parts[4]);
                String status = parts[5];
                String mode = parts[6];
                boolean labRequired = Boolean.parseBoolean(parts[7]);
                String roomID = parts[8];

                Course course = findCourseById(courseID);
                Room room = null;

                if (!roomID.equals("NONE")) {
                    room = findRoomById(roomID);
                }

                if (course != null) {
                    Lesson lesson = new Lesson(
                        lessonID,
                        date,
                        start,
                        end,
                        status,
                        mode,
                        labRequired,
                        new Vector<StudentGroup>()
                    );
                    
                    lesson.setRoom(room);
                    course.addLesson(lesson);
                    if (room != null) {
                        roomReservations.add(
                            new RoomResrvation(
                                room,
                                lesson,
                                lesson.getDurationTime()
                            )
                        );
                    }
                }
            }
        }

        br.close();
    }
    private Room findRoomById(String roomID)
    {
        for (Room room : rooms) {
            if (room.getRoomID().equals(roomID)) {
                return room;
            }
        }

        return null;
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
    	Lecturer lecturer = getLecturerForCourse(course);
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
    			new Vector<>(rooms),
    			roomLock,
    			new Vector<>(groupEnrolments), true));
    	
    	availabilityThread.start();
    }
   //get lecturer total hours
   public double calculateLecturerActualHours(Lecturer lecturer) {
	    double actualHours = 0;

	    for (AssignedToTeach assigned : assignedToTeachList) {
	        if (assigned.getLecturer().equals(lecturer)) {
	            actualHours += assigned.calculateLecturerHours();
	        }
	    }

	    return actualHours;
	}
   //Adapting a course to the group
   public boolean isCourseRelevantToGroup(String courseName, StudentGroup group)
   {
       String department = group.getDepartment();
       
         //Course for everyone
       if (courseName.equals("Mathematics") ||
           courseName.equals("Physics"))
           return true;

       if (courseName.equals("Java") ||
           courseName.equals("Databases") ||
           courseName.equals("Software Engineering") ||
           courseName.equals("Algorithms"))
           return department.equals("Computer Engineering") ||
                  department.equals("Computer Science");

       if (courseName.equals("Electronics") ||
           courseName.equals("Signals"))
           return department.equals("Electrical Engineering") ||
                  department.equals("Computer Engineering");

       if (courseName.equals("Industrial Engineering"))
           return department.equals("Industrial Engineering and Management");

       if (courseName.equals("Economics") ||
           courseName.equals("Business Administration"))
           return department.equals("Economics and Business Administration") ||
                  department.equals("Industrial Engineering and Management");

       if (courseName.equals("Psychology"))
           return department.equals("Psychology") ||
                  department.equals("Nursing");

       if (courseName.equals("Nursing"))
           return department.equals("Nursing");

       return false;
   }
   
  
   //GET ROOM LOAD
   public double calculateRoomLoad(Room room)
   {
       double totalLoad = 0;

       for (RoomResrvation reservation : roomReservations)
       {
           if (reservation.getRoom().equals(room))
           {
               totalLoad += reservation.getOccupancyTime().toMinutes() / 45;
           }
       }

       return totalLoad;
   }
   //student group load
   public boolean isStudentGroupOverloaded(Course course)
   {
       StudentGroup group = getGroupForCourse(course);

       if (group == null)
           return false;

       ArrayList<GroupEnrolment> groupCourses = new ArrayList<GroupEnrolment>();

       for (GroupEnrolment enrolment : groupEnrolments)
       {
           if (enrolment.getGroup().equals(group))
           {
               groupCourses.add(enrolment);
           }
       }

       return group.isScheduleOverloaded(
               groupCourses.toArray(new GroupEnrolment[groupCourses.size()])
       );
   }
   //REMOVE LESSONS
   public void removeLesson(Course course, Lesson lesson)
   {
       roomReservations.removeIf(
               reservation -> reservation.getLesson().equals(lesson)
       );

       if (lesson.getRoom() != null)
       {
           lesson.getRoom().setStatus("AVAILABLE");
       }

       course.getLessons().remove(lesson);
   }
   
   //FIND A LECTURER BY COURSE
   public Lecturer getLecturerForCourse(Course course) {
	    for (AssignedToTeach assigned : assignedToTeachList) {
	        if (assigned.getCourse().equals(course)) {
	            return assigned.getLecturer();
	        }
	    }
	    return null;
	}

public String getGroupForCourse(int courseID) {
	Course course = findCourseById(courseID);
    if (course == null) return "Unknown";
    
    for (GroupEnrolment enrolment : groupEnrolments) {
        if (enrolment.getCourse().equals(course)) {
            StudentGroup group = enrolment.getGroup();
            return group.getDepartment() + " Year " + group.getStudyYear();
        }
    }
    return "No group found";
}   
}
