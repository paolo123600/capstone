package com.medicall.capstone.secretary;

public class SecretaryListModel {

    private String FirstName;
    private String LastName;
    private String DocType;
    private String ClinicName;
    private String UserId;
    private String CLuid;

    public String getStorageId() {
        return StorageId;
    }

    public void setStorageId(String storageId) {
        StorageId = storageId;
    }

    private String StorageId;

    private SecretaryListModel() {}

    private SecretaryListModel(String FirstName, String LastName, String DocType, String ClinicName, String UserId, String CLuid, String storageId) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.DocType = DocType;
        this.ClinicName = ClinicName;
        this.UserId = UserId;
        this.CLuid = CLuid;
        this.StorageId = storageId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDocType() {
        return DocType;
    }

    public void setDocType(String docType) {
        DocType = docType;
    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        this.ClinicName = clinicName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCLuid() {
        return CLuid;
    }

    public void setCLuid(String CLuid) {
        this.CLuid = CLuid;
    }
}
