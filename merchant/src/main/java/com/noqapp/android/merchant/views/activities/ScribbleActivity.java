package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.model.PatientProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.provider.UserDictionary;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;


public class ScribbleActivity extends AppCompatActivity implements MedicalRecordPresenter, PatientProfilePresenter {

    private TextView tv_patient_name, tv_address, tv_info;
    private ProgressDialog progressDialog;
    private EditText edt_prescription;
    private String codeQR;
    private final String packageName = "com.google.android.apps.handwriting.ime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (new AppUtils().isTablet(getApplicationContext())) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        final MedicalHistoryModel medicalHistoryModel = new MedicalHistoryModel(this);
        edt_prescription = findViewById(R.id.edt_prescription);
        final JsonQueuedPerson jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        codeQR = getIntent().getStringExtra("qCodeQR");
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_address = findViewById(R.id.tv_address);
        tv_info = findViewById(R.id.tv_info);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_prescription));
        initProgress();
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtils().hideKeyBoard(ScribbleActivity.this);
                progressDialog.show();
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
                jsonMedicalRecord.setFormVersion(FormVersionEnum.MFS1);
                jsonMedicalRecord.setCodeQR(codeQR);
                jsonMedicalRecord.setNoteForPatient(edt_prescription.getText().toString());
                //  if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
                //      jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(sp_preferred_list.getSelectedItemPosition()).getBizStoreId());
                medicalHistoryModel.add(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        jsonMedicalRecord);

            //    takeScreenshot();
            }
        });
        if(TextUtils.isEmpty(tv_patient_name.getText().toString())) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                PatientProfileModel profileModel = new PatientProfileModel(this);
                profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }

    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("fetching data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
        Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void patientProfileResponse(JsonProfile jsonProfile) {
        if (null != jsonProfile) {
            tv_patient_name.setText(jsonProfile.getName() + " (" + new AppUtils().calculateAge(jsonProfile.getBirthday()) + ", " + jsonProfile.getGender().name() + ")");
            tv_address.setText(jsonProfile.getAddress());
            tv_info.setText(Html.fromHtml("<b> Blood Group: </b> B+ ,<b> Weight: </b> 75 Kg"));
        }
        dismissProgress();

        if (!isAppInstalled(packageName)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater_inner = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater_inner.inflate(R.layout.dialog_logout, null, false);
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            Button btn_no = customDialogView.findViewById(R.id.btn_no);
            TextView tv_title = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("market://details?id=" + packageName);
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    }
                    mAlertDialog.dismiss();

                }
            });
            mAlertDialog.show();
            btn_yes.setText("Download");
            btn_no.setText("Cancel");
            tv_msg.setText("Download the Scribble writer app to make your life easy.");
            tv_title.setText("Scribble app missing");
        }
    }


    @Override
    public void patientProfileError() {
        dismissProgress();
    }

    private boolean isAppInstalled(String uri) {
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void takeScreenshot() {
        try {
            final Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss_record", now);
            final File imageFile = stringToPdf(edt_prescription.getText().toString(), now.toString());
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter pda = new PrintDocumentAdapter() {

                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    InputStream input = null;
                    OutputStream output = null;

                    try {

                        input = new FileInputStream(imageFile);
                        output = new FileOutputStream(destination.getFileDescriptor());

                        byte[] buf = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = input.read(buf)) > 0) {
                            output.write(buf, 0, bytesRead);
                        }

                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                    } catch (Exception e) {
                        //Catch exception
                    } finally {
                        try {
                            input.close();
                            output.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                    if (cancellationSignal.isCanceled()) {
                        callback.onLayoutCancelled();
                        return;
                    }

                    PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(now.toString()).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                    callback.onLayoutFinished(pdi, true);
                }
            };
            printManager.print("Document", pda, new PrintAttributes.Builder().build());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public File stringToPdf(String data, String now) {
        File pdfFile = null;
        drawMultilineText(data);
        String extstoragedir = Environment.getExternalStorageDirectory().toString();
        File fol = new File(extstoragedir, "pdf");
        // File folder = new File(fol, "pdf");
        if (!fol.exists()) {
            fol.mkdir();
        }
        try {
            pdfFile = new File(fol, now + ".pdf");
            pdfFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(pdfFile);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            float hight = displaymetrics.heightPixels;
            float width = displaymetrics.widthPixels;

            int convertHighet = (int) hight, convertWidth = (int) width;


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            TextPaint mTextPaint = new TextPaint();
            mTextPaint.setTextSize(30.0f);
            StaticLayout mTextLayout = new StaticLayout(data, mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();
            canvas.translate(100, 140);
            mTextLayout.draw(canvas);
            canvas.restore();
//            Paint paint1 = new Paint();
//            paint1.setColor(Color.parseColor("#000000"));
//            paint1.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(20, 10, canvas.getWidth() - 10, canvas.getHeight() - 10, paint1);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

        } catch (IOException e) {
            Log.i("error", e.getLocalizedMessage());
        }
        return pdfFile;
    }

    void drawMultilineText(String str) {
        String[] lines = str.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            UserDictionary.Words.addWord(this, lines[i], 100, UserDictionary.Words.LOCALE_TYPE_ALL);
        }
    }

}
