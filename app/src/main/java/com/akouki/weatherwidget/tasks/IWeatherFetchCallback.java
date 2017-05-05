package com.akouki.weatherwidget.tasks;

public interface IWeatherFetchCallback {
    void OnTaskCompleted(String jsonData);
    void OnTaskFailed(String reason);
}
