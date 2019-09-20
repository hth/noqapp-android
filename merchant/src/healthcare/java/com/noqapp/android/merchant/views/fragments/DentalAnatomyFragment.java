package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.model.types.medical.DentalOptionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;

import java.util.ArrayList;
import java.util.List;

public class DentalAnatomyFragment extends BaseFragment {

    protected List<ToothProcedure> getFrontAllViews() {
        List<ToothProcedure> drawables = new ArrayList<>();

        drawables.add(new ToothProcedure(R.drawable.tooth_18, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_17, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_16, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_15, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_14, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_13, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_12, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_11, DentalOptionEnum.NOR.getDescription()));

        drawables.add(new ToothProcedure(R.drawable.tooth_21, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_22, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_23, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_24, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_25, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_26, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_27, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_28, DentalOptionEnum.NOR.getDescription()));

        drawables.add(new ToothProcedure(R.drawable.tooth_48, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_47, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_46, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_45, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_44, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_43, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_42, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_41, DentalOptionEnum.NOR.getDescription()));

        drawables.add(new ToothProcedure(R.drawable.tooth_31, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_32, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_33, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_34, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_35, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_36, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_37, DentalOptionEnum.NOR.getDescription()));
        drawables.add(new ToothProcedure(R.drawable.tooth_38, DentalOptionEnum.NOR.getDescription()));

        return drawables;
    }


    protected List<ToothProcedure> getTopOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        int[] drawable = new int[7];
        for (int i = 0; i < 7; i++) {
            drawable[i] = this.getResources().getIdentifier(String.valueOf("tooth_o_" + (i + 1)), "drawable", getActivity().getPackageName());
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
        int[] drawable = new int[10];
        for (int i = 0; i < 10; i++) {
            drawable[i] = this.getResources().getIdentifier(String.valueOf("tooth__o_f_" + (i + 1)), "drawable", getActivity().getPackageName());
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 10;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }
}
