package model;

import java.util.Objects;

public final class TimeSlot {
  public enum Day {
    MON, TUE, WED, THU, FRI
  }

  private final Day day;
  private final int startMinutes; // minutes from 00:00
  private final int endMinutes; // exclusive

  public TimeSlot(Day day, int startMinutes, int endMinutes) {
    if (day == null) {
      throw new IllegalArgumentException("day cannot be null");
    }
    if (startMinutes < 0 || endMinutes < 0 || startMinutes >= endMinutes) {
      throw new IllegalArgumentException("invalid time range");
    }
    this.day = day;
    this.startMinutes = startMinutes;
    this.endMinutes = endMinutes;
  }

  public Day getDay() {
    return day;
  }

  public int getStartMinutes() {
    return startMinutes;
  }

  public int getEndMinutes() {
    return endMinutes;
  }

  public boolean conflictsWith(TimeSlot other) {
    if (other == null) {
      return false;
    }
    if (day != other.day) {
      return false;
    }
    return startMinutes < other.endMinutes && other.startMinutes < endMinutes;
  }

  public String toDisplayString() {
    return day + " " + format(startMinutes) + "-" + format(endMinutes);
  }

  private static String format(int minutes) {
    int h = minutes / 60;
    int m = minutes % 60;
    String hh = (h < 10 ? "0" : "") + h;
    String mm = (m < 10 ? "0" : "") + m;
    return hh + ":" + mm;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TimeSlot)) {
      return false;
    }
    TimeSlot that = (TimeSlot) o;
    return startMinutes == that.startMinutes && endMinutes == that.endMinutes && day == that.day;
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, startMinutes, endMinutes);
  }
}
