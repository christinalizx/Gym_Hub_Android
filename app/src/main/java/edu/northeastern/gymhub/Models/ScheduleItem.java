package edu.northeastern.gymhub.Models;

public class ScheduleItem {
    private String itemName;
    private String itemTime;

    // Add a default constructor
    public ScheduleItem() {
        // Default constructor required for Firebase
    }

    public ScheduleItem(String itemName, String itemTime) {
        this.itemName = itemName;
        this.itemTime = itemTime;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemTime() {
        return itemTime;
    }
}
