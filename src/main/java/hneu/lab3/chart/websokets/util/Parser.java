package hneu.lab3.chart.websokets.util;

public interface Parser<T> {

    T parseToObject(String json, Class<T> clazz);
    String parseToJson(T object, Class<T> clazz);
    T parseJsonFileToObject(Class<T> clazz);

}