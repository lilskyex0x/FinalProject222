package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Course {
  private final String code;
  private String title;
  private int credits;
  private final List<String> prerequisites;

  public Course(String code, String title, int credits) {
    this(code, title, credits, new ArrayList<String>());
  }

  public Course(String code, String title, int credits, List<String> prerequisites) {
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException("course code required");
    }
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("course title required");
    }
    if (credits <= 0) {
      throw new IllegalArgumentException("credits must be positive");
    }
    this.code = code.trim().toUpperCase();
    this.title = title.trim();
    this.credits = credits;
    this.prerequisites = new ArrayList<String>();
    if (prerequisites != null) {
      for (String p : prerequisites) {
        if (p != null && !p.trim().isEmpty()) {
          this.prerequisites.add(p.trim().toUpperCase());
        }
      }
    }
  }

  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }

  public int getCredits() {
    return credits;
  }

  public List<String> getPrerequisites() {
    return Collections.unmodifiableList(prerequisites);
  }

  public void setTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("title required");
    }
    this.title = title.trim();
  }

  public void setCredits(int credits) {
    if (credits <= 0) {
      throw new IllegalArgumentException("credits must be positive");
    }
    this.credits = credits;
  }

  @Override
  public String toString() {
    return code + " (" + credits + " cr): " + title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Course)) {
      return false;
    }
    Course course = (Course) o;
    return Objects.equals(code, course.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}
