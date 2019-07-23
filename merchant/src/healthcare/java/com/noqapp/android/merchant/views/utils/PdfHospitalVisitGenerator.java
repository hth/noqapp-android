package com.noqapp.android.merchant.views.utils;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.PdfHelper;
import com.noqapp.android.merchant.views.pojos.Receipt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfHospitalVisitGenerator extends PdfHelper {
    private BaseFont baseFont;
    private Context mContext;
    private Font normalFont;
    private Font normalBoldFont;
    private Font normalBigFont;
    private String notAvailable = "";



    public PdfHospitalVisitGenerator(Context mContext) {
        this.mContext = mContext;
        try {
            baseFont = BaseFont.createFont("assets/fonts/opensan.ttf", "UTF-8", BaseFont.EMBEDDED);
            normalFont = new Font(baseFont, 10.0f, Font.NORMAL, BaseColor.BLACK);
            normalBoldFont = new Font(baseFont, 10.0f, Font.BOLD, BaseColor.BLACK);
            normalBigFont = new Font(baseFont, 12.0f, Font.BOLD, BaseColor.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPdf(List<JsonHospitalVisitSchedule> immunizationList, JsonQueuedPerson jsonQueuedPerson) {
        String fileName = new SimpleDateFormat("'NoQueue_" + jsonQueuedPerson.getCustomerName() + "_Vaccination_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
        File dest = new File(getAppPath(mContext) + fileName);
        if (dest.exists()) {
            Log.d("Delete", "File deleted successfully " + dest.delete());
        }

        try {
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            // Open to write
            document.open();
            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("NoQueue Health Merchant");
            document.addCreator("NoQueue Technologies");
            Chunk glue = new Chunk(new VerticalPositionMark());

            Font titleFont = new Font(baseFont, 13.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk titleChunk = new Chunk(jsonQueuedPerson.getCustomerName(), titleFont);
            Paragraph titleParagraph = new Paragraph();
            titleParagraph.add(titleChunk);
            document.add(titleParagraph);

            Font noqFont = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);
            Chunk degreeChunk = new Chunk("Education" + " (Reg. No.: " + "License" + ")", normalFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk(" ", noqFont));
            document.add(degreeParagraph);
            addVerticalSpace();

            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk("Store Name", normalBoldFont));
            hospital.add(new Chunk(", " + "address", normalFont));
            document.add(hospital);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            document.add(addVerticalSpaceBefore(20f));
            document.add(new Paragraph(""));

            Chunk chunkInvestigation = new Chunk("Immunisation Record:", normalBigFont);
            Paragraph paragraphInvestigation = new Paragraph(chunkInvestigation);
            document.add(paragraphInvestigation);
            document.add(addVerticalSpaceAfter(5f));
            document.add(getImmunisationHeaderData());
            document.add(getImmunisationData(immunizationList));
            document.add(addVerticalSpaceBefore(20.0f));






            Chunk chunkInstructionValue = new Chunk("\n\n", normalFont);
            Paragraph paragraphInstructionValue = new Paragraph(chunkInstructionValue);
            document.add(paragraphInstructionValue);
            document.add(addVerticalSpace());

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date c = Calendar.getInstance().getTime();
            String formattedDate = df.format(c);

            Paragraph p_sign = new Paragraph();
            p_sign.add(new Chunk("Signature: ", normalBigFont));
            p_sign.add(new Chunk(new LineSeparator()));
            p_sign.add("                                                                   ");
            p_sign.add(new Chunk("Date: ", normalBigFont));
            p_sign.add(new Chunk(formattedDate, normalFont));
            document.add(p_sign);
            document.close();

            new CustomToast().showToast(mContext, "Immunisation Record Generated");
            openFile(mContext, dest);
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            new CustomToast().showToast(mContext, "No application found to open this file.");
        }
    }

    private PdfPTable getImmunisationHeaderData() {
        PdfPTable table = new PdfPTable(5);
        try {
            table.addCell(pdfPCellHeader("AGE", normalBigFont, 5));
            table.addCell(pdfPCellHeader("VACCINE", normalBigFont, 5));
            table.addCell(pdfPCellHeader("STATUS", normalBigFont, 5));
            table.addCell(pdfPCellHeader("DUE DATE", normalBigFont, 5));
            table.addCell(pdfPCellHeader("GIVEN DATE", normalBigFont, 5));
            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    private PdfPTable getImmunisationData(List<JsonHospitalVisitSchedule> immunizationList) {
        PdfPTable table = new PdfPTable(5);
        for (int i = 0; i < immunizationList.size(); i++) {
            JsonHospitalVisitSchedule jsonHospitalVisitSchedule = immunizationList.get(i);
            Map<String, BooleanReplacementEnum> visitingFor = jsonHospitalVisitSchedule.getVisitingFor();
            for (Map.Entry<String,BooleanReplacementEnum> entry : visitingFor.entrySet()) {
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());
                String dateVisited = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI,
                        jsonHospitalVisitSchedule.getVisitedDate());
                String dateExpected = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI,
                        jsonHospitalVisitSchedule.getExpectedDate());
                table.addCell(pdfPCellWithBorder(jsonHospitalVisitSchedule.getHeader(), normalFont));
                table.addCell(pdfPCellWithBorder(entry.getKey(), normalFont));
                table.addCell(pdfPCellWithBorder("Status- "+entry.getValue().getDescription(), normalFont));
                table.addCell(pdfPCellWithBorder(dateExpected, normalFont));
                table.addCell(pdfPCellWithBorder(dateVisited, normalFont));
            }
        }

        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }

    private static void openFile(Context context, File url) throws ActivityNotFoundException {
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private static String getAppPath(Context context) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + context.getResources().getString(R.string.app_name)
                + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getPath() + File.separator;
    }


    private PdfPCell pdfPCellHeader(String label, Font font, int padding) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }


}

