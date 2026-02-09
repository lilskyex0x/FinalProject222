package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CourseOffering {
  private final String semester;
  private final Course course;
  private boolean open;
  private int seatLimit;
  private final List<TimeSlot> timeSlots;
  private final Set<String> enrolledStudentIds;

  public CourseOffering(String semester, Course course, int seatLimit, List<TimeSlot> timeSlots) {
    if (semester == null || semester.trim().isEmpty()) {
      throw new IllegalArgumentException("semester required");
    }
    if (course == null) {
      throw new IllegalArgumentException("course required");
    }
    if (seatLimit < 0) {
      throw new IllegalArgumentException("seatLimit must be >= 0");
    }
    this.semester = semester.trim();
    this.course = course;
    this.open = true;
    this.seatLimit = seatLimit;
    this.timeSlots = new ArrayList<TimeSlot>();
    if (timeSlots != null) {
      this.timeSlots.addAll(timeSlots);
    }
    this.enrolledStudentIds = new HashSet<String>();
  }

  public String getSemester() {
    return semester;
  }

  public Course getCourse() {
    return course;
  }

  public String getKey() {
    return semester + ":" + course.getCode();
  }

  public boolean isOpen() {
    return open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  public int getSeatLimit() {
    return seatLimit;
  }

  public void setSeatLimit(int seatLimit) {
    if (seatLimit < 0) {
      throw new IllegalArgumentException("seatLimit must be >= 0");
    }
    this.seatLimit = seatLimit;
  }

  public List<TimeSlot> getTimeSlots() {
    return Collections.unmodifiableList(timeSlots);
  }

  public int getEnrolledCount() {
    return enrolledStudentIds.size();
  }

  public boolean hasSeatAvailable() {
    if (seatLimit == 0) {
      return true; // treat 0 as unlimited for simplicity
    }
    return enrolledStudentIds.size() < seatLimit;
  }

  public boolean isStudentEnrolled(String studentId) {
    return enrolledStudentIds.contains(studentId);
  }

  public boolean enroll(String studentId) {
    if (!open) {
      return false;
    }
    if (!hasSeatAvailable()) {
      return false;
    }
    return enrolledStudentIds.add(studentId);
  }

  public boolean withdraw(String studentId) {
    return enrolledStudentIds.remove(studentId);
  }

  public String timeSlotsDisplay() {
    if (timeSlots.isEmpty()) {
      return "TBA";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < timeSlots.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(timeSlots.get(i).toDisplayString());
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    String status = open ? "OPEN" : "CLOSED";
    String seats = seatLimit == 0 ? (getEnrolledCount() + "/unlimited") : (getEnrolledCount() + "/" + seatLimit);
    return getKey() + " | " + course + " | " + status + " | seats " + seats + " | " + timeSlotsDisplay();
  }
}
