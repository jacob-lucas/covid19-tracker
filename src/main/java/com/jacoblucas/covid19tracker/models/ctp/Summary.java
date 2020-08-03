package com.jacoblucas.covid19tracker.models.ctp;

import java.util.Optional;

public abstract class Summary {
    // Date on which data was collected by The COVID Tracking Project.
    public abstract int getDate();

    // Total fatalities with confirmed OR probable COVID-19 case diagnosis.
    public abstract Optional<Integer> getDeath();

    // Increase in death computed by subtracting the value of death for the
    // previous day from the value of death for the current day.
    public abstract Optional<Integer> getDeathIncrease();

    // Computed by adding positive and negative values to work around reporting lags
    // between positives and total tests and because some states do not report totals.
    public abstract Optional<Integer> getTotalTestResults();

    // Daily Difference in totalTestResults.
    public abstract Optional<Integer> getTotalTestResultsIncrease();

    // Individuals with confirmed or probable COVID-19.
    public abstract Optional<Integer> getPositive();

    // Increase in positive computed by subtracting the value of positive from the
    // previous day from the value of positive for the current day.
    public abstract Optional<Integer> getPositiveIncrease();
}
