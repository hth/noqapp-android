package com.noqapp.client.views.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.NoQueueDBPresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.utils.Constants;
import com.noqapp.client.utils.Formatter;
import com.noqapp.client.views.activities.JoinQueueActivity;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.adapters.ExpandableListAdapter;
import com.noqapp.client.views.interfaces.Token_QueueViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ListQueueFragment extends NoQueueBaseFragment implements TokenAndQueuePresenter, Token_QueueViewInterface {



    private RelativeLayout rl_empty_screen;
    public static boolean isCurrentQueueCall = false;
    private String TAG = ListQueueFragment.class.getSimpleName();
    private FragmentActivity context;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<JsonTokenAndQueue>> listDataChild;
    public ListQueueFragment() {

    }

    public static ListQueueFragment getInstance() {
        return new ListQueueFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void callQueue() {
        LaunchActivity.getLaunchActivity().progressDialog.show();
        QueueModel.tokenAndQueuePresenter = this;
        QueueModel.getAllJoinedQueue(LaunchActivity.DID);
        isCurrentQueueCall = true;
    }

    public void callQueueHistory() {

        QueueModel.tokenAndQueuePresenter = this;
        //Todo Check the flow of history queue
        //QueueModel.getAllHistoricalJoinedQueue(LaunchActivity.DID);
        DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
        QueueModel.getAllHistoricalJoinedQueue("123", deviceToken);
        //QueueModel.getAllJoinedQueue(LaunchActivity.DID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_listqueue, container, false);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        //ButterKnife.bind(this,view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            callQueue();
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Queues");

    }


    @Override
    public void queueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        Log.d(TAG, "Tokent and Queue Response::" + tokenAndQueues.size());
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveToken_Queue(tokenAndQueues, isCurrentQueueCall);

    }

    @Override
    public void queueError() {
        Log.d(TAG, "Token and queue Error");

    }

    @Override
    public void noCurentQueue() {
        dataSavedStatus(0);
    }

    @Override
    public void noHistoryQueue() {
        dataSavedStatus(0);
    }

    @Override
    public void dataSavedStatus(int msg) {
        Log.d(TAG, String.valueOf(msg));
        if (isCurrentQueueCall) {
            isCurrentQueueCall = false;
            callQueueHistory();

        } else {
            fetchCurrentAndHistoryList();
        }

    }


    public void fetchCurrentAndHistoryList() {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.currentandHistoryTokenQueueListFromDB();
    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Current Queue Count : " + String.valueOf(currentlist.size()) + "::" + String.valueOf(historylist.size()));
       initListView(currentlist,historylist);
        rl_empty_screen.setVisibility(View.GONE);
        expListView.setVisibility(View.VISIBLE);

    }


    private void initListView(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<JsonTokenAndQueue>>();

        // Adding child data
        listDataHeader.add("Current Queue");
        listDataHeader.add("History");
        listDataChild.put(listDataHeader.get(0), currentlist); // Header, Child data
        listDataChild.put(listDataHeader.get(1), historylist);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);//By default expand the list first group
        expListView.expandGroup(1);
        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                if(groupPosition==0){
                    JsonTokenAndQueue jsonQueue= listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                    Intent intent = new Intent(getActivity(), JoinQueueActivity.class);
                    intent.putExtra(JoinQueueActivity.KEY_CODEQR, jsonQueue.getCodeQR());
                    intent.putExtra(JoinQueueActivity.KEY_DISPLAYNAME, jsonQueue.getBusinessName());
                    intent.putExtra(JoinQueueActivity.KEY_STOREPHONE, jsonQueue.getStorePhone());
                    intent.putExtra(JoinQueueActivity.KEY_QUEUENAME, jsonQueue.getDisplayName());
                    intent.putExtra(JoinQueueActivity.KEY_ADDRESS, Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
                    intent.putExtra(JoinQueueActivity.KEY_TOPIC, jsonQueue.getTopic());
                    getActivity().startActivityForResult(intent, Constants.requestCodeJoinQActivity);
                }else {
                    Toast.makeText(
                            getActivity(),
                            listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT)
                            .show();
                }
                return false;
            }
        });
    }
}
