package com.educonnect.application.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class CreateTeacherRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String fullName;
    private String phone;
    private String nrc;
    private String education;
    private String bio;
    private Set<String> specializations;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNrc() { return nrc; }
    public void setNrc(String nrc) { this.nrc = nrc; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Set<String> getSpecializations() { return specializations; }
    public void setSpecializations(Set<String> specializations) { this.specializations = specializations; }
}
