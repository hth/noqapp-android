package com.noqapp.android.common.views.activities.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.noqapp.android.common.utils.CommonHelper;

public class CapitalizeEachWordFirstLetterTextWatcher implements TextWatcher {
    private EditText sourceTextView;


    public CapitalizeEachWordFirstLetterTextWatcher(EditText sourceTextView) {
        this.sourceTextView = sourceTextView;
    }

    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    public void afterTextChanged(Editable editable) {
        if (editable.length() != 0) {
            sourceTextView.removeTextChangedListener(this);
            sourceTextView.setText(CommonHelper.capitalizeEachWordFirstLetter(editable.toString()));
            sourceTextView.setSelection(sourceTextView.getText().length());
            sourceTextView.addTextChangedListener(this);
        }
    }
}