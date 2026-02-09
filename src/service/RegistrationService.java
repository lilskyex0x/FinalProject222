package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Course;
import model.CourseOffering;
import model.Curriculum;
import model.Student;
import model.TimeSlot;

public final class RegistrationService {

  public static final class Result {
    private final boolean success;
    private final String message;

    public Result(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public String toString() {
      return (success ? "SUCCESS: " : "FAILED: ") + message;
    }
  }

  private final UniversitySystem system;

  public RegistrationService(UniversitySystem system) {
    this.system = system;
  }

  public Result register(String studentId, String offeringKey) {
    Student student = system.getStudent(studentId);
    if (student == null) {
      return new Result(false, "Student not found.");
    }

    CourseOffering offering = system.getOffering(offeringKey);
    if (offering == null) {
      return new Result(false, "Course offering not found.");
    }

    Course course = offering.getCourse();

    if (!offering.isOpen()) {
      return new Result(false, "Course is closed for registration.");
    }

    if (student.hasCompleted(course.getCode())) {
      return new Result(false, "Course already completed.");
    }

    if (student.isRegisteredForOffering(offering.getKey())) {
      return new Result(false, "Already registered for this course.");
    }

    // Curriculum match
    Curriculum curriculum = system.getCurriculum();
    if (!curriculum.isInCurriculum(student.getTrack(), course.getCode())) {
      return new Result(false, "Course is not in the student's curriculum/track.");
    }

    // Rule order:
    // 1) prerequisites
    for (String pre : course.getPrerequisites()) {
      if (!student.hasCompleted(pre)) {
        return new Result(false, "Missing prerequisite: " + pre);
      }
    }

    // 2) credit limit
    int currentCredits = getRegisteredCreditsForSemester(student, offering.getSemester());
    if (currentCredits + course.getCredits() > student.getMaxCreditsPerSemester()) {
      return new Result(false,
          "Credit limit exceeded (" + currentCredits + " + " + course.getCredits() + " > " + student.getMaxCreditsPerSemester() + ").");
    }

    // 3) time conflict
    List<TimeSlot> newSlots = offering.getTimeSlots();
    if (!newSlots.isEmpty()) {
      for (CourseOffering existing : getRegisteredOfferingsForSemester(student, offering.getSemester())) {
        for (TimeSlot a : existing.getTimeSlots()) {
          for (TimeSlot b : newSlots) {
            if (a.conflictsWith(b)) {
              return new Result(false, "Time conflict with " + existing.getCourse().getCode() + " (" + a.toDisplayString() + ").");
            }
          }
        }
      }
    }

    // 4) seats available
    if (!offering.hasSeatAvailable()) {
      return new Result(false, "No seats available.");
    }

    boolean enrolled = offering.enroll(student.getId());
    if (!enrolled) {
      return new Result(false, "Could not enroll (course may be closed or full).");
    }

    student.registerOffering(offering.getKey());
    return new Result(true, "Registered for " + offering.getKey());
  }

  public Result withdraw(String studentId, String offeringKey) {
    Student student = system.getStudent(studentId);
    if (student == null) {
      return new Result(false, "Student not found.");
    }

    CourseOffering offering = system.getOffering(offeringKey);
    if (offering == null) {
      return new Result(false, "Course offering not found.");
    }

    if (!student.isRegisteredForOffering(offering.getKey())) {
      return new Result(false, "Student is not registered for this offering.");
    }

    offering.withdraw(student.getId());
    student.withdrawOffering(offering.getKey());
    return new Result(true, "Withdrawn from " + offering.getKey());
  }

  public int getRegisteredCreditsForSemester(Student student, String semester) {
    int sum = 0;
    for (CourseOffering o : getRegisteredOfferingsForSemester(student, semester)) {
      sum += o.getCourse().getCredits();
    }
    return sum;
  }

  public List<CourseOffering> getRegisteredOfferingsForSemester(Student student, String semester) {
    Set<String> keys = student.getRegisteredOfferingKeys();
    List<CourseOffering> list = new ArrayList<CourseOffering>();
    for (String key : keys) {
      CourseOffering o = system.getOffering(key);
      if (o != null && o.getSemester().equals(semester)) {
        list.add(o);
      }
    }
    return list;
  }
}
