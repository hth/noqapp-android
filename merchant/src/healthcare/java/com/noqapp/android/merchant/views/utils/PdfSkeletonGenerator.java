package com.noqapp.android.merchant.views.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.HeaderFooterPageEvent;
import com.noqapp.android.common.utils.PdfHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.pojos.Receipt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PdfSkeletonGenerator extends PdfHelper {
    private String notAvailable = "";
    private String pulse = "";
    private String weight = "";
    private String height = "";
    private String temperature = "";
    private String bloodPressure = "";
    private String respiration = "";

    public PdfSkeletonGenerator(Context mContext) {
        super(mContext);
    }

    public void createPdf(Receipt receipt, JsonMedicalRecord jsonMedicalRecord) {
        initPhysical(jsonMedicalRecord);
        String fileName = new SimpleDateFormat("'NoQueue_" + receipt.getJsonProfile().getName() + "_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
        File dest = new File(getAppPath(mContext.getResources().getString(R.string.app_name)) + fileName);
        if (dest.exists()) {
            Log.d("Delete", "File deleted successfully " + dest.delete());
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
            Chunk titleChunk = new Chunk(receipt.displayProfessionalName(), titleFont);
            Paragraph titleParagraph = new Paragraph();
            titleParagraph.add(titleChunk);
            document.add(titleParagraph);

            Font noqFont = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);
            String license = AppUtils.getCompleteEducation(receipt.getLicenses());
            String temp = TextUtils.isEmpty(license) ? notAvailable : license;
            String education = AppUtils.getCompleteEducation(receipt.getEducation());
            String education_temp = TextUtils.isEmpty(education) ? notAvailable : education;
            Chunk degreeChunk = new Chunk(education_temp + " (Reg. No.: " + temp + ")", normalFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk(" ", noqFont)); //"NoQueue"
            document.add(degreeParagraph);
            addVerticalSpace();

            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk(receipt.getBusinessName(), normalBoldFont));
            hospital.add(new Chunk(", " + receipt.getStoreAddress(), normalFont));
            document.add(hospital);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            document.add(new Chunk(lineSeparator));
            document.add(addVerticalSpaceBefore(10f));
            document.add(getPatientData(receipt));

            document.add(addVerticalSpaceBefore(20f));
            document.add(new Paragraph(""));

            {
                Chunk chunkInvestigation = new Chunk("Symptoms:", normalBigFont);
                Paragraph paragraphInvestigation = new Paragraph(chunkInvestigation);
                document.add(paragraphInvestigation);
                document.add(addVerticalSpaceAfter(5f));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(""));
                document.add(addVerticalSpaceBefore(20.0f));
            }
            {
                Chunk chunkInvestigation = new Chunk("Clinical Findings:", normalBigFont);
                Paragraph paragraphInvestigation = new Paragraph(chunkInvestigation);
                document.add(paragraphInvestigation);
                document.add(addVerticalSpaceAfter(5f));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(""));
                document.add(addVerticalSpaceBefore(20.0f));
            }
            {
                Chunk chunkInvestigation = new Chunk("Provisional Diagnosis:", normalBigFont);
                Paragraph paragraphInvestigation = new Paragraph(chunkInvestigation);
                document.add(paragraphInvestigation);
                document.add(addVerticalSpaceAfter(5f));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(""));
                document.add(addVerticalSpaceBefore(20.0f));
            }

            Chunk chunkInvestigation = new Chunk("Investigation:", normalBigFont);
            Paragraph paragraphInvestigation = new Paragraph(chunkInvestigation);
            document.add(paragraphInvestigation);
            document.add(addVerticalSpaceAfter(5f));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(""));
            document.add(addVerticalSpaceBefore(20.0f));

            Chunk chunkDiagnosis = new Chunk("Diagnosis:", normalBigFont);
            Paragraph paragraphDiagnosis = new Paragraph(chunkDiagnosis);
            document.add(paragraphDiagnosis);

            Chunk chunkDiagnosisValue = new Chunk("\n\n\n", normalFont);
            Paragraph paragraphDiagnosisValue = new Paragraph(chunkDiagnosisValue);
            document.add(paragraphDiagnosisValue);
            document.add(addVerticalSpaceAfter(20.0f));

            Chunk chunkMedicine = new Chunk("Medicines:", normalBigFont);
            Paragraph paragraphMedicine = new Paragraph(chunkMedicine);
            document.add(paragraphMedicine);
            document.add(addVerticalSpace());
            document.add(getMedicineHeaderData());
            document.add(getMedicineData());
            document.add(new Paragraph("\n"));
            document.add(addVerticalSpaceBefore(20f));

            Chunk chunkInstruction = new Chunk("Instruction:", normalBigFont);
            Paragraph paragraphInstruction = new Paragraph(chunkInstruction);
            document.add(paragraphInstruction);

            Chunk chunkInstructionValue = new Chunk("\n\n", normalFont);
            Paragraph paragraphInstructionValue = new Paragraph(chunkInstructionValue);
            document.add(paragraphInstructionValue);
            document.add(addVerticalSpace());


            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String followupDate = "";

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
            openFile(mContext, dest);
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            new CustomToast().showToast(mContext, "No application found to open this file.");
        }
    }

    private PdfPTable getPatientData(Receipt receipt) {
        JsonProfile jsonProfile = receipt.getJsonProfile();
        PdfPTable table = new PdfPTable(3);
        table.addCell(getCellWithTextAndImage(jsonProfile.getName(), "user.png"));
        // table.addCell(pdfPCellWithoutBorder(pulse, normalFont));
        table.addCell(getCellWithTextAndImage(pulse, "pulse.png"));
        table.addCell(getCellWithTextAndImage(height, "height.png"));
        table.addCell(getCellWithTextAndImage(jsonProfile.getGender().getDescription() + ", " + AppUtils.calculateAge(jsonProfile.getBirthday()), "gender.png"));
        table.addCell(getCellWithTextAndImage(bloodPressure, "blood.png"));
        table.addCell(getCellWithTextAndImage(respiration, "respiration.png"));
        table.addCell(getCellWithTextAndImage(checkNull(jsonProfile.findPrimaryOrAnyExistingAddress().getAddress()), "address.png"));
        table.addCell(getCellWithTextAndImage(weight, "weight.png"));
        table.addCell(getCellWithTextAndImage(temperature, "temperature.png"));
        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }

    private PdfPCell getCellWithTextAndImage(String label, String icon) {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingBottom(5);
        cell.setBorder(Rectangle.NO_BORDER);
        try {
            InputStream ims = mContext.getAssets().open(icon);
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image img = Image.getInstance(stream.toByteArray());
            img.scaleAbsolute(15f, 15f);
            img.setAlignment(Element.ALIGN_LEFT);

            Paragraph paragraph = new Paragraph();
            paragraph.add(new Chunk(img, 0, 0));
            Phrase phrase = new Phrase(" " + label, normalFont);
            paragraph.add(phrase);
            paragraph.setAlignment(Element.ALIGN_TOP);
            cell.addElement(paragraph);
            // return cell;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cell;
    }

    private PdfPTable getMedicineHeaderData() {
        PdfPTable table = new PdfPTable(3);
        try {
            table.setWidthPercentage(100);
            table.setWidths(new int[]{6, 5, 5});

            table.addCell(pdfPCellHeader("DRUG", normalBoldFont));
            table.addCell(pdfPCellHeader("DOSE", normalBoldFont));
            table.addCell(pdfPCellHeader("ROUTE", normalBoldFont));
            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    private PdfPTable getMedicineData() {
        PdfPTable table = new PdfPTable(4);
        try {
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 5, 5, 5});
            for (int i = 0; i < 5; i++) {
                table.addCell(pdfPCellWithBorder("" + (i + 1), normalFont));
                table.addCell(pdfPCellWithBorder("", normalFont));
                table.addCell(pdfPCellWithBorder("", normalFont));
                table.addCell(pdfPCellWithBorder("O/IM/IV/SC", normalFont));
            }
            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    private PdfPCell pdfPCellHeader(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private void initPhysical(JsonMedicalRecord jsonMedicalRecord) {
        if (null != jsonMedicalRecord && null != jsonMedicalRecord.getMedicalPhysical()) {
            Log.e("data", jsonMedicalRecord.toString());
            try {
                if (null != jsonMedicalRecord.getMedicalPhysical().getPulse()) {
                    pulse = "Pulse: " + jsonMedicalRecord.getMedicalPhysical().getPulse() + " bpm";
                } else {
                    pulse = "Pulse:__________________________" + notAvailable;
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    bloodPressure = "Blood Pressure: " + jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0] + "/" + jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1] + " mmHg";
                } else {
                    bloodPressure = "Blood Pressure:________________" + notAvailable;
                }

                if (null != jsonMedicalRecord.getMedicalPhysical().getHeight()) {
                    height = "Height: " + jsonMedicalRecord.getMedicalPhysical().getHeight() + " cm";
                } else {
                    height = "Height:_________________________" + notAvailable;
                }

                if (null != jsonMedicalRecord.getMedicalPhysical().getRespiratory()) {
                    respiration = "Respiration Rate: " + jsonMedicalRecord.getMedicalPhysical().getRespiratory();
                } else {
                    respiration = "Respiration Rate:_______________" + notAvailable;
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                    weight = "Weight: " + jsonMedicalRecord.getMedicalPhysical().getWeight() + " kg";
                } else {
                    weight = "Weight:_________________________" + notAvailable;
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                    temperature = "Temperature: " + jsonMedicalRecord.getMedicalPhysical().getTemperature();
                } else {
                    temperature = "Temperature:___________________" + notAvailable;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pulse = "Pulse:__________________________" + notAvailable;
            bloodPressure = "Blood Pressure:________________" + notAvailable;
            height = "Height:_________________________" + notAvailable;
            respiration = "Respiration Rate:_______________" + notAvailable;
            weight = "Weight:_________________________" + notAvailable;
            temperature = "Temperature:___________________" + notAvailable;
        }
    }
}
