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
			system.addRoom("102", 3, "Computer Lab", 3);
			
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
					LocalDate.of(2026, 6, 10),
					LocalTime.of(10, 0),
					LocalTime.of(12, 15),
					"SCHEDULED", "FRONTAL", false,
					system.getRooms().get(0));
			
			system.addLessonToCourse(2, 2,
					LocalDate.of(2026, 6, 11), 
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
			
			
			/*
			while (true)
			{
				System.out.println("\nSelect an option:");
				System.out.println("1. Add a new lecturer");
				System.out.println("2. Add a new course");
				System.out.println("3. Add a new student group");
				System.out.println("4. Add a new room"); ///???
				System.out.println("5. Add a new lesson");
				System.out.println("6. Cancel a lesson");
				System.out.println("7. Displaying a timetable for a specific group of students");
				System.out.println("8. Displaying a timetable for a specific lecturer");
				System.out.println("9. show all rooms by size");
				System.out.println("10. show all lecturers");
				System.out.println("11. Exit");
	
				int choice;
				try
				{
					choice = sc.nextInt();
				}
				catch (InputMismatchException e)
				{
					System.out.println("Please enter a number");
					sc.next();  
					continue;    					    
				}
				switch(choice)
				{
				case 1:
					break;
	
				case 3: 
					break;
					
				case 4: 
					break;
					
				case 5: 
					break;
					
				case 6: 
					System.out.println("Enter course ID: ");
					int courseID = sc.nextInt();
					
					System.out.println("Enter lesson ID: ");
					int lessonID = sc.nextInt();
					
					System.out.println("Enter new date (DD-MM-YYYY)");
					LocalDate newDate = LocalDate.parse(sc.next());
					
					system.cancelLesson(courseID, lessonID, newDate, sharedRoomLock);
					
				case 7: 
					break;
					
				case 8: 
					break;
					
				case 9: 
					break;
					
				case 10: 
					break;
	
				case 11:
					
					sc.close();
					System.exit(0);
				default:
					System.out.println("Choose 1 - 11 only");
				}
			}
			*/
		}
	}
