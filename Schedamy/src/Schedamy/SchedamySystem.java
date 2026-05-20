package Schedamy;

import java.util.ArrayList;
import java.util.List;

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

    public void addCourse(Course course)
    {
        courses.add(course);
    }

    public void addLecturer(Lecturer lecturer)
    {
        lecturers.add(lecturer);
    }

    public void addRoom(Room room)
    {
        rooms.add(room);
    }
 
    public void addRoomReservation(RoomResrvation roomReservation)
    {
        roomReservations.add(roomReservation);
    }

    public void addStudentGroup(StudentGroup studentGroup)
    {
        studentGroups.add(studentGroup);
    }

    public void addGroupEnrolment(GroupEnrolment groupEnrolment)
    {
        groupEnrolments.add(groupEnrolment);
    }

    public void addAssignedToTeach(AssignedToTeach assignedToTeach)
    {
        assignedToTeachList.add(assignedToTeach);
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
}