import 'package:flutter/material.dart';
import 'package:yandex_mapkit/yandex_mapkit.dart';
import 'api_service.dart';
import 'service_center_model.dart';

class ServiceMapScreen extends StatefulWidget {
  final int customerId;
  final int carId;

  const ServiceMapScreen({super.key, required this.customerId, required this.carId});

  @override
  State<ServiceMapScreen> createState() => _ServiceMapScreenState();
}

class _ServiceMapScreenState extends State<ServiceMapScreen> {
  final ApiService api = ApiService();
  List<ServiceCenter> centers = [];
  List<PlacemarkMapObject> mapObjects = [];
  YandexMapController? _mapController;

  @override
  void initState() {
    super.initState();
    _loadServiceCenters();
  }

  @override
  void dispose() {
    _mapController?.dispose();
    super.dispose();
  }

  Future<void> _loadServiceCenters() async {
    try {
      final data = await api.getServiceCenters();
      setState(() {
        centers = data;
        _createMapObjects();
      });
      
      if (_mapController != null && centers.isNotEmpty) {
        await _moveToFirstCenter();
      }
    } catch (e) {
      print('Ошибка загрузки сервисов: $e');
    }
  }

  void _createMapObjects() {
    mapObjects = centers.map((center) {
      return PlacemarkMapObject(
        mapId: MapObjectId('service_${center.id}'),
        point: Point(latitude: center.lat, longitude: center.lon),
        icon: PlacemarkIcon.single(
          PlacemarkIconStyle(
            image: BitmapDescriptor.fromAssetImage('assets/pin.png'),
            scale: 1.0,
          ),
        ),
        opacity: 1.0,
        onTap: (PlacemarkMapObject mapObject, Point point) async {
          await _showServiceCenterInfo(center);
        },
      );
    }).toList();
  }

  Future<void> _moveToFirstCenter() async {
    if (centers.isNotEmpty && _mapController != null) {
      await _mapController!.moveCamera(
        CameraUpdate.newCameraPosition(
          CameraPosition(
            target: Point(
              latitude: centers.first.lat,
              longitude: centers.first.lon,
            ),
            zoom: 12,
          ),
        ),
        animation: const MapAnimation(duration: 1.0),
      );
    }
  }

  Future<void> _showServiceCenterInfo(ServiceCenter center) async {
    await showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              center.name,
              style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 10),
            Text('📍 ${center.address}'),
            const SizedBox(height: 5),
            Text('📞 ${center.phone}'),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () async {
                  Navigator.pop(context);
                  await _createServiceRequest(center);
                },
                child: const Text('Записаться'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _createServiceRequest(ServiceCenter center) async {
    try {
      final success = await api.createServiceRequest(
        customerId: widget.customerId,
        carId: widget.carId,
        serviceCenterId: center.id,
        description: 'Запись через мобильное приложение',
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(success ? '✅ Заявка создана!' : '❌ Ошибка'),
            backgroundColor: success ? Colors.green : Colors.red,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Карта автосервисов'),
      ),
      body: Stack(
        children: [
          YandexMap(
            mapObjects: mapObjects,
            onMapCreated: (YandexMapController controller) async {
              _mapController = controller;
              
              if (centers.isNotEmpty) {
                await _moveToFirstCenter();
              }
            },
          ),
          if (centers.isEmpty)
            const Center(
              child: CircularProgressIndicator(),
            ),
        ],
      ),
      floatingActionButton: centers.isNotEmpty
          ? FloatingActionButton(
              onPressed: () async {
                if (_mapController != null && centers.isNotEmpty) {
                  await _moveToFirstCenter();
                }
              },
              child: const Icon(Icons.center_focus_strong),
            )
          : null,
    );
  }
}