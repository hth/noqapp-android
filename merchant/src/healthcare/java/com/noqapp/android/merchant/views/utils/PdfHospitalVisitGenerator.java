package com.noqapp.android.merchant.views.utils;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.PdfHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import org.apache.commons.lang3.StringUtils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfHospitalVisitGenerator extends PdfHelper {

    public PdfHospitalVisitGenerator(Context mContext) {
        super(mContext);
    }

    public void createPdf(
            List<JsonHospitalVisitSchedule> immunizationList,
            JsonQueuedPerson jsonQueuedPerson,
            JsonMedicalRecord jsonMedicalRecord
    ) {
        String fileName = new SimpleDateFormat("'NoQueue_" + jsonQueuedPerson.getCustomerName() + "_Immunization_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
        File dest = new File(getAppPath(mContext.getResources().getString(R.string.app_name)) + fileName);
        if (dest.exists()) {
            Log.d("Delete", "File deleted successfully " + dest.delete());
        }

        try {
            Document document = new Document();
            // Location to save
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(dest));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            pdfWriter.setPageEvent(event);

            // Open to write
            document.open();
            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("NoQueue Health Merchant");
            document.addCreator("NoQueue Technologies");
            Chunk glue = new Chunk(new VerticalPositionMark());

            Font noqFont = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);

            Chunk degreeChunk = new Chunk(jsonMedicalRecord.getBusinessName(), noqFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk("NoQueue", noqFont));
            document.add(degreeParagraph);
            addVerticalSpace();

            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk(jsonMedicalRecord.getAreaAndTown(), normalFont));
            document.add(hospital);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            document.add(new Chunk(lineSeparator));
            document.add(addVerticalSpaceBefore(10f));

            document.add(addVerticalSpaceBefore(20f));
            document.add(new Paragraph(""));

            document.add(addVerticalSpaceAfter(5f));
            addTable(jsonQueuedPerson.getCustomerName(), document, immunizationList);
            document.add(addVerticalSpaceBefore(20.0f));

            Chunk chunkInstructionValue = new Chunk("\n\n", normalFont);
            Paragraph paragraphInstructionValue = new Paragraph(chunkInstructionValue);
            document.add(paragraphInstructionValue);
            document.add(addVerticalSpace());
            document.close();

            new CustomToast().showToast(mContext, "Immunization Record Generated");
            openFile(mContext, dest);
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            new CustomToast().showToast(mContext, "No application found to open this file.");
        }
    }

    private PdfPCell pdfPCellWithBorder1(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell();
        Paragraph p1 = new Paragraph();
        Font zapfdingbats1 = new Font(Font.FontFamily.ZAPFDINGBATS, 14);
        Chunk chunk1 = new Chunk("q", zapfdingbats1);
        p1.add(chunk1);
        p1.add(" " +"✓"+ label);
        pdfPCell.addElement(p1);
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private void addTable(String patientName, Document document, List<JsonHospitalVisitSchedule> immunizationList) {
        try {
            PdfPTable patientDetailTable = new PdfPTable(1);
            PdfPCell pdfPatientCell = pdfPCellWithoutBorder(patientName + " Immunization Record", thirteenBigFont);
            pdfPatientCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPatientCell.setPaddingBottom(10.0f);
            patientDetailTable.addCell(pdfPatientCell);
            document.add(patientDetailTable);

            for (int i = 0; i < immunizationList.size(); i++) {
                JsonHospitalVisitSchedule jsonHospitalVisitSchedule = immunizationList.get(i);
                if (i == 6) {
                    document.newPage();
                }

                PdfPTable ppptable = new PdfPTable(1);
                PdfPCell pdfPCell = pdfPCellWithBorder(jsonHospitalVisitSchedule.getHeader(), normalBoldFont);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfPCell.setUseVariableBorders(true);
                pdfPCell.setPadding(5);
                pdfPCell.setBorderColor(BaseColor.LIGHT_GRAY);
                ppptable.addCell(pdfPCell);
                document.add(ppptable);
                document.add(addVerticalSpaceBefore(5.0f));
                Map<String, BooleanReplacementEnum> visitingFor = jsonHospitalVisitSchedule.getVisitingFor();
                String dateExpected = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonHospitalVisitSchedule.getExpectedDate());
                String dateVisited = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonHospitalVisitSchedule.getVisitedDate());
                PdfPTable ptable = new PdfPTable(3);
                ptable.addCell(pdfPCellWithoutBorder("Due: " + dateExpected, normalFont));
                ptable.addCell(pdfPCellWithoutBorder("Administered: " + dateVisited, normalFont));
                ptable.addCell(pdfPCellWithoutBorder("By: " + (StringUtils.isBlank(jsonHospitalVisitSchedule.getPerformedBy()) ? "" : jsonHospitalVisitSchedule.getPerformedBy()), normalFont));
                document.add(ptable);
                PdfPTable table = new PdfPTable(3);
                int size = visitingFor.size();
                int remain = 3 - (size % 3);

                for (Map.Entry<String, BooleanReplacementEnum> entry : visitingFor.entrySet()) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    table.addCell(pdfPCellWithBorder1(entry.getKey(), normalFont));
                }

                for (int j = 0; j < remain; j++) {
                    table.addCell(pdfPCellWithoutBorder("", normalFont));
                }

                document.add(table);
                document.add(addVerticalSpaceBefore(5.0f));
                document.add(addVerticalSpace());
                document.add(addVerticalSpaceBefore(20.0f));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

