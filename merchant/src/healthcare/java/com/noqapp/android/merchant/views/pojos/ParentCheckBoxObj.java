package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.util.ArrayList;
import java.util.List;

public class ParentCheckBoxObj {

    private List<CheckBoxObj> checkBoxObjList = new ArrayList<>();
    private JsonTopic jsonTopic;
    private int selectedPos = -1;

    public List<CheckBoxObj> getCheckBoxObjList() {
        return checkBoxObjList;
    }

    public ParentCheckBoxObj setCheckBoxObjList(List<CheckBoxObj> checkBoxObjList) {
        this.checkBoxObjList = checkBoxObjList;
        return this;
    }

    public JsonTopic getJsonTopic() {
        return jsonTopic;
    }

    public ParentCheckBoxObj setJsonTopic(JsonTopic jsonTopic) {
        this.jsonTopic = jsonTopic;
        return this;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public ParentCheckBoxObj setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        return this;
    }
}
