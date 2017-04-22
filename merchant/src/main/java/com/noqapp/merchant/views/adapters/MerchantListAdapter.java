package com.noqapp.merchant.views.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.merchant.R;
import com.noqapp.merchant.presenter.beans.ListQueue;


public class MerchantListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ListQueue> items;

	public MerchantListAdapter(Context context, ArrayList<ListQueue> arrayList) {
		this.context = context;
		this.items = arrayList;
	}

	public int getCount() {
		return this.items.size();
	}

	public Object getItem(int n) {
		return null;
	}

	public long getItemId(int n) {
		return 0;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		RecordHolder recordHolder;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		if (view == null) {
			recordHolder = new RecordHolder();
			view = layoutInflater.inflate(R.layout.listitem_currentqueue, null);
			recordHolder.txtNumber = (TextView) view.findViewById(R.id.txtNumber);
			recordHolder.txtStoreName = (TextView) view
					.findViewById(R.id.txtStoreName);
			recordHolder.txtStorePhoneNo = (TextView) view
					.findViewById(R.id.txtStorePhoneNo);

			recordHolder.txtToken = (TextView) view
					.findViewById(R.id.txtToken);

			view.setTag(recordHolder);
		} else {
			recordHolder = (RecordHolder) view.getTag();
		}
		ListQueue listQueueItem = items.get(position);
		recordHolder.txtNumber.setText(String.valueOf(position));
		recordHolder.txtStoreName.setText(listQueueItem.getDisplayName());
		recordHolder.txtStorePhoneNo.setText(String.valueOf(listQueueItem.getServingNumber()));
		recordHolder.txtToken.setText(String.valueOf(listQueueItem.getToken()));


		return view;
	}

	static class RecordHolder {
		TextView txtNumber;
		TextView txtStoreName;
		TextView txtStorePhoneNo;
		TextView txtToken;

		RecordHolder() {
		}
	}

}
