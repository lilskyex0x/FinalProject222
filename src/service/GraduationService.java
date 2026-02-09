package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Course;
import model.Curriculum;
import model.MajorTrack;
import model.Student;

public final class GraduationService {

  public static final class Progress {
    public final int completedCredits;
    public final int remainingCredits;
    public final List<String> remainingRequiredCourses;
    public final int completedTrackElectives;
    public final int remainingTrackElectives;
    public final boolean eligibleToGraduate;

    public Progress(int completedCredits,
        int remainingCredits,
        List<String> remainingRequiredCourses,
        int completedTrackElectives,
        int remainingTrackElectives,
        boolean eligibleToGraduate) {
      this.completedCredits = completedCredits;
      this.remainingCredits = remainingCredits;
      this.remainingRequiredCourses = remainingRequiredCourses;
      this.completedTrackElectives = completedTrackElectives;
      this.remainingTrackElectives = remainingTrackElectives;
      this.eligibleToGraduate = eligibleToGraduate;
    }
  }

  private final UniversitySystem system;

  public GraduationService(UniversitySystem system) {
    this.system = system;
  }

  public Progress computeProgress(String studentId) {
    Student student = system.getStudent(studentId);
    if (student == null) {
      return new Progress(0, 0, new ArrayList<String>(), 0, 0, false);
    }

    Curriculum curriculum = system.getCurriculum();

    int completedCredits = 0;
    for (String code : student.getCompletedCourses().keySet()) {
      Course c = system.getCourse(code);
      if (c != null) {
        completedCredits += c.getCredits();
      }
    }

    List<String> remainingRequired = new ArrayList<String>();
    Set<String> required = curriculum.getRequiredCourseCodes();
    for (String req : required) {
      if (!student.hasCompleted(req)) {
        remainingRequired.add(req);
      }
    }

    int completedElectives = 0;
    MajorTrack track = student.getTrack();
    if (track != null) {
      Set<String> electives = curriculum.getTrackElectiveCourseCodes(track);
      for (String e : electives) {
        if (student.hasCompleted(e)) {
          completedElectives++;
        }
      }
    }

    int remainingElectives = Math.max(0, curriculum.getMinTrackElectives() - completedElectives);

    int remainingCredits = Math.max(0, curriculum.getTotalCreditsToGraduate() - completedCredits);

    boolean eligible = remainingRequired.isEmpty() && remainingElectives == 0 && remainingCredits == 0;

    return new Progress(completedCredits, remainingCredits, remainingRequired, completedElectives, remainingElectives, eligible);
  }

  public String graduationRiskSummary(String studentId, int semestersRemaining) {
    Student student = system.getStudent(studentId);
    if (student == null) {
      return "Student not found.";
    }

    Progress p = computeProgress(studentId);
    int maxPossibleCredits = semestersRemaining * student.getMaxCreditsPerSemester();

    if (p.eligibleToGraduate) {
      return "On track: already eligible to graduate.";
    }

    if (p.remainingCredits > maxPossibleCredits) {
      return "RISK: Remaining credits (" + p.remainingCredits + ") exceed max possible before target (" + maxPossibleCredits + ").";
    }

    return "OK: Remaining credits (" + p.remainingCredits + ") are feasible within " + semestersRemaining + " semester(s) at max " + student.getMaxCreditsPerSemester() + " credits/semester.";
  }
}