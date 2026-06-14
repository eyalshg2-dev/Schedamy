package Schedamy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

public class AvailabilityThread implements Runnable {

    private static final java.time.format.DateTimeFormatter DATE_FORMAT =
            java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Lecturer lecturer;
    private Lesson lesson;
    private LocalDate date;
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

    public AvailabilityThread(Lecturer lecturer, Lesson lesson,
            LocalDate date, Vector<Room> rooms,
            Object roomLock, Vector<GroupEnrolment> groupEnrolment,
            boolean isCancellation, StudentGroup studentGroup) {
        this.lecturer = lecturer;
        this.lesson = lesson;
        this.date = date;
        this.rooms = rooms;
        this.roomLock = roomLock;
        this.groupEnrolment = groupEnrolment;
        this.isCancellation = isCancellation;
        this.startTime = lesson.getStartTime();
        this.endTime = lesson.getEndTime();
        this.studentGroup = studentGroup;
    }

    public void run() {
        try {
            if (isCancellation) {
                System.out.println("Lesson Cancelled!");
                return;
            }

            System.out.println("AvailabilityThread searching date: " + date.format(DATE_FORMAT));

            Thread.sleep(1000);

            if ("FRONTAL".equals(lesson.getTeachingMode())) {
                System.out.println("Searching for available slot...");
                findAvailableSlot(studentGroup);
            } else {
                handleZoom();
            }

            System.out.println("Search Complete: " + (suggestion.isEmpty() ? "No slot found" : suggestion));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findAvailableSlot(StudentGroup group) {
    	long lessonDuration =
    	        java.time.Duration.between(
    	                lesson.getStartTime(),
    	                lesson.getEndTime()
    	        		).toHours();
        // set time based on the program
        LocalTime rangeStart = LocalTime.of(8, 0);
        LocalTime rangeEnd = LocalTime.of(17, 0);

        if (group != null && group.getProgramName().equals("Evening")) {
            rangeStart = LocalTime.of(16, 0);
            rangeEnd = LocalTime.of(22, 0);
        }

        // an interval of 2 hours difference
        LocalTime slotStart = rangeStart;
        while (!slotStart.plusHours(lessonDuration).isAfter(rangeEnd)) {
            LocalTime slotEnd = slotStart.plusHours(lessonDuration);

            /*System.out.println("Trying slot: " + slotStart + " - " + slotEnd);*/

            // Check lecturer free at this time
            boolean lecturerFree = true;
            for (Lesson current : lecturer.getLessons()) {
                if (current.getStatus().equalsIgnoreCase("CANCELLED")) continue;
                if (current.getLessonDate().equals(date)) {
                    boolean overlap = slotStart.isBefore(current.getEndTime().plusMinutes(15)) &&
                                      slotEnd.isAfter(current.getStartTime().plusMinutes(15));
                    if (overlap) {
                        lecturerFree = false;
                        break;
                    }
                }
            }
            if (!lecturerFree) {
                slotStart = slotStart.plusHours(1);
                continue;
            }

            // Check group free at this time
            boolean groupFree = true;
            if (group != null) {
                for (GroupEnrolment enrolment : groupEnrolment) {
                    if (!enrolment.getGroup().equals(group)) continue;
                    for (Lesson otherLesson : enrolment.getCourse().getLessons()) {
                        /*if (otherLesson.getLessonID() == lesson.getLessonID()) continue;*/
                        if (otherLesson.getStatus().equalsIgnoreCase("CANCELLED")) continue;
                        if (otherLesson.getLessonDate().equals(date)) {
                            boolean overlap = slotStart.isBefore(otherLesson.getEndTime().plusMinutes(15)) &&
                                              slotEnd.isAfter(otherLesson.getStartTime().plusMinutes(15));
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
                	System.out.println(room.getRoomID() + "TYPE: " + room.getRoomType());
                    if ("PENDING".equals(room.getStatus())) continue;
                    if (lesson.isLabRoomRequired() &&
                    		!"Computer Lab".equalsIgnoreCase(room.getRoomType())) {
                    	continue;
                    }
                    
                    if (room.getCapacity() < studentCount) continue;
                    

                    boolean roomFree = true;
                    for (GroupEnrolment enrolment : groupEnrolment) {
                        for (Lesson l : enrolment.getCourse().getLessons()) {
                            if (l.getRoom() == null) continue;
                            if (!l.getRoom().getRoomID().equals(room.getRoomID())) continue;
                            if (l.getStatus().equalsIgnoreCase("CANCELLED")) continue;
                            if (!l.getLessonDate().equals(date)) continue;

                            boolean overlap = slotStart.isBefore(l.getEndTime().plusMinutes(15)) &&
                                              slotEnd.isAfter(l.getStartTime().plusMinutes(15));
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
                    /*availableRoom.setStatus("PENDING");*/
                    suggestedRoom = availableRoom;
                    suggestedDate = date;
                    startTime = slotStart;
                    endTime = slotEnd;
                    suggestion = "Date: " + date.format(DATE_FORMAT) +
                                 "\nTime: " + slotStart + " - " + slotEnd +
                                 "\nRoom: " + availableRoom.getRoomID();
                    System.out.println("[FRONTAL] Suggestion found: " + suggestion);
                    return;
                }
            }

            slotStart = slotStart.plusHours(1);
        }

        System.out.println("No available slot found on: " + date);
    }

    private void handleZoom() {
        suggestedDate = date;

        if (studentGroup != null && studentGroup.getProgramName().equals("Evening")) {
            startTime = LocalTime.of(18, 0);
            endTime = LocalTime.of(20, 0);
        } else {
            startTime = LocalTime.of(10, 0);
            endTime = LocalTime.of(12, 0);
        }

        suggestion = "Date: " + date.format(DATE_FORMAT) +
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