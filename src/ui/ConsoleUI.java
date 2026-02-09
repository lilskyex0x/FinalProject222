package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.Course;
import model.CourseOffering;
import model.MajorTrack;
import model.Student;
import service.GraduationService;
import service.RegistrationService;
import service.UniversitySystem;

public final class ConsoleUI {
  private final UniversitySystem system;
  private final Scanner in;

  public ConsoleUI(UniversitySystem system) {
    this.system = system;
    this.in = new Scanner(System.in);
  }

  public void run() {
    while (true) {
      System.out.println();
      System.out.println("=== Rule-Based Course Registration & Curriculum Tracking ===");
      System.out.println("1) Admin");
      System.out.println("2) Advisor");
      System.out.println("3) Student");
      System.out.println("0) Exit");
      int choice = readInt("Choose role: ");
      if (choice == 0) {
        System.out.println("Goodbye.");
        return;
      }
      if (choice == 1) {
        adminMenu();
      } else if (choice == 2) {
        advisorMenu();
      } else if (choice == 3) {
        studentMenu();
      }
    }
  }

  private void adminMenu() {
    while (true) {
      System.out.println();
      System.out.println("--- Admin Menu ---");
      System.out.println("1) List courses");
      System.out.println("2) Add course");
      System.out.println("3) Remove course");
      System.out.println("4) List offerings");
      System.out.println("5) Open/close offering");
      System.out.println("6) Set seat limit");
      System.out.println("7) System statistics");
      System.out.println("0) Back");

      int c = readInt("Choose: ");
      if (c == 0) {
        return;
      }
      switch (c) {
        case 1:
          listCourses();
          break;
        case 2:
          addCourse();
          break;
        case 3:
          removeCourse();
          break;
        case 4:
          listOfferings();
          break;
        case 5:
          toggleOffering();
          break;
        case 6:
          setSeatLimit();
          break;
        case 7:
          showStats();
          break;
        default:
          System.out.println("Unknown option.");
      }
    }
  }

  private void advisorMenu() {
    while (true) {
      System.out.println();
      System.out.println("--- Advisor Menu ---");
      System.out.println("1) Search student by ID");
      System.out.println("2) View curriculum progress");
      System.out.println("3) See remaining required courses");
      System.out.println("4) Register course for student");
      System.out.println("5) Withdraw course for student");
      System.out.println("6) Detect graduation risk");
      System.out.println("0) Back");

      int c = readInt("Choose: ");
      if (c == 0) {
        return;
      }
      switch (c) {
        case 1:
          showStudent();
          break;
        case 2:
          showProgress();
          break;
        case 3:
          showRemainingRequired();
          break;
        case 4:
          advisorRegister();
          break;
        case 5:
          advisorWithdraw();
          break;
        case 6:
          detectRisk();
          break;
        default:
          System.out.println("Unknown option.");
      }
    }
  }

  private void studentMenu() {
    String id = readLine("Enter your student ID (e.g., S1001): ").trim();
    Student s = system.getStudent(id);
    if (s == null) {
      System.out.println("Student not found.");
      return;
    }

    while (true) {
      System.out.println();
      System.out.println("--- Student Menu (" + s + ") ---");
      System.out.println("1) View completed courses");
      System.out.println("2) View remaining requirements");
      System.out.println("3) Check prerequisites for a course");
      System.out.println("4) Register for a course offering");
      System.out.println("5) View registration status");
      System.out.println("0) Back");

      int c = readInt("Choose: ");
      if (c == 0) {
        return;
      }
      switch (c) {
        case 1:
          showCompleted(s);
          break;
        case 2:
          showProgressForStudent(s);
          break;
        case 3:
          checkPrereqs(s);
          break;
        case 4:
          studentRegister(s);
          break;
        case 5:
          showRegistration(s);
          break;
        default:
          System.out.println("Unknown option.");
      }
    }
  }

  // --- Admin actions ---

  private void listCourses() {
    System.out.println();
    System.out.println("Courses:");
    for (Course c : system.getCoursesByCode().values()) {
      System.out.println("- " + c);
    }
  }

  private void addCourse() {
    System.out.println();
    String code = readLine("Code (e.g., CS150): ");
    String title = readLine("Title: ");
    int credits = readInt("Credits: ");
    Course course = new Course(code, title, credits);
    system.addCourse(course);
    System.out.println("Added: " + course);
  }

  private void removeCourse() {
    String code = readLine("Course code to remove: ");
    system.removeCourse(code);
    System.out.println("Removed (if existed): " + code.trim().toUpperCase());
  }

  private void listOfferings() {
    System.out.println();
    System.out.println("Offerings:");
    for (CourseOffering o : system.getOfferingsByKey().values()) {
      System.out.println("- " + o);
    }
  }

  private void toggleOffering() {
    String key = readLine("Offering key (semester:courseCode), e.g., Spring-2026:CS101: ");
    CourseOffering o = system.getOffering(key);
    if (o == null) {
      System.out.println("Not found.");
      return;
    }
    o.setOpen(!o.isOpen());
    System.out.println("Now " + (o.isOpen() ? "OPEN" : "CLOSED") + ": " + o.getKey());
  }

  private void setSeatLimit() {
    String key = readLine("Offering key (semester:courseCode): ");
    CourseOffering o = system.getOffering(key);
    if (o == null) {
      System.out.println("Not found.");
      return;
    }
    int limit = readInt("New seat limit (0 = unlimited): ");
    o.setSeatLimit(limit);
    System.out.println("Updated: " + o);
  }

  private void showStats() {
    System.out.println();
    System.out.println("Total courses: " + system.getCoursesByCode().size());
    System.out.println("Total offerings: " + system.getOfferingsByKey().size());
    System.out.println("Total students: " + system.getStudentsById().size());
    System.out.println("Total enrollments: " + system.getTotalEnrollments());
  }

  // --- Advisor actions ---

  private Student requireStudent() {
    String id = readLine("Student ID: ").trim();
    Student s = system.getStudent(id);
    if (s == null) {
      System.out.println("Student not found.");
      return null;
    }
    return s;
  }

  private void showStudent() {
    Student s = requireStudent();
    if (s != null) {
      System.out.println(s);
    }
  }

  private void showProgress() {
    Student s = requireStudent();
    if (s == null) {
      return;
    }
    showProgressForStudent(s);
  }

  private void showRemainingRequired() {
    Student s = requireStudent();
    if (s == null) {
      return;
    }
    GraduationService.Progress p = system.getGraduationService().computeProgress(s.getId());
    System.out.println("Remaining required courses: " + p.remainingRequiredCourses);
  }

  private void advisorRegister() {
    Student s = requireStudent();
    if (s == null) {
      return;
    }
    listOfferings();
    String key = readLine("Offering key to register: ");
    RegistrationService.Result r = system.getRegistrationService().register(s.getId(), key);
    System.out.println(r);
  }

  private void advisorWithdraw() {
    Student s = requireStudent();
    if (s == null) {
      return;
    }
    showRegistration(s);
    String key = readLine("Offering key to withdraw: ");
    RegistrationService.Result r = system.getRegistrationService().withdraw(s.getId(), key);
    System.out.println(r);
  }

  private void detectRisk() {
    Student s = requireStudent();
    if (s == null) {
      return;
    }
    int semesters = readInt("Semesters remaining until target graduation: ");
    System.out.println(system.getGraduationService().graduationRiskSummary(s.getId(), semesters));
  }

  // --- Student actions ---

  private void showCompleted(Student s) {
    System.out.println();
    if (s.getCompletedCourses().isEmpty()) {
      System.out.println("No completed courses yet.");
      return;
    }
    for (Map.Entry<String, String> e : s.getCompletedCourses().entrySet()) {
      System.out.println("- " + e.getKey() + " (grade: " + e.getValue() + ")");
    }
  }

  private void showProgressForStudent(Student s) {
    GraduationService.Progress p = system.getGraduationService().computeProgress(s.getId());
    System.out.println();
    System.out.println("Completed credits: " + p.completedCredits);
    System.out.println("Remaining credits (to reach program total): " + p.remainingCredits);
    System.out.println("Remaining required courses: " + p.remainingRequiredCourses);
    System.out.println("Track electives completed: " + p.completedTrackElectives);
    System.out.println("Track electives remaining (minimum): " + p.remainingTrackElectives);
    System.out.println("Eligible to graduate: " + (p.eligibleToGraduate ? "YES" : "NO"));
  }

  private void checkPrereqs(Student s) {
    String code = readLine("Course code to check: ").trim().toUpperCase();
    Course c = system.getCourse(code);
    if (c == null) {
      System.out.println("Course not found.");
      return;
    }
    if (c.getPrerequisites().isEmpty()) {
      System.out.println("No prerequisites for " + c.getCode());
      return;
    }
    List<String> missing = new ArrayList<String>();
    for (String pre : c.getPrerequisites()) {
      if (!s.hasCompleted(pre)) {
        missing.add(pre);
      }
    }
    if (missing.isEmpty()) {
      System.out.println("All prerequisites satisfied for " + c.getCode());
    } else {
      System.out.println("Missing prerequisites for " + c.getCode() + ": " + missing);
    }
  }

  private void studentRegister(Student s) {
    listOfferings();
    String key = readLine("Offering key to register: ");
    RegistrationService.Result r = system.getRegistrationService().register(s.getId(), key);
    System.out.println(r);
  }

  private void showRegistration(Student s) {
    System.out.println();
    if (s.getRegisteredOfferingKeys().isEmpty()) {
      System.out.println("No current registrations.");
      return;
    }
    for (String key : s.getRegisteredOfferingKeys()) {
      CourseOffering o = system.getOffering(key);
      System.out.println("- " + (o == null ? key : o.toString()));
    }
  }

  // --- IO helpers ---

  private String readLine(String prompt) {
    System.out.print(prompt);
    return in.nextLine();
  }

  private int readInt(String prompt) {
    while (true) {
      String s = readLine(prompt);
      try {
        return Integer.parseInt(s.trim());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number.");
      }
    }
  }
}
