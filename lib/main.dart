import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'models/auth_status.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const AuthView(),
    );
  }
}

class AuthView extends StatefulWidget {
  const AuthView({Key? key}) : super(key: key);

  @override
  State<AuthView> createState() => _AuthViewState();
}

class _AuthViewState extends State<AuthView> {
  /// The current authentication status.
  AuthStatus authStatus = AuthStatus.idle;
  final platform = const MethodChannel('sample/biometrics');

  Future<void> authenticateWithBiometrics() async {
    try {
      await platform.invokeMethod('authenticateWithBiometrics');
      platform.setMethodCallHandler((call) async {
        if (call.method == 'authenticationResult') {
          if (call.arguments ?? false) {
            authStatus = AuthStatus.success;
          } else {
            authStatus = AuthStatus.failed;
          }
        }
        setState(() {
          authStatus = authStatus;
        });
      });
    } on PlatformException catch (e) {
      setState(() {
        authStatus = AuthStatus.failed;
        error = e.message.toString();
      });
    }
  }

  /// If not null, the error message coming from platform.
  String? error;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Biometrics Sample')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('Authentication status'),
            Chip(
              label: Text(
                authStatus.value.text,
                style: const TextStyle(color: Colors.white),
              ),
              backgroundColor: authStatus.value.color,
            ),
            TextButton(
              onPressed: authenticateWithBiometrics,
              child: const Text("Sign in"),
            ),
            if (error != null) Text(error!)
          ],
        ),
      ),
    );
  }
}
