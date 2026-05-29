package Schedamy;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import java.awt.event.*;


public class SchedamyGUI extends Frame implements ActionListener {
	
    private int nextGroupID = 1;// Running number for student groups
    private int nextCourseID = 1;// Running number for course
    private int nextLessonID = 1; //Running number for lesson
    private MenuBar menuBar;

    private Menu fileMenu;
    private Menu manageMenu;
    private Menu viewMenu;
    private Menu threadsMenu;
    private Menu helpMenu;
    
    private SchedamySystem system;
    
    public SchedamyGUI() {
        super("Schedamy System");

        system = new SchedamySystem();
        setSize(500, 350);
        buildMenuBar();
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void buildMenuBar() {
        menuBar = new MenuBar();

        buildFileMenu();
        buildManageMenu();
        buildViewMenu();
        buildThreadsMenu();
        buildHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(viewMenu);
        menuBar.add(threadsMenu);
        menuBar.add(helpMenu);

        setMenuBar(menuBar);
    }

    private void buildFileMenu() {
        fileMenu = new Menu("File");

        MenuItem saveItem = new MenuItem("Save Data", new MenuShortcut(KeyEvent.VK_S));
        MenuItem loadItem = new MenuItem("Load Data", new MenuShortcut(KeyEvent.VK_O));
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(this);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
    }

    private void buildManageMenu() {
        manageMenu = new Menu("Manage");
        MenuItem addLecturerItem = new MenuItem("Add Lecturer");
        MenuItem addCourseItem = new MenuItem("Add Course");
        MenuItem addStudentGroupItem = new MenuItem("Add Student Group");
        MenuItem addRoomItem = new MenuItem("Add Room");
        
        addLecturerItem.addActionListener(this);
        addCourseItem.addActionListener(this);
        addStudentGroupItem.addActionListener(this);
        addRoomItem.addActionListener(this);
        
        manageMenu.add(addLecturerItem);
        manageMenu.add(addCourseItem);
        manageMenu.add(addStudentGroupItem);
        manageMenu.add(addRoomItem);
        MenuItem addLessonItem = new MenuItem("Add Lesson");
        addLessonItem.addActionListener(this);
        manageMenu.add(addLessonItem);
        manageMenu.addSeparator();
        manageMenu.add(new MenuItem("Cancel Lesson"));
        manageMenu.add(new MenuItem("Reschedule Lesson"));
    }

    private void buildViewMenu() {
        viewMenu = new Menu("View");

        MenuItem roomsItem = new MenuItem("Rooms");
        roomsItem.addActionListener(this);
        viewMenu.add(roomsItem);
        MenuItem lecturersItem = new MenuItem("Lecturers");
        lecturersItem.addActionListener(this);
        viewMenu.add(lecturersItem);
        MenuItem coursesItem = new MenuItem("Courses");
        coursesItem.addActionListener(this);
        viewMenu.add(coursesItem);
        MenuItem groupsItem = new MenuItem("Student Groups");
        groupsItem.addActionListener(this);
        viewMenu.add(groupsItem);
         MenuItem lessonsItem = new MenuItem("Lessons");
        lessonsItem.addActionListener(this);
        viewMenu.add(lessonsItem);

        viewMenu.addSeparator();

        Menu timetableMenu = new Menu("Timetables");
        timetableMenu.add(new MenuItem("Lecturer Timetable"));
        timetableMenu.add(new MenuItem("Student Group Timetable"));

        viewMenu.add(timetableMenu);
    }

    private void buildThreadsMenu() {
        threadsMenu = new Menu("Threads");

        threadsMenu.add(new MenuItem("Check Availability"));
        threadsMenu.add(new MenuItem("Calculate Lecturer Hours"));
        threadsMenu.add(new MenuItem("Active Threads Status"));
    }

    private void buildHelpMenu() {
        helpMenu = new Menu("Help");
        MenuItem instructionsItem = new MenuItem("Instructions");
        MenuItem aboutItem = new MenuItem("About Schedamy");

        instructionsItem.addActionListener(this);
        aboutItem.addActionListener(this);

        helpMenu.add(instructionsItem);
        helpMenu.add(aboutItem);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String commandExit = e.getActionCommand();
        if (commandExit.equals("Exit")) {
            System.exit(0);
        }
   
        String commandHelp = e.getActionCommand();
        if (commandHelp.equals("Instructions")) {
            JOptionPane.showMessageDialog(
                this,
                "Use Manage to add or update data.\n" +
                "Use View to display rooms, lecturers, courses and timetables.\n" +
                "Use File to save or load system data.\n" +
                "Use Threads to run background scheduling actions.",
                "Schedamy Instructions",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        if (commandHelp.equals("About Schedamy")) {
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
        String commandAddRoom = e.getActionCommand();
        if (commandAddRoom.equals("Add Room")) {
            openAddRoomDialog();
        }
        String commandAddCourse = e.getActionCommand();
        if (commandAddCourse.equals("Add Course")) {
            openAddCourseDialog();
        }
        
        String commandAddStudentGroup = e.getActionCommand();
        if (commandAddStudentGroup.equals("Add Student Group")) {
            openAddStudentGroupDialog();
        }
        String commandAddLecturer = e.getActionCommand();
        if (commandAddLecturer.equals("Add Lecturer")) {
            openAddLecturerDialog();
        }
        if (e.getActionCommand().equals("Lecturers"))
        {
            openLecturersView();
        }
        if (e.getActionCommand().equals("Courses"))
        {
            openCoursesView();
        }
        if (e.getActionCommand().equals("Student Groups")) {
            openStudentGroupsView();
        }
        if (e.getActionCommand().equals("Rooms")) {
            openRoomsView();
        }
        if (e.getActionCommand().equals("Add Lesson")) {
            openAddLessonDialog();
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
        formPanel.add(equipment);
		formPanel.add(equipmentField);
        
        Choice roomTypeChoice = new Choice();
        roomTypeChoice.add("Classroom");
        roomTypeChoice.add("Computer Lab");
        roomTypeChoice.add("Laboratory");
        roomTypeChoice.add("Auditorium");

        
        Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
        Button addButton = new Button("Add");
        Button cancelButton = new Button("Cancel");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                system.addRoom(Integer.parseInt(roomNumberField.getText()),Integer.parseInt(buildingField.getText()),roomTypeChoice.getSelectedItem(),Integer.parseInt(capacityField.getText()),equipmentField.getText());
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

	    for (Lecturer lecturer : system.getLecturers()) {
	        lecturerChoice.add(
	            lecturer.getLecturerID() + " - " +
	            lecturer.getFirstName() + " " +
	            lecturer.getLastName()
	        );
	    }
	    Choice groupChoice = new Choice();

	    for (StudentGroup group : system.getStudentGroups()) {
	        groupChoice.add(
	            group.getGroupID() + " - " +
	            group.getDepartment()
	        );
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
	        	int groupID = Integer.parseInt(groupText.split(" - ")[0]);
	        	system.addCourse(courseID,capitalizeFirstLetter(courseNameField.getText()),Integer.parseInt(creditsField.getText()),courseTypeChoice.getSelectedItem(),lecturerID,groupID);;

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

        for (Course course : system.getCourses())
        {
            courseChoice.add(course.getCourseID() + " - " + course.getCourseName());
        }

        TextField dateField = new TextField();      // 2026-05-29
        TextField startField = new TextField();     // 08:30
        TextField endField = new TextField();       // 10:00

        Choice statusChoice = new Choice();
        statusChoice.add("SCHEDULED");
        statusChoice.add("RESCHEDULED");
        statusChoice.add("CANCELLED");

        Choice modeChoice = new Choice();
        modeChoice.add("FRONTAL");
        modeChoice.add("ZOOM");
        modeChoice.add("HYBRID");
        Choice roomChoice = new Choice();
        roomChoice.setEnabled(true);
        modeChoice.addItemListener(e -> {

            String mode = modeChoice.getSelectedItem();

            if (mode.equals("ZOOM"))
                roomChoice.setEnabled(false);
            else
                roomChoice.setEnabled(true);
        });
        for (Room room : system.getRooms()) {
            roomChoice.add(room.getRoomID() + " - " + room.toString());
        }

        Choice labChoice = new Choice();
        labChoice.add("false");
        labChoice.add("true");

        formPanel.add(new Label("Course:"));
        formPanel.add(courseChoice);

        formPanel.add(new Label("Date yyyy-mm-dd:"));
        formPanel.add(dateField);

        formPanel.add(new Label("Start HH:mm:"));
        formPanel.add(startField);

        formPanel.add(new Label("End HH:mm:"));
        formPanel.add(endField);

        formPanel.add(new Label("Status:"));
        formPanel.add(statusChoice);

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
                String dateText = dateField.getText();
                String startText = startField.getText();
                String endText = endField.getText();

                if (!dateText.matches("\\d{4}-\\d{2}-\\d{2}"))
                    throw new IllegalArgumentException("Date must be in format yyyy-mm-dd");

                if (!startText.matches("\\d{2}:\\d{2}"))
                    throw new IllegalArgumentException("Start time must be in format HH:mm");

                if (!endText.matches("\\d{2}:\\d{2}"))
                    throw new IllegalArgumentException("End time must be in format HH:mm");

                LocalDate lessonDate = LocalDate.parse(dateText);
                LocalTime startTime = LocalTime.parse(startText);
                LocalTime endTime = LocalTime.parse(endText);

                if (lessonDate.isBefore(LocalDate.now()))
                    throw new IllegalArgumentException("Lesson date cannot be in the past");

                if (!endTime.isAfter(startTime))
                    throw new IllegalArgumentException("End time must be after start time");

                system.addLessonToCourse(courseID,nextLessonID++,lessonDate,startTime,endTime,statusChoice.getSelectedItem(),modeChoice.getSelectedItem(),false);
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

}
