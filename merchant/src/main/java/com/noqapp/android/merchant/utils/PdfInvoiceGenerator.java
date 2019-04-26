package com.noqapp.android.merchant.utils;


import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.pojos.InvoiceObj;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
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
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfInvoiceGenerator {
    private BaseFont baseFont;
    private Context mContext;
    private JsonQueuedPerson jsonQueuedPerson;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private Font normalFont;
    private Font normalBoldFont;
    private Font normalBigFont;
    private Font urFontName;
    private String notAvailable = "N/A";
    private String currencySymbol;
    private InvoiceObj invoiceObj;
    public static final String RUPEE = "The Rupee character \u20B9 and the Rupee symbol \u20A8";

    public PdfInvoiceGenerator(Context mContext) {
        this.mContext = mContext;
        try {
            baseFont = BaseFont.createFont("assets/fonts/opensan.ttf", "UTF-8", BaseFont.EMBEDDED);
            normalFont = new Font(baseFont, 10.0f, Font.NORMAL, BaseColor.BLACK);
            normalBoldFont = new Font(baseFont, 10.0f, Font.BOLD, BaseColor.BLACK);
            normalBigFont = new Font(baseFont, 12.0f, Font.BOLD, BaseColor.BLACK);
            BaseFont urName = BaseFont.createFont("assets/fonts/hindi.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            urFontName = new Font(urName, 10, Font.NORMAL, BaseColor.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createPdf(JsonQueuedPerson jsonQueuedPerson) {
        this.jsonQueuedPerson = jsonQueuedPerson;
        jsonPurchaseOrder = jsonQueuedPerson.getJsonPurchaseOrder();
        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        currencySymbol = "₹";
        initPdfObj();
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

            Font titleFont = new Font(baseFont, 13.0f, Font.BOLD, BaseColor.BLACK);
            Chunk titleChunk = new Chunk(invoiceObj.getBusinessName(), titleFont);
            Paragraph titleParagraph = new Paragraph();
            titleParagraph.add(titleChunk);
            document.add(titleParagraph);


            Font noqFont = new Font(baseFont, 13.0f, Font.BOLD, BaseColor.BLACK);


            Chunk degreeChunk = new Chunk(invoiceObj.getBusinessAddress(), normalFont);
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.add(degreeChunk);
            degreeParagraph.add(glue);
            degreeParagraph.add(new Chunk(" ", noqFont));
            document.add(degreeParagraph);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date c = Calendar.getInstance().getTime();
            String formattedDate = df.format(c);

            document.add(new Chunk(getLineSeparator()));
            document.add(getGeneralInfoData());
            document.add(addVerticalSpaceBefore(5f));
            document.add(addVerticalSpaceAfter(5f));
            document.add(getOrderHeader());
            document.add(addVerticalSpaceBefore(5f));
            document.add(addVerticalSpaceAfter(5f));
            document.add(getOrderData());
            document.add(new Chunk(getLineSeparator()));
            document.add(getPaymentInfoTable());
            document.add(addVerticalSpaceBefore(10f));
            document.add(addVerticalSpaceAfter(10f));

            Paragraph p_sign = new Paragraph();
            p_sign.add(new Chunk("Signature: ", normalBigFont));
            p_sign.add(new Chunk(new LineSeparator()));
            p_sign.add("                                                                   ");
            p_sign.add(new Chunk("Date: ", normalBigFont));
            p_sign.add(new Chunk(formattedDate, normalFont));
            document.add(p_sign);
            document.close();

            Toast.makeText(mContext, "Invoice Generated", Toast.LENGTH_SHORT).show();
            openFile(mContext, new File(dest));
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LineSeparator getLineSeparator() {
        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        //lineSeparator.setOffset(-20);
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
        PdfPTable table = new PdfPTable(5);
        try {
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 4
                    , 1, 2, 2});
            for (int i = 0; i < invoiceObj.getPurchaseOrderProducts().size(); i++) {
                JsonPurchaseOrderProduct jpop = invoiceObj.getPurchaseOrderProducts().get(i);
                table.addCell(pdfPCellWithoutBorder(String.valueOf(i + 1), normalFont));
                table.addCell(pdfPCellWithoutBorder(jpop.getProductName(), normalFont));
                table.addCell(pdfPCellWithoutBorder(String.valueOf(jpop.getProductQuantity()), normalFont));
                table.addCell(pdfPCellWithoutBorder(currencySymbol + " " + CommonHelper.displayPrice(jpop.getProductPrice()), urFontName));
                table.addCell(pdfPCellWithoutBorder(currencySymbol + " " + CommonHelper.displayPrice(new BigDecimal(jpop.getProductPrice()).multiply(new BigDecimal(jpop.getProductQuantity())).toString()), urFontName));
            }
            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    private PdfPTable getPaymentInfoTable() {
        PdfPTable table = new PdfPTable(4);

        try {
            table.setWidthPercentage(100);
            table.addCell(pdfPCellWithoutBorderWithPadding("Payment Status:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(invoiceObj.getPayment_status(), normalFont, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Total Cost:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(invoiceObj.getTotal_amount(), urFontName, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Balance Amount:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(invoiceObj.getBalance_amount(), urFontName, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Paid Amount:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(invoiceObj.getPaid_amount(), urFontName, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Transaction Via:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(invoiceObj.getTransaction_via(), normalFont, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Payment Mode:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(invoiceObj.getPayment_mode(), normalFont, 5));

            table.setTotalWidth(PageSize.A4.getWidth() - 80);
            table.setLockedWidth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    private PdfPTable getOrderHeader() {
        PdfPTable table = new PdfPTable(5);
        //table.getDefaultCell().setBorder(Rectangle.BOTTOM | Rectangle.TOP);
        try {
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 4
                    , 1, 2, 2});
            table.addCell(pdfPCellWithTopBottomBorder("Sr. No ", normalBoldFont));
            table.addCell(pdfPCellWithTopBottomBorder("Service Name", normalBoldFont));
            table.addCell(pdfPCellWithTopBottomBorder("Qty:", normalBoldFont));
            table.addCell(pdfPCellWithTopBottomBorder("Rate", normalBoldFont));
            table.addCell(pdfPCellWithTopBottomBorder("Net Amount", normalBoldFont));
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }

    private PdfPTable getGeneralInfoData() {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        Date c = Calendar.getInstance().getTime();
        String formattedDate = df.format(c);

        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
        PdfPTable table = new PdfPTable(4);

        // Line 1
        table.addCell(pdfPCellWithoutBorder("MR No                   :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(invoiceObj.getBusinessCustomerId(), normalFont));
        table.addCell(pdfPCellWithoutBorder("Order Id                :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(invoiceObj.getOrderId(), normalFont));

        // Line 2
        table.addCell(pdfPCellWithoutBorder("Customer Name       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(invoiceObj.getCustomerName(), normalFont));
        table.addCell(pdfPCellWithoutBorder("Date                       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(formattedDate, normalFont));

        // Line 3
        table.addCell(pdfPCellWithoutBorder("Doctor Name       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(invoiceObj.getDoctorName(), normalFont));
        table.addCell(pdfPCellWithoutBorder("Time                       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(time, normalFont));


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

    private PdfPCell pdfPCellWithTopBottomBorder(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
        pdfPCell.setPaddingBottom(8);
        pdfPCell.setPaddingTop(5);
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

    private void initPdfObj() {
        invoiceObj = new InvoiceObj();
        invoiceObj.setBusinessName("SSD Hospital ???");
        invoiceObj.setBusinessAddress("Koparkhairne AreaAndTown?? ");
        invoiceObj.setBusinessCustomerId(TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId()) ? notAvailable : jsonQueuedPerson.getBusinessCustomerId());
        invoiceObj.setCustomerName(jsonQueuedPerson.getCustomerName());
        invoiceObj.setDoctorName("Rohan Mudgar ?????");
        invoiceObj.setOrderId(jsonPurchaseOrder.getTransactionId());


        String payment_mode = "";
        String payment_status = "";
        String paid_amount = "";
        String balance_amount = "";
        try {
            if (TextUtils.isEmpty(jsonPurchaseOrder.getPartialPayment())) {
                paid_amount = currencySymbol + " " + String.valueOf(0);
                balance_amount = currencySymbol + " " + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice());
            } else {
                paid_amount = currencySymbol + " " + CommonHelper.displayPrice(jsonPurchaseOrder.getPartialPayment());
                balance_amount = currencySymbol + " " + CommonHelper.displayPrice(String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) -
                        Double.parseDouble(jsonPurchaseOrder.getPartialPayment())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
            payment_mode = jsonPurchaseOrder.getPaymentMode().getDescription();

            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
                balance_amount = currencySymbol + " " + String.valueOf(0);
                paid_amount = currencySymbol + " " + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice());
            }
        }
        payment_status = null != jsonPurchaseOrder.getPaymentStatus() ? jsonPurchaseOrder.getPaymentStatus().getDescription() : notAvailable;
        if (PurchaseOrderStateEnum.CO == jsonPurchaseOrder.getPresentOrderState() && null == jsonPurchaseOrder.getPaymentMode()) {
            payment_mode = notAvailable;
        }

        invoiceObj.setPayment_mode(payment_mode);
        invoiceObj.setPayment_status(payment_status);
        invoiceObj.setPaid_amount(paid_amount);
        invoiceObj.setBalance_amount(balance_amount);
        invoiceObj.setTotal_amount(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())));
        invoiceObj.setTransaction_via(null != jsonPurchaseOrder.getTransactionVia() ? jsonPurchaseOrder.getTransactionVia().getFriendlyDescription() : notAvailable);
        invoiceObj.setPurchaseOrderProducts(jsonPurchaseOrder.getPurchaseOrderProducts());
    }
}
