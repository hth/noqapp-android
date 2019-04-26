package com.noqapp.android.merchant.views.utils;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
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
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.FileProvider;

public class PdfInvoiceGenerator {
    private BaseFont baseFont;
    private Context mContext;
    private JsonQueuedPerson jsonQueuedPerson;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private Font normalFont;
    private Font normalBoldFont;
    private Font normalBigFont;
    private String notAvailable = "N/A";
    private String currencySymbol;
    public static final String FONT1 = "resources/fonts/PlayfairDisplay-Regular.ttf";
    public PdfInvoiceGenerator(Context mContext) {
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


    public void createPdf(JsonQueuedPerson jsonQueuedPerson) {
        this.jsonQueuedPerson = jsonQueuedPerson;
        jsonPurchaseOrder = jsonQueuedPerson.getJsonPurchaseOrder();
        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        String fileName = new SimpleDateFormat("'NoQueue_" + jsonQueuedPerson.getCustomerName() + "_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
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

            Font titleFont = new Font(baseFont, 13.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk titleChunk = new Chunk(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getName(), titleFont);
            Paragraph titleParagraph = new Paragraph();
            titleParagraph.add(titleChunk);
            document.add(titleParagraph);


            Font noqFont = new Font(baseFont, 23.0f, Font.BOLD, BaseColor.BLACK);
            String license = new AppUtils().getCompleteEducation(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getLicenses());
            String temp = TextUtils.isEmpty(license) ? notAvailable : license;
            Chunk degreeChunk = new Chunk(new AppUtils().getCompleteEducation(LaunchActivity.getLaunchActivity().getUserProfessionalProfile().getEducation())
                    + " (Reg. Id: " + temp + ")", normalFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk("NoQueue", noqFont));
            document.add(degreeParagraph);
            addVerticalSpace();


            Paragraph hospital = new Paragraph();
            hospital.add(new Chunk("BusinessName??", normalBoldFont));
            hospital.add(new Chunk(", " + "AreaAndTown??", normalFont));
            document.add(hospital);

            document.add(new Chunk(getLineSeparator()));
            document.add(addVerticalSpaceBefore(10f));


            Chunk chunkOrder = new Chunk("Total Items: (1)", normalBigFont);
            Paragraph paragraphOrder = new Paragraph(chunkOrder);
            document.add(paragraphOrder);
            document.add(addVerticalSpace());
            document.add(getOrderData());
            document.add(addVerticalSpaceBefore(20f));
             String RUPEE = "The Rupee character \u20B9 and the Rupee symbol \u20A8";

            Font f1 = FontFactory.getFont(FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);

           // document.add(new Paragraph(RUPEE, f1));


            Chunk chunkPaymentInfo = new Chunk("Payment Info:", normalBigFont);
            Paragraph paragraphPaymentInfo = new Paragraph(chunkPaymentInfo);
            document.add(paragraphPaymentInfo);
            document.add(new Chunk(getLineSeparator()));
            document.add(addVerticalSpaceBefore(10f));
            document.add(addVerticalSpaceAfter(5f));
            document.add(getPaymentInfoTable());
            document.add(new Paragraph(""));
            document.add(addVerticalSpaceBefore(20.0f));


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

            Toast.makeText(mContext, "Report Generated", Toast.LENGTH_SHORT).show();
            openFile(mContext, new File(dest));
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    private LineSeparator getLineSeparator() {
        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        return lineSeparator;
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


    private PdfPTable getOrderData() {
        PdfPTable table = new PdfPTable(2);
        table.addCell(pdfPCellWithoutBorderWithPadding("Consultation Fees:", normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())), normalFont));


        table.addCell(pdfPCellWithoutBorderWithPadding("Order State:", normalBoldFont, 5));
        table.addCell(pdfPCellWithoutBorder(jsonPurchaseOrder.getPresentOrderState().getDescription(), normalFont));
        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }

    private PdfPTable getPaymentInfoTable() {
        PdfPTable table = new PdfPTable(2);

        try {
            table.addCell(pdfPCellWithoutBorderWithPadding("Payment Mode:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(null != jsonPurchaseOrder.getPaymentMode() ? jsonPurchaseOrder.getPaymentMode().getDescription() : notAvailable, normalFont));

            table.addCell(pdfPCellWithoutBorderWithPadding("Payment Status:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(null != jsonPurchaseOrder.getPaymentStatus() ? jsonPurchaseOrder.getPaymentStatus().getDescription() : notAvailable, normalFont));

            table.addCell(pdfPCellWithoutBorderWithPadding("Transaction Via:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(null != jsonPurchaseOrder.getTransactionVia() ? jsonPurchaseOrder.getTransactionVia().getDescription() : notAvailable, normalFont));

            table.addCell(pdfPCellWithoutBorderWithPadding("Total Cost:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())), normalFont));

            String paid_amount = "";
            String pending_amount = "";
            try {
                if (TextUtils.isEmpty(jsonPurchaseOrder.getPartialPayment())) {
                    paid_amount = currencySymbol + " " + String.valueOf(0);
                    pending_amount = currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
                } else {
                    paid_amount = currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100);
                    pending_amount = currencySymbol + " " + String.valueOf((Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) -
                            Double.parseDouble(jsonPurchaseOrder.getPartialPayment())) / 100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            table.addCell(pdfPCellWithoutBorderWithPadding("Total Paid Amount:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(paid_amount, normalFont));

            table.addCell(pdfPCellWithoutBorderWithPadding("Total Pending Amount:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorder(pending_amount, normalFont));

            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        }catch (Exception e){
            e.printStackTrace();
        }
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
}
