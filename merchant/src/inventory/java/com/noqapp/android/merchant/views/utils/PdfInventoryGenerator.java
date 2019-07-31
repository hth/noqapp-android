package com.noqapp.android.merchant.views.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.HeaderFooterPageEvent;
import com.noqapp.android.common.utils.PdfHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;

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
import java.util.Map;

public class PdfInventoryGenerator extends PdfHelper {
    private String hospitalName = "SSD Hospital floor";
    private String hospitalAddress = "Kopairkhairane, Navi Mumbai";

    public PdfInventoryGenerator(Context mContext) {
        super(mContext);
    }

    public void createPdf(Map<String, List<JsonCheckAsset>> dataList) {
        String fileName = new SimpleDateFormat("'NoQueue_" + hospitalName + "_Immunization_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
        File dest = new File(getAppPath(mContext.getResources().getString(R.string.app_name)) + fileName);
        if (dest.exists()) {
            Log.d("Delete", "File deleted successfully " + dest.delete());
        }

        try {
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(dest));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            pdfWriter.setPageEvent(event);

            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("NoQueue Health Merchant");
            document.addCreator("NoQueue Technologies");
            Chunk glue = new Chunk(new VerticalPositionMark());

            Font noqFont = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);

            Chunk degreeChunk = new Chunk(hospitalName, noqFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk("NoQueue", noqFont));
            document.add(degreeParagraph);
            addVerticalSpace();

            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk(hospitalAddress, normalFont));
            document.add(hospital);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            document.add(new Chunk(lineSeparator));
            document.add(addVerticalSpaceBefore(10f));


            document.add(addVerticalSpaceAfter(5f));
            addTable( document, dataList);
            document.add(addVerticalSpaceBefore(20.0f));


            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date c = Calendar.getInstance().getTime();
            String formattedDate = df.format(c);
            Paragraph p_sign = new Paragraph();
            p_sign.add(new Chunk("Created By: ", normalBigFont));
            p_sign.add(new Chunk(hospitalName, normalFont));
            p_sign.add("            ");
            p_sign.add("                                                                   ");
            p_sign.add(new Chunk("Date: ", normalBigFont));
            p_sign.add(new Chunk(formattedDate, normalFont));
            document.add(p_sign);

            Chunk chunkInstructionValue = new Chunk("\n\n", normalFont);
            Paragraph paragraphInstructionValue = new Paragraph(chunkInstructionValue);
            document.add(paragraphInstructionValue);
            document.add(addVerticalSpace());
            document.close();

            new CustomToast().showToast(mContext, "Inventory Record Generated");
            openFile(mContext, dest);
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            new CustomToast().showToast(mContext, "No application found to open this file.");
        }
    }


    private void addTable(Document document, Map<String, List<JsonCheckAsset>> dataList) {
        try {
            for (Map.Entry<String, List<JsonCheckAsset>> entry : dataList.entrySet()) {

                List<JsonCheckAsset> checkAssetList = entry.getValue();
                PdfPTable table_header = new PdfPTable(1);
                PdfPCell pdfPCell = pdfPCellWithBorder(entry.getKey(), normalBoldFont);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfPCell.setUseVariableBorders(true);
                pdfPCell.setPadding(5);
                pdfPCell.setBorderColor(BaseColor.LIGHT_GRAY);
                table_header.addCell(pdfPCell);
                document.add(table_header);
               // document.add(addVerticalSpaceBefore(5.0f));
                PdfPTable table = new PdfPTable(2);
                for (int j = 0; j < checkAssetList.size(); j++) {
                    table.addCell(pdfPCellWithBorder(checkAssetList.get(j).getAssetName(), normalFont));
                    table.addCell(pdfPCellWithBorder(checkAssetList.get(j).isStatus()?"OK":"Not OK", normalFont));
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

