package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.Utils.GridItem;
import com.noqapp.android.merchant.views.interfaces.GridCommunication;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class GridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<GridItem> gridItems;
    private GridCommunication gridCommunication;
    private String key;
    public GridAdapter(Context context, ArrayList<GridItem> gridItems,GridCommunication gridCommunication,String key) {
        this.context = context;
        this.gridItems = gridItems;
        this.gridCommunication =gridCommunication;
        this.key =key ;
    }

    @Override
    public int getCount() {
        return gridItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gridItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (convertView == null) {
            recordHolder = new RecordHolder();
            convertView = layoutInflater.inflate(R.layout.grid_item_tests, null);
            recordHolder.cb_select = convertView.findViewById(R.id.cb_select);
            recordHolder.iv_favourite = convertView.findViewById(R.id.iv_favourite);
            convertView.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) convertView.getTag();
        }
        
        recordHolder.cb_select.setText(gridItems.get(position).getLabel());
        recordHolder.iv_favourite.setBackgroundResource(gridItems.get(position).isFavourite()?R.drawable.ic_favorite:R.drawable.ic_favorite_border);
        recordHolder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    gridCommunication.addDeleteItems(gridItems.get(position), isChecked, key);
                    gridItems.get(position).setSelect(isChecked);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        recordHolder.iv_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gridItems.get(position).isFavourite()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Delete Favourite");
                    tv_msg.setText("Do you want to delete it from favroite list.");
                    Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                    Button btn_no = customDialogView.findViewById(R.id.btn_no);
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_yes.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context,"Deleted from Favourite",Toast.LENGTH_LONG).show();
                            gridItems.get(position).setFavourite(false);
                           // adapterCommunicate.updateFavouriteList(medicalRecord,false);
                            notifyDataSetChanged();
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();
                }else{
                    gridItems.get(position).setFavourite(true);
                    //adapterCommunicate.updateFavouriteList(medicalRecord,true);
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    static class RecordHolder {
        ImageView iv_favourite;
        CheckBox cb_select;

        RecordHolder() {
        }
    }
    
}


