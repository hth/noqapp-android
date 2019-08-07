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
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadOperation extends AsyncTask<String, Void, String> implements ImageUploadPresenter {
    private Context context;
    private List<MedicalFile> medicalFiles;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private int i = 0;

    public FileUploadOperation(Context context, List<MedicalFile> medicalFiles) {
        this.context = context;
        this.medicalFiles = medicalFiles;
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
    }

    @Override
    protected String doInBackground(String... params) {
        for (i = 0; i < medicalFiles.size(); i++) {
            try {
                File file = new File(medicalFiles.get(i).getFileLocation());
                String type = getMimeType(Uri.fromFile(file));
                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), medicalFiles.get(i).getRecordReferenceId());
                medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isCancelled())
                break;
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
               // MedicalFilesDB.deleteMedicalFile(medicalFiles.get(i).getRecordReferenceId());
            }
        } else {
            //update the table
        }

    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {

    }

    @Override
    public void imageUploadError() {

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {

    }

    @Override
    public void responseErrorPresenter(int errorCode) {

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