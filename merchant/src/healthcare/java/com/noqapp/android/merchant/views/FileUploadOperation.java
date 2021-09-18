package com.noqapp.android.merchant.views;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.model.database.utils.MedicalFilesDB;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.pojos.MedicalFile;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadOperation extends AsyncTask<String, Void, String> implements ImageUploadPresenter {
    private Context context;
    private MedicalFile medicalFile;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;


    public FileUploadOperation(Context context, MedicalFile medicalFile) {
        this.context = context;
        this.medicalFile = medicalFile;
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            File file = new File(medicalFile.getFileLocation());
            String type = getMimeType(Uri.fromFile(file));
            MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(file, MediaType.parse(type)));
            RequestBody requestBody = RequestBody.create(medicalFile.getRecordReferenceId(), MediaType.parse("text/plain"));
            medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            if (null != LaunchActivity.getLaunchActivity()) {
                MedicalFilesDB.deleteMedicalFile(medicalFile.getRecordReferenceId());
            }
        } else {
            //update the table
            MedicalFilesDB.updateMedicalFile(medicalFile);
        }

    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        // do nothing
    }

    @Override
    public void imageUploadError() {
        MedicalFilesDB.updateMedicalFile(medicalFile);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        MedicalFilesDB.updateMedicalFile(medicalFile);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        MedicalFilesDB.updateMedicalFile(medicalFile);
    }

    @Override
    public void authenticationFailure() {

    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (null != context) {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
            return mimeType;
        } else {
            return "";
        }
    }
}