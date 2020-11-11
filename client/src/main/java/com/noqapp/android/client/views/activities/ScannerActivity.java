package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AuthenticateClientInQueueApiCalls;
import com.noqapp.android.client.presenter.ClientInQueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonInQueuePerson;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.TokenStatusUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.ScannerFragment;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.utils.CustomProgressBar;
import com.noqapp.android.common.utils.PermissionUtils;

import org.apache.commons.lang3.StringUtils;

public class ScannerActivity extends AppCompatActivity implements
    ClientInQueuePresenter {
    public static final int RC_BARCODE_CAPTURE = 23;
    private final String TAG = ScannerActivity.class.getSimpleName();
    private int requestCode;
    private CustomProgressBar customProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customProgressBar = new CustomProgressBar(this);
        requestCode = scanCodeQRType();
    }

    protected void startScanningBarcode() {
        if (PermissionUtils.isCameraAndStoragePermissionAllowed(this)) {
            scanBarcode();
        } else {
            requestCameraAndStoragePermission();
        }
    }

    private void scanBarcode() {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        startActivityForResult(intent, requestCode);
    }

    private void requestCameraAndStoragePermission() {
        ActivityCompat.requestPermissions(this,
            PermissionUtils.CAMERA_AND_STORAGE_PERMISSIONS,
            PermissionUtils.PERMISSION_REQUEST_CAMERA_AND_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CAMERA_AND_STORAGE) {
            try {
                //both remaining permission allowed
                if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    scanBarcode();
                } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//one remaining permission allowed
                    scanBarcode();
                } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //No permission allowed
                    //Do nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                Log.v(TAG, "Scanned CodeQR=" + contents);
                if (StringUtils.isBlank(contents)) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    if (requestCode == RC_BARCODE_CAPTURE) {
                        if (contents.startsWith("https://q.noqapp.com")) {
                            try {
                                if (contents.endsWith(MessageOriginEnum.AU.name())) {
                                    String[] codeQR = contents.split("https://q.noqapp.com/");
                                    String[] scanData = codeQR[1].split("#");
                                    qrCodeResult(scanData);
                                    Log.d("Scan Result", codeQR[1]);
                                } else {
                                    String[] codeQR = contents.split("/");
                                    //ends with - q.htm, o.htm or b.htm
                                    //to define if we need to show category screen or join screen or order screen
                                    barcodeResult(codeQR[3], contents);
                                    Bundle params = new Bundle();
                                    params.putString("codeQR", codeQR[3]);
                                    AppInitialize.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_SCAN_STORE_CODE_QR_SCREEN, params);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Failed parsing codeQR reason=" + e.getLocalizedMessage(), e);
                            }
                        } else {
                            Toast toast = Toast.makeText(this, getString(R.string.error_qrcode_scan), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        // do nothing
                    }
                }
                Log.d(TAG, "Barcode read: " + contents);
            } else {
                // statusMessage.setText(R.string.barcode_failure);
                Log.d(TAG, "No barcode captured, intent data is null");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private int scanCodeQRType() {
        return ScannerFragment.RC_BARCODE_CAPTURE;
    }

    private void barcodeResult(String codeQR, String contents) {
        if (contents.endsWith("b.htm")) {
            Intent in = new Intent(this, CategoryInfoActivity.class);
            Bundle b = new Bundle();
            b.putString(IBConstant.KEY_CODE_QR, codeQR);
            b.putBoolean(IBConstant.KEY_FROM_LIST, false);
            b.putBoolean(IBConstant.KEY_IS_TEMPLE, false);
            in.putExtra("bundle", b);
            startActivity(in);
        } else if (contents.endsWith("o.htm")) {
            //Show orders instead
        } else {
            Intent in = new Intent(this, BeforeJoinActivity.class);
            in.putExtra(IBConstant.KEY_CODE_QR, codeQR);
            in.putExtra(IBConstant.KEY_FROM_LIST, false);
            in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
            startActivity(in);
        }
    }

    private void qrCodeResult(String[] scanData) {
        if (scanData.length > 2) {
            AuthenticateClientInQueueApiCalls authenticateClientInQueueApiCalls = new AuthenticateClientInQueueApiCalls(this);
            customProgressBar.showProgress();
            customProgressBar.setProgressMessage("Validating token...");
            authenticateClientInQueueApiCalls.clientInQueue(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth(),
                scanData[0],
                scanData[1]);
        }
    }

    @Override
    public void clientInQueueResponse(JsonInQueuePerson jsonInQueuePerson) {
        Log.e("JsonInQueuePerson", jsonInQueuePerson.toString());
        displayValidUserDialog(jsonInQueuePerson);
    }

    @Override
    public void clientInQueueErrorPresenter(ErrorEncounteredJson eej) {
        Log.e("JsonInQueuePerson error", eej.toString());
        ShowAlertInformation.showInfoDisplayDialog(this, "Invalid Token", "This token is not valid to queue");
    }

    private void displayValidUserDialog(JsonInQueuePerson jsonInQueuePerson) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_valid_user);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_store_name = dialog.findViewById(R.id.tv_store_name);
        TextView tv_user_name = dialog.findViewById(R.id.tv_user_name);
        TextView tv_user_token = dialog.findViewById(R.id.tv_user_token);
        TextView tv_user_status = dialog.findViewById(R.id.tv_user_status);
        TextView tv_user_time_slot = dialog.findViewById(R.id.tv_user_time_slot);
        tv_store_name.setText(jsonInQueuePerson.getDisplayName().trim());
        tv_user_name.setText(TextUtils.isEmpty(jsonInQueuePerson.getCustomerName()) ? "Guest User" : jsonInQueuePerson.getCustomerName());
        tv_user_token.setText(String.valueOf(jsonInQueuePerson.getToken()));
        tv_user_status.setText(jsonInQueuePerson.getQueueUserState().getDescription());
        tv_user_time_slot.setText(TokenStatusUtils.timeSlot(jsonInQueuePerson.getExpectedServiceBegin()));
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        btnPositive.setOnClickListener((View v) -> dialog.dismiss());
        //dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        /* dismissProgress(); no progress bar silent call here */
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        /* dismissProgress(); no progress bar silent call here */
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing(this);
    }
}
