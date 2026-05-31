package Schedamy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Main {
	public static void main(String[] args)
	{
		System.out.println("Hiiiiii\n");
		Scanner sc = new Scanner(System.in);
		Object sharedRoomLock = new Object();
		SchedamySystem system = new SchedamySystem();

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
				System.out.println("Choose a lecturer: ");
				List<Lecturer> lecturers8 = system.getLecturers();
				if (lecturers8.isEmpty()) {
					System.out.println("No lecturers found");
					break;
				}
				
				
				for (int i = 0; i < lecturers8.size(); i++) {
                    System.out.println((i + 1) + ". " + lecturers8.get(i).toString());
                }
				
                int lecturerChoice8 = sc.nextInt() - 1;
                Lecturer selectedLecturer8 = lecturers8.get(lecturerChoice8);

                
                System.out.println("\n--- Timetable for: " + selectedLecturer8.getFirstName()
                        + " " + selectedLecturer8.getLastName() + " ---");
                for (Lesson lesson : selectedLecturer8.getLessons()) {
                    System.out.println(lesson.toString());
                }

                
                AssignedToTeach assignedToTeach = null;
                for (AssignedToTeach att : system.getAssignedToTeachList()) {
                    if (att.getLecturer().getLecturerID() == selectedLecturer8.getLecturerID()) {
                        assignedToTeach = att;
                        break;
                    }
                }

                if (assignedToTeach == null) {
                    System.out.println("No courses assigned to this lecturer");
                    break;
                }

                try {
                    CalculateHoursThread hoursThread = new CalculateHoursThread(assignedToTeach);
                    hoursThread.start();
                    hoursThread.join();
                    
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted: " + e.getMessage());
                }
				break;
				
			case 9: 
				break;
				
			case 10: 
				break;

			case 11:
				//////////////Save data
				sc.close();
				System.exit(0);
			default:
				System.out.println("Choose 1 - 11 only");
			}
		}
	}
}
