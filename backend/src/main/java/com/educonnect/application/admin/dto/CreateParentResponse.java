package com.educonnect.application.admin.dto;

public class CreateParentResponse {
    private String id;
    private String email;
    private String temporaryPassword;

    public CreateParentResponse() {}
    public CreateParentResponse(String id, String email, String temporaryPassword) {
        this.id = id;
        this.email = email;
        this.temporaryPassword = temporaryPassword;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTemporaryPassword() { return temporaryPassword; }
    public void setTemporaryPassword(String temporaryPassword) { this.temporaryPassword = temporaryPassword; }
}
