package Schedamy;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;


import java.awt.event.*;

// Main GUI class of the Schedamy system.
// This class creates the main window, menus, dialogs and user actions.
public class SchedamyGUI extends Frame implements ActionListener {
	
    // Running IDs for new objects created from the GUI.
    // Each time a new object is added, the matching counter is increased.
    private int nextGroupID = 1;
    private int nextCourseID = 1;
    private int nextLessonID = 1;

    // Main menu bar and menu categories.
    private MenuBar menuBar;
    private Menu fileMenu;
    private Menu manageMenu;
    private Menu viewMenu;
    private Menu helpMenu;
    
    // The main system object that stores and manages all data.
    private SchedamySystem system;
    private final Object sharedRoomLock = new Object();
    
    // Constructor - creates the main window and initializes the system.
    public SchedamyGUI() {
        super("Schedamy System");

        // Create the main system object.
        system = new SchedamySystem();

        // Set the size of the main window.
        setSize(500, 350);

        // Build and attach the menu bar to the window.
        buildMenuBar();

        // Show the main window.
        setVisible(true);

        // Close the program when the user closes the window.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
 // Builds the main menu bar 
 private void buildMenuBar() {

     // Create the main menu bar object.
     menuBar = new MenuBar();

     // Build all menus.
     buildFileMenu();
     buildManageMenu();
     buildViewMenu();
     buildHelpMenu();

     // Add menus to the menu bar.
     menuBar.add(fileMenu);
     menuBar.add(manageMenu);
     menuBar.add(viewMenu);
     menuBar.add(helpMenu);

     // Attach the menu bar to the main window.
     setMenuBar(menuBar);
 }

 // Creates the "File" menu and its menu items.
 private void buildFileMenu() {
     fileMenu = new Menu("File");

     // Create menu items with keyboard shortcuts.
     MenuItem saveItem =
         new MenuItem("Save Data",
         new MenuShortcut(KeyEvent.VK_S));

     MenuItem loadItem =
         new MenuItem("Load Data",
         new MenuShortcut(KeyEvent.VK_O));

     MenuItem exitItem = new MenuItem("Exit");

     // Connect menu items to the ActionListener.
     exitItem.addActionListener(this);
     saveItem.addActionListener(this);
     loadItem.addActionListener(this);

     // Add items to the menu.
     fileMenu.add(saveItem);
     fileMenu.add(loadItem);

     // Add a visual separator line.
     fileMenu.addSeparator();

     fileMenu.add(exitItem);
 }
//Builds the "Manage" menu and all management actions.
private void buildManageMenu() {

  manageMenu = new Menu("Manage");

  // Menu items for adding new objects to the system.
  MenuItem addLecturerItem = new MenuItem("Add Lecturer");
  MenuItem addCourseItem = new MenuItem("Add Course");
  MenuItem addStudentGroupItem = new MenuItem("Add Student Group");
  MenuItem addRoomItem = new MenuItem("Add Room");
  MenuItem addLessonItem = new MenuItem("Add Lesson");
  
  // Connect menu items to the ActionListener.
  addLecturerItem.addActionListener(this);
  addCourseItem.addActionListener(this);
  addStudentGroupItem.addActionListener(this);
  addRoomItem.addActionListener(this);
  addLessonItem.addActionListener(this);
  
  // Add items to the menu.
  manageMenu.add(addLecturerItem);
  manageMenu.add(addCourseItem);
  manageMenu.add(addStudentGroupItem);
  manageMenu.add(addRoomItem);
  manageMenu.add(addLessonItem);
  
  manageMenu.addSeparator();

  // Cancel and reschedule Lesson actions.
  MenuItem cancelLessonItem = new MenuItem("Cancel Lesson");
  cancelLessonItem.addActionListener(this);
  manageMenu.add(cancelLessonItem);

  MenuItem rescheduleLessonItem = new MenuItem("Reschedule Lesson");
  rescheduleLessonItem.addActionListener(this);
  manageMenu.add(rescheduleLessonItem);
}

//Builds the "View" menu 
//This menu displays information stored in the system.
private void buildViewMenu() {

 viewMenu = new Menu("View");

 // View rooms.
 MenuItem roomsItem = new MenuItem("Rooms");
 roomsItem.addActionListener(this);
 viewMenu.add(roomsItem);

 // View lecturers.
 MenuItem lecturersItem = new MenuItem("Lecturers");
 lecturersItem.addActionListener(this);
 viewMenu.add(lecturersItem);

 // View courses.
 MenuItem coursesItem = new MenuItem("Courses");
 coursesItem.addActionListener(this);
 viewMenu.add(coursesItem);

 // View student groups.
 MenuItem groupsItem = new MenuItem("Student Groups");
 groupsItem.addActionListener(this);
 viewMenu.add(groupsItem);

 // View lessons.
 MenuItem lessonsItem = new MenuItem("Lessons");
 lessonsItem.addActionListener(this);
 viewMenu.add(lessonsItem);

 viewMenu.addSeparator();

 // Menu for timetable views.
 Menu timetableMenu = new Menu("Timetables");

 MenuItem lecturerTimetableItem =new MenuItem("Lecturer Timetable");
 lecturerTimetableItem.addActionListener(this);
 timetableMenu.add(lecturerTimetableItem);

 MenuItem groupTimetableItem = new MenuItem("Student Group Timetable");
 groupTimetableItem.addActionListener(this);
 timetableMenu.add(groupTimetableItem);

 viewMenu.add(timetableMenu);
}

//Builds the "Help" menu.
//This menu contains general information about the system.
private void buildHelpMenu() {

 helpMenu = new Menu("Help");

 MenuItem instructionsItem = new MenuItem("Instructions");
 instructionsItem.addActionListener(this);
 helpMenu.add(instructionsItem);
 
 MenuItem aboutItem = new MenuItem("About Schedamy");
 aboutItem.addActionListener(this);
 helpMenu.add(aboutItem);
}

@Override
public void actionPerformed(ActionEvent e) {

    // Get the text of the menu item that was clicked.
    String command = e.getActionCommand();

    // File menu actions.
    if (command.equals("Exit")) {
        System.exit(0);
    }

    if (command.equals("Save Data")) {
        try {
            system.saveDataToFile();
            JOptionPane.showMessageDialog(
                this,
                "Data saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error saving data: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    if (command.equals("Load Data")) {
        try {
            system.loadDataFromFile();

            JOptionPane.showMessageDialog(
                this,
                "Data loaded successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading data: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Help menu actions.
    if (command.equals("Instructions")) {
        JOptionPane.showMessageDialog(
            this,
            "Use Manage to add or update data.\n" +
            "Use View to display rooms, lecturers, courses and timetables.\n" +
            "Use File to save or load system data.",
            "Schedamy Instructions",
            JOptionPane.INFORMATION_MESSAGE);
    }

    if (command.equals("About Schedamy")) {
        JOptionPane.showMessageDialog(
            this,
            "Schedamy System\n" +
            "Software Engineering Project\n" +
            "By Idan Zilcha, Orel Levi, Eyal Shigris and Ola Jangirian\n"+
            "We are four third-year engineering students who experienced many schedule and classroom changes\n"+
            "during our studies due to unexpected events such as viruses, wars, and maybe even alien invasions.\n"+              
            "To improve and simplify the process, we created Schedamy -  a smart scheduling system,\n"+
            "designed to manage lesson cancellations, rescheduling, classroom changes, and lecturer availability more efficiently.\n"+
            "The name \"Schedamy\" combines the words Schedule + Academy,\n"+
            "and also sounds like the Hebrew word \"Shakadnu\" (we worked hard) - which we definitely did.\n",
            "About Schedamy", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Add dialogs.
    if (command.equals("Add Room")) {
        openAddRoomDialog();
    }

    if (command.equals("Add Course")) {
        openAddCourseDialog();
    }

    if (command.equals("Add Student Group")) {
        openAddStudentGroupDialog();
    }

    if (command.equals("Add Lecturer")) {
        openAddLecturerDialog();
    }

    if (command.equals("Add Lesson")) {
        openAddLessonDialog();
    }

    // View dialogs.
    if (command.equals("Lecturers")) {
        openLecturersView();
    }

    if (command.equals("Courses")) {
        openCoursesView();
    }

    if (command.equals("Student Groups")) {
        openStudentGroupsView();
    }

    if (command.equals("Rooms")) {
        openRoomsView();
    }

    if (command.equals("Lessons")) {
        openLessonsView();
    }

    // Lesson update actions.
    if (command.equals("Cancel Lesson")) {
        openCancelLessonDialog();
    }

    if (command.equals("Reschedule Lesson")) {
        openRescheduleLessonDialog();
    }

    // Timetable views.
    if (command.equals("Lecturer Timetable")) {
        openLecturerTimetableDialog();
    }

    if (command.equals("Student Group Timetable")) {
        openStudentGroupTimetableDialog();
    }
}
    private void openAddLecturerDialog() {

    // Create a pop-up dialog window for adding a new lecturer
    Dialog dialog = new Dialog(this, "Add Lecturer", true);
    // Organize the dialog into sections
    dialog.setLayout(new BorderLayout());
    dialog.setSize(450, 380);
    
    Panel formPanel = new Panel(new GridLayout(5, 2, 5, 5));
    Label idLabel = new Label("Lecturer ID:");
    TextField idField = new TextField();
    formPanel.add(idLabel);
    formPanel.add(idField);

    Label firstNameLabel = new Label("First Name:");
    TextField firstNameField = new TextField();
    formPanel.add(firstNameLabel);
    formPanel.add(firstNameField);

    Label lastNameLabel = new Label("Last Name:");
    TextField lastNameField = new TextField();
    formPanel.add(lastNameLabel);
    formPanel.add(lastNameField);

    Label teachingScoreLabel = new Label("Teaching Score:");
    TextField teachingScoreField = new TextField();
    formPanel.add(teachingScoreLabel);
    formPanel.add(teachingScoreField);

    Label fteLabel = new Label("FTE:");
    Choice fteChoice = new Choice();
    //  FTE values
    fteChoice.add("0.25");
    fteChoice.add("0.33");
    fteChoice.add("0.5");
    fteChoice.add("0.75");
    fteChoice.add("1.0");

    formPanel.add(fteLabel);
    formPanel.add(fteChoice);

    // separate panel for specializations
    Panel specPanel = new Panel(new BorderLayout());
    Label specializationLabel = new Label("Specializations:");

    // List component with multiple selection 
    List specializationList = new List(8, true);

    // specialization options
    specializationList.add("Java");
    specializationList.add("Databases");
    specializationList.add("Software Engineering");
    specializationList.add("Algorithms");
    specializationList.add("Mathematics");
    specializationList.add("Electronics");
    specializationList.add("Physics");
    specializationList.add("Signals");

    // Add label and list into the specialization panel
    specPanel.add(specializationLabel, BorderLayout.NORTH);
    specPanel.add(specializationList, BorderLayout.CENTER);
    
    // Panel for the Add and Cancel buttons
    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
    Button addButton = new Button("Add");
    Button cancelButton = new Button("Cancel");

    buttonPanel.add(addButton);
    buttonPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.NORTH);
    dialog.add(specPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
            	 if (!idField.getText().matches("\\d+"))
                     throw new IllegalArgumentException("Lecturer ID must contain only digits");

                 if (!teachingScoreField.getText().matches("\\d+(\\.\\d+)?"))
                     throw new IllegalArgumentException("Teaching score must be a positive number");
                int id = Integer.parseInt(idField.getText());
                String firstName = capitalizeFirstLetter(firstNameField.getText());
                String lastName = capitalizeFirstLetter(lastNameField.getText());
             
                double teachingScore = Double.parseDouble(teachingScoreField.getText());
                double fte = Double.parseDouble(fteChoice.getSelectedItem());

                ArrayList<String> specializations = new ArrayList<String>();
                String[] selectedSpecs = specializationList.getSelectedItems();
                for (String spec : selectedSpecs) 
                    specializations.add(spec);
            
                system.addLecturer(id,firstName,lastName,specializations,teachingScore,fte);
                System.out.println(system.getLecturers());
                JOptionPane.showMessageDialog(
                	    this,
                	    "Lecturer added successfully!\n" +
                	    "Total lecturers: " + system.getLecturers().size(),
                	    "Success",
                	    JOptionPane.INFORMATION_MESSAGE);
                   
                dialog.dispose();
            } 
            catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
    private void openAddRoomDialog() {
        Dialog dialog = new Dialog(this, "Add Room", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(420, 280);

        Panel formPanel = new Panel(new GridLayout(5, 2, 5, 5));
        Label roomNumber = new Label("Room number");
        TextField roomNumberField = new TextField();
        formPanel.add(roomNumber);
        formPanel.add(roomNumberField);
        		
        Label building = new Label("Building");
        TextField buildingField = new TextField();
        formPanel.add(building);
		formPanel.add(buildingField);
		
		Label capacity = new Label("Capacity");
        TextField capacityField = new TextField();
        formPanel.add(capacity);
		formPanel.add(capacityField);
		
		Label equipment = new Label("Special Equipment");
		TextField equipmentField = new TextField();
		equipmentField.setText("NONE");
		formPanel.add(equipment);
		formPanel.add(equipmentField);
        
        Choice roomTypeChoice = new Choice();
        roomTypeChoice.add("Classroom");
        roomTypeChoice.add("Computer Lab");
        roomTypeChoice.add("Laboratory");
        roomTypeChoice.add("Auditorium");

        Label roomTypeLabel = new Label("Room Type");
        formPanel.add(roomTypeLabel);
        formPanel.add(roomTypeChoice);
        
        Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
        Button addButton = new Button("Add");
        Button cancelButton = new Button("Cancel");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> {
            try {
            	if (!roomNumberField.getText().matches("\\d+"))
            	    throw new IllegalArgumentException("Room number must be a number");

            	if (!buildingField.getText().matches("\\d+"))
            	    throw new IllegalArgumentException("Building must be a number");

            	if (!capacityField.getText().matches("\\d+"))
            	    throw new IllegalArgumentException("Capacity must be a number");

                String specEquipment =equipmentField.getText().trim();

                if (specEquipment.isEmpty()) {
                    specEquipment = "NONE";
                }

                system.addRoom(
                    roomNumberField.getText(),
                    Integer.parseInt(buildingField.getText()),
                    roomTypeChoice.getSelectedItem(),
                    Integer.parseInt(capacityField.getText()),
                    specEquipment
                );
                JOptionPane.showMessageDialog(
                    this,
                    "Room added successfully!\n" +
                    "Total rooms: " + system.getRooms().size(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();
            } 
            catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
	private void openAddCourseDialog() {
	    Dialog dialog = new Dialog(this, "Add Course", true);
	    dialog.setLayout(new BorderLayout());
	    dialog.setSize(420, 260);
	    Panel formPanel = new Panel(new GridLayout(5,1,5,5));
	    
	    
	    TextField courseNameField = new TextField();
	    TextField creditsField = new TextField();

	    Choice courseTypeChoice = new Choice();
	    courseTypeChoice.add("mandatory");
	    courseTypeChoice.add("elective");
	    Choice lecturerChoice = new Choice();

	    for (Lecturer lecturer : system.getLecturers())
	    {
	        lecturerChoice.add(lecturer.getLecturerID() + " - " +lecturer.getFirstName() + " " +lecturer.getLastName());
	    }
	    Choice groupChoice = new Choice();

	    for (StudentGroup group : system.getStudentGroups()) 
	    {
	        groupChoice.add(group.getGroupID() +"-" +group.getDepartment() +"  Year-" +group.getStudyYear() +" - " +group.getProgramName());
	    }
	    Panel namePanel = new Panel(new BorderLayout());
	    namePanel.add(new Label("Course Name:"), BorderLayout.WEST);
	    namePanel.add(courseNameField, BorderLayout.CENTER);
	    formPanel.add(namePanel); 
	    
	    Panel creditsPanel = new Panel(new BorderLayout());
	    creditsPanel.add(new Label("Credits:          "), BorderLayout.WEST);
	    creditsPanel.add(creditsField, BorderLayout.CENTER);
	    formPanel.add(creditsPanel);
	    
	    Panel typePanel = new Panel(new BorderLayout());
	    typePanel.add(new Label("Course Type:"), BorderLayout.WEST);
	    typePanel.add(courseTypeChoice, BorderLayout.CENTER);
	    formPanel.add(typePanel);
	    
	    Panel lecturerPanel = new Panel(new BorderLayout());
	    lecturerPanel.add(new Label("Lecturer:       "), BorderLayout.WEST);
	    lecturerPanel.add(lecturerChoice, BorderLayout.CENTER);
	    formPanel.add(lecturerPanel);

	    Panel groupPanel = new Panel(new BorderLayout());
	    groupPanel.add(new Label("Student Group:"), BorderLayout.WEST);
	    groupPanel.add(groupChoice, BorderLayout.CENTER);
	    formPanel.add(groupPanel);
	    
	    
	    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    Button addButton = new Button("Add");
	    Button cancelButton = new Button("Cancel");
	    buttonPanel.add(addButton);
	    buttonPanel.add(cancelButton);

	    dialog.add(formPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	    addButton.addActionListener(e -> {
	        try {
	        	int courseID = nextCourseID++;
	        	String lecturerText = lecturerChoice.getSelectedItem();
	        	String groupText = groupChoice.getSelectedItem();
	        	int lecturerID = Integer.parseInt(lecturerText.split(" - ")[0]);
	        	int groupID = Integer.parseInt(groupText.split("-")[0]);
	        	if (!creditsField.getText().matches("\\d+"))
	        	    throw new IllegalArgumentException("Credits must be positive a number");

	        	system.addCourse(courseID,capitalizeFirstLetter(courseNameField.getText()),Integer.parseInt(creditsField.getText()),courseTypeChoice.getSelectedItem(),lecturerID,groupID);
	        
	            JOptionPane.showMessageDialog(this,
	                "Course added successfully!\nTotal courses: " + system.getCourses().size() + "\n"+ "Course ID "+ nextCourseID);
	            dialog.dispose();
	            

	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this,
	                "Invalid input: " + ex.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> dialog.dispose());

	    dialog.setVisible(true);
	}
	
	private void openAddStudentGroupDialog() {
	   
		// Create dialog window
	    Dialog dialog = new Dialog(this, "Add Student Group", true);
	    //BorderLayout for better organization
	    dialog.setLayout(new BorderLayout());
	    // Set dialog size
	    dialog.setSize(450, 300);
	    //   Main form panel
	    Panel formPanel = new Panel(new GridLayout(4, 2, 5, 5));
	    //   Department selection
	    Label departmentLabel = new Label("Department:");
	    Choice departmentChoice = new Choice();
	    departmentChoice.add("Electrical Engineering");
	    departmentChoice.add("Computer Engineering");
	    departmentChoice.add("Industrial Engineering and Management");
	    departmentChoice.add("Economics and Business Administration");
	    departmentChoice.add("Nursing");
	    departmentChoice.add("Psychology");
	    departmentChoice.add("Computer Science");
	
	    formPanel.add(departmentLabel);
	    formPanel.add(departmentChoice);

	    //   Study year selection
	    Label studyYearLabel = new Label("Study Year:");
	    Choice studyYearChoice = new Choice();
	    studyYearChoice.add("1");
	    studyYearChoice.add("2");
	    studyYearChoice.add("3");
	    studyYearChoice.add("4");
	
	    formPanel.add(studyYearLabel);
	    formPanel.add(studyYearChoice);
	
		// Student count
	    Label studentCountLabel = new Label("Student Count:");
	    TextField studentCountField = new TextField();
	    formPanel.add(studentCountLabel);
	    formPanel.add(studentCountField);
	    
	    //Program type	
	    Label programLabel = new Label("Program:");
	    Choice programChoice = new Choice();
	    programChoice.add("Morning");
	    programChoice.add("Evening");
	    formPanel.add(programLabel);
	    formPanel.add(programChoice);
	    
	    // Buttons panel
	    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    Button addButton = new Button("Add");
	    Button cancelButton = new Button("Cancel");
	
	    buttonPanel.add(addButton);
	    buttonPanel.add(cancelButton);
	    dialog.add(formPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	      // Add button action
	    addButton.addActionListener(e -> {
	        try {
	        	if (!studentCountField.getText().matches("\\d+"))
	        	    throw new IllegalArgumentException("Student count must be a positive number");
	        system.addStudentGroup(nextGroupID++,departmentChoice.getSelectedItem(),Integer.parseInt(studyYearChoice.getSelectedItem()),Integer.parseInt(studentCountField.getText()),programChoice.getSelectedItem());
	            //success and error massages
	            JOptionPane.showMessageDialog(
	                this,
	                "Student group added successfully!\n" +
	                "Group ID: " + nextGroupID + "\n" + "\n" +
	                "Total groups: " + system.getStudentGroups().size(),
	                "Success",JOptionPane.INFORMATION_MESSAGE);
	            // Close dialog window
	            dialog.dispose();
	        }
	        catch (Exception ex) {
	            JOptionPane.showMessageDialog(this,"Invalid input: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	        }
	    });
	    cancelButton.addActionListener(e -> dialog.dispose());

	    dialog.setVisible(true);
	}
	
	
	private void openCancelLessonDialog()
	{
	    Dialog dialog = new Dialog(this, "Cancel Lesson", true);
	    dialog.setLayout(new BorderLayout());
	    dialog.setSize(500, 220);

	    Panel formPanel = new Panel(new GridLayout(2, 2, 5, 5));

	    Choice lessonChoice = new Choice();

	    // This vector keeps  Lesson objects in the same order as the Choice list.
	    Vector<Lesson> lessonsList = new Vector<Lesson>();

	    for (Course course : system.getCourses()) {
	        for (Lesson lesson : course.getLessons()) {

	            lessonChoice.add(
	                course.getCourseID() + " - " +
	                course.getCourseName() + " | Lesson " +
	                lesson.getLessonID() + " | " +
	                lesson.getLessonDate() + " " +
	                lesson.getStartTime()    	
	            );

	            lessonsList.add(lesson);
	        }
	    }

	    TextField reasonField = new TextField();

	    formPanel.add(new Label("Lesson:"));
	    formPanel.add(lessonChoice);

	    formPanel.add(new Label("Cancel Reason:"));
	    formPanel.add(reasonField);

	    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    Button cancelLessonButton = new Button("Cancel Lesson");
	    Button closeButton = new Button("Close");

	    buttonPanel.add(cancelLessonButton);
	    buttonPanel.add(closeButton);

	    dialog.add(formPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	    cancelLessonButton.addActionListener(e -> {
	        try {
	            if (lessonChoice.getItemCount() == 0) {
	                throw new IllegalArgumentException("No lessons found.");
	            }

	            String reason = reasonField.getText().trim();

	            if (reason.isEmpty()) {
	                throw new IllegalArgumentException("Cancel reason cannot be empty.");
	            }

	            // Get the selected Lesson directly by index
	            int selectedIndex = lessonChoice.getSelectedIndex();
	            Lesson selectedLesson = lessonsList.get(selectedIndex);

	            selectedLesson.setStatus("CANCELLED");

	            JOptionPane.showMessageDialog(
	                this,
	                "Lesson cancelled successfully!\nReason: " + reason,
	                "Success",
	                JOptionPane.INFORMATION_MESSAGE
	            );

	            dialog.dispose();

	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(
	                this,
	                "Invalid input: " + ex.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE
	            );
	        }
	    });

	    closeButton.addActionListener(e -> dialog.dispose());

	    dialog.setVisible(true);
	}
	
	private void openRescheduleLessonDialog()
	{
	    Dialog dialog = new Dialog(this, "Reschedule Lesson", true);
	    dialog.setLayout(new BorderLayout());
	    dialog.setSize(520, 320);

	    Panel formPanel = new Panel(new GridLayout(6, 2, 5, 5));

	    Choice lessonChoice = new Choice();
	    Vector<Lesson> lessonsList = new Vector<Lesson>();

	    for (Course course : system.getCourses()) {
	        for (Lesson lesson : course.getLessons()) {
	            lessonChoice.add(
	                course.getCourseName() + " | Lesson " +
	                lesson.getLessonID() + " | " +
	                lesson.getLessonDate() + " " +
	                lesson.getStartTime()
	            );

	            lessonsList.add(lesson);
	        }
	    }

	    TextField dateField = new TextField();
	    TextField startField = new TextField();
	    TextField endField = new TextField();

	    Choice modeChoice = new Choice();
	    modeChoice.add("FRONTAL");
	    modeChoice.add("ZOOM");
	    modeChoice.add("HYBRID");

	    Choice roomChoice = new Choice();

	    for (Room room : system.getRooms()) {
	        roomChoice.add(room.getRoomID() + " - " + room.toString());
	    }

	    formPanel.add(new Label("Lesson:"));
	    formPanel.add(lessonChoice);

	    formPanel.add(new Label("New Date yyyy-mm-dd:"));
	    formPanel.add(dateField);

	    formPanel.add(new Label("New Start HH:mm:"));
	    formPanel.add(startField);

	    formPanel.add(new Label("New End HH:mm:"));
	    formPanel.add(endField);

	    formPanel.add(new Label("Teaching Mode:"));
	    formPanel.add(modeChoice);

	    formPanel.add(new Label("Room:"));
	    formPanel.add(roomChoice);

	    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    Button rescheduleButton = new Button("Reschedule");
	    Button closeButton = new Button("Close");

	    buttonPanel.add(rescheduleButton);
	    buttonPanel.add(closeButton);

	    dialog.add(formPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	    modeChoice.addItemListener(e -> {
	        if (modeChoice.getSelectedItem().equals("ZOOM")) {
	            roomChoice.setEnabled(false);
	        } else {
	            roomChoice.setEnabled(true);
	        }
	    });

	    rescheduleButton.addActionListener(e -> {
	        try {
	            if (lessonChoice.getItemCount() == 0) {
	                throw new IllegalArgumentException("No lessons found.");
	            }

	            int selectedIndex = lessonChoice.getSelectedIndex();
	            Lesson selectedLesson = lessonsList.get(selectedIndex);

	            String dateText = dateField.getText().trim();
	            String startText = startField.getText().trim();
	            String endText = endField.getText().trim();

	            if (dateText.length() != 10 ||
	            	    dateText.charAt(4) != '-' ||
	            	    dateText.charAt(7) != '-') {

	            	    throw new IllegalArgumentException(
	            	        "Date must be in format yyyy-mm-dd"
	            	    );
	            	}

	            if (startText.length() != 5 ||
	            	    startText.charAt(2) != ':') {

	            	    throw new IllegalArgumentException(
	            	        "Start time must be in format HH:mm"
	            	    );
	            	}

	            if (endText.length() != 5 ||
	            	    startText.charAt(2) != ':') {

	            	    throw new IllegalArgumentException(
	            	        "End time must be in format HH:mm"
	            	    );
	            	}

	            LocalDate newDate = LocalDate.parse(dateText);
	            LocalTime newStart = LocalTime.parse(startText);
	            LocalTime newEnd = LocalTime.parse(endText);

	            if (newDate.isBefore(LocalDate.now())) {
	                throw new IllegalArgumentException("Lesson date cannot be in the past");
	            }

	            if (!newEnd.isAfter(newStart)) {
	                throw new IllegalArgumentException("End time must be after start time");
	            }

	            selectedLesson.setLessonDate(newDate);
	            selectedLesson.setStartTime(newStart);
	            selectedLesson.setEndTime(newEnd);
	            selectedLesson.setTeachingMode(modeChoice.getSelectedItem());
	            selectedLesson.setStatus("RESCHEDULED");

	            JOptionPane.showMessageDialog(
	                this,
	                "Lesson rescheduled successfully!",
	                "Success",
	                JOptionPane.INFORMATION_MESSAGE
	            );

	            dialog.dispose();

	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(
	                this,
	                "Invalid input: " + ex.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE
	            );
	        }
	    });

	    closeButton.addActionListener(e -> dialog.dispose());

	    dialog.setVisible(true);
	}
	
	private void openLecturerTimetableDialog()
	{
	    Dialog dialog = new Dialog(this, "Lecturer Timetable", true);
	    dialog.setLayout(new BorderLayout());
	    dialog.setSize(450, 180);

	    Choice lecturerChoice = new Choice();
	    Vector<Lecturer> lecturersList = new Vector<Lecturer>();

	    for (Lecturer lecturer : system.getLecturers()) {
	        lecturerChoice.add(
	            lecturer.getLecturerID() + " - " +
	            lecturer.getFirstName() + " " +
	            lecturer.getLastName()
	        );
	        lecturersList.add(lecturer);
	    }

	    Button showButton = new Button("Show Timetable");
	    Button closeButton = new Button("Close");

	    Panel formPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    formPanel.add(new Label("Lecturer:"));
	    formPanel.add(lecturerChoice);

	    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    buttonPanel.add(showButton);
	    buttonPanel.add(closeButton);

	    dialog.add(formPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	    showButton.addActionListener(e -> {
	        try {
	            if (lecturerChoice.getItemCount() == 0) {
	                throw new IllegalArgumentException("No lecturers found.");
	            }

	            Lecturer selectedLecturer =
	                lecturersList.get(lecturerChoice.getSelectedIndex());

	            String text = "";

	            for (AssignedToTeach assigned : system.getAssignedToTeachList()) {
	                if (assigned.getLecturer().equals(selectedLecturer)) {
	                    Course course = assigned.getCourse();

	                    text += "Course: " + course.getCourseName() + "\n";

	                    for (Lesson lesson : course.getLessons()) {
	                        text += lesson.toString() + "\n";
	                    }

	                    text += "\n";
	                }
	            }

	            if (text.isEmpty()) {
	                text = "No lessons found for this lecturer.";
	            }

	            JOptionPane.showMessageDialog(
	                this,
	                text,
	                "Lecturer Timetable",
	                JOptionPane.INFORMATION_MESSAGE
	            );

	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this,
	                "Error: " + ex.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    closeButton.addActionListener(e -> dialog.dispose());

	    dialog.setVisible(true);
	}
	
	private void openStudentGroupTimetableDialog()
	{
	    Dialog dialog = new Dialog(this, "Student Group Timetable", true);
	    dialog.setLayout(new BorderLayout());
	    dialog.setSize(450, 180);

	    Choice groupChoice = new Choice();
	    Vector<StudentGroup> groupsList = new Vector<StudentGroup>();

	    for (StudentGroup group : system.getStudentGroups()) {
	        groupChoice.add(
	            group.getGroupID() + " - " +
	            group.getDepartment() + " Year " +
	            group.getStudyYear()
	        );

	        groupsList.add(group);
	    }

	    Button showButton = new Button("Show Timetable");
	    Button closeButton = new Button("Close");

	    Panel formPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    formPanel.add(new Label("Student Group:"));
	    formPanel.add(groupChoice);

	    Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
	    buttonPanel.add(showButton);
	    buttonPanel.add(closeButton);

	    dialog.add(formPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	    showButton.addActionListener(e -> {
	        try {
	            if (groupChoice.getItemCount() == 0) {
	                throw new IllegalArgumentException("No student groups found.");
	            }

	            StudentGroup selectedGroup =
	                groupsList.get(groupChoice.getSelectedIndex());

	            String text = "";

	            for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
	                if (enrolment.getGroup().equals(selectedGroup)) {
	                    Course course = enrolment.getCourse();

	                    text += "Course: " + course.getCourseName() + "\n";

	                    for (Lesson lesson : course.getLessons()) {
	                        text += lesson.toString() + "\n";
	                    }

	                    text += "\n";
	                }
	            }

	            if (text.isEmpty()) {
	                text = "No lessons found for this student group.";
	            }

	            JOptionPane.showMessageDialog(
	                this,
	                text,
	                "Student Group Timetable",
	                JOptionPane.INFORMATION_MESSAGE
	            );

	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(
	                this,
	                "Error: " + ex.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE
	            );
	        }
	    });

	    closeButton.addActionListener(e -> dialog.dispose());

	    dialog.setVisible(true);
	}
	
	private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        text = text.trim().toLowerCase();
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    public static void main(String[] args)
    {
        new SchedamyGUI();
    }
    private void openLecturersView() {
        String text = "";

        for (Lecturer lecturer : system.getLecturers()) {
            text += lecturer.toString() + "\n\n";
        }

        if (text.isEmpty()) {
            text = "No lecturers found.";
        }

        JOptionPane.showMessageDialog(
            this,
            text,
            "Lecturers",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    private void openCoursesView() {
        String text = "";

        for (Course course : system.getCourses()) {
            text += course.toString();
            text +=  system.getCourseInfo(course);
            text += "\n\n";
        }

        if (text.isEmpty()) {
            text = "No courses found.";
        }
        JOptionPane.showMessageDialog(
            this,
            text,
            "Courses",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    private void openStudentGroupsView()
    {
        String text = "";
        for (StudentGroup group : system.getStudentGroups()) {
            text += group.toString() + "\n\n";
        }
        if (text.isEmpty())
            text = "No student groups found.";
        JOptionPane.showMessageDialog(
            this,
            text,
            "Student Groups",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    private void openRoomsView()
    {
        String text = "";
        for (Room room : system.getRooms()) {
            text += room.toString() + "\n\n";
        }
        if (text.isEmpty())
            text = "No rooms found.";
        JOptionPane.showMessageDialog(
            this,
            text,
            "Rooms",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    private void openAddLessonDialog()
    {
        Dialog dialog = new Dialog(this, "Add Lesson", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 350);

        Panel formPanel = new Panel(new GridLayout(7, 2, 5, 5));

        Choice courseChoice = new Choice();
        
        Vector<Room> roomsList = new Vector<Room>();

        for (Course course : system.getCourses())
        {
            courseChoice.add(course.getCourseID() + " - " + course.getCourseName());
        }

        Choice yearChoice = new Choice();
        yearChoice.add("2026");

        Choice monthChoice = new Choice();
        for (int i = 1; i <= 12; i++) {
            monthChoice.add(String.valueOf(i));
        }

        Choice dayChoice = new Choice();
        int year = Integer.parseInt(yearChoice.getSelectedItem());
        int month = Integer.parseInt(monthChoice.getSelectedItem());

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            dayChoice.add(String.valueOf(day));
        }

        Choice startHourChoice = new Choice();
        Choice startMinuteChoice = new Choice();
        Choice endHourChoice = new Choice();
        Choice endMinuteChoice = new Choice();

        for (int h = 8; h <= 21; h++) {
            startHourChoice.add(String.format("%02d", h));
            endHourChoice.add(String.format("%02d", h));
        }

        startMinuteChoice.add("00");
        startMinuteChoice.add("15");
        startMinuteChoice.add("30");
        startMinuteChoice.add("45");

        endMinuteChoice.add("00");
        endMinuteChoice.add("15");
        endMinuteChoice.add("30");
        endMinuteChoice.add("45");


        Choice modeChoice = new Choice();
        modeChoice.add("FRONTAL");
        modeChoice.add("ZOOM");
        modeChoice.add("HYBRID");
        Checkbox labCheckbox = new Checkbox("Lab Required");
        Choice roomChoice = new Choice();
        roomChoice.setEnabled(true);
        modeChoice.addItemListener(e -> {

            String mode = modeChoice.getSelectedItem();

            if (mode.equals("ZOOM"))
                roomChoice.setEnabled(false);
            else
                roomChoice.setEnabled(true);
        });
        for (Room room : system.getRooms()) 
        {
            roomChoice.add(room.getRoomID() +" - Capacity: " +room.getCapacity());
            roomsList.add(room);
        }

        Choice labChoice = new Choice();
        labChoice.add("false");
        labChoice.add("true");

        formPanel.add(new Label("Course:"));
        formPanel.add(courseChoice);

        Panel datePanel = new Panel(new GridLayout(1, 3, 5, 5));
        datePanel.add(yearChoice);
        datePanel.add(monthChoice);
        datePanel.add(dayChoice);

        formPanel.add(new Label("Date:"));
        formPanel.add(datePanel);

        Panel startPanel = new Panel(new GridLayout(1, 2, 5, 5));
        startPanel.add(startHourChoice);
        startPanel.add(startMinuteChoice);

        formPanel.add(new Label("Start Time:"));
        formPanel.add(startPanel);

        Panel endPanel = new Panel(new GridLayout(1, 2, 5, 5));
        endPanel.add(endHourChoice);
        endPanel.add(endMinuteChoice);

        formPanel.add(new Label("End Time:"));
        formPanel.add(endPanel);
        formPanel.add(new Label("Lab Required:"));
        formPanel.add(labCheckbox);
        formPanel.add(new Label("Teaching Mode:"));
        formPanel.add(modeChoice);
        formPanel.add(new Label("Room:"));
        formPanel.add(roomChoice); 

        Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
        Button addButton = new Button("Add");
        Button cancelButton = new Button("Cancel");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                String courseText = courseChoice.getSelectedItem();
                int courseID = Integer.parseInt(courseText.split(" - ")[0]);
                LocalDate lessonDate = LocalDate.of(
                	    Integer.parseInt(yearChoice.getSelectedItem()),
                	    Integer.parseInt(monthChoice.getSelectedItem()),
                	    Integer.parseInt(dayChoice.getSelectedItem())
                	);

                	LocalTime startTime = LocalTime.of(
                	    Integer.parseInt(startHourChoice.getSelectedItem()),
                	    Integer.parseInt(startMinuteChoice.getSelectedItem())
                	);

                	LocalTime endTime = LocalTime.of(
                	    Integer.parseInt(endHourChoice.getSelectedItem()),
                	    Integer.parseInt(endMinuteChoice.getSelectedItem())
                	);

                if (lessonDate.isBefore(LocalDate.now()))
                    throw new IllegalArgumentException("Lesson date cannot be in the past");

                if (!endTime.isAfter(startTime))
                    throw new IllegalArgumentException("End time must be after start time");
                
                Room selectedRoom = null;

                if (!modeChoice.getSelectedItem().equals("ZOOM")) {
                    selectedRoom = roomsList.get(roomChoice.getSelectedIndex());
                }
                system.addLessonToCourse(
                	    courseID,
                	    nextLessonID++,
                	    lessonDate,
                	    startTime,
                	    endTime,
                	    "SCHEDULED",
                	    modeChoice.getSelectedItem(),
                	    labCheckbox.getState(),
                	    selectedRoom
                	);
                JOptionPane.showMessageDialog(
                    this,
                    "Lesson added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
    private void openLessonsView()
    {
        String text = "";

        for (Course course : system.getCourses()) {
            text += "Course: " + course.getCourseName() + "\n";

            for (Lesson lesson : course.getLessons()) {
                text += lesson.toString() + "\n";
            }

            text += "\n";
        }

        if (text.isEmpty()) {
            text = "No lessons found.";
        }

        JOptionPane.showMessageDialog(
            this,
            text,
            "Lessons",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

}
