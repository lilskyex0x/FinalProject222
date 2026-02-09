package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Curriculum {
  private final Set<String> requiredCourseCodes;
  private final Map<MajorTrack, Set<String>> trackElectiveCourseCodes;
  private final int totalCreditsToGraduate;
  private final int minTrackElectives;

  public Curriculum(int totalCreditsToGraduate, int minTrackElectives) {
    if (totalCreditsToGraduate <= 0) {
      throw new IllegalArgumentException("totalCreditsToGraduate must be positive");
    }
    if (minTrackElectives < 0) {
      throw new IllegalArgumentException("minTrackElectives must be >= 0");
    }
    this.requiredCourseCodes = new HashSet<String>();
    this.trackElectiveCourseCodes = new HashMap<MajorTrack, Set<String>>();
    this.totalCreditsToGraduate = totalCreditsToGraduate;
    this.minTrackElectives = minTrackElectives;
  }

  public int getTotalCreditsToGraduate() {
    return totalCreditsToGraduate;
  }

  public int getMinTrackElectives() {
    return minTrackElectives;
  }

  public void addRequired(String courseCode) {
    if (courseCode != null && !courseCode.trim().isEmpty()) {
      requiredCourseCodes.add(courseCode.trim().toUpperCase());
    }
  }

  public void addTrackElective(MajorTrack track, String courseCode) {
    if (track == null) {
      return;
    }
    if (courseCode == null || courseCode.trim().isEmpty()) {
      return;
    }
    Set<String> set = trackElectiveCourseCodes.get(track);
    if (set == null) {
      set = new HashSet<String>();
      trackElectiveCourseCodes.put(track, set);
    }
    set.add(courseCode.trim().toUpperCase());
  }

  public Set<String> getRequiredCourseCodes() {
    return Collections.unmodifiableSet(requiredCourseCodes);
  }

  public Set<String> getTrackElectiveCourseCodes(MajorTrack track) {
    Set<String> set = trackElectiveCourseCodes.get(track);
    if (set == null) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(set);
  }

  public boolean isInCurriculum(MajorTrack track, String courseCode) {
    if (courseCode == null) {
      return false;
    }
    String c = courseCode.trim().toUpperCase();
    if (requiredCourseCodes.contains(c)) {
      return true;
    }
    if (track != null && getTrackElectiveCourseCodes(track).contains(c)) {
      return true;
    }
    return false;
  }
}
