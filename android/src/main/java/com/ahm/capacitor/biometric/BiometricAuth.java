package com.ahm.capacitor.biometric;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

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
import java.util.concurrent.Executors;

import javax.crypto.KeyGenerator;

@NativePlugin()
public class BiometricAuth extends Plugin {

    private CancellationSignal cancellationSignal;

    @PluginMethod()
    public void isAvailable(PluginCall call) {
        JSObject ret = new JSObject();


        ret.put("has", isBiometryAvailable());
        call.resolve(ret);
    }

    @PluginMethod()
    public void verify(PluginCall call) {
        displayBiometricPrompt(call);
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(final PluginCall call) {
        Context context = getContext();

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle(call.getString("title", "Biometric"))
                .setSubtitle(call.getString("subTitle", "Authentication is required to continue"))
                .setDescription(call.getString("description", "This app uses biometric authentication to protect your data."))
                .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        call.reject("failed");
                    }
                })
                .build();
        biometricPrompt.authenticate(getCancellationSignal(call), context.getMainExecutor(), getAuthenticationCallback(call));
    }

    private boolean isBiometryAvailable() {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            return false;
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            return false;
        }

        if (keyGenerator == null || keyStore == null) {
            return false;
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder("dummy_key",
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            return false;
        }
        return true;

    }

    @TargetApi(Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback(final PluginCall call) {

        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              CharSequence errString) {

                super.onAuthenticationError(errorCode, errString);
                call.reject("failed");
            }

            @Override
            public void onAuthenticationHelp(int helpCode,
                                             CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                call.reject("failed");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                call.reject("failed");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                JSObject ret = new JSObject();
                ret.put("auth", true);
                call.resolve(ret);
            }
        };
    }

    private CancellationSignal getCancellationSignal(final PluginCall call) {

        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                call.reject("failed");
            }
        });

        return cancellationSignal;
    }
}
