package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ClientInQModel;
import com.noqapp.android.merchant.presenter.ClientInQPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.QueueDetail;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.common.api.Status;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CustomSimpleOnPageChangeListener.OnPageChangePosition, ClientInQPresenter {
    protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";
    private int currentPosition;
    private ScreenSlidePagerAdapter fragmentStatePagerAdapter;
    private MediaRouter mediaRouter;
    private CastDevice castDevice;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        List<TvObject> list = getAdViewModels();

        final ViewPager viewPager = findViewById(R.id.pager);
        fragmentStatePagerAdapter =
                new ScreenSlidePagerAdapter(getSupportFragmentManager());
        fragmentStatePagerAdapter.addAds(list);
        CustomSimpleOnPageChangeListener customSimpleOnPageChangeListener =
                new CustomSimpleOnPageChangeListener(this);
        if (viewPager != null) {
            viewPager.setAdapter(fragmentStatePagerAdapter);
            viewPager.addOnPageChangeListener(customSimpleOnPageChangeListener);
            /*After setting the adapter use the timer */
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == fragmentStatePagerAdapter.getCount()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        }
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            //progressDialog.show();
            QueueDetail queueDetail = getQueueDetails();
            ClientInQModel clientInQModel = new ClientInQModel(this);
            clientInQModel.toBeServedClients(
                    UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(),queueDetail);
        }
        setupMediaRouter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRemoteDisplaying()) {
            if (castDevice != null) {
                startCastService(castDevice);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaRouter != null) {
            mediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.action_cast);
        return true;
    }

    private void setupMediaRouter() {
        mediaRouter = MediaRouter.getInstance(getApplicationContext());
        MediaRouteSelector mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(
                CastMediaControlIntent.categoryForCast(getString(R.string.app_cast_id))).build();
        if (isRemoteDisplaying()) {
            this.castDevice = CastDevice.getFromBundle(mediaRouter.getSelectedRoute().getExtras());
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE);
            }
        }

        mediaRouter.addCallback(mediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    private boolean isRemoteDisplaying() {
        return CastRemoteDisplayLocalService.getInstance() != null;
    }

    private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            castDevice = CastDevice.getFromBundle(info.getExtras());
            Toast.makeText(getApplicationContext(),
                    getString(R.string.cast_connected_to) + info.getName(), Toast.LENGTH_LONG).show();
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

    private void startCastService(CastDevice castDevice) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        CastRemoteDisplayLocalService.NotificationSettings settings =
                new CastRemoteDisplayLocalService.NotificationSettings.Builder().setNotificationPendingIntent(
                        notificationPendingIntent).build();

        CastRemoteDisplayLocalService.startService(MainActivity.this, PresentationService.class,
                getString(R.string.app_cast_id), castDevice, settings,
                new CastRemoteDisplayLocalService.Callbacks() {
                    @Override
                    public void onServiceCreated(CastRemoteDisplayLocalService service) {
                        ((PresentationService) service).setTvObject(
                                fragmentStatePagerAdapter.getAdAt(currentPosition));
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

    private void initError() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_connection_error,
                Toast.LENGTH_SHORT);
        if (mediaRouter != null) {
            mediaRouter.selectRoute(mediaRouter.getDefaultRoute());
        }
        toast.show();
    }

    @Override
    public void onCurrentPageChange(int position) {
        currentPosition = position;
        if (CastRemoteDisplayLocalService.getInstance() != null) {
            ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setTvObject(
                    fragmentStatePagerAdapter.getAdAt(position));
        }
    }

    @NonNull
    private List<TvObject> getAdViewModels() {
        List<TvObject> tvObjects = new ArrayList<>();
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            tvObjects.add(new TvObject().setJsonTopic(LaunchActivity.merchantListFragment.getTopics().get(i)).setJsonQueuedPersonList(LaunchActivity.merchantListFragment.merchantDetailFragment.getJsonQueuedPersonArrayList()));
        }
        return tvObjects;
    }

    @NonNull
    private QueueDetail getQueueDetails() {
        QueueDetail queueDetail = new QueueDetail();
        List<String> tvObjects = new ArrayList<>();
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            tvObjects.add(LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR());
        }
        queueDetail.setCodeQRs(tvObjects);
        return queueDetail;
    }

    @Override
    public void ClientInResponse(JsonQueueTVList jsonQueueTVList) {
        if(null != jsonQueueTVList){

        }

    }

    @Override
    public void authenticationFailure() {
        //dismissProgress();
        AppUtils.authenticationProcessing();
        //finish();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
       // dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        //dismissProgress();
        if (null != eej) {
            new ErrorResponseHandler().processError(this, eej);
        }
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final List<TvObject> ads = new ArrayList<>();

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DetailFragment.newInstance(ads.get(position));
        }

        @Override
        public int getCount() {
            return ads.size();
        }

        void addAds(List<TvObject> ads) {
            this.ads.addAll(ads);
            notifyDataSetChanged();
        }

        public TvObject getAdAt(int position) {
            return ads.get(position);
        }
    }

}
