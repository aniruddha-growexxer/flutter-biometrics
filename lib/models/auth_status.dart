import 'package:flutter/material.dart';

class AuthStatusItem {
  const AuthStatusItem({
    required this.color,
    required this.text,
  });
  final Color color;
  final String text;
}

enum AuthStatus {
  idle(AuthStatusItem(color: Colors.blueGrey, text: 'Not started')),
  success(AuthStatusItem(color: Colors.green, text: 'Successful!')),
  failed(AuthStatusItem(color: Colors.red, text: 'Failed!'));

  final AuthStatusItem value;
  const AuthStatus(this.value);
}