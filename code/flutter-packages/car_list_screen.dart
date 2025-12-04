import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'api_service.dart';
import 'add_car_screen.dart';
import 'edit_car_screen.dart';
import 'login_screen.dart';
import 'service_requests_screen.dart';
import 'service_map_screen.dart';

class CarListScreen extends StatefulWidget {
  const CarListScreen({super.key});

  @override
  State<CarListScreen> createState() => _CarListScreenState();
}

class _CarListScreenState extends State<CarListScreen> {
  final ApiService api = ApiService();
  Future<List<dynamic>>? carsFuture;
  Map<String, dynamic>? userProfile;
  bool isLoadingProfile = false;
  int? userId;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  Future<void> _loadUserData() async {
    final prefs = await SharedPreferences.getInstance();
    final loadedUserId = prefs.getInt('customer_id');
    
    if (loadedUserId != null) {
      setState(() {
        userId = loadedUserId;
      });
      await _refreshAllData();
    } else {
      _logout();
    }
  }

  Future<void> _refreshAllData() async {
    setState(() {
      isLoadingProfile = true;
    });
    
    try {
      final profileFuture = api.getCustomerProfile(userId!);
      final carsFutureData = api.getUserCars(userId!);
      
      final results = await Future.wait([profileFuture, carsFutureData]);
      
      setState(() {
        userProfile = results[0] as Map<String, dynamic>?;
        carsFuture = Future.value(results[1] as List<dynamic>);
        isLoadingProfile = false;
      });
    } catch (e) {
      print('Ошибка загрузки данных.: $e');
      setState(() {
        isLoadingProfile = false;
      });
    }
  }

  Future<void> _refreshData() async {
    if (userId != null) {
      setState(() {
        carsFuture = api.getUserCars(userId!);
      });
      
      try {
        final profile = await api.getCustomerProfile(userId!);
        setState(() {
          userProfile = profile;
        });
      } catch (e) {
        print('Error refreshing profile: $e');
      }
    }
  }

   Future<void> _forceRefreshData() async {
    if (userId != null) {
      setState(() {
        isLoadingProfile = true;
      });
      
      try {
        final profile = await api.getCustomerProfile(userId!);
        final cars = await api.getUserCars(userId!);

        setState(() {
          userProfile = profile;
          carsFuture = Future.value(cars);
          isLoadingProfile = false;
        });
      } catch (e) {
        setState(() {
          isLoadingProfile = false;
        });
        print('Error force refreshing: $e');
      }
    }
  }

  Future<void> _logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    
    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (_) => const LoginScreen()),
      (route) => false,
    );
  }

  void _showCarDetails(Map<String, dynamic> car) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => CarDetailsSheet(car: car),
    );
  }
  
  Widget _buildCarCard(Map<String, dynamic> car, int index) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: () => _showCarDetails(car),
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    '${car['brand']} ${car['model']}',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  if (car['year'] != null)
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.blue.shade100,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        car['year'].toString(),
                        style: const TextStyle(
                          color: Colors.blue,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                ],
              ),
              const SizedBox(height: 12),
              _buildCarInfoRow('VIN:', car['vin'] ?? '—'),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: () async {
                        final updated = await Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => EditCarScreen(car: car),
                          ),
                        );
                        if (updated == true) await _forceRefreshData();
                      },
                      icon: const Icon(Icons.edit, size: 18),
                      label: const Text('Редактировать'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.grey.shade100,
                        foregroundColor: Colors.grey.shade800,
                        elevation: 0,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: () async {
                        await _confirmDeleteCar(car['id']);
                      },
                      icon: const Icon(Icons.delete, size: 18),
                      label: const Text('Удалить'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red.shade50,
                        foregroundColor: Colors.red,
                        elevation: 0,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCarInfoRow(String label, String value) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: TextStyle(
            color: Colors.grey.shade600,
            fontWeight: FontWeight.w500,
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: Text(
            value,
            style: const TextStyle(
              fontWeight: FontWeight.w500,
            ),
          ),
        ),
      ],
    );
  }

  Future<void> _confirmDeleteCar(int carId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Удалить автомобиль'),
        content: const Text('Вы уверены, что хотите удалить этот автомобиль?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Отмена'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text(
              'Удалить',
              style: TextStyle(color: Colors.red),
            ),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        final success = await api.deleteCar(carId);
        if (success) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Автомобиль удален'),
              backgroundColor: Colors.green,
            ),
          );
          // Используем force refresh для полного обновления
          await _forceRefreshData();
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Widget _buildUserInfo() {
    if (isLoadingProfile) {
      return Padding(
        padding: const EdgeInsets.all(16),
        child: Center(child: CircularProgressIndicator()),
      );
    }

    if (userProfile == null) {
      return Container(
        margin: const EdgeInsets.all(16),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Colors.grey.withOpacity(0.2),
              blurRadius: 10,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Column(
          children: [
            Icon(
              Icons.error_outline,
              size: 48,
              color: Colors.orange.shade400,
            ),
            const SizedBox(height: 12),
            const Text(
              'Не удалось загрузить профиль',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'User ID: ${userId ?? "не найден"}',
              style: TextStyle(
                color: Colors.grey.shade600,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _forceRefreshData,
              child: const Text('Повторить загрузку'),
            ),
          ],
        ),
      );
    }

    // Безопасное получение данных
    final firstName = userProfile!['firstName'] as String? ?? 'Имя';
    final lastName = userProfile!['lastName'] as String? ?? 'Фамилия';
    final email = userProfile!['email'] as String? ?? '—';
    final phone = userProfile!['phone'] as String? ?? '—';
    
    // Получаем актуальное количество автомобилей
    final cars = userProfile!['cars'];
    final carsCount = cars is List ? cars.length : 0;
    
    final serviceRequests = userProfile!['serviceRequests'];
    final requestsCount = serviceRequests is List ? serviceRequests.length : 0;

    return Container(
      margin: const EdgeInsets.all(16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.2),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 30,
                backgroundColor: Colors.blue.shade100,
                child: Icon(
                  Icons.person,
                  size: 30,
                  color: Colors.blue.shade600,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '$firstName $lastName',
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      email,
                      style: TextStyle(color: Colors.grey.shade600),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      phone,
                      style: TextStyle(color: Colors.grey.shade600),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Divider(color: Colors.grey.shade300),
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                children: [
                  Text(
                    carsCount.toString(),
                    style: const TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Colors.blue,
                    ),
                  ),
                  Text(
                    'автомобилей',
                    style: TextStyle(color: Colors.grey.shade600),
                  ),
                ],
              ),
              Column(
                children: [
                  Text(
                    requestsCount.toString(),
                    style: const TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Colors.green,
                    ),
                  ),
                  Text(
                    'заявок',
                    style: TextStyle(color: Colors.grey.shade600),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCarList() {
    return FutureBuilder<List<dynamic>>(
      future: carsFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Icon(
                  Icons.error_outline,
                  size: 48,
                  color: Colors.red.shade300,
                ),
                const SizedBox(height: 16),
                Text(
                  'Ошибка загрузки автомобилей: ${snapshot.error}',
                  textAlign: TextAlign.center,
                  style: TextStyle(color: Colors.grey.shade600),
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: _forceRefreshData,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: const Text(
                    'Повторить',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ],
            ),
          );
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return Container(
            margin: const EdgeInsets.all(16),
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: Colors.grey.withOpacity(0.1),
                  blurRadius: 8,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            child: Column(
              children: [
                Icon(
                  Icons.directions_car_outlined,
                  size: 64,
                  color: Colors.grey.shade400,
                ),
                const SizedBox(height: 16),
                Text(
                  'Нет автомобилей',
                  style: TextStyle(
                    fontSize: 18,
                    color: Colors.grey.shade600,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Добавьте ваш первый автомобиль',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: Colors.grey.shade500,
                  ),
                ),
                const SizedBox(height: 24),
                ElevatedButton.icon(
                  onPressed: () async {
                    final added = await Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) => const AddCarScreen(),
                      ),
                    );
                    if (added == true) await _forceRefreshData();
                  },
                  icon: const Icon(Icons.add),
                  label: const Text('Добавить автомобиль'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                    padding: const EdgeInsets.symmetric(
                      horizontal: 24,
                      vertical: 12,
                    ),
                  ),
                ),
              ],
            ),
          );
        } else {
          final cars = snapshot.data!;
          return ListView.builder(
            physics: const NeverScrollableScrollPhysics(),
            shrinkWrap: true,
            itemCount: cars.length,
            itemBuilder: (context, index) {
              return _buildCarCard(cars[index], index);
            },
          );
        }
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey.shade50,
      body: RefreshIndicator(
        onRefresh: _forceRefreshData,
        child: CustomScrollView(
          slivers: [
            SliverAppBar(
              expandedHeight: 120,
              floating: true,
              pinned: true,
              backgroundColor: Colors.white,
              elevation: 0,
              flexibleSpace: FlexibleSpaceBar(
                title: Text(
                  'Мои автомобили',
                  style: TextStyle(
                    color: Colors.grey.shade800,
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                centerTitle: true,
                background: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [
                        Colors.blue.shade50,
                        Colors.grey.shade50,
                      ],
                    ),
                  ),
                ),
              ),
            actions: [
              // Кнопка "Найти сервис" в AppBar (только если есть автомобили)
              if (carsFuture != null)
                FutureBuilder<List<dynamic>>(
                  future: carsFuture,
                  builder: (context, snapshot) {
                    if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                      // Берем первый автомобиль или можно сделать выбор
                      final firstCar = snapshot.data!.first;
                      return IconButton(
                        icon: Icon(Icons.location_on, color: Colors.grey.shade700),
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (_) => ServiceMapScreen(
                                customerId: userId!,
                                carId: firstCar['id'],
                              ),
                            ),
                          );
                        },
                        tooltip: 'Найти сервис',
                      );
                    }
                    return Container(); // Пустой виджет если нет авто
                  },
                ),
              IconButton(
                icon: Icon(Icons.receipt_long, color: Colors.grey.shade700),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const ServiceRequestsScreen(),
                    ),
                  );
                },
                tooltip: 'Мои заявки',
              ),
              IconButton(
                icon: Icon(Icons.refresh, color: Colors.grey.shade700),
                onPressed: _forceRefreshData,
                tooltip: 'Обновить',
              ),
              IconButton(
                icon: Icon(Icons.logout, color: Colors.grey.shade700),
                onPressed: _logout,
                tooltip: 'Выйти',
              ),
            ],
            ),
            SliverList(
              delegate: SliverChildListDelegate([
                _buildUserInfo(),
                const SizedBox(height: 8),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: Text(
                    'Список автомобилей',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: Colors.grey.shade700,
                    ),
                  ),
                ),
                const SizedBox(height: 8),
                _buildCarList(),
              ]),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async {
          final added = await Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const AddCarScreen()),
          );
          if (added == true) await _forceRefreshData();
        },
        icon: const Icon(Icons.add),
        label: const Text('Добавить'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
        elevation: 4,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
      ),
    );
  }
}

class CarDetailsSheet extends StatelessWidget {
  final Map<String, dynamic> car;

  const CarDetailsSheet({super.key, required this.car});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Center(
            child: Container(
              width: 60,
              height: 4,
              decoration: BoxDecoration(
                color: Colors.grey.shade300,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
          const SizedBox(height: 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                '${car['brand']} ${car['model']}',
                style: const TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              if (car['year'] != null)
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: Colors.blue.shade100,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    '${car['year']} г.',
                    style: TextStyle(
                      color: Colors.blue.shade800,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 24),
          _buildDetailRow('Марка:', car['brand'] ?? '—'),
          const SizedBox(height: 12),
          _buildDetailRow('Модель:', car['model'] ?? '—'),
          const SizedBox(height: 12),
          _buildDetailRow('VIN код:', car['vin'] ?? '—'),
          const SizedBox(height: 12),
          if (car['customer'] != null && car['customer'] is Map) ...[
            const Divider(),
            const SizedBox(height: 8),
            Text(
              'Владелец',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w600,
                color: Colors.grey.shade600,
              ),
            ),
            const SizedBox(height: 8),
            _buildDetailRow(
              'Имя:',
              '${(car['customer'] as Map)['firstName']} ${(car['customer'] as Map)['lastName']}',
            ),
            const SizedBox(height: 8),
            _buildDetailRow('Телефон:', (car['customer'] as Map)['phone'] ?? '—'),
            const SizedBox(height: 8),
            _buildDetailRow('Email:', (car['customer'] as Map)['email'] ?? '—'),
          ],
          const SizedBox(height: 32),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () => Navigator.pop(context),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.grey.shade100,
                foregroundColor: Colors.grey.shade800,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text('Закрыть'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          flex: 2,
          child: Text(
            label,
            style: TextStyle(
              color: Colors.grey.shade600,
              fontWeight: FontWeight.w500,
            ),
          ),
        ),
        Expanded(
          flex: 3,
          child: Text(
            value,
            style: const TextStyle(
              fontWeight: FontWeight.w500,
            ),
          ),
        ),
      ],
    );
  }
}