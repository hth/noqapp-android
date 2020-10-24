package com.noqapp.android.merchant.views.utils;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.HeaderFooterPageEvent;
import com.noqapp.android.common.utils.PdfHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.pojos.CaseHistory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BlankPdfGenerator extends PdfHelper {
    private CaseHistory caseHistory;
    private String notAvailable = "    ";

    public BlankPdfGenerator(Context mContext) {
        super(mContext);
    }


    public void createPdf(CaseHistory mcp, boolean isDental, JsonMedicalRecord jsonMedicalRecord) {
        this.caseHistory = mcp;
        String fileName = new SimpleDateFormat("'NoQueue_" + caseHistory.getName() + "_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
        String dest = getAppPath(mContext.getResources().getString(R.string.app_name)) + fileName;
        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(dest));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(mContext);
            pdfWriter.setPageEvent(event);
            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("NoQueue Health Merchant");
            document.addCreator("NoQueue Technologies");
            Chunk glue = new Chunk(new VerticalPositionMark());

            Font titleFont = new Font(baseFont, 13.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk titleChunk = new Chunk(AppInitialize.getUserProfessionalProfile().getName(), titleFont);
            Paragraph titleParagraph = new Paragraph();
            titleParagraph.add(titleChunk);
            document.add(titleParagraph);


            Font noqFont = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);
            String license = AppUtils.getCompleteEducation(AppInitialize.getUserProfessionalProfile().getLicenses());
            String temp = TextUtils.isEmpty(license) ? notAvailable : license;
            Chunk degreeChunk = new Chunk(AppUtils.getCompleteEducation(AppInitialize.getUserProfessionalProfile().getEducation())
                    + " (Reg. Id: " + temp + ")", normalFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk("NoQueue", noqFont));
            document.add(degreeParagraph);
            addVerticalSpace();


            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk(jsonMedicalRecord.getBusinessName(), normalBoldFont));
            hospital.add(new Chunk(", " + jsonMedicalRecord.getAreaAndTown(), normalFont));
            document.add(hospital);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
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
            document.add(addVerticalSpaceBefore(80.0f));

            Chunk chunkDiagnosis = new Chunk("Diagnosis:", normalBigFont);
            Paragraph paragraphDiagnosis = new Paragraph(chunkDiagnosis);
            document.add(paragraphDiagnosis);

            document.add(addVerticalSpaceAfter(80.0f));

            Chunk chunkMedicine = new Chunk("Medicines:", normalBigFont);
            Paragraph paragraphMedicine = new Paragraph(chunkMedicine);
            document.add(paragraphMedicine);
            document.add(addVerticalSpace());
            document.add(addVerticalSpaceBefore(100f));

            if (isDental) {
                Chunk chunkTr = new Chunk("Treatment Plan:", normalBigFont);
                Paragraph paragraphTr = new Paragraph(chunkTr);
                document.add(paragraphTr);
                document.add(addVerticalSpace());
                document.add(addVerticalSpaceBefore(40f));
            }

            Chunk chunkInstruction = new Chunk("Instruction:", normalBigFont);
            Paragraph paragraphInstruction = new Paragraph(chunkInstruction);
            document.add(paragraphInstruction);
            document.add(addVerticalSpaceBefore(40f));


            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Paragraph followup = new Paragraph();
            followup.add(new Chunk("Follow up: ", normalBigFont));
            followup.add(new Chunk("", normalFont));
            document.add(followup);
            document.add(addVerticalSpaceAfter(20f));

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

            new CustomToast().showToast(mContext, "Report Generated");
            openFile(mContext, new File(dest));
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            new CustomToast().showToast(mContext, "No application found to open this file.");
        }
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

    private String getRespiratory() {
        return "Respiration Rate: " + notAvailable;
    }

    private String getHeight() {
        return "Height: " + notAvailable;
    }

    private String getWeight() {
        return "Weight: " + notAvailable;
    }

    private String getTemperature() {
        return "Temperature: " + notAvailable;
    }

    private String getPulse() {
        return "Pulse: " + notAvailable;
    }

    private String getBloodPressure() {
        return "Blood Pressure: " + notAvailable;
    }

    private PdfPCell getExaminationPdfCell() {
        PdfPTable testTable = new PdfPTable(1);
        testTable.addCell(pdfPCellWithoutBorder("Clinical Findings: ", normalBoldFont));
        testTable.addCell(pdfPCellWithoutBorder("                   ", normalFont));
        testTable.addCell(pdfPCellWithoutBorder("                   ", normalFont));
        testTable.addCell(pdfPCellWithoutBorder("                   ", normalFont));
        testTable.addCell(pdfPCellWithoutBorder("Result: ", normalBoldFont));
        testTable.addCell(pdfPCellWithoutBorder("                   ", normalFont));
        testTable.addCell(pdfPCellWithoutBorder("                   ", normalFont));
        testTable.addCell(pdfPCellWithoutBorder("                   ", normalFont));
        PdfPCell cell = new PdfPCell(testTable);
        cell.setPadding(0);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
