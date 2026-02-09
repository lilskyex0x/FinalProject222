package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Course;
import model.CourseOffering;
import model.Curriculum;
import model.MajorTrack;
import model.Student;
import model.TimeSlot;

public final class UniversitySystem {
  private final Map<String, Course> coursesByCode;
  private final Map<String, CourseOffering> offeringsByKey;
  private final Map<String, Student> studentsById;
  private final Curriculum curriculum;

  private final RegistrationService registrationService;
  private final GraduationService graduationService;

  private UniversitySystem(Curriculum curriculum) {
    this.coursesByCode = new HashMap<String, Course>();
    this.offeringsByKey = new HashMap<String, CourseOffering>();
    this.studentsById = new HashMap<String, Student>();
    this.curriculum = curriculum;

    this.registrationService = new RegistrationService(this);
    this.graduationService = new GraduationService(this);
  }

  public static UniversitySystem createWithSampleData() {
    Curriculum curriculum = new Curriculum(120, 2);
    UniversitySystem sys = new UniversitySystem(curriculum);

    // Courses
    sys.addCourse(new Course("CS101", "Programming I", 3));
    sys.addCourse(new Course("CS102", "Programming II", 3, asList("CS101")));
    sys.addCourse(new Course("CS201", "Data Structures", 3, asList("CS102")));
    sys.addCourse(new Course("MA101", "Calculus I", 3));
    sys.addCourse(new Course("SE210", "Software Requirements", 3, asList("CS102")));
    sys.addCourse(new Course("DA220", "Intro to Data Analytics", 3, asList("CS102")));
    sys.addCourse(new Course("NS230", "Network Fundamentals", 3, asList("CS101")));
    sys.addCourse(new Course("EC240", "E-Commerce Systems", 3, asList("CS101")));

    // Curriculum requirements
    curriculum.addRequired("CS101");
    curriculum.addRequired("CS102");
    curriculum.addRequired("CS201");
    curriculum.addRequired("MA101");

    curriculum.addTrackElective(MajorTrack.SOFTWARE_ENGINEERING, "SE210");
    curriculum.addTrackElective(MajorTrack.DATA_ANALYTICS, "DA220");
    curriculum.addTrackElective(MajorTrack.NETWORK_SECURITY, "NS230");
    curriculum.addTrackElective(MajorTrack.E_COMMERCE, "EC240");

    // Semester offerings (Spring 2026)
    String sem = "Spring-2026";
    sys.addOffering(new CourseOffering(sem, sys.getCourse("CS101"), 30,
        asList(new TimeSlot(TimeSlot.Day.MON, 9 * 60, 10 * 60 + 15), new TimeSlot(TimeSlot.Day.WED, 9 * 60, 10 * 60 + 15))));
    sys.addOffering(new CourseOffering(sem, sys.getCourse("CS102"), 25,
        asList(new TimeSlot(TimeSlot.Day.TUE, 11 * 60, 12 * 60 + 15), new TimeSlot(TimeSlot.Day.THU, 11 * 60, 12 * 60 + 15))));
    sys.addOffering(new CourseOffering(sem, sys.getCourse("CS201"), 20,
        asList(new TimeSlot(TimeSlot.Day.MON, 10 * 60 + 30, 11 * 60 + 45), new TimeSlot(TimeSlot.Day.WED, 10 * 60 + 30, 11 * 60 + 45))));
    sys.addOffering(new CourseOffering(sem, sys.getCourse("MA101"), 40,
        asList(new TimeSlot(TimeSlot.Day.TUE, 9 * 60, 10 * 60 + 15), new TimeSlot(TimeSlot.Day.THU, 9 * 60, 10 * 60 + 15))));

    sys.addOffering(new CourseOffering(sem, sys.getCourse("SE210"), 15,
        asList(new TimeSlot(TimeSlot.Day.FRI, 9 * 60, 11 * 60))));
    sys.addOffering(new CourseOffering(sem, sys.getCourse("DA220"), 15,
        asList(new TimeSlot(TimeSlot.Day.FRI, 11 * 60 + 15, 13 * 60 + 15))));
    sys.addOffering(new CourseOffering(sem, sys.getCourse("NS230"), 15,
        asList(new TimeSlot(TimeSlot.Day.FRI, 13 * 60 + 30, 15 * 60))));
    sys.addOffering(new CourseOffering(sem, sys.getCourse("EC240"), 15,
        asList(new TimeSlot(TimeSlot.Day.FRI, 15 * 60 + 15, 17 * 60))));

    // Students
    Student s1 = new Student("S1001", "Amina", MajorTrack.SOFTWARE_ENGINEERING, 18);
    s1.addCompletedCourse("CS101", "A");
    sys.addStudent(s1);

    Student s2 = new Student("S1002", "Omar", MajorTrack.DATA_ANALYTICS, 15);
    s2.addCompletedCourse("CS101", "B");
    s2.addCompletedCourse("CS102", "B+");
    sys.addStudent(s2);

    return sys;
  }

  private static <T> List<T> asList(T a) {
	    List<T> list = new ArrayList<T>();
	    list.add(a);
	    return list;
	  }

  private static <T> List<T> asList(T a, T b) {
    List<T> list = new ArrayList<T>();
    list.add(a);
    list.add(b);
    return list;
  }

  public Curriculum getCurriculum() {
    return curriculum;
  }

  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  public GraduationService getGraduationService() {
    return graduationService;
  }

  public void addCourse(Course course) {
    coursesByCode.put(course.getCode(), course);
  }

  public Course getCourse(String courseCode) {
    if (courseCode == null) {
      return null;
    }
    return coursesByCode.get(courseCode.trim().toUpperCase());
  }

  public Map<String, Course> getCoursesByCode() {
    return Collections.unmodifiableMap(coursesByCode);
  }

  public void removeCourse(String courseCode) {
    if (courseCode == null) {
      return;
    }
    coursesByCode.remove(courseCode.trim().toUpperCase());
  }

  public void addOffering(CourseOffering offering) {
    offeringsByKey.put(offering.getKey(), offering);
  }

  public CourseOffering getOffering(String offeringKey) {
    if (offeringKey == null) {
      return null;
    }
    return offeringsByKey.get(offeringKey.trim());
  }

  public Map<String, CourseOffering> getOfferingsByKey() {
    return Collections.unmodifiableMap(offeringsByKey);
  }

  public void addStudent(Student student) {
    studentsById.put(student.getId(), student);
  }

  public Student getStudent(String studentId) {
    if (studentId == null) {
      return null;
    }
    return studentsById.get(studentId.trim());
  }

  public Map<String, Student> getStudentsById() {
    return Collections.unmodifiableMap(studentsById);
  }

  public int getTotalEnrollments() {
    int sum = 0;
    for (CourseOffering o : offeringsByKey.values()) {
      sum += o.getEnrolledCount();
    }
    return sum;
  }
}
