package com.noqapp.android.merchant.views.activities;

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
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;


public class ScribbleActivity extends BaseActivity {

    private EditText edt_prescription;
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
        edt_prescription = findViewById(R.id.edt_prescription);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_prescription));
        setProgressMessage("Fetching data...");
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(v -> {
            new AppUtils().hideKeyBoard(ScribbleActivity.this);
            takeScreenshot();
        });
        Button btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(v -> {
            new AppUtils().hideKeyBoard(ScribbleActivity.this);
            edt_prescription.setText("");
        });
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
            btn_no.setOnClickListener(v -> mAlertDialog.dismiss());
            btn_yes.setOnClickListener(v -> {
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

            });
            mAlertDialog.show();
            btn_yes.setText("Download");
            btn_no.setText("Cancel");
            tv_msg.setText("Download the Scribble writer app to make your life easy.");
            tv_title.setText("Scribble app missing");
        }

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
