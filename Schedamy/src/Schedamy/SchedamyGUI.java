package Schedamy;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.time.format.DateTimeFormatter;
import java.awt.event.*;


//======================================================
//Fields
//Class fields, counters, menus and shared objects
//======================================================

// Main GUI class of Schedemy system.
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
	private Menu optionsMenu;


	// The main system object that stores and manages all data.
	private SchedamySystem system;
	private final Object sharedRoomLock = new Object();
	private Panel mainPanel;

	//Date format
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	//Popup Menu
	private PopupMenu reportsPopup;

	//CheckBox MenuItem
	private CheckboxMenuItem showCapacityItem;

	// ======================================================
	// Constructor
	// Initializes the GUI and application data
	// ======================================================

	public SchedamyGUI(SchedamySystem system) {
		super("Schedemy System");

		this.system = system;
		updateNextIDs();

		// Set the size of the main window.
		setSize(750, 500);
		setLocationRelativeTo(null);

		// Build and attach the menu bar to the window.
		buildMenuBar();
		buildPopupMenu();
		buildMainPanel();

		// Show the main window.
		setVisible(true);

		// Load previous data when starting
		askLoadOnStart();

		// Confirm exit when the user closes the window.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}
		});

	}



	// ======================================================
	// Main Window
	// Main panel, layout and common UI components
	// ======================================================

	private void buildMainPanel() {
		setLayout(new BorderLayout());

		mainPanel = new Panel(new BorderLayout(20, 20));
		mainPanel.setBackground(new Color(245, 247, 250));

		// Header
		Panel headerPanel = new Panel(new GridLayout(3, 1));
		headerPanel.setBackground(new Color(245, 247, 250));

		Label titleLabel = new Label("Schedemy", Label.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 34));

		Label subtitleLabel = new Label("College Scheduling Management System", Label.CENTER);
		subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));

		Label descriptionLabel = new Label(
				"Manage courses, lecturers, rooms, student groups and weekly schedules",
				Label.CENTER
				);
		descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

		headerPanel.add(titleLabel);
		headerPanel.add(subtitleLabel);
		headerPanel.add(descriptionLabel);
		// Center buttons
		Panel cardsPanel = new Panel(new GridLayout(3, 3, 15, 15));
		cardsPanel.setBackground(new Color(245, 247, 250));

		cardsPanel.add(createDashboardButton("Courses"));
		cardsPanel.add(createDashboardButton("Lecturers"));
		cardsPanel.add(createDashboardButton("Rooms"));
		cardsPanel.add(createDashboardButton("Student Groups"));
		cardsPanel.add(createDashboardButton("Lessons"));
		cardsPanel.add(createDashboardButton("Scheduling"));
		cardsPanel.add(createDashboardButton("Reports"));

		Panel wrapper = new Panel(new BorderLayout());
		wrapper.setBackground(new Color(245, 247, 250));

		Panel leftPadding = new Panel();
		leftPadding.setBackground(new Color(245, 247, 250));
		leftPadding.setPreferredSize(new Dimension(15, 0));

		Panel rightPadding = new Panel();
		rightPadding.setBackground(new Color(245, 247, 250));
		rightPadding.setPreferredSize(new Dimension(15, 0));

		Panel bottomPadding = new Panel();
		bottomPadding.setBackground(new Color(245, 247, 250));
		bottomPadding.setPreferredSize(new Dimension(0, 15));

		wrapper.add(leftPadding, BorderLayout.WEST);
		wrapper.add(rightPadding, BorderLayout.EAST);
		wrapper.add(bottomPadding, BorderLayout.SOUTH);
		wrapper.add(cardsPanel, BorderLayout.CENTER);

		mainPanel.add(headerPanel, BorderLayout.NORTH);
		mainPanel.add(wrapper, BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);
	}

	private Button createDashboardButton(String text) {
		Button button = new Button(text);
		button.setFont(new Font("Arial", Font.BOLD, 16));
		button.setBackground(new Color(230, 235, 242));
		button.setForeground(new Color(30, 45, 70));
		button.setActionCommand(text);
		button.addActionListener(this);
		return button;
	}

	private void clearMainPanel() {
		remove(mainPanel);

		mainPanel = new Panel(new BorderLayout(20, 20));
		mainPanel.setBackground(new Color(245, 247, 250));

		add(mainPanel, BorderLayout.CENTER);
		validate();
		repaint();
	}



	// ======================================================
	// Menu Building
	// Creates the menu bar and all application menus
	// ======================================================

	// Builds the main menu bar 
	private void buildMenuBar() {



		// Create the main menu bar object.
		menuBar = new MenuBar();

		// Build all menus.
		buildFileMenu();
		buildManageMenu();
		buildViewMenu();
		buildReportsMenu();
		buildHelpMenu();


		// Add menus to the menu bar.
		menuBar.add(fileMenu);
		menuBar.add(manageMenu);
		menuBar.add(viewMenu);
		menuBar.add(optionsMenu);
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

		/*
  MenuItem rescheduleLessonItem = new MenuItem("Reschedule Lesson");
  rescheduleLessonItem.addActionListener(this);
  manageMenu.add(rescheduleLessonItem);
		 */
	}
	//Builds the "View" menu.

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

		//Show room capacity checkBox 
		viewMenu.addSeparator();
		showCapacityItem = new CheckboxMenuItem("Show Room Capacity", true);
		viewMenu.add(showCapacityItem);
	}


	//Builds the "Help" menu.This menu contains general information about the system.
	private void buildHelpMenu() {

		helpMenu = new Menu("Help");

		MenuItem instructionsItem = new MenuItem("Instructions");
		instructionsItem.addActionListener(this);
		helpMenu.add(instructionsItem);

		MenuItem aboutItem = new MenuItem("About Schedemy");
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);
	}
	// reports
	private void buildReportsMenu() {

		optionsMenu = new Menu("Reports");

		MenuItem lecturerLoadItem =
				new MenuItem("Calculate Lecturer Load");

		MenuItem studentLoadItem =
				new MenuItem("Calculate Student Load");

		MenuItem roomLoadItem =
				new MenuItem("Calculate Room Load");

		MenuItem moreItem =
				new MenuItem("More...");

		optionsMenu.add(lecturerLoadItem);
		optionsMenu.add(studentLoadItem);
		optionsMenu.add(roomLoadItem);

		optionsMenu.addSeparator();
		optionsMenu.add(moreItem);

		lecturerLoadItem.addActionListener(this);
		studentLoadItem.addActionListener(this);
		roomLoadItem.addActionListener(this);
		moreItem.addActionListener(this);

		menuBar.add(optionsMenu);
	}

	private void buildPopupMenu() {

		reportsPopup = new PopupMenu();

		MenuItem lecturerTimetableItem =
				new MenuItem("Lecturer Timetable");

		MenuItem groupTimetableItem =
				new MenuItem("Student Group Timetable");

		lecturerTimetableItem.addActionListener(this);
		groupTimetableItem.addActionListener(this);

		reportsPopup.add(lecturerTimetableItem);
		reportsPopup.add(groupTimetableItem);

		add(reportsPopup);
	}


	// ======================================================
	// Event Handling
	// Handles user actions and menu commands
	// ======================================================

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		System.out.println("Clicked command: " + command);

		// ======================================================
		// Home Navigation
		// ======================================================

		if (command.equals("HOME")) {
			remove(mainPanel);
			buildMainPanel();
			validate();
			repaint();
			return;
		}

		// ======================================================
		// Main Dashboards
		// ======================================================

		if (command.equals("Courses")) {
			showCoursesDashboard();
			return;
		}

		if (command.equals("Lecturers")) {
			showLecturersDashboard();
			return;
		}

		if (command.equals("Rooms")) {
			showRoomsDashboard();
			return;
		}

		if (command.equals("Student Groups")) {
			showStudentGroupsDashboard();
			return;
		}

		if (command.equals("Lessons")) {
			showLessonsDashboard();
			return;
		}

		if (command.equals("Scheduling")) {
			showSchedulingDashboard();
			return;
		}

		if (command.equals("Reports")) {
			showReportsDashboard();
			return;
		}

		// ======================================================
		// Details Screens
		// ======================================================

		if (command.startsWith("COURSE_NAME_")) {
			String courseName = command.replace("COURSE_NAME_", "");
			showCourseDetailsByName(courseName);
			return;
		}

		if (command.startsWith("LECTURER_DETAILS_")) {
			String lecturerID = command.replace("LECTURER_DETAILS_", "");
			showLecturerDetails(lecturerID);
			return;
		}

		if (command.startsWith("ROOM_DETAILS_")) {
			String details = command.replace("ROOM_DETAILS_", "");
			String[] parts = details.split("_");

			String roomID = parts[0];
			int building = Integer.parseInt(parts[1]);

			showRoomDetails(roomID, building);
			return;
		}

		if (command.startsWith("GROUP_DETAILS_")) {
			int groupID = Integer.parseInt(command.replace("GROUP_DETAILS_", ""));
			showStudentGroupDetails(groupID);
			return;
		}

		// ======================================================
		// Lesson Screens
		// ======================================================

		if (command.equals("LESSONS_BY_COURSE")) {
			showLessonsByCourseSelection();
			return;
		}

		if (command.startsWith("LESSONS_COURSE_ID_")) {
			int courseID = Integer.parseInt(command.replace("LESSONS_COURSE_ID_", ""));
			showLessonsOfCourseID(courseID);
			return;
		}

		if (command.equals("LESSONS_BY_LECTURER")) {
			showLessonsByLecturerSelection();
			return;
		}

		if (command.startsWith("LESSONS_LECTURER_")) {
			String lecturerID = command.replace("LESSONS_LECTURER_", "");
			showLessonsOfLecturer(lecturerID);
			return;
		}

		if (command.equals("LESSONS_BY_ROOM")) {
			showLessonsByRoomSelection();
			return;
		}

		if (command.startsWith("LESSONS_ROOM_")) {
			String details = command.replace("LESSONS_ROOM_", "");
			String[] parts = details.split("_");
			String roomID = parts[0];
			int building = Integer.parseInt(parts[1]);
			showLessonsOfRoom(roomID, building);
			return;
		}
		if (command.equals("LESSONS_BY_GROUP")) {
			showLessonsByGroupSelection();
			return;
		}

		if (command.startsWith("LESSONS_GROUP_")) {
			int groupID = Integer.parseInt(command.replace("LESSONS_GROUP_", ""));
			showLessonsOfStudentGroup(groupID);
			return;
		}

		// ======================================================
		// File Menu Actions
		// ======================================================

		if (command.equals("Save Data")) {
			try {
				system.saveDataToFile();
				JOptionPane.showMessageDialog(this, "Data saved successfully!",
						"Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		if (command.equals("Load Data")) {
			try {
				system.loadDataFromFile();
				updateNextIDs();

				JOptionPane.showMessageDialog(this, "Data loaded successfully!",
						"Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		if (command.equals("Exit")) {
			confirmExit();
			return;
		}

		// ======================================================
		// Add Dialogs
		// ======================================================

		if (command.equals("Add Room")) {
			openAddRoomDialog();
			return;
		}

		if (command.equals("Add Course")) {
			openAddCourseDialog();
			return;
		}

		if (command.equals("Add Student Group")) {
			openAddStudentGroupDialog();
			return;
		}

		if (command.equals("Add Lecturer")) {
			openAddLecturerDialog();
			return;
		}

		if (command.equals("Add Lesson")) {
			openAddLessonDialog();
			return;
		}

		// ======================================================
		// Scheduling Actions
		// ======================================================

		if (command.equals("Cancel Lesson")) {
			openCancelLessonDialog();
			return;
		}

		if (command.equals("Reschedule Lesson")) {
			openRescheduleLessonDialog();
			return;
		}



		// ======================================================
		// Reports and Timetable Views
		// ======================================================

		if (command.equals("Calculate Lecturer Load")) {
			openLecturerLoadDialog();
			return;
		}

		if (command.equals("Calculate Student Load")) {
			openStudentLoadDialog();
			return;
		}

		if (command.equals("Calculate Room Load")) {
			openRoomLoadDialog();
			return;
		}

		if (command.equals("More...")) {
			reportsPopup.show(this, 250, 80);
			return;
		}

		if (command.equals("Lecturer Timetable")) {
			openLecturerTimetableDialog();
			return;
		}

		if (command.equals("Student Group Timetable")) {
			openStudentGroupTimetableDialog();
			return;
		}



		// ======================================================
		// Help
		// ======================================================

		if (command.equals("Instructions")) {
			JOptionPane.showMessageDialog(
					this,
					"Use Manage to add or update data.\n" +
							"Use View to display rooms, lecturers, courses and timetables.\n" +
							"Use File to save or load system data.",
							"Schedamy Instructions",
							JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (command.equals("About Schedemy")) {
			JOptionPane.showMessageDialog(
					this,
					"Schedemy System\n" +
							"Software Engineering Project\n" +
							"By Idan Zilcha, Orel Levi, Eyal Shigris and Ola Jangirian\n" +
							"We are four third-year engineering students who experienced many schedule and classroom changes\n" +
							"during our studies due to unexpected events such as viruses, wars, and maybe even alien invasions.\n" +
							"To improve and simplify the process, we created Schedemy - a smart scheduling system,\n" +
							"designed to manage lesson cancellations, rescheduling, classroom changes, and lecturer availability more efficiently.\n" +
							"The name \"Schedemy\" combines the words Schedule + Academy,\n" +
							"and also sounds like the Hebrew word \"Shakadnu\" (we worked hard) - which we definitely did.\n",
							"About Schedemy",
							JOptionPane.INFORMATION_MESSAGE);
			return;
		}

	}



	// ======================================================
	// Dash boards
	// Main navigation screens of the application
	// ======================================================

	private void showCoursesDashboard() {
		clearMainPanel();

		Label title = new Label("Courses", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel coursesPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		coursesPanel.setBackground(new Color(245, 247, 250));

		ArrayList<String> courseNames = new ArrayList<String>();

		for (Course course : system.getCourses()) {
			if (!courseNames.contains(course.getCourseName())) {
				courseNames.add(course.getCourseName());

				Button button = createDashboardButton(course.getCourseName());
				button.setPreferredSize(new Dimension(250, 90));
				button.setActionCommand("COURSE_NAME_" + course.getCourseName());
				coursesPanel.add(button);
			}
		}

		Button backButton = createBackButton("Back to Home", "HOME");

		mainPanel.add(title, BorderLayout.NORTH);

		setCardsPanelSize(coursesPanel, courseNames.size());
		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(coursesPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLecturersDashboard() {
		clearMainPanel();

		Label title = new Label("Lecturers", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel lecturersPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		lecturersPanel.setBackground(new Color(245, 247, 250));

		for (Lecturer lecturer : system.getLecturers()) {
			String name = lecturer.getFirstName() + " " + lecturer.getLastName();

			Button button = createDashboardButton(name);
			button.setPreferredSize(new Dimension(250, 90));
			button.setActionCommand("LECTURER_DETAILS_" + lecturer.getLecturerID());

			lecturersPanel.add(button);
		}

		Button backButton = createBackButton("Back to Home", "HOME");

		mainPanel.add(title, BorderLayout.NORTH);

		setCardsPanelSize(lecturersPanel, system.getLecturers().size());
		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(lecturersPanel);

		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showStudentGroupsDashboard() {
		clearMainPanel();

		Label title = new Label("Student Groups", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel groupsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		groupsPanel.setBackground(new Color(245, 247, 250));

		for (StudentGroup group : system.getStudentGroups()) {
			Button button = createDashboardButton(
					shortenDepartment(group.getDepartment()) +
					" | Y" + group.getStudyYear() +
					" | " + group.getProgramName()
					);
			button.setPreferredSize(new Dimension(250, 90));
			button.setActionCommand("GROUP_DETAILS_" + group.getGroupID());

			groupsPanel.add(button);
		}

		Button backButton = createBackButton("Back to Home", "HOME");

		mainPanel.add(title, BorderLayout.NORTH);

		setCardsPanelSize(groupsPanel, system.getStudentGroups().size());
		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(groupsPanel);

		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showRoomsDashboard() {
		clearMainPanel();

		Label title = new Label("Rooms", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel roomsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		roomsPanel.setBackground(new Color(245, 247, 250));


		for (Room room : system.getRooms()) {
			String roomText = "Room " + room.getRoomID() + " | Building " + room.getBuilding();
			if (showCapacityItem != null && showCapacityItem.getState()) {
				roomText += " | Capacity " + room.getCapacity();
			}

			Button button = createDashboardButton(roomText);
			button.setFont(new Font("Arial", Font.BOLD, 13));
			button.setPreferredSize(new Dimension(250, 90));
			button.setActionCommand("ROOM_DETAILS_" + room.getRoomID() + "_" + room.getBuilding());

			roomsPanel.add(button);
		}

		Button backButton = createBackButton("Back to Home", "HOME");
		mainPanel.add(title, BorderLayout.NORTH);

		setCardsPanelSize(roomsPanel, system.getRooms().size());
		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(roomsPanel);

		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLessonsDashboard() {
		clearMainPanel();

		Label title = new Label("Lessons", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel optionsPanel =  new Panel(new GridLayout(0, 2, 25, 25));
		optionsPanel.setBackground(new Color(245, 247, 250));

		Button byCourse = createActionButton("By Course", "LESSONS_BY_COURSE");
		Button byLecturer = createActionButton("By Lecturer", "LESSONS_BY_LECTURER");
		Button byRoom = createActionButton("By Room", "LESSONS_BY_ROOM");
		Button byGroup = createActionButton("By Student Group", "LESSONS_BY_GROUP");

		optionsPanel.add(byCourse);
		optionsPanel.add(byLecturer);
		optionsPanel.add(byRoom);
		optionsPanel.add(byGroup);

		Button backButton = createBackButton("Back to Home", "HOME");

		mainPanel.add(title, BorderLayout.NORTH);

		Panel wrapper = new Panel(new BorderLayout());
		wrapper.setBackground(new Color(245, 247, 250));

		Panel topPadding = new Panel();
		topPadding.setPreferredSize(new Dimension(0, 20));

		Panel leftPadding = new Panel();
		leftPadding.setPreferredSize(new Dimension(25, 0));

		Panel rightPadding = new Panel();
		rightPadding.setPreferredSize(new Dimension(25, 0));

		wrapper.add(topPadding, BorderLayout.NORTH);
		wrapper.add(leftPadding, BorderLayout.WEST);
		wrapper.add(rightPadding, BorderLayout.EAST);
		wrapper.add(optionsPanel, BorderLayout.CENTER);

		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(wrapper);

		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showSchedulingDashboard() {
		clearMainPanel();

		Label title = new Label("Scheduling", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel optionsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 25, 25));

		optionsPanel.setBackground(new Color(245, 247, 250));

		Button addLessonButton = createActionButton("Add Lesson", "Add Lesson");
		Button cancelLessonButton = createActionButton("Cancel Lesson", "Cancel Lesson");

		optionsPanel.add(addLessonButton);
		optionsPanel.add(cancelLessonButton);
		/*
        optionsPanel.add(rescheduleLessonButton);
		 */


		Button backButton = createBackButton("Back to Home", "HOME");

		mainPanel.add(title, BorderLayout.NORTH);

		Panel wrapper = new Panel(new BorderLayout());
		wrapper.setBackground(new Color(245, 247, 250));

		Panel topPadding = new Panel();
		topPadding.setPreferredSize(new Dimension(0, 20));

		Panel leftPadding = new Panel();
		leftPadding.setPreferredSize(new Dimension(25, 0));

		Panel rightPadding = new Panel();
		rightPadding.setPreferredSize(new Dimension(25, 0));

		wrapper.add(topPadding, BorderLayout.NORTH);
		wrapper.add(leftPadding, BorderLayout.WEST);
		wrapper.add(rightPadding, BorderLayout.EAST);
		wrapper.add(optionsPanel, BorderLayout.CENTER);

		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(wrapper);

		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showReportsDashboard() {
		clearMainPanel();

		Label title = new Label("Reports", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel optionsPanel =  new Panel(new GridLayout(0, 2, 25, 25));
		optionsPanel.setBackground(new Color(245, 247, 250));

		Button lecturerLoadButton = createActionButton("Lecturer Load", "Calculate Lecturer Load");
		Button studentLoadButton = createActionButton("Student Group Load", "Calculate Student Load");
		Button roomLoadButton = createActionButton("Room Load", "Calculate Room Load");
		Button lecturerTimetableButton = createActionButton("Lecturer Timetable", "Lecturer Timetable");
		Button groupTimetableButton = createActionButton("Student Group Timetable", "Student Group Timetable");

		optionsPanel.add(lecturerTimetableButton);
		optionsPanel.add(groupTimetableButton);
		optionsPanel.add(lecturerLoadButton);
		optionsPanel.add(studentLoadButton);
		optionsPanel.add(roomLoadButton);


		Button backButton = createBackButton("Back to Home", "HOME");

		mainPanel.add(title, BorderLayout.NORTH);

		Panel wrapper = new Panel(new BorderLayout());
		wrapper.setBackground(new Color(245, 247, 250));

		Panel topPadding = new Panel();
		topPadding.setPreferredSize(new Dimension(0, 20));

		Panel leftPadding = new Panel();
		leftPadding.setPreferredSize(new Dimension(25, 0));

		Panel rightPadding = new Panel();
		rightPadding.setPreferredSize(new Dimension(25, 0));

		wrapper.add(topPadding, BorderLayout.NORTH);
		wrapper.add(leftPadding, BorderLayout.WEST);
		wrapper.add(rightPadding, BorderLayout.EAST);
		wrapper.add(optionsPanel, BorderLayout.CENTER);

		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(wrapper);

		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}



	// ======================================================
	// Details Screens
	// Displays detailed information about system entities
	// ======================================================

	private void showCourseDetailsByName(String courseName) {
		clearMainPanel();

		Label title = new Label(courseName, Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel detailsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		detailsPanel.setBackground(new Color(245, 247, 250));

		boolean found = false;

		for (Course course : system.getCourses()) {
			if (course.getCourseName().equals(courseName)) {

				found = true;

				String lecturerText = "No lecturer";
				String groupText = "No student group";

				for (AssignedToTeach assigned : system.getAssignedToTeachList()) {
					if (assigned.getCourse().getCourseID() == course.getCourseID()) {
						Lecturer lecturer = assigned.getLecturer();
						lecturerText = lecturer.getFirstName() + " " + lecturer.getLastName();
						break;
					}
				}

				for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
					if (enrolment.getCourse().getCourseID() == course.getCourseID()) {
						StudentGroup group = enrolment.getGroup();
						groupText = group.getDepartment() +
								" | Year " + group.getStudyYear() +
								" | " + group.getProgramName();
						break;
					}
				}

				Panel card = new Panel(new GridLayout(3, 1));
				card.setPreferredSize(new Dimension(650, 90));
				card.setBackground(Color.WHITE);

				Label groupLabel = new Label(groupText, Label.CENTER);
				groupLabel.setFont(new Font("Arial", Font.BOLD, 14));

				Label lecturerLabel = new Label("Lecturer: " + lecturerText, Label.CENTER);
				lecturerLabel.setFont(new Font("Arial", Font.PLAIN, 13));

				Label detailsLabel = new Label(
						"Credits: " + course.getCredits() +
						" | " + course.getCourseType() +
						" | Lessons: " + course.getLessons().size(),
						Label.CENTER
						);
				detailsLabel.setFont(new Font("Arial", Font.PLAIN, 13));

				card.add(groupLabel);
				card.add(lecturerLabel);
				card.add(detailsLabel);

				detailsPanel.add(card);
			}
		}

		if (!found) {
			detailsPanel.add(new Label("No course details found.", Label.CENTER));
		}

		Button backButton = createBackButton("Back to Courses", "Courses");


		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(detailsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLecturerDetails(String lecturerID) {
		clearMainPanel();

		Lecturer selectedLecturer = null;

		for (Lecturer lecturer : system.getLecturers()) {
			if (lecturer.getLecturerID().equals(lecturerID)) {
				selectedLecturer = lecturer;
				break;
			}
		}

		if (selectedLecturer == null) {
			JOptionPane.showMessageDialog(this, "Lecturer not found");
			return;
		}

		Label title = new Label(
				selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName(),
				Label.CENTER
				);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel detailsPanel = new Panel(new GridLayout(0, 1, 20, 20));
		detailsPanel.setBackground(new Color(245, 247, 250));

		Panel infoCard = new Panel(new GridLayout(3, 1));
		infoCard.setPreferredSize(new Dimension(650, 90));
		infoCard.setBackground(Color.WHITE);

		Label idLabel = new Label("Lecturer ID: " + selectedLecturer.getLecturerID(), Label.CENTER);
		idLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label scoreLabel = new Label(
				"Teaching Score: " + selectedLecturer.getTeachingScore() +
				" | FTE: " + selectedLecturer.getFTE(),
				Label.CENTER
				);
		scoreLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label specsLabel = new Label(
				"Specializations: " + selectedLecturer.getSpecializations(),
				Label.CENTER
				);
		specsLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		infoCard.add(idLabel);
		infoCard.add(scoreLabel);
		infoCard.add(specsLabel);

		detailsPanel.add(infoCard);

		for (AssignedToTeach assigned : system.getAssignedToTeachList()) {
			if (assigned.getLecturer().getLecturerID().equals(selectedLecturer.getLecturerID())) {
				Course course = assigned.getCourse();

				Panel courseCard = new Panel(new GridLayout(3, 1));
				courseCard.setPreferredSize(new Dimension(650, 90));
				courseCard.setBackground(Color.WHITE);

				Label courseLabel = new Label(course.getCourseName(), Label.CENTER);
				courseLabel.setFont(new Font("Arial", Font.BOLD, 14));

				Label courseDetailsLabel = new Label(
						"Credits: " + course.getCredits() +
						" | " + course.getCourseType() +
						" | Lessons: " + course.getLessons().size(),
						Label.CENTER
						);
				courseDetailsLabel.setFont(new Font("Arial", Font.PLAIN, 13));

				Label hoursLabel = new Label(
						"Course hours: " + course.calculateCourseHours(),
						Label.CENTER
						);
				hoursLabel.setFont(new Font("Arial", Font.PLAIN, 13));

				courseCard.add(courseLabel);
				courseCard.add(courseDetailsLabel);
				courseCard.add(hoursLabel);

				detailsPanel.add(courseCard);
			}
		}
		Button backButton = createBackButton("Back to Lecturers", "Lecturers");

		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(detailsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showRoomDetails(String roomID,int building) {
		clearMainPanel();
		Room selectedRoom = null;
		for (Room room : system.getRooms()) 
		{
			if (room.getRoomID().equals(roomID) && room.getBuilding() == building)
			{
				selectedRoom = room;
				break;
			}
		}

		if (selectedRoom == null) {
			JOptionPane.showMessageDialog(this, "Room not found");
			return;
		}

		Label title = new Label("Room " + selectedRoom.getRoomID() +" | Building " + selectedRoom.getBuilding(),Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel detailsPanel = new Panel(new GridLayout(0, 1, 20, 20));
		detailsPanel.setBackground(new Color(245, 247, 250));

		Panel infoCard = new Panel(new GridLayout(5, 1));
		infoCard.setPreferredSize(new Dimension(650, 130));
		infoCard.setBackground(Color.WHITE);

		Label buildingLabel = new Label("Building: " + selectedRoom.getBuilding(), Label.CENTER);
		buildingLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label typeLabel = new Label("Type: " + selectedRoom.getRoomType(), Label.CENTER);
		typeLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label capacityLabel = new Label("Capacity: " + selectedRoom.getCapacity(), Label.CENTER);
		capacityLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label sizeLabel = new Label("Room Size: " + selectedRoom.classifyRoomSize(), Label.CENTER);
		sizeLabel.setFont(new Font("Arial", Font.BOLD, 14));

		infoCard.add(buildingLabel);
		infoCard.add(typeLabel);
		infoCard.add(capacityLabel);
		infoCard.add(sizeLabel);

		detailsPanel.add(infoCard);
		Button backButton = createBackButton("Back to Rooms", "Rooms");

		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(detailsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showStudentGroupDetails(int groupID) {
		clearMainPanel();

		StudentGroup selectedGroup = null;

		for (StudentGroup group : system.getStudentGroups()) {
			if (group.getGroupID() == groupID) {
				selectedGroup = group;
				break;
			}
		}

		if (selectedGroup == null) {
			JOptionPane.showMessageDialog(this, "Student group not found");
			return;
		}

		Label title = new Label(
				selectedGroup.getDepartment() + " | Year " + selectedGroup.getStudyYear(),
				Label.CENTER
				);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel detailsPanel = new Panel(new GridLayout(0, 1, 20, 20));
		detailsPanel.setBackground(new Color(245, 247, 250));

		Panel infoCard = new Panel(new GridLayout(3, 1));
		infoCard.setPreferredSize(new Dimension(650, 90));
		infoCard.setBackground(Color.WHITE);

		Label idLabel = new Label("Group ID: " + selectedGroup.getGroupID(), Label.CENTER);
		idLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label programLabel = new Label(
				"Program: " + selectedGroup.getProgramName() +
				" | Students: " + selectedGroup.getStudentCount(),
				Label.CENTER
				);
		programLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label departmentLabel = new Label(
				"Department: " + selectedGroup.getDepartment(),
				Label.CENTER
				);
		departmentLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		infoCard.add(idLabel);
		infoCard.add(programLabel);
		infoCard.add(departmentLabel);

		detailsPanel.add(infoCard);

		for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
			if (enrolment.getGroup().getGroupID() == selectedGroup.getGroupID()) {
				Course course = enrolment.getCourse();

				Panel courseCard = new Panel(new GridLayout(3, 1));
				courseCard.setPreferredSize(new Dimension(650, 90));
				courseCard.setBackground(Color.WHITE);

				Label courseLabel = new Label(course.getCourseName(), Label.CENTER);
				courseLabel.setFont(new Font("Arial", Font.BOLD, 14));

				Label detailsLabel = new Label(
						"Credits: " + course.getCredits() +
						" | " + course.getCourseType() +
						" | Lessons: " + course.getLessons().size(),
						Label.CENTER
						);
				detailsLabel.setFont(new Font("Arial", Font.PLAIN, 13));

				Label attendanceLabel = new Label(
						"Attendance Required: " + enrolment.isAttendanceRequired(),
						Label.CENTER
						);
				attendanceLabel.setFont(new Font("Arial", Font.PLAIN, 13));

				courseCard.add(courseLabel);
				courseCard.add(detailsLabel);
				courseCard.add(attendanceLabel);

				detailsPanel.add(courseCard);
			}
		}
		Button backButton = createBackButton("Back to Student Groups", "Student Groups");


		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(detailsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}



	// ======================================================
	// Lesson Screens
	// Displays lessons by course, lecturer, room or group
	// ======================================================

	private Panel createLessonCard(Course course, Lesson lesson) {
		Panel card = new Panel(new GridLayout(4, 1));
		card.setPreferredSize(new Dimension(650, 110));
		card.setBackground(Color.WHITE);

		String roomText = "Zoom";
		if (lesson.getRoom() != null) {
			roomText = "Room " + lesson.getRoom().getRoomID();
		}
		StudentGroup group = system.getGroupForCourse(course);
		Lecturer lecturer = system.getLecturerForCourse(course);
		String courseText = course.getCourseName();

		if (lecturer != null)
		{
			courseText += " | " +
					lecturer.getFirstName() + " " +
					lecturer.getLastName();
		}
		if (group != null)
		{
			courseText += " | " +
					group.getDepartment() +
					" Year " + group.getStudyYear();
		}
		Label courseLabel = new Label(courseText, Label.CENTER);
		courseLabel.setFont(new Font("Arial", Font.BOLD, 14));

		Label dateLabel = new Label(
				lesson.getLessonDate().format(DATE_FORMAT) +
				" | " + lesson.getStartTime() + " - " + lesson.getEndTime(),
				Label.CENTER
				);
		dateLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label placeLabel = new Label(
				roomText + " | " + lesson.getTeachingMode(),
				Label.CENTER
				);
		placeLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		Label statusLabel = new Label(
				"Status: " + getFriendlyStatus(lesson.getStatus()),
				Label.CENTER
				);
		statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));

		card.add(courseLabel);
		card.add(dateLabel);
		card.add(placeLabel);
		card.add(statusLabel);

		return card;
	}

	// ------------------------------------------------------
	// Lessons By Course
	// ------------------------------------------------------
	private void showLessonsByCourseSelection() {
		clearMainPanel();

		Label title = new Label("Lessons By Course", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel coursesPanel =  new Panel(new GridLayout(0, 2, 25, 25));
		coursesPanel.setBackground(new Color(245, 247, 250));

		ArrayList<String> courseNames = new ArrayList<String>();

		for (Course course : system.getCourses()) {
			courseNames.add(course.getCourseName());

			Lecturer lecturer = system.getLecturerForCourse(course);
			StudentGroup group = system.getGroupForCourse(course);

			String buttonText = course.getCourseName();

			if (lecturer != null) {
				buttonText += " | " + lecturer.getFirstName() + " " + lecturer.getLastName();
			}

			if (group != null) {
				buttonText += " | " + shortenDepartment(group.getDepartment()) +
						" Y" + group.getStudyYear();
			}

			Button button = createDashboardButton(buttonText);
			button.setPreferredSize(new Dimension(250, 90));
			button.setFont(new Font("Arial", Font.BOLD, 14));

			// Action command must stay clean
			button.setActionCommand("LESSONS_COURSE_ID_" + course.getCourseID());

			coursesPanel.add(button);
		}
		Button backButton = createBackButton("Back to Lessons", "Lessons");

		mainPanel.add(title, BorderLayout.NORTH);

		mainPanel.add(createPaddedScrollPane(coursesPanel), BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLessonsOfCourseID(int courseID) {
		clearMainPanel();

		Label title = new Label("Lessons - Course " + courseID, Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 15));

		Panel lessonsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		lessonsPanel.setBackground(new Color(245, 247, 250));

		boolean foundLessons = false;

		for (Course course : system.getCourses()) {
			if (course.getCourseID() == courseID) {
				for (Lesson lesson : course.getLessons()) {
					lessonsPanel.add(createLessonCard(course, lesson));
					foundLessons = true;
				}
			}
		}

		if (!foundLessons) {
			lessonsPanel.add(new Label("No lessons found for this course.", Label.CENTER));
		}
		Button backButton = createBackButton("Back to Courses", "LESSONS_BY_COURSE");

		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(lessonsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	// ------------------------------------------------------
	// Lessons By Lecturer
	// ------------------------------------------------------
	private void showLessonsByLecturerSelection() {
		clearMainPanel();

		Label title = new Label("Lessons By Lecturer", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel lecturersPanel = new Panel(new GridLayout(0, 2, 25, 25));
		lecturersPanel.setBackground(new Color(245, 247, 250));

		for (Lecturer lecturer : system.getLecturers()) {
			String lecturerName = lecturer.getFirstName() + " " + lecturer.getLastName();

			Button button = createDashboardButton(lecturerName);
			button.setPreferredSize(new Dimension(250, 90));
			button.setActionCommand("LESSONS_LECTURER_" + lecturer.getLecturerID());

			lecturersPanel.add(button);
		}

		Button backButton = createBackButton("Back to Lessons", "Lessons");

		mainPanel.add(title, BorderLayout.NORTH);

		mainPanel.add(createPaddedScrollPane(lecturersPanel), BorderLayout.CENTER);
		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLessonsOfLecturer(String lecturerID) {
		clearMainPanel();

		Lecturer selectedLecturer = null;

		for (Lecturer lecturer : system.getLecturers()) {
			if (lecturer.getLecturerID().equals(lecturerID)) {
				selectedLecturer = lecturer;
				break;
			}
		}

		if (selectedLecturer == null) {
			JOptionPane.showMessageDialog(this, "Lecturer not found");
			return;
		}

		Label title = new Label(
				"Lessons - " + selectedLecturer.getFirstName() + " " + selectedLecturer.getLastName(),
				Label.CENTER
				);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel lessonsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		lessonsPanel.setBackground(new Color(245, 247, 250));

		boolean foundLesson = false;

		for (AssignedToTeach assigned : system.getAssignedToTeachList()) {
			if (assigned.getLecturer().getLecturerID().equals(selectedLecturer.getLecturerID())) {
				Course course = assigned.getCourse();

				for (Lesson lesson : course.getLessons()) {
					lessonsPanel.add(createLessonCard(course, lesson));
					foundLesson = true;
				}
			}
		}

		if (!foundLesson) {
			lessonsPanel.add(new Label("No lessons found for this lecturer.", Label.CENTER));
		}
		Button backButton = createBackButton("Back to Lecturers", "LESSONS_BY_LECTURER");

		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(lessonsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	// ------------------------------------------------------
	// Lessons By Room
	// ------------------------------------------------------  
	private void showLessonsByRoomSelection() {
		clearMainPanel();

		Label title = new Label("Lessons By Room", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel roomsPanel = new Panel(new GridLayout(0, 2, 25, 25));
		roomsPanel.setBackground(new Color(245, 247, 250));

		for (Room room : system.getRooms()) {

			Button button = createDashboardButton("Room " + room.getRoomID() + " | Building " + room.getBuilding());
			button.setPreferredSize(new Dimension(250, 90));
			button.setActionCommand("LESSONS_ROOM_" + room.getRoomID() + "_" + room.getBuilding());

			roomsPanel.add(button);
		}
		Button backButton = createBackButton("Back to Lessons", "Lessons");

		mainPanel.add(title, BorderLayout.NORTH);
		mainPanel.add(createPaddedScrollPane(roomsPanel), BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLessonsOfRoom(String roomID, int building) {
		clearMainPanel();

		Room selectedRoom = null;

		for (Room room : system.getRooms()) {
			if (room.getRoomID().equals(roomID) && room.getBuilding() == building) {
				selectedRoom = room;
				break;
			}
		}

		if (selectedRoom == null) {
			JOptionPane.showMessageDialog(this, "Room not found");
			return;
		}

		Label title = new Label("Lessons - Room " +selectedRoom.getRoomID() +" (Building " +selectedRoom.getBuilding() +")",Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel lessonsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		lessonsPanel.setBackground(new Color(245, 247, 250));

		boolean foundLesson = false;

		for (Course course : system.getCourses()) {
			for (Lesson lesson : course.getLessons()) {
				if (lesson.getRoom() != null &&
						lesson.getRoom().getRoomID().equals(selectedRoom.getRoomID()) &&
						lesson.getRoom().getBuilding() == selectedRoom.getBuilding()) 
				{
					lessonsPanel.add(createLessonCard(course, lesson));
					foundLesson = true;
				}
			}
		}

		if (!foundLesson) {
			lessonsPanel.add(new Label("No lessons found for this room.", Label.CENTER));
		}
		Button backButton = createBackButton("Back to Rooms", "LESSONS_BY_ROOM");

		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(lessonsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	// ------------------------------------------------------
	// Lessons By Student Group
	// ------------------------------------------------------
	private void showLessonsByGroupSelection() {
		clearMainPanel();

		Label title = new Label("Lessons By Student Group", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel groupsPanel = new Panel(new GridLayout(0, 2, 25, 25));;
		groupsPanel.setBackground(new Color(245, 247, 250));

		for (StudentGroup group : system.getStudentGroups()) {
			Button button = createDashboardButton(
					shortenDepartment(group.getDepartment()) + " | Year " + group.getStudyYear()
					);

			button.setPreferredSize(new Dimension(250, 90));
			button.setActionCommand("LESSONS_GROUP_" + group.getGroupID());

			groupsPanel.add(button);
		}
		Button backButton = createBackButton("Back to Lessons", "Lessons");

		mainPanel.add(title, BorderLayout.NORTH);
		mainPanel.add(createPaddedScrollPane(groupsPanel), BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}

	private void showLessonsOfStudentGroup(int groupID) {
		clearMainPanel();

		StudentGroup selectedGroup = null;

		for (StudentGroup group : system.getStudentGroups()) {
			if (group.getGroupID() == groupID) {
				selectedGroup = group;
				break;
			}
		}

		if (selectedGroup == null) {
			JOptionPane.showMessageDialog(this, "Student group not found");
			return;
		}

		Label title = new Label(
				"Lessons - " + selectedGroup.getDepartment() +
				" | Year " + selectedGroup.getStudyYear(),
				Label.CENTER
				);
		title.setFont(new Font("Arial", Font.BOLD, 28));

		Panel lessonsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		lessonsPanel.setBackground(new Color(245, 247, 250));

		boolean foundLesson = false;

		for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
			if (enrolment.getGroup().getGroupID() == selectedGroup.getGroupID()) {
				Course course = enrolment.getCourse();

				for (Lesson lesson : course.getLessons()) {
					lessonsPanel.add(createLessonCard(course, lesson));
					foundLesson = true;
				}
			}
		}

		if (!foundLesson) {
			lessonsPanel.add(new Label("No lessons found for this student group.", Label.CENTER));
		}
		Button backButton = createBackButton("Back to Student Groups", "LESSONS_BY_GROUP");

		mainPanel.add(title, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(lessonsPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(backButton, BorderLayout.SOUTH);

		validate();
		repaint();
	}



	// ======================================================
	// Add Dialogs
	// Creates dialogs for adding new system entities
	// ======================================================

	private void openAddLecturerDialog() {

		// Create a pop-up dialog window for adding a new lecturer
		Dialog dialog = new Dialog(this, "Add Lecturer", true);
		enableDialogCloseButton(dialog);
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
		specializationList.add("Industrial Engineering");
		specializationList.add("Economics");
		specializationList.add("Business Administration");
		specializationList.add("Psychology");
		specializationList.add("Nursing");
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
				String scoreText = teachingScoreField.getText().trim();
				if (!scoreText.matches("\\d+"))
				    throw new IllegalArgumentException("Teaching Score must contain only digits");
				double score = Double.parseDouble(teachingScoreField.getText().trim());
				if (!idField.getText().matches("\\d+"))
					throw new IllegalArgumentException("Lecturer ID must contain only digits");
				

				if (score > 100 || score < 0) {
					throw new IllegalArgumentException("Teaching Score must be between 0 and 100");
				}

				String id = idField.getText().trim();
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
		enableDialogCloseButton(dialog);
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
					throw new IllegalArgumentException("Room number must be a whole and positive number");

				if (!buildingField.getText().matches("\\d+"))
					throw new IllegalArgumentException("Building must be a whole and positive number");

				if (!capacityField.getText().matches("\\d+"))
					throw new IllegalArgumentException("Capacity must be a whole and positive number");
				if (Integer.parseInt(roomNumberField.getText()) < 0 ||
					    Integer.parseInt(roomNumberField.getText()) > 999)
					{
					    throw new IllegalArgumentException("Room number must be between 0 and 999");
					}
					if (Integer.parseInt(buildingField.getText()) < 0 ||
					    Integer.parseInt(buildingField.getText()) > 100)
					{
					    throw new IllegalArgumentException("Building number must be between 0 and 100");
					}
					if (Integer.parseInt(capacityField.getText()) < 0 ||
					    Integer.parseInt(capacityField.getText()) > 200)
					{
					    throw new IllegalArgumentException("Capacity must be between 0 and 200");
					}
				system.addRoom(
						roomNumberField.getText(),
						Integer.parseInt(buildingField.getText()),
						roomTypeChoice.getSelectedItem(),
						Integer.parseInt(capacityField.getText()));
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
		enableDialogCloseButton(dialog);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(420, 260);
		Panel formPanel = new Panel(new GridLayout(5,1,5,5));
		Choice courseNameChoice = new Choice();

		courseNameChoice.add("Java");
		courseNameChoice.add("Databases");
		courseNameChoice.add("Software Engineering");
		courseNameChoice.add("Algorithms");
		courseNameChoice.add("Mathematics");
		courseNameChoice.add("Electronics");
		courseNameChoice.add("Physics");
		courseNameChoice.add("Signals");
		courseNameChoice.add("Industrial Engineering");
		courseNameChoice.add("Economics");
		courseNameChoice.add("Business Administration");
		courseNameChoice.add("Psychology");
		courseNameChoice.add("Nursing");
		TextField creditsField = new TextField();

		Choice courseTypeChoice = new Choice();
		courseTypeChoice.add("mandatory");
		courseTypeChoice.add("elective");
		Choice lecturerChoice = new Choice();
		Choice groupChoice = new Choice();

		Runnable updateChoices = () -> {
			lecturerChoice.removeAll();
			groupChoice.removeAll();

			String selectedCourse = courseNameChoice.getSelectedItem();

			for (Lecturer lecturer : system.getLecturers())
			{
				if (lecturer.getSpecializations().contains(selectedCourse))
				{
					lecturerChoice.add(lecturer.getLecturerID() + " - " +
							lecturer.getFirstName() + " " + lecturer.getLastName());
				}
			}
			for (StudentGroup group : system.getStudentGroups())
			{
				if (system.isCourseRelevantToGroup(selectedCourse, group))
				{
					groupChoice.add(group.getGroupID() + "-" +
							group.getDepartment() + "  Year-" +
							group.getStudyYear() + " - " +
							group.getProgramName());
				}
			}
		};

		updateChoices.run();

		courseNameChoice.addItemListener(e -> {
			updateChoices.run();
		});
		Panel namePanel = new Panel(new BorderLayout());
		namePanel.add(new Label("Course Name:"), BorderLayout.WEST);
		namePanel.add(courseNameChoice, BorderLayout.CENTER);
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
				int courseID = nextCourseID; 
				if (lecturerChoice.getItemCount() == 0)
					throw new IllegalArgumentException("No lecturer matches this course");

				if (groupChoice.getItemCount() == 0)
					throw new IllegalArgumentException("No student group matches this course");
				String lecturerText = lecturerChoice.getSelectedItem();
				String groupText = groupChoice.getSelectedItem();
				String lecturerID = lecturerText.split(" - ")[0];
				int groupID = Integer.parseInt(groupText.split("-")[0]);
				String creditsText = creditsField.getText().trim();

				
				if (!creditsText.matches("\\d+"))
				    throw new IllegalArgumentException("Credits must be a whole and positive number");
				int credits = Integer.parseInt(creditsText);
				if (credits < 1 || credits > 10)
				    throw new IllegalArgumentException("Credits must be between 1 and 10");
				system.addCourse(courseID,courseNameChoice.getSelectedItem(),
						Integer.parseInt(creditsField.getText()),
						courseTypeChoice.getSelectedItem(),
						lecturerID,
						groupID);
				nextCourseID++;
				JOptionPane.showMessageDialog(this,
						"Course added successfully!\nTotal courses: " + system.getCourses().size());
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
		enableDialogCloseButton(dialog);
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
					throw new IllegalArgumentException("Student count must be a whole and positive number");
				system.addStudentGroup(nextGroupID, departmentChoice.getSelectedItem(),
						Integer.parseInt(studyYearChoice.getSelectedItem()),
						Integer.parseInt(studentCountField.getText()),
						programChoice.getSelectedItem());
				nextGroupID++;
				//success and error massages
				JOptionPane.showMessageDialog(
						this,
						"Student group added successfully!\n",
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

	private void openAddLessonDialog()
	{
		Dialog dialog = new Dialog(this, "Add Lesson", true);
		enableDialogCloseButton(dialog);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(750, 380);
		dialog.setLocationRelativeTo(this);

		Panel formPanel = new Panel(new GridLayout(7, 2, 5, 5));

		Choice courseChoice = new Choice();

		Vector<Room> roomsList = new Vector<Room>();

		for (Course course : system.getCourses())
		{
			courseChoice.add(getCourseDisplayName(course));
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

			if (modeChoice.getSelectedItem().equals("ZOOM")) {
				roomChoice.setEnabled(false);
				labCheckbox.setState(false);
				labCheckbox.setEnabled(false);
			} else {
				roomChoice.setEnabled(true);
				labCheckbox.setEnabled(true);
			}
		});
		Runnable updateRoomChoices = () -> {
			roomChoice.removeAll();
			roomsList.clear();

			for (Room room : system.getRooms())
			{
				if (labCheckbox.getState())
				{
					boolean isLab =
							room.getRoomType().equals("Laboratory") ||
							room.getRoomType().equals("Computer Lab");

					if (!isLab)
						continue;
				}
				roomChoice.add(
						"Room " + room.getRoomID() +" - Building " + room.getBuilding()+" - "+room.getRoomType() +" - Capacity: " + room.getCapacity());
				roomsList.add(room);
			}
		};
		updateRoomChoices.run();

		labCheckbox.addItemListener(e -> {
			updateRoomChoices.run();
		});
		formPanel.add(new Label("Course:"));
		formPanel.add(courseChoice);

		Panel datePanel = new Panel(new GridLayout(1, 3, 5, 5));
		datePanel.add(dayChoice);
		datePanel.add(monthChoice);
		datePanel.add(yearChoice);

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
				if (lessonDate.equals(LocalDate.now()))
				{
					if (!startTime.isAfter(LocalTime.now()))
					{
						throw new IllegalArgumentException(
								"Start time must be in the future for today's date");
					}
				}
				if (!endTime.isAfter(startTime))
					throw new IllegalArgumentException("End time must be after start time");

				Room selectedRoom = null;

				if (!modeChoice.getSelectedItem().equals("ZOOM")) {
					selectedRoom = roomsList.get(roomChoice.getSelectedIndex());
				}
				system.addLessonToCourse(
						courseID,
						nextLessonID,
						lessonDate,
						startTime,
						endTime,
						"SCHEDULED",
						modeChoice.getSelectedItem(),
						labCheckbox.getState(),
						selectedRoom
						);
				nextLessonID++;
				System.out.println("Lesson was added to course ID: " + courseID);
				System.out.println("Next lesson ID is now: " + nextLessonID);

				AssignedToTeach assigned = null;
				for (AssignedToTeach a : system.getAssignedToTeachList()) {
					if (a.getCourse().getCourseID() == courseID) {
						assigned = a;
						break;
					}
				}

				//CalculateHoursThread thread for checking if a lecturer is overloading after adding a lesson
				if (assigned != null)
				{
					AssignedToTeach finalAssigned = assigned;
					CalculateHoursThread thread = new CalculateHoursThread(finalAssigned);
					thread.start();
					thread.join();

					double totalHours = thread.getTotalHours();
					double requiredHours = assigned.getLecturer().getRequiredHours();

					System.out.println("Total hours: " + totalHours);
					System.out.println("Required hours: " + requiredHours);

					if(totalHours > requiredHours) {
						Lesson addedLesson = assigned.getCourse().getLessons().get(
								assigned.getCourse().getLessons().size() - 1
								);

						system.removeLesson(assigned.getCourse(), addedLesson);
						nextLessonID--;

						JOptionPane.showMessageDialog(
								this,
								"Cannot add lesson.\n\n" +
										assigned.getLecturer().getFirstName() + " " +
										assigned.getLecturer().getLastName() +
										" is overloaded.\n" +
										"Total Hours: " + totalHours +
										"\nRequired Hours: " + requiredHours +
										"\n\nThe lesson was not added.",
										"Lesson Not Added",
										JOptionPane.WARNING_MESSAGE
								);
						return;
					}
				}
				if (assigned != null &&
						system.isStudentGroupOverloaded(assigned.getCourse()))
				{
					Lesson addedLesson =
							assigned.getCourse().getLessons().get(
									assigned.getCourse().getLessons().size() - 1);

					system.removeLesson(assigned.getCourse(), addedLesson);
					nextLessonID--;

					StudentGroup group =
							system.getGroupForCourse(assigned.getCourse());

					JOptionPane.showMessageDialog(
							this,
							"Cannot add lesson.\n\n" +
									group.getDepartment() +
									" Year " +
									group.getStudyYear() +
									" is overloaded.\n\n" +
									"The lesson was not added.",
									"Lesson Not Added",
									JOptionPane.WARNING_MESSAGE
							);
					return;
				}
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



	// ======================================================
	// Scheduling Dialogs
	// Add, cancel and reschedule lessons
	// ======================================================

	private void openCancelLessonDialog()
	{
		Dialog dialog = new Dialog(this, "Cancel Lesson", true);
		enableDialogCloseButton(dialog);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(500, 220);
		dialog.setLocationRelativeTo(this);

		Panel formPanel = new Panel(new GridLayout(2, 2, 5, 5));

		Choice lessonChoice = new Choice();

		// This vector keeps  Lesson objects in the same order as the Choice list.
		Vector<Lesson> lessonsList = new Vector<Lesson>();
		Vector<Integer> courseIDs = new Vector<Integer>();

		for (Course course : system.getCourses()) {
			for (Lesson lesson : course.getLessons()) {
				if (!lesson.getStatus().equalsIgnoreCase("CANCELLED")&&
						lesson.getLessonDate().isAfter(LocalDate.now())){
					lessonChoice.add(
							course.getCourseID() + " - " +
									course.getCourseName() + " | Lesson " +
									lesson.getLessonID() + " | " +
									lesson.getLessonDate().format(DATE_FORMAT) + " " +
									lesson.getStartTime()    	
							);

					lessonsList.add(lesson);
					courseIDs.add(course.getCourseID());
				}
			}
		}

		if (lessonChoice.getItemCount() == 0) {
			JOptionPane.showMessageDialog(this,
					"There are no lessons available to cancel.",
					"No Lessons Found",
					JOptionPane.INFORMATION_MESSAGE);
			return; 
		}

		TextField reasonField = new TextField();

		formPanel.add(new Label("Lesson:"));
		formPanel.add(lessonChoice);

		formPanel.add(new Label("Cancel Reason:"));
		formPanel.add(reasonField);

		Panel buttonPanel = new Panel(new GridLayout(1, 2, 5, 5));
		Button cancelLessonButton = new Button("Cancel");
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

				//ask user before cancelling
				String [] options = {"Cancel Only", "Reschedule", "Go Back"};

				int choice = JOptionPane.showOptionDialog(
						this,
						"Would You Like To Reschedule?",
						"Cancel Lesson",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]);

				if (choice == 2 || choice == -1) {
					return; //Go back or closed dialog
				}

				// Get the selected lesson directly by index
				int selectedIndex = lessonChoice.getSelectedIndex();
				Lesson selectedLesson = lessonsList.get(selectedIndex);
				int courseID = courseIDs.get(selectedIndex);
				LocalDate newDate1 = selectedLesson.getLessonDate().plusWeeks(1);
				LocalDate newDate2 = selectedLesson.getLessonDate().plusWeeks(2);

				if (choice == 0) {
					system.cancelLesson(courseID, selectedLesson.getLessonID(), newDate1, sharedRoomLock);
					JOptionPane.showMessageDialog(
							this,
							"Lesson cancelled successfully!\nReason: " + reason,
							"Success", JOptionPane.INFORMATION_MESSAGE);
					dialog.dispose();
					return;
				}

				if (choice == 1) {
					dialog.dispose();
				}

				JOptionPane.showMessageDialog(
						this,
						"Searching for available slot...",
						"Searching",
						JOptionPane.INFORMATION_MESSAGE);

				Lecturer lecturer = null;
				for (AssignedToTeach a : system.getAssignedToTeachList()) {
					if (a.getCourse().getCourseID() == courseID) {
						lecturer = a.getLecturer();
						break;
					}
				}

				AvailabilityThread availThread1 = new AvailabilityThread(
						lecturer, selectedLesson, newDate1,
						new Vector<>(system.getRooms()),
						sharedRoomLock,
						new Vector<>(system.getGroupEnrolments()), false);
				AvailabilityThread availThread2 = new AvailabilityThread(
						lecturer, selectedLesson, newDate2,
						new Vector<>(system.getRooms()),
						sharedRoomLock,
						new Vector<>(system.getGroupEnrolments()), false);

				Thread t1 = new Thread(availThread1);
				Thread t2 = new Thread(availThread2);
				t1.start();
				t2.start();

				t1.join();
				t2.join();

				String suggestion1 = availThread1.getSuggestion();
				String suggestion2 = availThread2.getSuggestion();

				if(suggestion1.isEmpty() && suggestion2.isEmpty()) {
					//neither found a slot
					JOptionPane.showMessageDialog(this,
							"No Available slot found automatically.\nPlease Reshcedule Manually.",
							"No Slot Found",
							JOptionPane.WARNING_MESSAGE);
					SwingUtilities.invokeLater(() -> openRescheduleLessonDialog());
				} else {
					// Build the message showing available options
					String message = "Available slots found!\n\n";

					ArrayList<String> optionsList = new ArrayList<>();

					if (!suggestion1.isEmpty()) {
						message += "Option 1:\n" + suggestion1 + "\n\n";
						optionsList.add("Option 1");
					}
					if (!suggestion2.isEmpty()) {
						message += "Option 2:\n" + suggestion2 + "\n\n";
						optionsList.add("Option 2");
					}
					optionsList.add("Pick Manually");

					message += "Lecturer: " + lecturer.getFirstName() + " " + lecturer.getLastName() +
							"\nStudent Group: " + system.getGroupForCourse(courseID);

					String[] option = optionsList.toArray(new String[0]);

					int approveChoice = JOptionPane.showOptionDialog(
							this,
							message,
							"Slots Found",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE,
							null,
							option,
							option[0]);

					// User picked Option 1
					if (approveChoice == 0 && !suggestion1.isEmpty()) {
						selectedLesson.setLessonDate(availThread1.getSuggestedDate());
						selectedLesson.setStatus("RESCHEDULED");
						if (availThread1.getSuggestedRoom() != null) {
							selectedLesson.setRoom(availThread1.getSuggestedRoom());
							availThread1.getSuggestedRoom().setStatus("SCHEDULED");
						}
						JOptionPane.showMessageDialog(this,
								"Lesson rescheduled successfully!",
								"Success", JOptionPane.INFORMATION_MESSAGE);

						// User picked Option 2
					} else if (approveChoice == 1 && !suggestion2.isEmpty() && suggestion1.isEmpty() == false) {
						selectedLesson.setLessonDate(availThread2.getSuggestedDate());
						selectedLesson.setStatus("RESCHEDULED");
						if (availThread2.getSuggestedRoom() != null) {
							selectedLesson.setRoom(availThread2.getSuggestedRoom());
							availThread2.getSuggestedRoom().setStatus("SCHEDULED");
						}
						JOptionPane.showMessageDialog(this,
								"Lesson rescheduled successfully!",
								"Success", JOptionPane.INFORMATION_MESSAGE);

						// User picked Pick Manually 
					} else {
						SwingUtilities.invokeLater(() -> openRescheduleLessonDialog());
					}
				}

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
		enableDialogCloseButton(dialog);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(600, 300);

		Panel formPanel = new Panel(new GridLayout(6, 2, 5, 5));

		Choice lessonChoice = new Choice();
		Vector<Lesson> lessonsList = new Vector<Lesson>();

		for (Course course : system.getCourses()) {
			for (Lesson lesson : course.getLessons()) {
				lessonChoice.add(
						course.getCourseName() + " | Lesson " +
								lesson.getLessonID() + " | " +
								lesson.getLessonDate().format(DATE_FORMAT) + " " +
								lesson.getStartTime()
						);
				lessonsList.add(lesson);
			}
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

		monthChoice.addItemListener(ev -> {
			dayChoice.removeAll();

			int selectedYear = Integer.parseInt(yearChoice.getSelectedItem());
			int selectedMonth = Integer.parseInt(monthChoice.getSelectedItem());
			int days = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth();

			for (int day = 1; day <= days; day++) {
				dayChoice.add(String.valueOf(day));
			}
		});

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

		Choice roomChoice = new Choice();

		Vector<Room> roomsList = new Vector<Room>();
		for (Room room : system.getRooms()) {
			roomChoice.add(room.getRoomID() + " - Capacity: " + room.getCapacity());
			roomsList.add(room);
		}

		formPanel.add(new Label("Lesson:"));
		formPanel.add(lessonChoice);

		Panel datePanel = new Panel(new GridLayout(1, 3, 5, 5));
		datePanel.add(dayChoice);
		datePanel.add(monthChoice);
		datePanel.add(yearChoice);

		formPanel.add(new Label("New Date:"));
		formPanel.add(datePanel);

		Panel startPanel = new Panel(new GridLayout(1, 2, 5, 5));
		startPanel.add(startHourChoice);
		startPanel.add(startMinuteChoice);

		formPanel.add(new Label("New Start Time:"));
		formPanel.add(startPanel);

		Panel endPanel = new Panel(new GridLayout(1, 2, 5, 5));
		endPanel.add(endHourChoice);
		endPanel.add(endMinuteChoice);

		formPanel.add(new Label("New End Time:"));
		formPanel.add(endPanel);

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
				LocalDate newDate = LocalDate.of(
						Integer.parseInt(yearChoice.getSelectedItem()),
						Integer.parseInt(monthChoice.getSelectedItem()),
						Integer.parseInt(dayChoice.getSelectedItem())
						);

				LocalTime newStart = LocalTime.of(
						Integer.parseInt(startHourChoice.getSelectedItem()),
						Integer.parseInt(startMinuteChoice.getSelectedItem())
						);

				LocalTime newEnd = LocalTime.of(
						Integer.parseInt(endHourChoice.getSelectedItem()),
						Integer.parseInt(endMinuteChoice.getSelectedItem())
						);

				if (newDate.isBefore(LocalDate.now())) {
					throw new IllegalArgumentException("Lesson date cannot be in the past");
				}

				if (newDate.equals(LocalDate.now())) {
					if (!newStart.isAfter(LocalTime.now())) {
						throw new IllegalArgumentException("Start time must be in the future for today date");
					}
				}

				if (!newEnd.isAfter(newStart)) {
					throw new IllegalArgumentException("End time must be after start time");
				}

				Room selectedRoom = roomsList.get(roomChoice.getSelectedIndex());
				if (!modeChoice.getSelectedItem().equals("ZOOM")) {

					for (Course c : system.getCourses()) {
						for (Lesson otherLesson : c.getLessons()) {
							if (otherLesson.getLessonID() == selectedLesson.getLessonID())
								continue;
							if (otherLesson.getStatus().equalsIgnoreCase("CANCELLED"))
								continue;
							if (otherLesson.getRoom() != null &&
									otherLesson.getRoom().getRoomID().equals(selectedRoom.getRoomID()) &&
									otherLesson.getLessonDate().equals(newDate)) {
								boolean overlap = !newStart.isAfter(otherLesson.getEndTime()) &&
										!newEnd.isBefore(otherLesson.getStartTime());
								if (overlap) 
								{
									throw new IllegalArgumentException(
											"Room " + selectedRoom.getRoomID() +
											" is already occupied at this time");
								}
							}
						}
					}
				}

				for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
					for (Lesson otherLesson : enrolment.getCourse().getLessons()) {
						//skip the lesson we are rescheduling
						if (otherLesson.getLessonID() == selectedLesson.getLessonID()) {
							continue;
						}
						if (selectedRoom != null && enrolment.getGroup().getStudentCount() > selectedRoom.getCapacity())
						{
							throw new IllegalArgumentException("Room is too small for this student group");

						}

						//skip the lesson that is cancelled
						if (otherLesson.getStatus().equalsIgnoreCase("CANCELLED")) {
							continue;
						}
						//check if the lesson has the same date and if it overlaps
						if (otherLesson.getLessonDate().equals(newDate)) {
							boolean overlap = newStart.isBefore(otherLesson.getEndTime()) &&
									newEnd.isAfter(otherLesson.getStartTime());
							if (overlap) {
								throw new IllegalArgumentException("Cannot reschedule: room, lecturer, or student group is unavailable at this time\"\r\n"
										+ newDate.format(DATE_FORMAT));
							}
						}
					}
				}

				selectedLesson.setLessonDate(newDate);
				selectedLesson.setTime(newStart, newEnd);
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



	// ======================================================
	// Reports
	// Calculates and displays system reports and statistics
	// ======================================================

	// Lecturer Load
	private void openLecturerLoadDialog() {

		Choice lecturerChoice = new Choice();

		for (Lecturer lecturer : system.getLecturers()) {
			lecturerChoice.add(lecturer.getLecturerID() + " - " +
					lecturer.getFirstName() + " " + lecturer.getLastName());
		}

		int result = JOptionPane.showConfirmDialog(this, lecturerChoice, "Choose Lecturer",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int index = lecturerChoice.getSelectedIndex();
				Lecturer lecturer = system.getLecturers().get(index);

				double actualHours = system.calculateLecturerActualHours(lecturer);

				if (actualHours == 0) {
					JOptionPane.showMessageDialog(this,
							"This lecturer has no assigned courses or no scheduled lessons.",
							"Info",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				double requiredHours = lecturer.getRequiredHours();
				double diff = requiredHours - actualHours;


				JOptionPane.showMessageDialog(this,
						"Lecturer: " + lecturer.getFirstName() + " " + lecturer.getLastName() +
						"\nRequired load: " + requiredHours +
						"\nActual load: " + actualHours +
						"\nDifference: " + diff,
						"Lecturer Load Result",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
						this,
						"Error: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE
						);
			}
		}

	}
	// Student Group Load
	private void openStudentLoadDialog() {

		Choice groupChoice = new Choice();

		for (StudentGroup group : system.getStudentGroups()) {
			groupChoice.add(
					group.getGroupID() + " - " +
							group.getDepartment() + " Year " +
							group.getStudyYear() + " - " +
							group.getProgramName()
					);
		}

		int result = JOptionPane.showConfirmDialog(
				this,
				groupChoice,
				"Choose Student Group",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
				);

		if (result == JOptionPane.OK_OPTION) {

			StudentGroup group =
					system.getStudentGroups().get(groupChoice.getSelectedIndex());

			ArrayList<GroupEnrolment> groupCourses = new ArrayList<GroupEnrolment>();

			for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
				if (enrolment.getGroup().equals(group)) {
					groupCourses.add(enrolment);
				}
			}

			GroupEnrolment[] enrolments =
					groupCourses.toArray(new GroupEnrolment[groupCourses.size()]);

			double totalHours = group.calculateTotalHours(enrolments);
			boolean overloaded = group.isScheduleOverloaded(enrolments);

			JOptionPane.showMessageDialog(
					this,
					"Student Group: " + group.getDepartment() +
					" Year " + group.getStudyYear() +
					"\nProgram: " + group.getProgramName() +
					"\nTotal load: " + String.format("%.2f", totalHours)+
					"\nOverloaded: " + overloaded
					);
		}
	}
	// Room Load
	private void openRoomLoadDialog()
	{
		Choice roomChoice = new Choice();

		for (Room room : system.getRooms())
		{
			roomChoice.add("Room " + room.getRoomID() +" | Building " + room.getBuilding());
		}
		int result = JOptionPane.showConfirmDialog(this,roomChoice,"Choose Room",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {

			Room room = system.getRooms().get(roomChoice.getSelectedIndex());
			double load = system.calculateRoomLoad(room);

			JOptionPane.showMessageDialog(
					this,
					"Room: " + room.getRoomID() +
					"\nBuilding: " + room.getBuilding() +
					"\nSize: " + room.classifyRoomSize() +
					"\nAcademic Hours: " + load);
		}
	}

	// Timetable dialogs
	private void openLecturerTimetableDialog()
	{
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

		int result = JOptionPane.showConfirmDialog(
				this,
				lecturerChoice,
				"Choose Lecturer",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
				);

		if (result == JOptionPane.OK_OPTION) {
			try {
				if (lecturerChoice.getItemCount() == 0) {
					throw new IllegalArgumentException("No lecturers found.");
				}

				Lecturer selectedLecturer =
						lecturersList.get(lecturerChoice.getSelectedIndex());

				String text = "Lecturer: " +
						selectedLecturer.getFirstName() + " " +
						selectedLecturer.getLastName() +
						" (ID: " + selectedLecturer.getLecturerID() + ")\n\n";

				text += "--------------------------------------------------------------------------------\n";

				boolean foundLesson = false;

				for (AssignedToTeach assigned : system.getAssignedToTeachList()) {
					if (assigned.getLecturer().equals(selectedLecturer)) {
						Course course = assigned.getCourse();

						for (Lesson lesson : course.getLessons()) {
							if (!lesson.getStatus().equals("CANCELLED")) {
								text += buildLessonRow(course, lesson);
								foundLesson = true;
							}
						}
					}
				}

				if (!foundLesson) {
					text += "No lessons found for this lecturer.";
				}

				showTextDialog("Lecturer Timetable", text);

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
						this,
						"Error: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE
						);
			}
		}
	}

	private void openStudentGroupTimetableDialog()
	{
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

		int result = JOptionPane.showConfirmDialog(
				this,
				groupChoice,
				"Choose Student Group",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
				);

		if (result == JOptionPane.OK_OPTION) {
			try {
				if (groupChoice.getItemCount() == 0) {
					throw new IllegalArgumentException("No student groups found.");
				}

				StudentGroup selectedGroup =
						groupsList.get(groupChoice.getSelectedIndex());

				String text = "Student Group: " +
						selectedGroup.getGroupID() + " - " +
						selectedGroup.getDepartment() + ", " +
						" Year " + selectedGroup.getStudyYear() + ", " +
						selectedGroup.getProgramName() +
						", (" + selectedGroup.getStudentCount() + " students)\n\n";

				text += "--------------------------------------------------------------------------------\n";

				boolean foundLesson = false;

				for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
					if (enrolment.getGroup().equals(selectedGroup)) {
						Course course = enrolment.getCourse();

						for (Lesson lesson : course.getLessons()) {
							if (!lesson.getStatus().equals("CANCELLED")) {
								text += buildLessonRow(course, lesson);
								foundLesson = true;
							}
						}
					}
				}

				if (!foundLesson) {
					text += "No lessons found for this student group.";
				}

				showTextDialog("Student Group Timetable", text);

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
						this,
						"Error: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE
						);
			}
		}
	}



	// ======================================================
	// Helpers
	// Utility methods used throughout the GUI
	// ======================================================

	// ------------------------------------------------------
	// ID Management
	// ------------------------------------------------------
	private void updateNextIDs() {
		int maxCourseID = 0;
		int maxGroupID = 0;
		int maxLessonID = 0;

		for (Course course : system.getCourses()) {
			if (course.getCourseID() > maxCourseID) {
				maxCourseID = course.getCourseID();
			}

			for (Lesson lesson : course.getLessons()) {
				if (lesson.getLessonID() > maxLessonID) {
					maxLessonID = lesson.getLessonID();
				}
			}
		}

		for (StudentGroup group : system.getStudentGroups()) {
			if (group.getGroupID() > maxGroupID) {
				maxGroupID = group.getGroupID();
			}
		}

		nextCourseID = maxCourseID + 1;
		nextGroupID = maxGroupID + 1;
		nextLessonID = maxLessonID + 1;
	}

	// ------------------------------------------------------
	// DATA Management
	// ------------------------------------------------------
	private void confirmExit() {
		int result = JOptionPane.showConfirmDialog(
				this,
				"Do you want to save data before exiting?",
				"Exit Schedemy",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE
				);

		if (result == JOptionPane.YES_OPTION) {
			try {
				system.saveDataToFile();
				System.exit(0);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
						this,
						"Error saving data: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE
						);
			}
		}

		if (result == JOptionPane.NO_OPTION) {
			System.exit(0);
		}

		// CANCEL -> do nothing
	}

	private void askLoadOnStart() {
		int result = JOptionPane.showConfirmDialog(
				this,
				"Do you want to load saved data?",
				"Load Data",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
				);

		if (result == JOptionPane.YES_OPTION) {
			try {
				system.loadDataFromFile();
				updateNextIDs();

				JOptionPane.showMessageDialog(
						this,
						"Data loaded successfully!",
						"Load Data",
						JOptionPane.INFORMATION_MESSAGE
						);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
						this,
						"Error loading data: " + ex.getMessage(),
						"Load Data",
						JOptionPane.ERROR_MESSAGE
						);
			}
		}
	}
	// ------------------------------------------------------
	// Text Formatting
	// ------------------------------------------------------
	private String capitalizeFirstLetter(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		text = text.trim().toLowerCase();
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	// ------------------------------------------------------
	// Display Helpers
	// ------------------------------------------------------

	private String getCourseDisplayName(Course course) {
		String groupInfo = "";
		String lecturerInfo = "";

		Lecturer lecturer = system.getLecturerForCourse(course);

		if (lecturer != null) {
			lecturerInfo = " | " + lecturer.getFirstName() +
					" " + lecturer.getLastName();
		}

		for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
			if (enrolment.getCourse().getCourseID() == course.getCourseID()) {
				StudentGroup group = enrolment.getGroup();

				groupInfo = " | "+ shortenDepartment(group.getDepartment())+" Y" + group.getStudyYear()+" "+ group.getProgramName();
				break;
			}
		}
		return course.getCourseID() + " - " +course.getCourseName() +lecturerInfo +groupInfo;
	}

	private String shortenDepartment(String department) {
		if (department.equals("Computer Engineering")) return "CE";
		if (department.equals("Electrical Engineering")) return "EE";
		if (department.equals("Industrial Engineering and Management")) return "IEM";
		if (department.equals("Economics and Business Administration")) return "EBA";
		if (department.equals("Computer Science")) return "CS";

		return department;
	}

	private void showTextDialog(String title, String text)
	{
		Dialog dialog = new Dialog(this, title, true);
		enableDialogCloseButton(dialog);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(600, 450);
		dialog.setLocationRelativeTo(this);

		TextArea textArea = new TextArea(text);

		textArea.setEditable(false);

		Button closeButton = new Button("Close");
		closeButton.addActionListener(e -> dialog.dispose());

		dialog.add(textArea, BorderLayout.CENTER);
		dialog.add(closeButton, BorderLayout.SOUTH);

		dialog.setVisible(true);
	}

	private void enableDialogCloseButton(Dialog dialog) {
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.dispose();
			}
		});
	}

	private Button createBackButton(String text, String command) {
		Button button = createDashboardButton(text);
		button.setActionCommand(command);
		button.setPreferredSize(new Dimension(220, 45));
		button.setFont(new Font("Arial", Font.BOLD, 16));
		return button;
	}

	private Button createActionButton(String text, String command) {
		Button button = createDashboardButton(text);
		button.setActionCommand(command);
		button.setPreferredSize(new Dimension(250, 90));
		return button;
	}

	private void setCardsPanelSize(Panel panel, int itemCount) {
		int columns = 2;
		int buttonWidth = 250;
		int buttonHeight = 90;
		int hGap = 25;
		int vGap = 25;

		int rows = (int) Math.ceil(itemCount / 2.0);

		int width = columns * buttonWidth + hGap + 40;
		int height = rows * buttonHeight + Math.max(0, rows - 1) * vGap + 40;

		panel.setPreferredSize(new Dimension(width, height));
	}

	private ScrollPane createPaddedScrollPane(Panel contentPanel) {
		Panel wrapper = new Panel(new BorderLayout());
		wrapper.setBackground(new Color(245, 247, 250));

		Panel topPadding = new Panel();
		topPadding.setBackground(new Color(245, 247, 250));
		topPadding.setPreferredSize(new Dimension(0, 30));

		Panel leftPadding = new Panel();
		leftPadding.setBackground(new Color(245, 247, 250));
		leftPadding.setPreferredSize(new Dimension(30, 0));

		Panel rightPadding = new Panel();
		rightPadding.setBackground(new Color(245, 247, 250));
		rightPadding.setPreferredSize(new Dimension(30, 0));

		wrapper.add(topPadding, BorderLayout.NORTH);
		wrapper.add(leftPadding, BorderLayout.WEST);
		wrapper.add(rightPadding, BorderLayout.EAST);
		wrapper.add(contentPanel, BorderLayout.CENTER);

		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.add(wrapper);

		return scrollPane;
	}
	// ------------------------------------------------------
	// Lesson Helpers
	// ------------------------------------------------------
	private String getFriendlyStatus(String status) {
		if (status == null) return "Unknown";
		switch (status.toUpperCase()) {
		case "SCHEDULED": return "SCHEDULED";
		case "AVAILABLE": return "SCHEDULED";
		case "RESCHEDULED": return "RESCHEDULED";
		default: return status;
		}
	}

	private String buildLessonRow(Course course, Lesson lesson)
	{
		String roomText = "Zoom";

		if (lesson.getRoom() != null) {
			roomText = lesson.getRoom().getRoomID();
		}

		return "Course: " + course.getCourseName() + "\n" +
		"Date: " + lesson.getLessonDate().format(DATE_FORMAT) + "\n" +
		"Time: " + lesson.getStartTime() + " - " + lesson.getEndTime() + "\n" +
		"Room: " + roomText + "\n" +
		"Mode: " + lesson.getTeachingMode() + "\n" +
		"Status: " + lesson.getStatus() + "\n" +
		"------------------------------\n";
	}


}
