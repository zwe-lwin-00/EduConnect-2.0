package com.educonnect.application.admin.dto;

public class SystemSettingDto {
    private String id;
    private String keyName;
    private String value;
    private String description;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
