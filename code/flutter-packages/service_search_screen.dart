import 'package:flutter/material.dart';

class ServiceSearchScreen extends StatelessWidget {
  const ServiceSearchScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Поиск автосервисов'),
        backgroundColor: Colors.blue,
      ),
      body: Center(
        child: Text(
          'Здесь будет поиск автосервисов',
          style: TextStyle(fontSize: 18, color: Colors.grey.shade700),
        ),
      ),
    );
  }
}
