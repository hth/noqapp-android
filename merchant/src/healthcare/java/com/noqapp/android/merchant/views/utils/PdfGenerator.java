package com.noqapp.android.merchant.views.utils;


import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.pojos.CaseHistory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfGenerator {
    private BaseFont baseFont;
    private Context mContext;
    private CaseHistory caseHistory;
    private Font normalFont;
    private Font normalBoldFont;
    private Font normalBigFont;
    private String notAvailable = "N/A";

    public PdfGenerator(Context mContext) {
        this.mContext = mContext;
        try {
            baseFont = BaseFont.createFont("assets/fonts/opensan.ttf", "UTF-8", BaseFont.EMBEDDED);
            normalFont = new Font(baseFont, 9.0f, Font.NORMAL, BaseColor.BLACK);
            normalBoldFont = new Font(baseFont, 9.0f, Font.BOLD, BaseColor.BLACK);
            normalBigFont = new Font(baseFont, 11.0f, Font.BOLD, BaseColor.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createPdf(CaseHistory mcp) {
        this.caseHistory = mcp;
        String fileName = new SimpleDateFormat("'NoQueue_" + caseHistory.getName() + "_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
        String dest = getAppPath(mContext) + fileName;
        if (new File(dest).exists()) {
            new File(dest).delete();
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

            try {
                // get input stream
                InputStream ims = mContext.getAssets().open("logo.png");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleToFit(100, 30);

                Font titleFont = new Font(baseFont, 13.0f, Font.NORMAL, BaseColor.BLACK);
                Chunk titleChunk = new Chunk(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getName(), titleFont);
                Paragraph titleParagraph = new Paragraph();
                titleParagraph.add(titleChunk);
                titleParagraph.add(glue);
                titleParagraph.add(new Chunk(image, 0, -24));
                document.add(titleParagraph);
                addVerticalSpace();
            } catch (IOException ex) {
                return;
            }
//
//
//            Chunk degreeChunk = new Chunk(new AppUtils().getCompleteEducation(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getEducation()), normalFont);
//            Paragraph degreeParagraph = new Paragraph();
//            degreeParagraph.add(degreeChunk);
//            degreeParagraph.add(glue);
//            degreeParagraph.add("Koparkhairane, Navi Mumbai");
//            document.add(degreeParagraph);


//            Font titleFont = new Font(baseFont, 12.0f, Font.NORMAL, BaseColor.BLACK);
//            Chunk titleChunk = new Chunk(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getName(), titleFont);
//            Paragraph titleParagraph = new Paragraph();
//            titleParagraph.add(titleChunk);
//            document.add(titleParagraph);
//            addVerticalSpace();


            Chunk degreeChunk = new Chunk(new AppUtils().getCompleteEducation(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getEducation()), normalFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            document.add(degreeParagraph);

            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk("SSD Hospital", normalBoldFont));
            hospital.add(new Chunk(", Koparkhairane, Navi Mumbai", normalFont));
            document.add(hospital);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            // lineSeparator.setOffset(-14);
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            document.add(new Chunk(lineSeparator));
            document.add(addVerticalSpaceBefore(10f));
            document.add(getPatientData());

            document.add(addVerticalSpaceBefore(20f));
            document.add(new Paragraph(""));

            document.add(getSEPData());
            document.add(addVerticalSpaceBefore(20f));

            Chunk chunkInvestigation = new Chunk("Investigation:", normalBigFont);
            Paragraph paragraphInvestigation = new Paragraph(chunkInvestigation);
            document.add(paragraphInvestigation);
            document.add(addVerticalSpaceAfter(5f));
            document.add(getInvestigationData());
            document.add(new Paragraph(""));
            document.add(addVerticalSpaceBefore(20.0f));

            Chunk chunkDiagnosis = new Chunk("Diagnosis:", normalBigFont);
            Paragraph paragraphDiagnosis = new Paragraph(chunkDiagnosis);
            document.add(paragraphDiagnosis);

            Chunk chunkDiagnosisValue = new Chunk(caseHistory.getDiagnosis(), normalFont);
            Paragraph paragraphDiagnosisValue = new Paragraph(chunkDiagnosisValue);
            document.add(paragraphDiagnosisValue);
            document.add(addVerticalSpaceAfter(20.0f));

            Chunk chunkMedicine = new Chunk("Medicines:", normalBigFont);
            Paragraph paragraphMedicine = new Paragraph(chunkMedicine);
            document.add(paragraphMedicine);
            document.add(addVerticalSpace());
            document.add(getMedicineData());
            document.add(addVerticalSpaceBefore(20f));

            Chunk chunkInstruction = new Chunk("Instruction:", normalBigFont);
            Paragraph paragraphInstruction = new Paragraph(chunkInstruction);
            document.add(paragraphInstruction);

            Chunk chunkInstructionValue = new Chunk(caseHistory.getInstructions(), normalFont);
            Paragraph paragraphInstructionValue = new Paragraph(chunkInstructionValue);
            document.add(paragraphInstructionValue);
            document.add(addVerticalSpace());

            Paragraph followup = new Paragraph();
            followup.add(new Chunk("Follow up: ", normalBigFont));
            followup.add(new Chunk(caseHistory.getFollowup(), normalFont));
            document.add(followup);
            document.add(addVerticalSpace());

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);


            Paragraph p_sign = new Paragraph("Signature: ");
            p_sign.add(new Chunk(new LineSeparator()));
            p_sign.add("                                                                   ");
            p_sign.add("Date: " + formattedDate);
            document.add(p_sign);
            document.close();

            Toast.makeText(mContext, "Report Generated", Toast.LENGTH_SHORT).show();
            openFile(mContext, new File(dest));
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
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

    private PdfPTable getPatientData() {
        PdfPTable table = new PdfPTable(3);
        table.addCell(pdfPCellWithoutBorder(caseHistory.getName(), normalFont));
        table.addCell(pdfPCellWithoutBorder(getPulse(), normalFont));
        table.addCell(pdfPCellWithoutBorder(getHeight(), normalFont));
        table.addCell(pdfPCellWithoutBorder(caseHistory.getGender() + "," + caseHistory.getAge(), normalFont));
        table.addCell(pdfPCellWithoutBorder(getBloodPressure(), normalFont));
        table.addCell(pdfPCellWithoutBorder(getRespiratory(), normalFont));
        table.addCell(pdfPCellWithoutBorder(caseHistory.getAddress(), normalFont));
        table.addCell(pdfPCellWithoutBorder(getWeight(), normalFont));
        table.addCell(pdfPCellWithoutBorder(getTemperature(), normalFont));
        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }

    private PdfPTable getSEPData() {
        PdfPTable table = new PdfPTable(3);
        try {
            table.addCell(pdfPCellWithoutBorder("Symptoms:", normalBigFont, 5));
            table.addCell(pdfPCellWithoutBorder("Examination:", normalBigFont, 5));
            table.addCell(pdfPCellWithoutBorder("Provisional Diagnosis:", normalBigFont, 5));
            table.addCell(pdfPCellWithoutBorder(caseHistory.getSymptoms(), normalFont));
            table.addCell(getExaminationPdfCell());
            table.addCell(pdfPCellWithoutBorder(caseHistory.getProvisionalDiagnosis(), normalFont));

            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }


    private PdfPTable getInvestigationData() {
        PdfPTable table = new PdfPTable(1);
        table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.PATH.getDescription(), normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getPathologyList()), normalFont));
        table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.XRAY.getDescription(), normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getXrayList()), normalFont));
        table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.MRI.getDescription(), normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getMriList()), normalFont));
        table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.SONO.getDescription(), normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getSonoList()), normalFont));
        table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.SCAN.getDescription(), normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getScanList()), normalFont));
        table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.SPEC.getDescription(), normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getSpecList()), normalFont));
        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);

        return table;
    }


    private PdfPTable getMedicineData() {
        PdfPTable table = new PdfPTable(5);
        for (int i = 0; i < caseHistory.getJsonMedicineList().size(); i++) {
            JsonMedicalMedicine jsonMedicalMedicine = caseHistory.getJsonMedicineList().get(i);
            table.addCell(pdfPCellWithBorder(jsonMedicalMedicine.getPharmacyCategory(), normalFont));
            table.addCell(pdfPCellWithBorder(jsonMedicalMedicine.getName(), normalFont));
            table.addCell(pdfPCellWithBorder(jsonMedicalMedicine.getMedicationIntake(), normalFont));
            table.addCell(pdfPCellWithBorder(jsonMedicalMedicine.getDailyFrequency(), normalFont));
            table.addCell(pdfPCellWithBorder(jsonMedicalMedicine.getCourse(), normalFont));
        }

        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }


    private Paragraph addVerticalSpace() {
        Paragraph paragraph = new Paragraph("");
        paragraph.setSpacingAfter(10f); // for adding extra space after a view
        return paragraph;
    }


    private Paragraph addVerticalSpaceBefore(float space) {
        Paragraph paragraph = new Paragraph("");
        paragraph.setSpacingBefore(space); // for adding extra space after a view
        return paragraph;
    }

    private Paragraph addVerticalSpaceAfter(float space) {
        Paragraph paragraph = new Paragraph("");
        paragraph.setSpacingAfter(space); // for adding extra space after a view
        return paragraph;
    }

    private PdfPCell pdfPCellWithBorder(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        // pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private PdfPCell pdfPCellWithoutBorder(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private PdfPCell pdfPCellWithoutBorderWithPadding(String label, Font font, int padding) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.setPaddingBottom(padding);
        pdfPCell.setPaddingTop(padding);
        return pdfPCell;
    }

    private PdfPCell pdfPCellWithoutBorder(String label, Font font, int padding) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.setPaddingBottom(padding);
        return pdfPCell;
    }

    private String covertStringList2String(List<String> data) {
        String temp = "";
        for (String a : data) {
            temp += "   " + a + "\n";
        }
        return temp;
    }


    private String getRespiratory() {
        if (null == caseHistory.getRespiratory()) {
            return "Respiration Rate: " + notAvailable;
        } else {
            return "Respiration Rate: " + caseHistory.getRespiratory();
        }
    }

    private String getHeight() {
        if (null == caseHistory.getHeight()) {
            return "Height: " + notAvailable;
        } else {
            return "Height: " + caseHistory.getHeight() + " cm";
        }

    }

    private String getWeight() {
        if (null == caseHistory.getWeight()) {
            return "Weight: " + notAvailable;
        } else {
            return "Weight: " + caseHistory.getWeight() + " kg";
        }
    }

    private String getTemperature() {
        if (null == caseHistory.getTemperature()) {
            return "Temperature: " + notAvailable;
        } else {
            return "Temperature: " + caseHistory.getTemperature() + " F";
        }
    }

    private String getPulse() {
        if (null == caseHistory.getPulse()) {
            return "Pulse: " + notAvailable;
        } else {
            return "Pulse: " + caseHistory.getPulse() + " bpm";
        }
    }

    private String getBloodPressure() {
        if (null != caseHistory.getBloodPressure() && caseHistory.getBloodPressure().length == 2) {
            return "Blood Pressure: " + caseHistory.getBloodPressure()[0] + "/" + caseHistory.getBloodPressure()[1] + " mmHg";
        } else {
            return "Blood Pressure: " + notAvailable;
        }
    }

    private PdfPCell getExaminationPdfCell() {
        PdfPTable testTable = new PdfPTable(1);
        testTable.addCell(pdfPCellWithoutBorder("Clinical Findings: ", normalBoldFont));
        testTable.addCell(pdfPCellWithoutBorder(caseHistory.getClinicalFindings(), normalFont));
        testTable.addCell(pdfPCellWithoutBorder("Result: ", normalBoldFont));
        testTable.addCell(pdfPCellWithoutBorder(caseHistory.getExaminationResults(), normalFont));
        PdfPCell cell = new PdfPCell(testTable);
        cell.setPadding(0);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
