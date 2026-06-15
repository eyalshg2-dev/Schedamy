package Schedamy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

public class AvailabilityThread implements Runnable {

    private static final java.time.format.DateTimeFormatter DATE_FORMAT =
            java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Lecturer lecturer;
    private Lesson lesson;
    private LocalDate weekStart;
    private Vector<Room> rooms;
    private final Vector<GroupEnrolment> groupEnrolment;
    private final Object roomLock;
    private boolean isCancellation;
    private LocalTime startTime;
    private LocalTime endTime;
    private String suggestion = "";
    private Room suggestedRoom = null;
    private LocalDate suggestedDate = null;
    private StudentGroup studentGroup;
    private Room excludeRoom; // room to exclude (used by thread 2 if same room found)

    public AvailabilityThread(Lecturer lecturer, Lesson lesson,
            LocalDate weekStart, Vector<Room> rooms,
            Object roomLock, Vector<GroupEnrolment> groupEnrolment,
            boolean isCancellation, StudentGroup studentGroup) {
        this.lecturer = lecturer;
        this.lesson = lesson;
        this.weekStart = weekStart;
        this.rooms = rooms;
        this.roomLock = roomLock;
        this.groupEnrolment = groupEnrolment;
        this.isCancellation = isCancellation;
        this.startTime = lesson.getStartTime();
        this.endTime = lesson.getEndTime();
        this.studentGroup = studentGroup;
        this.excludeRoom = null;
    }

    // Used by GUI to tell thread 2 to skip a specific room
    public void setExcludeRoom(Room room) {
        this.excludeRoom = room;
    }

    public void run() {
        try {
            if (isCancellation) {
                System.out.println("Lesson Cancelled!");
                return;
            }

            System.out.println("AvailabilityThread searching week starting: " + weekStart.format(DATE_FORMAT));

            Thread.sleep(1000);
            
            java.util.List<Integer> days = new java.util.ArrayList<>();
            for (int i = 0; i < 7; i++) days.add(i);
            java.util.Collections.shuffle(days);

            for (int i : days) {
                LocalDate searchDate = weekStart.plusDays(i);
                if (searchDate.getDayOfWeek() == java.time.DayOfWeek.SATURDAY) {
                	continue;
                }
                System.out.println("Trying date: " + searchDate.format(DATE_FORMAT));

                if ("FRONTAL".equals(lesson.getTeachingMode())) {
                    findAvailableSlot(studentGroup, searchDate);
                } else {
                    handleZoom(searchDate);
                }

                if (!suggestion.isEmpty()) {
                    System.out.println("Slot found on: " + searchDate.format(DATE_FORMAT));
                    return;
                }
            }

            System.out.println("No slot found in week starting: " + weekStart.format(DATE_FORMAT));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findAvailableSlot(StudentGroup group, LocalDate searchDate) {
    	
    	long lessonDuration = java.time.Duration.between(
                lesson.getStartTime(),
                lesson.getEndTime()).toMinutes();
    	
    	if (lessonDuration <= 0) lessonDuration = 45;
    	
        LocalTime rangeStart = LocalTime.of(8, 0);
        LocalTime rangeEnd = LocalTime.of(17, 0);

        if (group != null && group.getProgramName().equals("Evening")) {
            rangeStart = LocalTime.of(16, 0);
            rangeEnd = LocalTime.of(22, 0);
        }

        LocalTime slotStart = rangeStart;
        while (!slotStart.plusMinutes(lessonDuration).isAfter(rangeEnd)) {
            LocalTime slotEnd = slotStart.plusMinutes(lessonDuration);

            // Check lecturer free at this time
            boolean lecturerFree = true;
            for (Lesson current : lecturer.getLessons()) {
                if (current.getStatus().equalsIgnoreCase("CANCELLED")) continue;
                if (current.getLessonDate().equals(searchDate)) {
                    boolean overlap = slotStart.isBefore(current.getEndTime()) &&
                                      slotEnd.isAfter(current.getStartTime());
                    if (overlap) {
                        lecturerFree = false;
                        break;
                    }
                }
            }
            if (!lecturerFree) {
                slotStart = slotStart.plusMinutes(lessonDuration);
                continue;
            }

            // Check group free at this time
            boolean groupFree = true;
            if (group != null) {
                for (GroupEnrolment enrolment : groupEnrolment) {
                    if (!enrolment.getGroup().equals(group)) continue;
                    for (Lesson otherLesson : enrolment.getCourse().getLessons()) {
                        if (otherLesson.getStatus().equalsIgnoreCase("CANCELLED")) continue;
                        if (otherLesson.getLessonDate().equals(searchDate)) {
                        	//15 minute buffer before and after each class
                        	LocalTime otherStart = otherLesson.getStartTime().minusMinutes(15);
                            LocalTime otherEnd = otherLesson.getEndTime().plusMinutes(15);
                            boolean overlap = slotStart.isBefore(otherEnd) && slotEnd.isAfter(otherStart);
                           
                            if (overlap) {
                                groupFree = false;
                                break;
                            }
                        }
                    }
                    if (!groupFree) break;
                }
            }
            if (!groupFree) {
                slotStart = slotStart.plusHours(1);
                continue;
            }

            // Check room free at this time
            synchronized (roomLock) {
                int studentCount = group != null ? group.getStudentCount() : 0;
                Room availableRoom = null;

                for (Room room : rooms) {
                    // Skip excluded room (set by GUI if both threads found same room)
                    if (excludeRoom != null &&
                        room.getRoomID().equals(excludeRoom.getRoomID())) continue;

                    if (room.getCapacity() < studentCount) continue;

                    boolean roomFree = true;
                    for (GroupEnrolment enrolment : groupEnrolment) {
                        for (Lesson l : enrolment.getCourse().getLessons()) {
                            if (l.getRoom() == null) continue;
                            if (!l.getRoom().getRoomID().equals(room.getRoomID())) continue;
                            if (l.getStatus().equalsIgnoreCase("CANCELLED")) continue;
                            if (!l.getLessonDate().equals(searchDate)) continue;

                            boolean overlap = slotStart.isBefore(l.getEndTime()) &&
                                              slotEnd.isAfter(l.getStartTime());
                            if (overlap) {
                                roomFree = false;
                                break;
                            }
                        }
                        if (!roomFree) break;
                    }

                    if (roomFree) {
                        availableRoom = room;
                        break;
                    }
                }

                if (availableRoom != null) {
                    suggestedRoom = availableRoom;
                    suggestedDate = searchDate;
                    startTime = slotStart;
                    endTime = slotEnd;
                    suggestion = "Date: " + searchDate.format(DATE_FORMAT) +
                                 "\nTime: " + slotStart + " - " + slotEnd +
                                 "\nRoom: " + availableRoom.getRoomID();
                    System.out.println("[FRONTAL] Suggestion found: " + suggestion);
                    return;
                }
            }

            slotStart = slotStart.plusMinutes(lessonDuration);
        }
    }

    private void handleZoom(LocalDate searchDate) {
        if (studentGroup != null && studentGroup.getProgramName().equals("Evening")) {
            startTime = LocalTime.of(18, 0);
            endTime = LocalTime.of(20, 0);
        } else {
            startTime = LocalTime.of(10, 0);
            endTime = LocalTime.of(12, 0);
        }

        suggestedDate = searchDate;
        suggestion = "Date: " + searchDate.format(DATE_FORMAT) +
                     "\nTime: " + startTime + " - " + endTime +
                     "\nMode: ZOOM (no room needed)";
        System.out.println("[ZOOM] Suggestion found: " + suggestion);
    }

    public String getSuggestion() { return suggestion; }
    public Room getSuggestedRoom() { return suggestedRoom; }
    public LocalDate getSuggestedDate() { return suggestedDate; }
    public LocalTime getSuggestedStartTime() { return startTime; }
    public LocalTime getSuggestedEndTime() { return endTime; }
}