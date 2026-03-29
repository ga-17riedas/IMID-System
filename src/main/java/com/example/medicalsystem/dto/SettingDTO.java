package com.example.medicalsystem.dto;

/**
 * 系统设置数据传输对象
 * 用于前后端交互时传递系统设置信息
 */
public class SettingDTO {
    
    private String key;
    private String value;
    private String type;
    private String group;
    private String displayName;
    private String description;
    private Boolean isReadonly;
    
    public SettingDTO() {
    }
    
    public SettingDTO(String key, String value, String type, String group, String displayName, String description, Boolean isReadonly) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.group = group;
        this.displayName = displayName;
        this.description = description;
        this.isReadonly = isReadonly;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsReadonly() {
        return isReadonly;
    }
    
    public void setIsReadonly(Boolean isReadonly) {
        this.isReadonly = isReadonly;
    }
    
    @Override
    public String toString() {
        return "SettingDTO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", group='" + group + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", isReadonly=" + isReadonly +
                '}';
    }
} 