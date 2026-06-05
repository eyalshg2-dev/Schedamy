package Schedamy;

public class CalculateHoursThread extends Thread{
	
	private AssignedToTeach assignedToTeach;
	
	public CalculateHoursThread(AssignedToTeach assignedToTeach) {
		this.assignedToTeach = assignedToTeach;
	}
	
	@Override
	public void run() {
		
		try {
		double totalHours = assignedToTeach.calculateLecturerHours();
		System.out.println("Total lecture hours:" + totalHours);
		} catch (Exception e) {
			System.out.println("[CalculateHoursThread] Error: " + e.getMessage());
		}
		
	}

}
