package Schedamy;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import java.time.format.DateTimeFormatter;


import java.awt.event.*;

// Main GUI class of  Schedamy system.
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
    private PopupMenu extraPopup;
    
    // The main system object that stores and manages all data.
    private SchedamySystem system;
    private final Object sharedRoomLock = new Object();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    private Panel mainPanel;
    
    // Constructor - creates the main window and initializes the system.
    public SchedamyGUI(SchedamySystem system) {
        super("Schedamy System");

        this.system = system;
        updateNextIDs();

        // Set the size of the main window.
        setSize(750, 500);
        setLocationRelativeTo(null);
        
        // Build and attach the menu bar to the window.
        buildMenuBar();

        buildMainPanel();
        
        // Show the main window.
        setVisible(true);

        // Close the program when the user closes the window.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    private void buildMainPanel() {
        setLayout(new BorderLayout());

        mainPanel = new Panel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        Panel headerPanel = new Panel(new GridLayout(3, 1));
        headerPanel.setBackground(new Color(245, 247, 250));

        Label titleLabel = new Label("Schedamy", Label.CENTER);
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
        Panel cardsPanel = new Panel(new GridLayout(3, 3, 18, 18));
        cardsPanel.setBackground(new Color(245, 247, 250));

        cardsPanel.add(createDashboardButton("Courses"));
        cardsPanel.add(createDashboardButton("Lecturers"));
        cardsPanel.add(createDashboardButton("Rooms"));
        cardsPanel.add(createDashboardButton("Student Groups"));
        cardsPanel.add(createDashboardButton("Lessons"));
        cardsPanel.add(createDashboardButton("Scheduling"));
        cardsPanel.add(createDashboardButton("Reports"));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);

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

    private void showCoursesDashboard() {
        clearMainPanel();

        Label title = new Label("Courses", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel coursesPanel = new Panel(new GridLayout(0, 2, 15, 15));
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

        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(coursesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showCourseDetailsByName(String courseName) {
        clearMainPanel();

        Label title = new Label(courseName, Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel detailsPanel = new Panel(new GridLayout(0, 1, 20, 20));
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

        Button backButton = createDashboardButton("Back to Courses");
        backButton.setActionCommand("Courses");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(detailsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
  
    private void showLecturersDashboard() {
        clearMainPanel();

        Label title = new Label("Lecturers", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel lecturersPanel = new Panel(new GridLayout(0, 2, 15, 15));
        lecturersPanel.setBackground(new Color(245, 247, 250));

        for (Lecturer lecturer : system.getLecturers()) {
            String name = lecturer.getFirstName() + " " + lecturer.getLastName();

            Button button = createDashboardButton(name);
            button.setPreferredSize(new Dimension(250, 90));
            button.setActionCommand("LECTURER_DETAILS_" + lecturer.getLecturerID());

            lecturersPanel.add(button);
        }

        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(lecturersPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLecturerDetails(int lecturerID) {
        clearMainPanel();

        Lecturer selectedLecturer = null;

        for (Lecturer lecturer : system.getLecturers()) {
            if (lecturer.getLecturerID() == lecturerID) {
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
            if (assigned.getLecturer().getLecturerID() == selectedLecturer.getLecturerID()) {
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

        Button backButton = createDashboardButton("Back to Lecturers");
        backButton.setActionCommand("Lecturers");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(detailsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showRoomsDashboard() {
        clearMainPanel();

        Label title = new Label("Rooms", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel roomsPanel = new Panel(new GridLayout(0, 2, 15, 15));
        roomsPanel.setBackground(new Color(245, 247, 250));

        for (Room room : system.getRooms()) {
            Button button = createDashboardButton("Room " + room.getRoomID());
            button.setPreferredSize(new Dimension(250, 90));
            button.setActionCommand("ROOM_DETAILS_" + room.getRoomID());
            roomsPanel.add(button);
        }

        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(roomsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showRoomDetails(String roomID) {
        clearMainPanel();

        Room selectedRoom = null;

        for (Room room : system.getRooms()) {
            if (room.getRoomID().equals(roomID)) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }

        Label title = new Label("Room " + selectedRoom.getRoomID(), Label.CENTER);
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

        Label equipmentLabel = new Label("Equipment: " + selectedRoom.getSpecialEquipment(), Label.CENTER);
        equipmentLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        infoCard.add(buildingLabel);
        infoCard.add(typeLabel);
        infoCard.add(capacityLabel);
        infoCard.add(sizeLabel);
        infoCard.add(equipmentLabel);

        detailsPanel.add(infoCard);

        Button backButton = createDashboardButton("Back to Rooms");
        backButton.setActionCommand("Rooms");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(detailsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showStudentGroupsDashboard() {
        clearMainPanel();

        Label title = new Label("Student Groups", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel groupsPanel = new Panel(new GridLayout(0, 2, 15, 15));
        groupsPanel.setBackground(new Color(245, 247, 250));

        for (StudentGroup group : system.getStudentGroups()) {
            Button button = createDashboardButton(
                group.getDepartment() + " | Year " + group.getStudyYear()
            );

            button.setPreferredSize(new Dimension(250, 90));
            button.setActionCommand("GROUP_DETAILS_" + group.getGroupID());

            groupsPanel.add(button);
        }

        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(groupsPanel);
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

        Button backButton = createDashboardButton("Back to Student Groups");
        backButton.setActionCommand("Student Groups");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(detailsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsDashboard() {
        clearMainPanel();

        Label title = new Label("Lessons", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel optionsPanel = new Panel(new GridLayout(2, 2, 20, 20));
        optionsPanel.setBackground(new Color(245, 247, 250));

        Button byCourse = createDashboardButton("By Course");
        byCourse.setActionCommand("LESSONS_BY_COURSE");

        Button byLecturer = createDashboardButton("By Lecturer");
        byLecturer.setActionCommand("LESSONS_BY_LECTURER");

        Button byRoom = createDashboardButton("By Room");
        byRoom.setActionCommand("LESSONS_BY_ROOM");

        Button byGroup = createDashboardButton("By Student Group");
        byGroup.setActionCommand("LESSONS_BY_GROUP");

        optionsPanel.add(byCourse);
        optionsPanel.add(byLecturer);
        optionsPanel.add(byRoom);
        optionsPanel.add(byGroup);

        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    private Color getLessonStatusColor(String status) {
        if (status == null) {
            return Color.WHITE;
        }

        if (status.equalsIgnoreCase("SCHEDULED")) {
            return Color.WHITE;
        }

        if (status.equalsIgnoreCase("RESCHEDULED")) {
            return new Color(220, 235, 255);
        }

        if (status.equalsIgnoreCase("CANCELLED")) {
            return new Color(255, 225, 225);
        }

        return Color.WHITE;
    }
    
    private Panel createLessonCard(Course course, Lesson lesson) {
        Panel card = new Panel(new GridLayout(4, 1));
        card.setPreferredSize(new Dimension(650, 110));
        card.setBackground(getLessonStatusColor(lesson.getStatus()));

        String roomText = "Zoom";
        if (lesson.getRoom() != null) {
            roomText = "Room " + lesson.getRoom().getRoomID();
        }

        Label courseLabel = new Label(course.getCourseName(), Label.CENTER);
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
    
    private void showLessonsByCourseSelection() {
        clearMainPanel();

        Label title = new Label("Lessons By Course", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel coursesPanel = new Panel(new GridLayout(0, 2, 15, 15));
        coursesPanel.setBackground(new Color(245, 247, 250));

        ArrayList<String> courseNames = new ArrayList<String>();

        for (Course course : system.getCourses()) {
            if (!courseNames.contains(course.getCourseName())) {
                courseNames.add(course.getCourseName());

                Button button = createDashboardButton(course.getCourseName());
                button.setPreferredSize(new Dimension(250, 90));
                button.setActionCommand("LESSONS_COURSE_NAME_" + course.getCourseName());

                coursesPanel.add(button);
            }
        }

        Button backButton = createDashboardButton("Back to Lessons");
        backButton.setActionCommand("Lessons");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(coursesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsOfCourseName(String courseName) {
        clearMainPanel();

        Label title = new Label("Lessons - " + courseName, Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel lessonsPanel = new Panel(new GridLayout(0, 1, 20, 20));
        lessonsPanel.setBackground(new Color(245, 247, 250));

        boolean foundLessons = false;

        for (Course course : system.getCourses()) {
            if (course.getCourseName().equals(courseName)) {
                for (Lesson lesson : course.getLessons()) {
                    lessonsPanel.add(createLessonCard(course, lesson));
                    foundLessons = true;
                }
            }
        }

        if (!foundLessons) {
            lessonsPanel.add(new Label("No lessons found for this course.", Label.CENTER));
        }

        Button backButton = createDashboardButton("Back to Courses");
        backButton.setActionCommand("LESSONS_BY_COURSE");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(lessonsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsByLecturerSelection() {
        clearMainPanel();

        Label title = new Label("Lessons By Lecturer", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel lecturersPanel = new Panel(new GridLayout(0, 2, 15, 15));
        lecturersPanel.setBackground(new Color(245, 247, 250));

        for (Lecturer lecturer : system.getLecturers()) {
            String lecturerName = lecturer.getFirstName() + " " + lecturer.getLastName();

            Button button = createDashboardButton(lecturerName);
            button.setPreferredSize(new Dimension(250, 90));
            button.setActionCommand("LESSONS_LECTURER_" + lecturer.getLecturerID());

            lecturersPanel.add(button);
        }

        Button backButton = createDashboardButton("Back to Lessons");
        backButton.setActionCommand("Lessons");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(lecturersPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsOfLecturer(int lecturerID) {
        clearMainPanel();

        Lecturer selectedLecturer = null;

        for (Lecturer lecturer : system.getLecturers()) {
            if (lecturer.getLecturerID() == lecturerID) {
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

        Panel lessonsPanel = new Panel(new GridLayout(0, 1, 20, 20));
        lessonsPanel.setBackground(new Color(245, 247, 250));

        boolean foundLesson = false;

        for (AssignedToTeach assigned : system.getAssignedToTeachList()) {
            if (assigned.getLecturer().getLecturerID() == selectedLecturer.getLecturerID()) {
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

        Button backButton = createDashboardButton("Back to Lecturers");
        backButton.setActionCommand("LESSONS_BY_LECTURER");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(lessonsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsByRoomSelection() {
        clearMainPanel();

        Label title = new Label("Lessons By Room", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel roomsPanel = new Panel(new GridLayout(0, 2, 15, 15));
        roomsPanel.setBackground(new Color(245, 247, 250));

        for (Room room : system.getRooms()) {
            Button button = createDashboardButton("Room " + room.getRoomID());
            button.setPreferredSize(new Dimension(250, 90));
            button.setActionCommand("LESSONS_ROOM_" + room.getRoomID());

            roomsPanel.add(button);
        }

        Button backButton = createDashboardButton("Back to Lessons");
        backButton.setActionCommand("Lessons");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(roomsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsOfRoom(String roomID) {
        clearMainPanel();

        Room selectedRoom = null;

        for (Room room : system.getRooms()) {
            if (room.getRoomID().equals(roomID)) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }

        Label title = new Label("Lessons - Room " + selectedRoom.getRoomID(), Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel lessonsPanel = new Panel(new GridLayout(0, 1, 20, 20));
        lessonsPanel.setBackground(new Color(245, 247, 250));

        boolean foundLesson = false;

        for (Course course : system.getCourses()) {
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getRoom() != null &&
                    lesson.getRoom().getRoomID().equals(selectedRoom.getRoomID())) {

                    lessonsPanel.add(createLessonCard(course, lesson));
                    foundLesson = true;
                }
            }
        }

        if (!foundLesson) {
            lessonsPanel.add(new Label("No lessons found for this room.", Label.CENTER));
        }

        Button backButton = createDashboardButton("Back to Rooms");
        backButton.setActionCommand("LESSONS_BY_ROOM");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(lessonsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showLessonsByGroupSelection() {
        clearMainPanel();

        Label title = new Label("Lessons By Student Group", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel groupsPanel = new Panel(new GridLayout(0, 2, 15, 15));
        groupsPanel.setBackground(new Color(245, 247, 250));

        for (StudentGroup group : system.getStudentGroups()) {
            Button button = createDashboardButton(
                group.getDepartment() + " | Year " + group.getStudyYear()
            );

            button.setPreferredSize(new Dimension(250, 90));
            button.setActionCommand("LESSONS_GROUP_" + group.getGroupID());

            groupsPanel.add(button);
        }

        Button backButton = createDashboardButton("Back to Lessons");
        backButton.setActionCommand("Lessons");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(groupsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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

        Panel lessonsPanel = new Panel(new GridLayout(0, 1, 20, 20));
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

        Button backButton = createDashboardButton("Back to Student Groups");
        backButton.setActionCommand("LESSONS_BY_GROUP");

        mainPanel.add(title, BorderLayout.NORTH);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(lessonsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showSchedulingDashboard() {
        clearMainPanel();

        Label title = new Label("Scheduling", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel optionsPanel = new Panel(new GridLayout(3, 1, 20, 20));
        optionsPanel.setBackground(new Color(245, 247, 250));

        Button addLessonButton = createDashboardButton("Add Lesson");
        addLessonButton.setActionCommand("Add Lesson");

        Button cancelLessonButton = createDashboardButton("Cancel Lesson");
        cancelLessonButton.setActionCommand("Cancel Lesson");

        Button rescheduleLessonButton = createDashboardButton("Reschedule Lesson");
        rescheduleLessonButton.setActionCommand("Reschedule Lesson");


        optionsPanel.add(addLessonButton);
        optionsPanel.add(cancelLessonButton);
        optionsPanel.add(rescheduleLessonButton);
       

        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
    private void showReportsDashboard() {
        clearMainPanel();

        Label title = new Label("Reports", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        Panel optionsPanel = new Panel(new GridLayout(3, 1, 20, 20));
        optionsPanel.setBackground(new Color(245, 247, 250));

        Button lecturerLoadButton = createDashboardButton("Lecturer Load");
        lecturerLoadButton.setActionCommand("Calculate Lecturer Load");

        Button studentLoadButton = createDashboardButton("Student Group Load");
        studentLoadButton.setActionCommand("Calculate Student Load");

        Button roomLoadButton = createDashboardButton("Room Load");
        roomLoadButton.setActionCommand("Calculate Room Load");



        optionsPanel.add(lecturerLoadButton);
        optionsPanel.add(studentLoadButton);
        optionsPanel.add(roomLoadButton);
       
        Button backButton = createDashboardButton("Back to Home");
        backButton.setActionCommand("HOME");

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }
    
 // Updates the next available IDs for courses and student groups
    
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

    
 // Builds the main menu bar 
    private void buildMenuBar() {

	
	 
     // Create the main menu bar object.
     menuBar = new MenuBar();

     // Build all menus.
     buildFileMenu();
     buildManageMenu();
     buildViewMenu();
     buildOptionsMenu();
     buildExtraPopup();
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

    System.out.println("Clicked command: " + command);
    
    if (command.equals("Courses")) {
        showCoursesDashboard();
        return;
    }

    if (command.equals("HOME")) {
        remove(mainPanel);
        buildMainPanel();
        validate();
        repaint();
        return;
    }

    if (command.startsWith("COURSE_NAME_")) {
        String courseName = command.replace("COURSE_NAME_", "");
        showCourseDetailsByName(courseName);
        return;
    }
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
            updateNextIDs();

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

    if (command.equals("Lessons")) {
        showLessonsDashboard();
        return;
    }
    if (command.startsWith("LESSONS_COURSE_NAME_")) {
        String courseName = command.replace("LESSONS_COURSE_NAME_", "");
        showLessonsOfCourseName(courseName);
        return;
    } 
    
    if (command.equals("LESSONS_BY_COURSE")) {
        showLessonsByCourseSelection();
        return;
    }
    
    if (command.startsWith("LESSONS_LECTURER_")) {
        int lecturerID = Integer.parseInt(command.replace("LESSONS_LECTURER_", ""));
        showLessonsOfLecturer(lecturerID);
        return;
    }

    if (command.equals("LESSONS_BY_LECTURER")) {
        showLessonsByLecturerSelection();
        return;
    }
    
    if (command.startsWith("LESSONS_ROOM_")) {
        String roomID = command.replace("LESSONS_ROOM_", "");
        showLessonsOfRoom(roomID);
        return;
    }

    if (command.equals("LESSONS_BY_ROOM")) {
        showLessonsByRoomSelection();
        return;
    }
    if (command.startsWith("LESSONS_GROUP_")) {
        int groupID = Integer.parseInt(command.replace("LESSONS_GROUP_", ""));
        showLessonsOfStudentGroup(groupID);
        return;
    }

    if (command.equals("LESSONS_BY_GROUP")) {
        showLessonsByGroupSelection();
        return;
    }

    // View dialogs.
    
    if (command.startsWith("LECTURER_DETAILS_")) {
        int lecturerID = Integer.parseInt(command.replace("LECTURER_DETAILS_", ""));
        showLecturerDetails(lecturerID);
        return;
    }
    
    if (command.equals("Lecturers")) {
        showLecturersDashboard();
        return;
    }

    if (command.equals("Courses")) {
        openCoursesView();
    }
    if (command.startsWith("GROUP_DETAILS_")) {
        int groupID = Integer.parseInt(command.replace("GROUP_DETAILS_", ""));
        showStudentGroupDetails(groupID);
        return;
    }

    if (command.equals("Student Groups")) {
        showStudentGroupsDashboard();
        return;
    }
    
    if (command.startsWith("ROOM_DETAILS_")) {
        String roomID = command.replace("ROOM_DETAILS_", "");
        showRoomDetails(roomID);
        return;
    }
    
    if (command.equals("Rooms")) {
        showRoomsDashboard();
        return;
    }
    if (command.equals("Scheduling")) {
        showSchedulingDashboard();
        return;
    }
    
    if (command.equals("Add Lesson")) {
        System.out.println("Opening Add Lesson Dialog");
        openAddLessonDialog();
        return;
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
    
    if (command.equals("Reports")) {
        showReportsDashboard();
        return;
    }
    
    //options in popupmanu
    if (command.equals("Extra Options")) {
        extraPopup.show(this, 180, 100);
    }
    if (command.equals("Calculate Lecturer Load")) {
        openLecturerLoadDialog();
    }
    if (command.equals("Calculate Student Load")) {
        openStudentLoadDialog();
    }
    if (command.equals("Calculate Room Load")) {
        openRoomLoadDialog();
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

	        	system.addCourse(courseID,capitalizeFirstLetter(courseNameField.getText()),
	        			Integer.parseInt(creditsField.getText()),
	        			courseTypeChoice.getSelectedItem(),
	        			lecturerID,
	        			groupID);
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
	
	
	private void openCancelLessonDialog()
	{
	    Dialog dialog = new Dialog(this, "Cancel Lesson", true);
	    dialog.setLayout(new BorderLayout());
	    dialog.setSize(500, 220);

	    Panel formPanel = new Panel(new GridLayout(2, 2, 5, 5));

	    Choice lessonChoice = new Choice();

	    // This vector keeps  Lesson objects in the same order as the Choice list.
	    Vector<Lesson> lessonsList = new Vector<Lesson>();
	    Vector<Integer> courseIDs = new Vector<Integer>();

	    for (Course course : system.getCourses()) {
	        for (Lesson lesson : course.getLessons()) {
	        	if (!lesson.getStatus().equalsIgnoreCase("CANCELLED")&&
	        		!lesson.getStatus().equalsIgnoreCase("RESCHEDULED")) {
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

	            // Get the selected Lesson directly by index
	            int selectedIndex = lessonChoice.getSelectedIndex();
	            Lesson selectedLesson = lessonsList.get(selectedIndex);
	            int courseID = courseIDs.get(selectedIndex);
	            
	            LocalDate newDate = selectedLesson.getLessonDate().plusWeeks(1);
	            
	            system.cancelLesson(courseID, selectedLesson.getLessonID(), newDate, sharedRoomLock);

	            JOptionPane.showMessageDialog(
	                this,
	                "Lesson cancelled successfully!\nReason: " + reason,
	                "Success",
	                JOptionPane.INFORMATION_MESSAGE
	            );
	            

	            dialog.dispose();
	            
	            openRescheduleLessonDialog();
	            
	            
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

	            if (!newEnd.isAfter(newStart)) {
	                throw new IllegalArgumentException("End time must be after start time");
	            }
	            
	            if (!modeChoice.getSelectedItem().equals("ZOOM")) {
	                Room selectedRoom = roomsList.get(roomChoice.getSelectedIndex());
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
	                            if (overlap) {
	                                throw new IllegalArgumentException(
	                                    "Room " + selectedRoom.getRoomID() +
	                                    " is already occupied at this time"
	                                );
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

	            String text = "Student Group: " +
	                selectedGroup.getGroupID() + " - " +
	                selectedGroup.getDepartment() + ", " +
	                " Year " + selectedGroup.getStudyYear() + ", " + selectedGroup.getProgramName() +
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
	
	private void openLecturersView()
	{
	    String text = "";

	    for (Lecturer lecturer : system.getLecturers()) {
	        text += lecturer.toString() + "\n\n";
	    }

	    if (text.isEmpty()) {
	        text = "No lecturers found.";
	    }

	    showTextDialog("Lecturers", text);
	}
  
	private void openCoursesView() {
	    String text = "";

	    for (Course course : system.getCourses()) {
	        text += "-----------------------------------------------------------\n";
	        text += course.getCourseName() + "\n";
	        text += "-----------------------------------------------------------\n\n";

	        text += "Course ID: " + course.getCourseID() + "\n";
	        text += "Credits: " + course.getCredits() + "\n";
	        text += "Type: " + course.getCourseType() + "\n";
	        text += "Number of lessons: " + course.getLessons().size() + "\n";
	        text += "\n";
	        text += system.getCourseInfo(course);

	        text += "\n\n";
	    }

	    if (text.isEmpty()) {
	        text = "No courses found.";
	    }

	    showTextDialog("Courses", text);
	}
    private void openStudentGroupsView()
    {
        String text = "";
        for (StudentGroup group : system.getStudentGroups()) {
            text += group.toString() + "\n\n";
        }
        if (text.isEmpty())
            text = "No student groups found.";

        showTextDialog("Student Groups", text);
    }
    private void openRoomsView()
    {
        String text = "";

        for (Room room : system.getRooms()) {
            text += room.toString() + "\n\n";
        }

        if (text.isEmpty()) {
            text = "No rooms found.";
        }

        showTextDialog("Rooms", text);
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
                if (assigned != null) {
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

                	    system.getRoomReservations().removeIf(
                	            reservation -> reservation.getLesson().equals(addedLesson)
                	    );

                	    if (addedLesson.getRoom() != null) {
                	        addedLesson.getRoom().setStatus("AVAILABLE");
                	    }

                	    assigned.getCourse().getLessons().remove(addedLesson);

                	    nextLessonID--;

                	    JOptionPane.showMessageDialog(this, 
                	            "Warning: " + assigned.getLecturer().getFirstName() + " " +
                	            assigned.getLecturer().getLastName() +
                	            " is now overloaded!\nTotalHours: " + totalHours + 
                	            "\nRequired hours: " + requiredHours, 
                	            "Overloaded warning",
                	            JOptionPane.WARNING_MESSAGE);

                	    dialog.dispose();
                	   
                	    return;
                	}
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
    private String getCourseDisplayName(Course course) {
        String groupInfo = "";

        for (GroupEnrolment enrolment : system.getGroupEnrolments()) {
            if (enrolment.getCourse().getCourseID() == course.getCourseID()) {
                StudentGroup group = enrolment.getGroup();

                groupInfo = " | Y" + group.getStudyYear() +
                            " | " + group.getProgramName() +
                            " | " + shortenDepartment(group.getDepartment());
                break;
            }
        }

        return course.getCourseID() + " - " + course.getCourseName() + groupInfo;
    }

    private String shortenDepartment(String department) {
        if (department.equals("Computer Engineering")) return "CE";
        if (department.equals("Electrical Engineering")) return "EE";
        if (department.equals("Industrial Engineering and Management")) return "IEM";
        if (department.equals("Economics and Business Administration")) return "EBA";
        if (department.equals("Computer Science")) return "CS";

        return department;
    }
    private String getFriendlyStatus(String status) {
    	if (status == null) return "Unknown";
    	switch (status.toUpperCase()) {
    	case "SCHEDULED": return "SCHEDULED";
    	case "AVAILABLE": return "SCHEDULED";
    	case "CANCELLED": return "NEEDS TO RESCHEDULE";
    	case "RESCHEDULED": return "RESCHEDULED";
    	default: return status;
    	}
    }
    private void openLessonsView()
    {
        String text = "";

        for (Course course : system.getCourses())
        {
            if (course.getLessons().isEmpty()) {
                continue;
            }

            text += "------------------------------------------------\n";
            text += course.getCourseName() + "\n";
            text += "------------------------------------------------\n\n";

            for (Lesson lesson : course.getLessons())
            {
                text += "Lesson #" + lesson.getLessonID() + "\n\n";

                text += "Date: " +
                        lesson.getLessonDate().format(DATE_FORMAT) + "\n";

                text += "Time: " +
                        lesson.getStartTime() +
                        " - " +
                        lesson.getEndTime() + "\n";

                text += "Room: " +
                        (lesson.getRoom() != null ?
                         lesson.getRoom().getRoomID() :
                         "Zoom") + "\n";

                text += "Mode: " +
                        lesson.getTeachingMode() + "\n";

                text += "Status: " +
                        getFriendlyStatus(lesson.getStatus()) + "\n";

                text += "\n----------------------------------------\n\n";
            }

            text += "\n";
        }

        if (text.isEmpty()) {
            text = "No lessons found.";
        }

        showTextDialog("Lessons", text);
    }
    // options  in popupmanu
    private void buildOptionsMenu() {
        optionsMenu = new Menu("Options");

        MenuItem extraOptionsItem = new MenuItem("Extra Options");
        extraOptionsItem.addActionListener(this);

        optionsMenu.add(extraOptionsItem);
    }
    private void buildExtraPopup() {
        extraPopup = new PopupMenu();

        MenuItem lecturerLoadItem = new MenuItem("Calculate Lecturer Load");
        MenuItem studentLoadItem = new MenuItem("Calculate Student Load");
        MenuItem roomLoadItem = new MenuItem("Calculate Room Load");
        
        lecturerLoadItem.addActionListener(this);
        roomLoadItem.addActionListener(this);
        studentLoadItem.addActionListener(this);

        extraPopup.add(lecturerLoadItem);
        extraPopup.add(studentLoadItem);  
        extraPopup.add(roomLoadItem);

        add(extraPopup);
    }
    
    //lecturer load
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
                

                /*
                  This version uses CalculateHoursThread with only one
                  AssignedToTeach object, and calculates  hours of
                  only one course, even if the lecturer teaches several courses.
                 
                 SchedamySystem calculateLecturerActualHours() checks all AssignedToTeach objects and sums
                 hours of every course that belongs to this lecturer,
                 to give lecturer's real total teaching load.
                 We can think if there is any other way, if we want to use the thread. 
                 (Now there is no use for CalculateHoursThread)
                 */
                
                /* Find AssignedToTeach for this lecturer
                AssignedToTeach assigned = null;
                for (AssignedToTeach a : system.getAssignedToTeachList()) {
                    if (a.getLecturer().equals(lecturer)) {
                        assigned = a;
                        break;
                    }
                }

                if (assigned == null) {
                    JOptionPane.showMessageDialog(this,
                        "This lecturer has no assigned courses.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Show calculating message
                JOptionPane.showMessageDialog(this,
                    "Calculating hours for " + lecturer.getFirstName() + "...\nPlease wait.",
                    "Calculating", JOptionPane.INFORMATION_MESSAGE);

                // Start the thread
                CalculateHoursThread thread = new CalculateHoursThread(assigned);
                thread.start();
                thread.join(); // wait for result
                double actualHours = thread.getTotalHours();
                double requiredHours = lecturer.getRequiredHours();
                double diff = requiredHours - actualHours;*/

         
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
    //student load
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

            int totalHours = group.calculateTotalHours(enrolments);
            boolean overloaded = group.isScheduleOverloaded(enrolments);

            JOptionPane.showMessageDialog(
                    this,
                    "Student Group: " + group.getDepartment() +
                    " Year " + group.getStudyYear() +
                    "\nProgram: " + group.getProgramName() +
                    "\nTotal load: " + totalHours +
                    "\nOverloaded: " + overloaded
            );
        }
    }
    //get room load
    private void openRoomLoadDialog()
    {
        Choice roomChoice = new Choice();

        for (Room room : system.getRooms()) 
        {
        	roomChoice.add(room.getRoomID());
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
    //helpers
    
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
    private void showTextDialog(String title, String text)
    {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 450);

        TextArea textArea = new TextArea(text);
        
        textArea.setEditable(false);

        Button closeButton = new Button("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(textArea, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
