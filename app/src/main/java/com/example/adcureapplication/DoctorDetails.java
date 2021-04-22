package com.example.adcureapplication;

public class DoctorDetails {
    private String City,Name,Email,Address,Date,Country,Experience,
    Hospital_Address,Hospital_Name,Id,Image,Number,Price,Qualification,Specialist,State,Time;
    public DoctorDetails(){}

    public DoctorDetails(String city, String name, String email, String address, String date, String country, String experience, String hospital_Address, String hospital_Name, String id, String image, String number, String price, String qualification, String specialist, String state, String time) {
        City = city;
        Name = name;
        Email = email;
        Address = address;
        Date = date;
        Country = country;
        Experience = experience;
        Hospital_Address = hospital_Address;
        Hospital_Name = hospital_Name;
        Id = id;
        Image = image;
        Number = number;
        Price = price;
        Qualification = qualification;
        Specialist = specialist;
        State = state;
        Time = time;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getName() {
        return Name;
    }

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getHospital_Address() {
        return Hospital_Address;
    }

    public void setHospital_Address(String hospital_Address) {
        Hospital_Address = hospital_Address;
    }

    public String getHospital_Name() {
        return Hospital_Name;
    }

    public void setHospital_Name(String hospital_Name) {
        Hospital_Name = hospital_Name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQualification() {
        return Qualification;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

    public String getSpecialist() {
        return Specialist;
    }

    public void setSpecialist(String specialist) {
        Specialist = specialist;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}