package com.noqapp.android.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.noqapp.android.common.R;


public class ShowUploadImageDialog {
    private Context context;

    private DialogClickListener dialogClickListener;


    public interface DialogClickListener {
        void btnPositiveClick();

        void btnNegativeClick();
    }

    public ShowUploadImageDialog setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
        return this;
    }

    public ShowUploadImageDialog(Context context) {
        this.context = context;
    }



    public void displayDialog(Bitmap bitmap) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_upload_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        ImageView iv_banner = dialog.findViewById(R.id.iv_banner);
        iv_banner.setImageBitmap(bitmap);
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);

        btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (null != dialogClickListener)
                dialogClickListener.btnPositiveClick();
        });
        btnNegative.setOnClickListener(v -> {
            dialog.dismiss();
            if (null != dialogClickListener)
                dialogClickListener.btnNegativeClick();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
