package com.carnasa.cr.projectkingdomwebpage.models.user.read;

public class UserContactDetailsDto {
    private String address1;
    private String address2;
    private String city;
    private String countyState;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private String mobilePhoneNumber;

    public UserContactDetailsDto(
            String address1,
            String address2,
            String city,
            String countyState,
            String postalCode,
            String country,
            String phoneNumber,
            String mobilePhoneNumber) {

        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.countyState = countyState;
        this.postalCode = postalCode;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountyState() {
        return countyState;
    }

    public void setCountyState(String countyState) {
        this.countyState = countyState;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }
}
