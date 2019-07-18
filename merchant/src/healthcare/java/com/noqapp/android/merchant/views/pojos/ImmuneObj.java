package com.noqapp.android.merchant.views.pojos;

import java.io.Serializable;

public class ImmuneObj implements Serializable {

    private String immuneTitle;
    private String immuneDate;
    private boolean isImmunationDone = false;

    public String getImmuneTitle() {
        return immuneTitle;
    }

    public void setImmuneTitle(String immuneTitle) {
        this.immuneTitle = immuneTitle;
    }

    public String getImmuneDate() {
        return immuneDate;
    }

    public void setImmuneDate(String immuneDate) {
        this.immuneDate = immuneDate;
    }

    public boolean isImmunationDone() {
        return isImmunationDone;
    }

    public void setImmunationDone(boolean immunationDone) {
        isImmunationDone = immunationDone;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmuneObj{");
        sb.append("immuneTitle='").append(immuneTitle).append('\'');
        sb.append(", immuneDate='").append(immuneDate).append('\'');
        sb.append(", isImmunationDone=").append(isImmunationDone);
        sb.append('}');
        return sb.toString();
    }
}
