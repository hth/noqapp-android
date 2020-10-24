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
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.common.utils.HeaderFooterPageEvent;
import com.noqapp.android.common.utils.PdfHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.pojos.CaseHistory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfGenerator extends PdfHelper {
    private CaseHistory caseHistory;
    private String notAvailable = "N/A";

    public PdfGenerator(Context mContext) {
        super(mContext);
    }


    public void createPdf(CaseHistory mcp, int follow_up, boolean isDental) {
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

//            try {
//                // get input stream
//                InputStream ims = mContext.getAssets().open("logo.png");
//                Bitmap bmp = BitmapFactory.decodeStream(ims);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                Image image = Image.getInstance(stream.toByteArray());
//                image.scaleToFit(100, 30);
//
//                Font titleFont = new Font(baseFont, 13.0f, Font.NORMAL, BaseColor.BLACK);
//                Font titleFont1 = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);
//                Chunk titleChunk = new Chunk(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getName(), titleFont);
//                Paragraph titleParagraph = new Paragraph();
//                titleParagraph.add(titleChunk);
//                titleParagraph.add(glue);
//                titleParagraph.add(new Chunk("NoQueue",titleFont1));
//                document.add(titleParagraph);
//                addVerticalSpace();
//            } catch (IOException ex) {
//                return;
//            }

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
            hospital.add(new Chunk(MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getBusinessName(), normalBoldFont));
            hospital.add(new Chunk(", " + MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getAreaAndTown(), normalFont));
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
            document.add(addVerticalSpaceAfter(5f));
            document.add(getInvestigationData());
            document.add(new Paragraph(""));
            document.add(addVerticalSpaceBefore(20.0f));

            Chunk chunkDiagnosis = new Chunk("Diagnosis:", normalBigFont);
            Paragraph paragraphDiagnosis = new Paragraph(chunkDiagnosis);
            document.add(paragraphDiagnosis);

            Chunk chunkDiagnosisValue = new Chunk(caseHistory.getDiagnosis() == null ? "" : caseHistory.getDiagnosis(), normalFont);
            Paragraph paragraphDiagnosisValue = new Paragraph(chunkDiagnosisValue);
            document.add(paragraphDiagnosisValue);
            document.add(addVerticalSpaceAfter(20.0f));

            Chunk chunkMedicine = new Chunk("Medicines:", normalBigFont);
            Paragraph paragraphMedicine = new Paragraph(chunkMedicine);
            document.add(paragraphMedicine);
            document.add(addVerticalSpace());
            document.add(getMedicineData());
            document.add(addVerticalSpaceBefore(20f));

            if (isDental) {
                Chunk chunkTr = new Chunk("Treatment Plan:", normalBigFont);
                Paragraph paragraphTr = new Paragraph(chunkTr);
                document.add(paragraphTr);
                document.add(addVerticalSpace());
                document.add(getTreatRecommendationData(caseHistory.getNoteForPatient()));
                document.add(addVerticalSpaceBefore(20f));
            }

            Chunk chunkInstruction = new Chunk("Instruction:", normalBigFont);
            Paragraph paragraphInstruction = new Paragraph(chunkInstruction);
            document.add(paragraphInstruction);

            Chunk chunkInstructionValue = new Chunk(caseHistory.getInstructions(), normalFont);
            Paragraph paragraphInstructionValue = new Paragraph(chunkInstructionValue);
            document.add(paragraphInstructionValue);
            document.add(addVerticalSpace());


            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String followupDate = "";
            if (follow_up > 0) {
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, follow_up);
                Date d = c1.getTime();
                followupDate = df.format(d);
            }

            Paragraph followup = new Paragraph();
            followup.add(new Chunk("Follow up: ", normalBigFont));
            followup.add(new Chunk(followupDate, normalFont));
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


    private PdfPTable getInvestigationData() {
        PdfPTable table = new PdfPTable(1);
        if (caseHistory.getPathologyList().size() > 0) {
            table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.PATH.getDescription(), normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getPathologyList()), normalFont));
        }
        if (caseHistory.getXrayList().size() > 0) {
            table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.XRAY.getDescription(), normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getXrayList()), normalFont));
        }
        if (caseHistory.getMriList().size() > 0) {
            table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.MRI.getDescription(), normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getMriList()), normalFont));
        }
        if (caseHistory.getSonoList().size() > 0) {
            table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.SONO.getDescription(), normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getSonoList()), normalFont));
        }
        if (caseHistory.getScanList().size() > 0) {
            table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.SCAN.getDescription(), normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getScanList()), normalFont));
        }
        if (caseHistory.getSpecList().size() > 0) {
            table.addCell(pdfPCellWithoutBorderWithPadding(HealthCareServiceEnum.SPEC.getDescription(), normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(covertStringList2String(caseHistory.getSpecList()), normalFont));
        }
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

    private PdfPTable getTreatRecommendationData(String str) {
        PdfPTable table = new PdfPTable(2);
        try {
            String[] temp = str.split("\\|");
            if (temp.length > 0) {
                for (String act : temp) {
                    if (act.contains(":")) {
                        String[] strArray = act.split(":");
                        String str1 = strArray[0].trim();
                        String str2 = strArray[1];
                        table.addCell(pdfPCellWithBorder(str1, normalFont));
                        table.addCell(pdfPCellWithBorder(str2, normalFont));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
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
