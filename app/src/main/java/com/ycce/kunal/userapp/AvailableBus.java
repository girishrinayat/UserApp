package com.ycce.kunal.userapp;

class AvailableBus {
    private String busNumber;
    private String currentBusStop;
    private String routeName;
    private String towards;
    private String location;

    public String getCurrentBusStop() {
        return currentBusStop;
    }

    public void setCurrentBusStop(String currentBusStop) {
        this.currentBusStop = currentBusStop;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTowards() {
        return towards;
    }

    public void setTowards(String towards) {
        this.towards = towards;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
}
