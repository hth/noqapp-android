package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.model.types.medical.DentalOptionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;

import java.util.ArrayList;
import java.util.List;

public class DentalAnatomyFragment extends BaseFragment {

    protected List<ToothProcedure> getFrontAllViews() {
        List<ToothProcedure> drawables = new ArrayList<>();

        drawables.add(new ToothProcedure("tooth_18", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_17", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_16", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_15", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_14", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_13", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_12", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_11", DentalOptionEnum.NOR.getDescription()));

        drawables.add(new ToothProcedure("tooth_21", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_22", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_23", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_24", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_25", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_26", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_27", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_28", DentalOptionEnum.NOR.getDescription()));

        drawables.add(new ToothProcedure("tooth_48", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_47", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_46", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_45", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_44", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_43", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_42", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_41", DentalOptionEnum.NOR.getDescription()));

        drawables.add(new ToothProcedure("tooth_31", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_32", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_33", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_34", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_35", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_36", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_37", DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure("tooth_38", DentalOptionEnum.NOR.getDescription()));

        return drawables;
    }


    protected List<ToothProcedure> getTopOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        String[] drawable = new String[7];
        for (int i = 0; i < 7; i++) {
            drawable[i] = "tooth_o_" + (i + 1);
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 7;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }

    protected List<ToothProcedure> getFrontOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        String[] drawable = new String[10];
        for (int i = 0; i < 10; i++) {
            drawable[i] = "tooth__o_f_" + (i + 1);
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 10;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }
}
