package com.example.feedingindiaapp;

public class Donation {

    private long donationId;
    private String requestTime;
    private String requestDate;
    private String pickupTime;
    private String pickupDate;
    private int donorId;
    private int volunteerId;
    private String pickupPhotoUrl;
    private String deliveryPhotoUrl;
    private String pickupCity;
    private String pickupArea;
    private String pickupStreet;
    private String pickupHouseNo;
    private boolean isPerishable;
    private boolean isAccepted;
    private boolean isPicked;
    private boolean isCompleted;
    private String otherDetails;

    // Need to change this, get all data values
    public Donation(String pickupTime, String pickupArea) {
        this.pickupTime = pickupTime;
        this.pickupArea = pickupArea;
    }

    /**
     * Getters
     */

    public long getDonationId() {
        return donationId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public int getDonorId() {
        return donorId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public String getPickupPhotoUrl() {
        return pickupPhotoUrl;
    }

    public String getDeliveryPhotoUrl() {
        return deliveryPhotoUrl;
    }

    public String getPickupCity() {
        return pickupCity;
    }

    public String getPickupArea() {
        return pickupArea;
    }

    public String getPickupStreet() {
        return pickupStreet;
    }

    public String getPickupHouseNo() {
        return pickupHouseNo;
    }

    public boolean isPerishable() {
        return isPerishable;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getOtherDetails() {
        return otherDetails;
    }
}

