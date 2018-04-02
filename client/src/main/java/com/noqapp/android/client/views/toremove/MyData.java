package com.noqapp.android.client.views.toremove;


import com.noqapp.android.client.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyData {

    public static String[] nameArray = {"Cakes", "Navratna", "Coffee Cafe Day", "Bikaner wala", "Haldiram"};
    public static Integer[] drawableArray = {R.drawable.aa, R.drawable.ab, R.drawable.ac,
            R.drawable.ae, R.drawable.af};


    public static HashMap<String, List<ChildData>> getData() {
        HashMap<String, List<ChildData>> expandableListDetail = new HashMap<String, List<ChildData>>();

        List<ChildData> cricket = new ArrayList<ChildData>();
        cricket.add(new ChildData("Phoha",""));
        cricket.add(new ChildData("Upma",""));
        cricket.add(new ChildData("Idli",""));
        cricket.add(new ChildData("Pav Bhaji",""));
        cricket.add(new ChildData("Daal vada",""));

        List<ChildData> football = new ArrayList<ChildData>();
        football.add(new ChildData("Veg Sandwich",""));
        football.add(new ChildData("Chocolate Sandwich",""));
        football.add(new ChildData("Jain Veg. Sandwich",""));
        football.add(new ChildData("Veg Cheese Sandwich",""));
        football.add(new ChildData("Masala Toast Sandwich",""));

        List<ChildData> basketball = new ArrayList<ChildData>();
        basketball.add(new ChildData("Soup",""));
        basketball.add(new ChildData("Tea",""));
        basketball.add(new ChildData("Coffee",""));
        basketball.add(new ChildData("Cold Drink",""));
        basketball.add(new ChildData("Cold Coffee",""));

        expandableListDetail.put("NASTA", cricket);
        expandableListDetail.put("SANDWICH", football);
        expandableListDetail.put("OTHERS", basketball);
        return expandableListDetail;
    }
}
