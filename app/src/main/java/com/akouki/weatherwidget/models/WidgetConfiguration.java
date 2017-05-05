package com.akouki.weatherwidget.models;

public class WidgetConfiguration {
    private int locationId;
    private String locationName;
    private String displayUnit;
    private int timeRawOffset;

    public int getLocation() {
        return locationId;
    }

    public void setLocation(int locationId) {
        this.locationId = locationId;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDisplayUnit() {
        return displayUnit;
    }

    public void setDisplayUnit(String displayUnit) {
        this.displayUnit = displayUnit;
    }

    public int getTimeRawOffset() {
        return timeRawOffset;
    }

    public void setTimeRawOffset(int timeRawOffset) {
        this.timeRawOffset = timeRawOffset;
    }
}