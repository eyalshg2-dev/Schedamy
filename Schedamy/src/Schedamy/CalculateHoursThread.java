package Schedamy;

public class CalculateHoursThread extends Thread{
	
	private AssignedToTeach assignedToTeach;
	
	public CalculateHoursThread(AssignedToTeach assignedToTeach) {
		this.assignedToTeach = assignedToTeach;
	}
	
	@Override
	public void run() {
		
		double totalHours = assignedToTeach.calculateLecturerWeeklyHours();
		
		System.out.println("Total lecture hours:" + totalHours);
		
	}

}
