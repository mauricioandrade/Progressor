package com.mauricioandrade.progressor.infrastructure.reports;

public class MeasurementRow {

  private final String metric;
  private final String initial;
  private final String current;
  private final String difference;
  private final Boolean improved;

  public MeasurementRow(String metric, String initial, String current, String difference,
      Boolean improved) {
    this.metric = metric;
    this.initial = initial;
    this.current = current;
    this.difference = difference;
    this.improved = improved;
  }

  public String getMetric() {
    return metric;
  }

  public String getInitial() {
    return initial;
  }

  public String getCurrent() {
    return current;
  }

  public String getDifference() {
    return difference;
  }

  public Boolean getImproved() {
    return improved;
  }
}
