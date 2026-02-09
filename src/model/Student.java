package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Student {
  private final String id;
  private String name;
  private MajorTrack track;

  // courseCode -> grade (null means in progress / unknown)
  private final Map<String, String> completedCourses;

  // offering keys (semester:courseCode)
  private final Set<String> registeredOfferingKeys;

  private int maxCreditsPerSemester;

  public Student(String id, String name, MajorTrack track, int maxCreditsPerSemester) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("student id required");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("student name required");
    }
    if (maxCreditsPerSemester <= 0) {
      throw new IllegalArgumentException("maxCreditsPerSemester must be positive");
    }
    this.id = id.trim();
    this.name = name.trim();
    this.track = track;
    this.completedCourses = new HashMap<String, String>();
    this.registeredOfferingKeys = new HashSet<String>();
    this.maxCreditsPerSemester = maxCreditsPerSemester;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public MajorTrack getTrack() {
    return track;
  }

  public int getMaxCreditsPerSemester() {
    return maxCreditsPerSemester;
  }

  public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("name required");
    }
    this.name = name.trim();
  }

  public void setTrack(MajorTrack track) {
    this.track = track;
  }

  public void setMaxCreditsPerSemester(int maxCreditsPerSemester) {
    if (maxCreditsPerSemester <= 0) {
      throw new IllegalArgumentException("maxCreditsPerSemester must be positive");
    }
    this.maxCreditsPerSemester = maxCreditsPerSemester;
  }

  public Map<String, String> getCompletedCourses() {
    return Collections.unmodifiableMap(completedCourses);
  }

  public void addCompletedCourse(String courseCode, String grade) {
    if (courseCode == null || courseCode.trim().isEmpty()) {
      return;
    }
    completedCourses.put(courseCode.trim().toUpperCase(), grade);
  }

  public boolean hasCompleted(String courseCode) {
    if (courseCode == null) {
      return false;
    }
    return completedCourses.containsKey(courseCode.trim().toUpperCase());
  }

  public Set<String> getRegisteredOfferingKeys() {
    return Collections.unmodifiableSet(registeredOfferingKeys);
  }

  public boolean isRegisteredForOffering(String offeringKey) {
    return registeredOfferingKeys.contains(offeringKey);
  }

  public boolean registerOffering(String offeringKey) {
    return registeredOfferingKeys.add(offeringKey);
  }

  public boolean withdrawOffering(String offeringKey) {
    return registeredOfferingKeys.remove(offeringKey);
  }

  @Override
  public String toString() {
    String t = (track == null) ? "(no track)" : track.displayName();
    return id + " - " + name + " - " + t;
  }
}