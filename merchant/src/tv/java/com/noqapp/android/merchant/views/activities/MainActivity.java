package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.VigyaapanTypeEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ClientInQueueModel;
import com.noqapp.android.merchant.model.VigyaapanModel;
import com.noqapp.android.merchant.presenter.ClientInQueuePresenter;
import com.noqapp.android.merchant.presenter.VigyaapanPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTV;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTV;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.common.api.Status;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ClientInQueuePresenter, VigyaapanPresenter {

    protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";
    private MediaRouter mediaRouter;
    private CastDevice castDevice;
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 10*1000;
    private HashMap<String, JsonTopic> topicHashMap = new HashMap<>();
    private ProgressDialog progressDialog;
    protected BroadcastReceiver broadcastReceiver;
    private JsonVigyaapanTV jsonVigyaapanTV;
    private boolean isNotification =false;
    private DetailFragment detailFragment;
    private List<TopicAndQueueTV> topicAndQueueTVList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        initProgress();
        setupMediaRouter();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    updateTv();
                }
            }
        };
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            QueueDetail queueDetail = getQueueDetails();
            ClientInQueueModel clientInQueueModel = new ClientInQueueModel(this);
            clientInQueueModel.toBeServedClients(
                    UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), queueDetail);

            VigyaapanModel vigyaapanModel = new VigyaapanModel();
            vigyaapanModel.setVigyaapanPresenter(this);
            vigyaapanModel.getVigyaapan(  UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(),VigyaapanTypeEnum.MV);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRemoteDisplaying()) {
            if (castDevice != null) {
                startCastService(castDevice);
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    @Override
    public void onDestroy() {
        if (mediaRouter != null) {
            mediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.action_cast);
        return true;
    }

    private void setupMediaRouter() {
        mediaRouter = MediaRouter.getInstance(getApplicationContext());
        MediaRouteSelector mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(getString(R.string.app_cast_id))).build();
        if (isRemoteDisplaying()) {
            this.castDevice = CastDevice.getFromBundle(mediaRouter.getSelectedRoute().getExtras());
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE);
            }
        }
        mediaRouter.addCallback(mediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    private boolean isRemoteDisplaying() {
        return CastRemoteDisplayLocalService.getInstance() != null;
    }

    private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            castDevice = CastDevice.getFromBundle(info.getExtras());
            Toast.makeText(getApplicationContext(), getString(R.string.cast_connected_to) + info.getName(), Toast.LENGTH_LONG).show();
            startCastService(castDevice);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            if (isRemoteDisplaying()) {
                CastRemoteDisplayLocalService.stopService();
            }
            castDevice = null;
        }
    };

    private void startCastService(final CastDevice castDevice) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent notificationPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

                CastRemoteDisplayLocalService.NotificationSettings settings =
                        new CastRemoteDisplayLocalService.NotificationSettings.Builder()
                                .setNotificationPendingIntent(notificationPendingIntent).build();

                CastRemoteDisplayLocalService.startService(MainActivity.this, PresentationService.class,
                        getString(R.string.app_cast_id), castDevice, settings,
                        new CastRemoteDisplayLocalService.Callbacks() {
                            @Override
                            public void onServiceCreated(CastRemoteDisplayLocalService service) {
                                ((PresentationService) service).setTopicAndQueueTV(
                                        topicAndQueueTVList.get(0),0);
                            }

                            @Override
                            public void onRemoteDisplaySessionStarted(CastRemoteDisplayLocalService service) {
                            }

                            @Override
                            public void onRemoteDisplaySessionError(Status errorReason) {
                                initError();

                                MainActivity.this.castDevice = null;
                                MainActivity.this.finish();
                            }

                            @Override
                            public void onRemoteDisplaySessionEnded(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {
                            }
                        });

            }
        });
    }

    private void initError() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_connection_error, Toast.LENGTH_SHORT);
        if (mediaRouter != null) {
            mediaRouter.selectRoute(mediaRouter.getDefaultRoute());
        }
        toast.show();
    }

    @NonNull
    private QueueDetail getQueueDetails() {
        QueueDetail queueDetail = new QueueDetail();
        List<String> tvObjects = new ArrayList<>();
        topicHashMap.clear();
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            JsonTopic jsonTopic = LaunchActivity.merchantListFragment.getTopics().get(i);
            String start = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getStartHour());
            String end = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getEndHour());
            if (isCurrentTimeInRange(start, end)) {
                tvObjects.add(LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR());
                topicHashMap.put(LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR(), LaunchActivity.merchantListFragment.getTopics().get(i));
            }
        }
        queueDetail.setCodeQRs(tvObjects);
        return queueDetail;
    }

    @Override
    public void clientInResponse(JsonQueueTVList jsonQueueTVList) {
        if (null != jsonQueueTVList && null != jsonQueueTVList.getQueues()) {

            Log.v("TV Data", jsonQueueTVList.getQueues().toString());
            topicAndQueueTVList.clear();
            for (int i = 0; i < jsonQueueTVList.getQueues().size(); i++) {
                JsonQueueTV jsonQueueTV = jsonQueueTVList.getQueues().get(i);
                topicAndQueueTVList.add(new TopicAndQueueTV().setJsonTopic(topicHashMap.get(jsonQueueTV.getCodeQR())).setJsonQueueTV(jsonQueueTV));
            }
            if(topicAndQueueTVList.size() ==0) {
                topicAndQueueTVList.add(new TopicAndQueueTV().setJsonTopic(null).setJsonQueueTV(null));
            }
            if(null == detailFragment)
              detailFragment = new DetailFragment();
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, detailFragment, "NewFragmentTag");
            ft.commit();

            if(!isNotification) {
                    final Handler handler = new Handler();
                    final Runnable Update = new Runnable() {
                        public void run() {
                            if (currentPage == topicAndQueueTVList.size()) {
                                currentPage = 0;
                            }
                            detailFragment = DetailFragment.newInstance(topicAndQueueTVList.get(currentPage));
                            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.frame_layout, detailFragment, "NewFragmentTag");
                            ft.commit();
                            Toast.makeText(MainActivity.this,"Screen changed",Toast.LENGTH_LONG).show();
                            if (CastRemoteDisplayLocalService.getInstance() != null) {
                                ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setVigyaapan(jsonVigyaapanTV,currentPage);
                                ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setTopicAndQueueTV(topicAndQueueTVList.get(currentPage),currentPage);
                            }
                            currentPage++;
                        }
                    };


                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    timer = new Timer(); // This will create a new Thread
                    timer.schedule(new TimerTask() { // task to be scheduled

                        @Override
                        public void run() {
                            handler.post(Update);
                        }
                    }, DELAY_MS, PERIOD_MS);
                }

        }
        dismissProgress();
        isNotification = false;
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
        isNotification = false;
        //finish();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        isNotification = false;
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej) {
            new ErrorResponseHandler().processError(this, eej);
        }
        isNotification = false;
    }

    public void updateTv() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            isNotification = true;
            progressDialog.show();
            QueueDetail queueDetail = getQueueDetails();
            ClientInQueueModel clientInQueueModel = new ClientInQueueModel(this);
            clientInQueueModel.toBeServedClients(
                    UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), queueDetail);
        }
    }

    @Override
    public void vigyaapanResponse(JsonVigyaapanTV jsonVigyaapanTV) {
        Log.v("data",jsonVigyaapanTV.toString());
        this.jsonVigyaapanTV = jsonVigyaapanTV;
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private boolean isCurrentTimeInRange(String start, String end) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm",Locale.getDefault());
            Date date = new Date();
            String current = formatter.format(date);

            Date time1 = new SimpleDateFormat("HH:mm",Locale.getDefault()).parse(start);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            Date time2 = new SimpleDateFormat("HH:mm",Locale.getDefault()).parse(end);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            Date d = new SimpleDateFormat("HH:mm",Locale.getDefault()).parse(current);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            return x.after(calendar1.getTime()) && x.before(calendar2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
