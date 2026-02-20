package com.educonnect.application.admin.dto;

import java.util.Set;

public class TeacherDto {
    private String id;
    private String userId;
    private String email;
    private String fullName;
    private String phone;
    private String education;
    private String bio;
    private Set<String> specializations;
    private String verificationStatus; // PENDING, VERIFIED, REJECTED
    private Double hourlyRate;
    private boolean active;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Set<String> getSpecializations() { return specializations; }
    public void setSpecializations(Set<String> specializations) { this.specializations = specializations; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
