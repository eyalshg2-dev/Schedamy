package Schedamy;

import java.util.ArrayList;
import java.util.Vector;

public class Lecturer {

	private int lecturerID;
	private String firstName;
	private String lastName; 
	private ArrayList<String> specializations ;
	private double teachingScore;  
	private double FTE;
	private static final int FULL_TIME_HOURS = 40;
	private Vector<Lesson> lessons;
	
	//Constructor
	public Lecturer(int lecturerID, String firstName, String lastName, ArrayList<String> specializations, double teachingScore, double FTE)
		{
			this.lecturerID = lecturerID;
			this.firstName = firstName;
			this.lastName = lastName;
			this.specializations = specializations;
			this.teachingScore = teachingScore;
			this.FTE = FTE;
			lessons = new Vector<>();
		}	
	// get lecturerId
	public int getLecturerID()
	{
		return this.lecturerID;
	}
	
	// get firstName
	public String getFirstName()
	{
		return this.firstName;
	}
	
	// get lastName
	public String getLastName()
	{
		return this.lastName;
	}
	
	// get specialization
	public ArrayList<String> getSpecializations()
	{
		return this.specializations;
	}
	
	// get teachingScore
	public double getTeachingScore()
	{
		return this.teachingScore;
	}
	
	// get FTE
	public double getFTE()
	{
		return this.FTE;
	}
	    
	//To String
	public String toString() 
	{
	    return "Lecturer ID: " + lecturerID + "\n" +
	           "Name: " + firstName + " " + lastName + "\n" +
	           "Specializations: " + specializations + "\n" +
	           "FTE: " + FTE + "\n" +
	           "Teaching Score: " + teachingScore + "\n" +
	           "-------------------------";
	}

	
	// calculate how many hours difference exists
	public double calculateWeeklyLoadDiff()
	{
		double requiredHours = FTE*FULL_TIME_HOURS;
		double actualHours = calculateLecturerWeeklyHours();
		return requiredHours-actualHours;
	}	
	
	private double calculateLecturerWeeklyHours() {
		// TODO Auto-generated method stub
		return 0;
	}
	//  did Lecturer meet required workload?
	public boolean hasRequiredHours()
	{
			return (calculateWeeklyLoadDiff()<=0);
			
	}
	
	
	public Vector<Lesson> getLessons() {
		return lessons;
	}
	
	
	public void addLesson(Lesson lesson) {
		lessons.add(lesson);
	}

}
