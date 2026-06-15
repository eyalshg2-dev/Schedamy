package Schedamy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args)
	{
		SchedamySystem system = new SchedamySystem();

		//Sample rooms
		system.addRoom("101", 3, "Classroom", 40);
		system.addRoom("102", 3, "Computer Lab", 50);

		//Sample lecturers
		ArrayList<String> specs1 = new ArrayList<>();
		specs1.add("Signals");
		specs1.add("Physics");
		system.addLecturer("123456789", "Drake", "Bell", specs1, 90, 1.0);

		ArrayList<String> specs2 = new ArrayList<>();
		specs2.add("Algorithms");
		specs2.add("Electronics");
		system.addLecturer("345678912", "Josh", "Peck", specs2, 80, 0.75);

		//sample student groups
		system.addStudentGroup(1, "Computer Engineering", 3, 35, "Morning");
		system.addStudentGroup(2, "Electrical Engineering", 2, 28, "Evening");

		//Sample courses
		system.addCourse(1, "Physics", 3, "mandatory", "123456789", 1);
		system.addCourse(2, "Algorithms", 4, "mandatory", "345678912", 2);

		//sample lessons
		system.addLessonToCourse(1, 1,
				LocalDate.of(2026, 6, 25),
				LocalTime.of(10, 0),
				LocalTime.of(12, 15),
				"SCHEDULED", "FRONTAL", false,
				system.getRooms().get(0));

		system.addLessonToCourse(2, 2,
				LocalDate.of(2026, 6, 28), 
				LocalTime.of(10, 0), 
				LocalTime.of(12, 15), 
				"SCHEDULED", "ZOOM", false, null);

		System.out.println("=== Schedamy System Initialized ===\n");

		System.out.println("--- Rooms --- ");
		for (Room room : system.getRooms())
			System.out.println(room.toString());
		System.out.println();

		System.out.println("--- Lecturers --- ");
		for (Lecturer lecturer : system.getLecturers())
			System.out.println(lecturer.toString());
		System.out.println();

		System.out.println("--- Student Groups --- ");
		for (StudentGroup group  : system.getStudentGroups())
			System.out.println(group.toString());
		System.out.println();


		System.out.println("--- Courses --- ");
		for (Course course : system.getCourses())
			System.out.println(course.toString());
		System.out.println();


		System.out.println("--- Lessons --- ");
		for (Course course : system.getCourses()) 
			for (Lesson lesson : course.getLessons())
				System.out.println(lesson.toString());
		System.out.println();

		//launch GUI
		System.out.println("=== LAUNCHING GUI ===");
		SwingUtilities.invokeLater(() -> {
			new SchedamyGUI(system);
		});


	}
}