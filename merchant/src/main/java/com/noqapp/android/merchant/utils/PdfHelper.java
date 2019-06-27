package com.noqapp.android.merchant.utils;

import android.text.TextUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PdfHelper {

    protected PdfPCell pdfPCellWithoutBorder(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    protected PdfPCell pdfPCellWithTopBottomBorder(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
        pdfPCell.setPaddingBottom(8);
        pdfPCell.setPaddingTop(5);
        return pdfPCell;
    }

    protected PdfPCell pdfPCellWithoutBorderWithPadding(String label, Font font, int padding) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.setPaddingBottom(padding);
        pdfPCell.setPaddingTop(padding);
        return pdfPCell;
    }

    protected PdfPCell pdfPCellWithoutBorder(String label, Font font, int padding) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.setPaddingBottom(padding);
        return pdfPCell;
    }

    protected Paragraph addVerticalSpace() {
        Paragraph paragraph = new Paragraph("");
        paragraph.setSpacingAfter(10f); // for adding extra space after a view
        return paragraph;
    }


    protected Paragraph addVerticalSpaceBefore(float space) {
        Paragraph paragraph = new Paragraph("");
        paragraph.setSpacingBefore(space); // for adding extra space after a view
        return paragraph;
    }

    protected Paragraph addVerticalSpaceAfter(float space) {
        Paragraph paragraph = new Paragraph("");
        paragraph.setSpacingAfter(space); // for adding extra space after a view
        return paragraph;
    }

    protected PdfPCell pdfPCellWithBorder(String label, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(label, font));
        // pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    protected LineSeparator getLineSeparator() {
        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        //lineSeparator.setOffset(-20);
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        return lineSeparator;
    }

    protected String checkNull(String input) {
        return TextUtils.isEmpty(input) ? "N/A" : input;
    }
}
