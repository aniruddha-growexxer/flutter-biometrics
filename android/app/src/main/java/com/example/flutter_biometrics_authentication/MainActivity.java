package com.example.flutter_biometrics_authentication;

import io.flutter.embedding.android.FlutterFragmentActivity;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_UNKNOWN;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import io.flutter.embedding.android.FlutterFragmentActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterFragmentActivity {
private val channelName = "samples/biometrics"
private lateinit var biometricsChannel: MethodChannel

override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
super.configureFlutterEngine(flutterEngine)

    // Initialize the method channel once the Flutter engines is attached.s
    biometricsChannel = MethodChannel(
        flutterEngine.dartExecutor.binaryMessenger,
        channelName
    );

    // Set a method call handler, whenever we invoke a method from Flutter
    // on "samples.invertase.io/biometrics" channel, this handler will be triggered.
    biometricsChannel.setMethodCallHandler { call, result ->
        if (call.method == "authenticateWithBiometrics") {
            authenticateWithBiometrics(result)
        } else {
            result.notImplemented()
        }
    }

    private fun authenticateWithBiometrics(@NonNull result: MethodChannel.Result) {
    var resultSent = false;
    val executor = ContextCompat.getMainExecutor(this)
    val biometricPrompt = BiometricPrompt(
        this as FragmentActivity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != 10 && !resultSent) {
                    result.error("AUTHFAILED", errString.toString(), null)
                    resultSent = true;
                }
            }

            override fun onAuthenticationSucceeded(
                authResult: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(authResult)
                if (!resultSent) {
                    result.success(null)
                    resultSent = true;
                }
                biometricsChannel.invokeMethod(
                    "authenticationResult",
                    authResult.authenticationType != AUTHENTICATION_RESULT_TYPE_UNKNOWN
                )
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                if (!resultSent) {
                    result.error("AUTHFAILED", "Unknown", null)
                    resultSent = true;
                }
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric login")
        .setSubtitle("Log in using your biometric credential")
        .setConfirmationRequired(false)
        .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        .build()

    biometricPrompt.authenticate(promptInfo)
    }
}
}
