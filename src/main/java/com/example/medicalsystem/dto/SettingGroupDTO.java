package com.example.medicalsystem.dto;

import java.util.List;

/**
 * 系统设置组数据传输对象
 * 用于将同一组的系统设置分组返回给前端
 */
public class SettingGroupDTO {
    
    private String groupName;
    private String displayName;
    private String description;
    private String icon;
    private Integer orderNum;
    private List<SettingDTO> settings;
    
    public SettingGroupDTO() {
    }
    
    public SettingGroupDTO(String groupName, String displayName, String description, String icon, Integer orderNum, List<SettingDTO> settings) {
        this.groupName = groupName;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.orderNum = orderNum;
        this.settings = settings;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public Integer getOrderNum() {
        return orderNum;
    }
    
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
    
    public List<SettingDTO> getSettings() {
        return settings;
    }
    
    public void setSettings(List<SettingDTO> settings) {
        this.settings = settings;
    }
    
    @Override
    public String toString() {
        return "SettingGroupDTO{" +
                "groupName='" + groupName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", orderNum=" + orderNum +
                ", settings=" + settings +
                '}';
    }
} 