package com.noqapp.android.common.pojos;

public class MedicalFile {
    private String recordReferenceId;
    private String fileLocation;
    private String fileCreatedDate;
    private String uploadStatus;
    private String uploadAttemptCount;
    private String formSubmissionStatus;

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public void setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFormSubmissionStatus() {
        return formSubmissionStatus;
    }

    public void setFormSubmissionStatus(String formSubmissionStatus) {
        this.formSubmissionStatus = formSubmissionStatus;
    }

    public String getUploadAttemptCount() {
        return uploadAttemptCount;
    }

    public void setUploadAttemptCount(String uploadAttemptCount) {
        this.uploadAttemptCount = uploadAttemptCount;
    }

    public String getFileCreatedDate() {
        return fileCreatedDate;
    }

    public void setFileCreatedDate(String fileCreatedDate) {
        this.fileCreatedDate = fileCreatedDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MedicalFile{");
        sb.append("recordReferenceId='").append(recordReferenceId).append('\'');
        sb.append(", fileLocation='").append(fileLocation).append('\'');
        sb.append(", fileCreatedDate='").append(fileCreatedDate).append('\'');
        sb.append(", uploadStatus='").append(uploadStatus).append('\'');
        sb.append(", uploadAttemptCount='").append(uploadAttemptCount).append('\'');
        sb.append(", formSubmissionStatus='").append(formSubmissionStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
