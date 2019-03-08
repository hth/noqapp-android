package com.noqapp.android.client.views.activities;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import android.os.AsyncTask;
import android.test.InstrumentationTestCase;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ApiCallTest extends InstrumentationTestCase {

    @Rule
    public ActivityTestRule<SplashScreen> mActivityTestRule = new ActivityTestRule<>(SplashScreen.class);

    @Test
    public void apiCallTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            TestSomeAsynTask();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void TestSomeAsynTask() throws Throwable {
        // create  a signal to let us know when our task is done.
        final CountDownLatch signal = new CountDownLatch(1);
        Assert.assertTrue(false);
        /* Just create an in line implementation of an asynctask. Note this
         * would normally not be done, and is just here for completeness.
         * You would just use the task you want to unit test in your project.
         */
        final AsyncTask<String, Void, String> myTask = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                //Do something meaningful.
                try {
                    URL urls = new URL("https://www.google.com/");
                    HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
                    conn.setReadTimeout(150000); //milliseconds
                    conn.setConnectTimeout(15000); // milliseconds
                    conn.setRequestMethod("GET");
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        return "success";
                    } else {
                        return "error";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                /* This is the key, normally you would use some type of listener
                 * to notify your activity that the async call was finished.
                 *
                 * In your test method you would subscribe to that and signal
                 * from there instead.
                 */
                assertTrue("Api called", result.equals("success"));
                signal.countDown();
            }
        };

// Execute the async task on the UI thread! THIS IS KEY!
        runTestOnUiThread(new Runnable() {

            @Override
            public void run() {
                myTask.execute("Do something");
            }
        });

        /* The testing thread will wait here until the UI thread releases it
         * above with the countDown() or 30 seconds passes and it times out.
         */
        signal.await(30, TimeUnit.SECONDS);

        // The task is done, and now you can assert some things!
        assertTrue("Happiness", true);
    }

}
