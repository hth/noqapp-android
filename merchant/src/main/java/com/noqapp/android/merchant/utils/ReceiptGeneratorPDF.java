package com.noqapp.android.merchant.utils;

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
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.HeaderFooterPageEvent;
import com.noqapp.android.common.utils.PdfHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.pojos.Receipt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReceiptGeneratorPDF extends PdfHelper {
    private Font urFontName;
    private String currencySymbol;
    private Receipt receipt;

    public ReceiptGeneratorPDF(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        try {
            BaseFont urName = BaseFont.createFont("assets/fonts/hindi.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            urFontName = new Font(urName, 10, Font.NORMAL, BaseColor.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPdf(Receipt temp) {
        this.receipt = temp;
        currencySymbol = AppInitialize.getCurrencySymbol();
        currencySymbol = "₹";
        String fileName = new SimpleDateFormat("'NoQueue_" + receipt.getJsonPurchaseOrder().getCustomerName() + "_'yyyyMMdd'.pdf'", Locale.getDefault()).format(new Date());
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

            Font titleFont = new Font(baseFont, 13.0f, Font.BOLD, BaseColor.BLACK);
            Chunk titleChunk = new Chunk(receipt.getBusinessName(), titleFont);
            Paragraph titleParagraph = new Paragraph();
            titleParagraph.add(titleChunk);
            document.add(titleParagraph);

            Font noqFont = new Font(baseFont, 13.0f, Font.BOLD, BaseColor.BLACK);

            Chunk degreeChunk = new Chunk(receipt.getStoreAddress(), normalFont);
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
            new CustomToast().showToast(mContext, "Invoice Generated");
            openFile(mContext, dest);
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            new CustomToast().showToast(mContext, "No application found to open this file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PdfPTable getOrderData() {
        PdfPTable table = new PdfPTable(5);
        try {
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 4, 1, 2, 2});
            for (int i = 0; i < receipt.getJsonPurchaseOrder().getPurchaseOrderProducts().size(); i++) {
                JsonPurchaseOrderProduct jpop = receipt.getJsonPurchaseOrder().getPurchaseOrderProducts().get(i);
                table.addCell(pdfPCellWithoutBorder(String.valueOf(i + 1), normalFont));
                table.addCell(pdfPCellWithoutBorder(jpop.getProductName() + " " + AppUtils.getPriceWithUnits(jpop.getJsonStoreProduct()), normalFont));
                table.addCell(pdfPCellWithoutBorder(String.valueOf(jpop.getProductQuantity()), normalFont));
                table.addCell(pdfPCellWithoutBorder(currencySymbol + CommonHelper.displayPrice(jpop.getProductPrice()), urFontName));
                table.addCell(pdfPCellWithoutBorder(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jpop.getProductPrice()).multiply(new BigDecimal(jpop.getProductQuantity())).toString()), urFontName));
            }
            //Add discount info in receipt
            if (!TextUtils.isEmpty(receipt.getJsonPurchaseOrder().getCouponId())) {
                table.addCell(pdfPCellWithoutBorder(String.valueOf(receipt.getJsonPurchaseOrder().getPurchaseOrderProducts().size() + 1), normalFont));
                table.addCell(pdfPCellWithoutBorder("Discount", normalFont));
                table.addCell(pdfPCellWithoutBorder("1", normalFont));
                table.addCell(pdfPCellWithoutBorder(Constants.MINUS + currencySymbol + CommonHelper.displayPrice(receipt.getJsonPurchaseOrder().getStoreDiscount()), urFontName));
                table.addCell(pdfPCellWithoutBorder(Constants.MINUS + currencySymbol + CommonHelper.displayPrice(receipt.getJsonPurchaseOrder().getStoreDiscount()), urFontName));
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
            table.addCell(pdfPCellWithoutBorderWithPadding(receipt.getJsonPurchaseOrder().getPaymentStatus().getDescription(), normalFont, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Total Cost:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(currencySymbol + CommonHelper.displayPrice(receipt.getJsonPurchaseOrder().getOrderPrice()), urFontName, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Balance Amount:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(currencySymbol + receipt.getJsonPurchaseOrder().computeBalanceAmount(), urFontName, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Paid Amount:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(currencySymbol + receipt.getJsonPurchaseOrder().computePaidAmount(), urFontName, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Transaction Via:", normalBoldFont, 5));
            String transactionVia = (null == receipt.getJsonPurchaseOrder().getTransactionVia()) ? "N/A" : receipt.getJsonPurchaseOrder().getTransactionVia().getFriendlyDescription();
            table.addCell(pdfPCellWithoutBorderWithPadding(transactionVia, normalFont, 5));

            table.addCell(pdfPCellWithoutBorderWithPadding("Payment Mode:", normalBoldFont, 5));
            table.addCell(pdfPCellWithoutBorderWithPadding(receipt.getJsonPurchaseOrder().getPaymentMode().getDescription(), normalFont, 5));

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
            table.setWidths(new int[]{1, 4, 1, 2, 2});
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
        table.addCell(pdfPCellWithoutBorder(checkNull(receipt.getBusinessCustomerId()), normalFont));
        table.addCell(pdfPCellWithoutBorder("Order Id                :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(CommonHelper.transactionForDisplayOnly(receipt.getTransactionId()), normalFont));

        // Line 2
        table.addCell(pdfPCellWithoutBorder("Customer Name       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(checkNull(receipt.getJsonPurchaseOrder().getCustomerName()), normalFont));
        table.addCell(pdfPCellWithoutBorder("Date                       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(formattedDate, normalFont));

        // Line 3
        table.addCell(pdfPCellWithoutBorder("Doctor Name       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(checkNull(receipt.getName()), normalFont));
        table.addCell(pdfPCellWithoutBorder("Time                       :", normalBoldFont));
        table.addCell(pdfPCellWithoutBorder(time, normalFont));

        table.setTotalWidth(PageSize.A4.getWidth() - 80);
        table.setLockedWidth(true);
        return table;
    }
}
