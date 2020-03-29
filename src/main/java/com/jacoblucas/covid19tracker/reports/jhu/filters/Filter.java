package com.jacoblucas.covid19tracker.reports.jhu.filters;

@FunctionalInterface
public interface Filter<T> {
    T apply(T t);
}
