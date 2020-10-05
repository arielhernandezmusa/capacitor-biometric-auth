package com.ahm.capacitor.biometric;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.biometric.BiometricConstants;
import androidx.biometric.BiometricManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;

@NativePlugin()
public class BiometricAuth extends Plugin {

    private CancellationSignal cancellationSignal;

    @PluginMethod()
    public void isAvailable(PluginCall call) {
        JSObject ret = new JSObject();
        int result = isBiometricAvailable();
        ret.put("has",  result == BiometricManager.BIOMETRIC_SUCCESS);
        ret.put("status",result);
        call.resolve(ret);
    }

    @PluginMethod()
    public void verify(PluginCall call) {
        displayBiometricPrompt(call);
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(final PluginCall call) {
        Context context = getContext();
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate();
        if(canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS){
            //BiometricManager.hasBiometrics(context);
                BiometricPrompt.Builder biometricPromptBuilder = new BiometricPrompt.Builder(context)
                    .setTitle(call.getString("title", "Biometric"))
                    .setSubtitle(call.getString("subTitle", "Authentication is required to continue"))
                    .setDescription(call.getString("description", "This app uses biometric authentication to protect your data."));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                biometricPromptBuilder.setAllowedAuthenticators(
                    android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
                    | android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK
                    | android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
                );
                } else{
                biometricPromptBuilder.setNegativeButton(call.getString("cancel", "Cancel"), context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    JSObject result = new JSObject();
                    result.put("verified",false);
                    result.put("status","16"); // user cancels
                    call.reject(result.toString());
                    }
                });
                }
                BiometricPrompt biometricPrompt = biometricPromptBuilder.build();
                biometricPrompt.authenticate(getCancellationSignal(call), context.getMainExecutor(), getAuthenticationCallback(call));
        } else{
            JSObject result = new JSObject();
            result.put("verified",false);
            result.put("status",convertErrorCodeToPluginErrorCode(canAuthenticate)); // user cancels
            call.reject(result.toString());
        }
    }

    private int isBiometricAvailable(){
      BiometricManager biometricManager = BiometricManager.from(getContext());
      int result = biometricManager.canAuthenticate();
      return convertErrorCodeToPluginErrorCode(result);
    }

    private int convertErrorCodeToPluginErrorCode(int code){
      int errorCode = code;
      switch(code){
        // errorCode = 10 -"The user failed to provide valid credentials" :-N/A as operation will not cancel on wrong fingerprint , either max attempt is reached or user gets lockaedout
        // errorCode = 12	The context is invalid : N/A No such error by andorid

        case BiometricManager.BIOMETRIC_SUCCESS:
          errorCode = 0;
          break;
        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
          errorCode = 1; //Biometric not available due to hw
          break;
        case BiometricConstants.ERROR_LOCKOUT:
        case BiometricConstants.ERROR_LOCKOUT_PERMANENT:
          errorCode = 2;
          break;
        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
          errorCode = 3; //	Authentication could not start because the user has not enrolled in biometric authentication.
          break;
        case BiometricConstants.ERROR_NO_SPACE:
          errorCode = 11 ; // "Authentication was cancelled by application"
          break;
        case BiometricConstants.ERROR_TIMEOUT:
          errorCode = 13; // "Not interactive" - operation timedout due to non-interaction
          break;
        case BiometricConstants.ERROR_NO_DEVICE_CREDENTIAL:
          errorCode = 14 ;// "Passcode is not set on the device"
          break;
        case BiometricConstants.ERROR_CANCELED:
          errorCode = 15 ;// "Authentication was cancelled by the system happen when the user is switched, the device is locked or another pending operation prevents or disables it."
          break;
        case BiometricConstants.ERROR_NEGATIVE_BUTTON:
          errorCode = 16; // "The user did cancel"
          break;
        case BiometricConstants.ERROR_USER_CANCELED:
          errorCode = 17 ;// "The user chose to use the fallback"
          break;
        default:
          errorCode = code;  // unexpected error code
      }
      return errorCode;
    }
    @TargetApi(Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback(final PluginCall call) {

        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              CharSequence errString) {

                super.onAuthenticationError(errorCode, errString);
                JSObject ret = new JSObject();
                ret.put("verified", false);
                ret.put("status",convertErrorCodeToPluginErrorCode(errorCode));
                call.reject(ret.toString());
            }

            @Override
            public void onAuthenticationHelp(int helpCode,
                                             CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                JSObject ret = new JSObject();
                ret.put("verified", false);
                ret.put("status",convertErrorCodeToPluginErrorCode(helpCode));
                call.reject(ret.toString());
            }

            @Override
            public void onAuthenticationFailed() {
              // when the user is rejected, for example when a non-enrolled fingerprint is placed on the sensor
              super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                JSObject ret = new JSObject();
                ret.put("verified", true);
                call.resolve(ret);
            }
        };
    }

    private CancellationSignal getCancellationSignal(final PluginCall call) {

        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                JSObject ret = new JSObject();
                ret.put("verified", false);
                ret.put("status",16);
                call.reject(ret.toString());
            }
        });

        return cancellationSignal;
    }
}
