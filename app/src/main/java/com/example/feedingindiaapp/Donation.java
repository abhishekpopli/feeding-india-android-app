package com.example.feedingindiaapp;

public class Donation {

    private long donationId;
    private String requestDateTime;
    private String pickupDateTime;
    private int donorId;
    private int volunteerId;
    private String pickupPhotoUrl;
    private String deliveryPhotoUrl;
    private String pickupCity;
    private String pickupArea;
    private String pickupStreet;
    private String pickupHouseNo;
    private boolean isVeg;
    private boolean isPerishable;
    private boolean isAccepted;
    private boolean isPicked;
    private boolean isCompleted;
    private String otherDetails;

    private String donorPhotoUrl;
    private String donorName;

    public Donation(long donationId, String pickupArea, boolean isVeg, boolean isPerishable, boolean isAccepted, boolean isPicked, String otherDetails, String donorPhotoUrl, String donorName) {
        this.donationId = donationId;
        this.pickupArea = pickupArea;
        this.isVeg = isVeg;
        this.isPerishable = isPerishable;
        this.isAccepted = isAccepted;
        this.isPicked = isPicked;
        this.otherDetails = otherDetails;
        this.donorPhotoUrl = donorPhotoUrl;
        this.donorName = donorName;
    }

    public Donation(long donationId, String requestDateTime, String pickupDateTime, int donorId, int volunteerId, String pickupPhotoUrl, String deliveryPhotoUrl, String pickupCity, String pickupArea, String pickupStreet, String pickupHouseNo, boolean isVeg, boolean isPerishable, boolean isAccepted, boolean isPicked, boolean isCompleted, String otherDetails, String donorPhotoUrl, String donorName) {
        this.donationId = donationId;
        this.requestDateTime = requestDateTime;
        this.pickupDateTime = pickupDateTime;
        this.donorId = donorId;
        this.volunteerId = volunteerId;
        this.pickupPhotoUrl = pickupPhotoUrl;
        this.deliveryPhotoUrl = deliveryPhotoUrl;
        this.pickupCity = pickupCity;
        this.pickupArea = pickupArea;
        this.pickupStreet = pickupStreet;
        this.pickupHouseNo = pickupHouseNo;
        this.isVeg = isVeg;
        this.isPerishable = isPerishable;
        this.isAccepted = isAccepted;
        this.isPicked = isPicked;
        this.isCompleted = isCompleted;
        this.otherDetails = otherDetails;
        this.donorPhotoUrl = donorPhotoUrl;
        this.donorName = donorName;
    }

    public long getDonationId() {
        return donationId;
    }

    public String getRequestDateTime() {
        return requestDateTime;
    }

    public String getPickupDateTime() {
        return pickupDateTime;
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

    public boolean isVeg() {
        return isVeg;
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

    public String getDonorPhotoUrl() {
        return donorPhotoUrl;
    }

    public String getDonorName() {
        return donorName;
    }
}

