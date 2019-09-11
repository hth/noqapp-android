package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.model.types.medical.DentalOptionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;

import java.util.ArrayList;
import java.util.List;

public class DentalAnatomyFragment extends BaseFragment{

    public List<ToothProcedure> getFrontAllViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_1_" + (i + 1)), "drawable", getActivity().getPackageName());
            drawables.add(new ToothProcedure(id, DentalOptionEnum.NOR.getDescription()));
        }
        return drawables;
    }


    public List<ToothProcedure> getTopOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        int[] drawable = new int[6];
        for (int i = 0; i < 6; i++) {
            drawable[i] = this.getResources().getIdentifier(String.valueOf("tooth_o_2_" + (i + 1)), "drawable", getActivity().getPackageName());
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 6;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }

    public List<ToothProcedure> getFrontOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        int[] drawable = new int[4];
        for (int i = 0; i < 4; i++) {
            drawable[i] = this.getResources().getIdentifier(String.valueOf("tooth_o_1_" + (i + 1)), "drawable", getActivity().getPackageName());
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 4;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }


    public ToothProcedure getTempDrawable(int pos) {
        if (pos == 1 || pos == 2 || pos == 3 || pos == 16 || pos == 15 || pos == 14) {
            return new ToothProcedure(R.drawable.t_1, DentalOptionEnum.NOR.getDescription());
        } else if (pos == 4 || pos == 13) {
            return new ToothProcedure(R.drawable.t_2, DentalOptionEnum.NOR.getDescription());
        } else if (pos > 4 && pos < 13) {
            return new ToothProcedure(R.drawable.t_3, DentalOptionEnum.NOR.getDescription());
        } else if (pos == 17 || pos == 18 || pos == 19 || pos == 32 || pos == 31 || pos == 30) {
            return new ToothProcedure(R.drawable.t_4, DentalOptionEnum.NOR.getDescription());
        } else if (pos == 20 || pos == 29) {
            return new ToothProcedure(R.drawable.t_5, DentalOptionEnum.NOR.getDescription());
        } else if (pos > 20 && pos < 29) {
            return new ToothProcedure(R.drawable.t_6, DentalOptionEnum.NOR.getDescription());
        } else {
            return new ToothProcedure(R.drawable.t_3, DentalOptionEnum.NOR.getDescription());
        }
    }
}
