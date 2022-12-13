import 'dart:convert';

import 'package:first_app/answer.dart';
import 'package:first_app/question.dart';
import 'package:first_app/quize.dart';
import 'package:first_app/result.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_broadcast_receiver/flutter_broadcast_receiver.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _MyAppState();
  }
}

class _MyAppState extends State<MyApp> {
  var text = 'Пример';
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
      //_getMsg();
  }
  @override
  Widget build(BuildContext context) {
  _getMsg();
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Scanner'),
        ),
        body: Center(
          child: Text(text),
        ),
      ),
    );
  }

  void _getMsg() {
    const EventChannel _stream = EventChannel('channel/scanners');
    _stream.receiveBroadcastStream().listen(
      (data) {
        var barcode = Barcode.fromJson(jsonDecode(data));
        setState(() {
          text = barcode.barcode;
        });
      },
    );
  }
}

class Barcode {
  const Barcode(this.tsd, this.barcode);
  final String tsd;
  final String barcode;

  Barcode.fromJson(Map<String, dynamic> json)
      : tsd = json['tsd'],
        barcode = json['barcode'];
}
