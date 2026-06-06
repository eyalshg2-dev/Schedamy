package Schedamy;

public class CalculateHoursThread extends Thread{
	
	private AssignedToTeach assignedToTeach;
	private double totalHours;
	
	public CalculateHoursThread(AssignedToTeach assignedToTeach) {
		this.assignedToTeach = assignedToTeach;
	}
	
	@Override
	public void run() {
		
		try {
			System.out.println("Calculating hours...");
			Thread.sleep(1500);
			
			totalHours = assignedToTeach.calculateLecturerHours();
			System.out.println("Calculation Complete!");
			System.out.println("Total lecture hours:" + totalHours);		
		} catch (Exception e) {
			System.out.println("[CalculateHoursThread] Error: " + e.getMessage());
		}
		
	}

	public double getTotalHours() {
		return totalHours;
	}

}
