//postgresql://fixmycar_user:uxY0d0hCcVRFEvraAZ9ZT8aPWiuSxJEV@dpg-d4oon1khg0os73dlckk0-a.oregon-postgres.render.com/fixmycar_zuqy
// uxY0d0hCcVRFEvraAZ9ZT8aPWiuSxJEV
// fixmycar_user
// fixmycar_zuqy
// dpg-d4oon1khg0os73dlckk0-a
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'service_center_model.dart';

class ApiService {
  final String baseUrl = "http://10.178.24.252:8080/api/home";

  Future<String?> login(String phone, String password) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/auth/login"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({'phone': phone, 'password': password}),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final token = data['token'];
        
        if (token != null) {
          final prefs = await SharedPreferences.getInstance();
          await prefs.setString('token', token);
          
          if (data['userId'] != null) {
            final userId = int.tryParse(data['userId'].toString());
            if (userId != null) {
              await prefs.setInt('customer_id', userId);
            }
          }
          
          if (data['name'] != null) {
            await prefs.setString('user_name', data['name']);
          }
        }
        return token;
      } else {
        throw Exception("Ошибка входа: ${response.statusCode}");
      }
    } catch (e) {
      print('Ошибка входа: $e');
      rethrow;
    }
  }

  Future<Map<String, dynamic>?> getCustomerProfile(int userId) async {
    try {
      final response = await http.get(
        Uri.parse("$baseUrl/customers/$userId"),
      );

      if (response.statusCode == 200) {
        // Явное приведение типа
        final dynamic decoded = json.decode(response.body);
        if (decoded is Map<String, dynamic>) {
          return decoded;
        } else {
          print('Unexpected response type: ${decoded.runtimeType}');
          return null;
        }
      } else {
        print('Profile error: ${response.statusCode} - ${response.body}');
        return null;
      }
    } catch (e) {
      print('Ошибка получения профиля клиента: $e');
      return null;
    }
  }

  Future<List<dynamic>> getUserCars(int userId) async {
    try {
      final response = await http.get(
        Uri.parse("$baseUrl/customers/$userId/cars"),
      );
      
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data;
      } else {
        throw Exception('Не удалось загрузить автомобили пользователей: ${response.statusCode}');
      }
    } catch (e) {
      print('Ошибка загрузки автомобилей: $e');
      rethrow;
    }
  }

  Future<bool> addCar(Map<String, dynamic> carData) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final userId = prefs.getInt('customer_id');
      
      if (userId == null) {
        throw Exception('User ID not found');
      }

      final Map<String, dynamic> requestBody = {
        'brand': carData['brand'] as String,
        'model': carData['model'] as String,
        'vin': carData['vin'] as String,
        'year': carData['year'],
        'customer': {'id': userId}
      };

      final response = await http.post(
        Uri.parse("$baseUrl/cars"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(requestBody),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        try {
          await getCustomerProfile(userId!);
        } catch (e) {
          print('Не удалось обновить профиль: $e');
        }
        return true;
      } else {
        throw Exception('Не удалось добавить автомобиль: ${response.statusCode} - ${response.body}');
      }
    } catch (e) {
      print('Ошибка при добавлении автомобиля: $e');
      rethrow;
    }
  }

  Future<bool> updateCar(int id, Map<String, dynamic> carData) async {
    try {
      final response = await http.put(
        Uri.parse("$baseUrl/cars/$id"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(carData),
      );

      return response.statusCode == 200;
    } catch (e) {
      print('Error updating car: $e');
      rethrow;
    }
  }

  Future<bool> deleteCar(int id) async {
    try {
      final response = await http.delete(
        Uri.parse("$baseUrl/cars/$id"),
      );

      return response.statusCode == 204;
    } catch (e) {
      print('Ошибка удаления автомобиля: $e');
      rethrow;
    }
  }

  Future<List<ServiceCenter>> getServiceCenters() async {
  try {
    print('Запрос сервисных центров по адресу: $baseUrl/service-centers');
    
    final response = await http.get(
      Uri.parse("$baseUrl/service-centers"),
      headers: {'Accept': 'application/json'},
    );

    print('Статус ответа: ${response.statusCode}');
    print('Тело ответа: ${response.body}');

    if (response.statusCode == 200) {
      try {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        print('Получено центров: ${data.length}');
        
        if (data.isEmpty) {
          print('Список сервисных центров пуст');
          // Возвращаем тестовые данные для демонстрации
          return [
            ServiceCenter(
              id: 1,
              name: 'Тестовый автосервис',
              address: 'ул. Тестовая, 1',
              phone: '+7 (999) 123-45-67',
              lat: 55.751244,
              lon: 37.618423,
            ),
          ];
        }
        
        return data.map((json) => ServiceCenter.fromJson(json)).toList();
      } catch (e) {
        print('Ошибка парсинга JSON: $e');
        // Возвращаем тестовые данные при ошибке парсинга
        return _getTestCenters();
      }
    } else {
      print('Ошибка HTTP: ${response.statusCode} - ${response.body}');
      // Возвращаем тестовые данные при ошибке сети
      return _getTestCenters();
    }
  } catch (e) {
    print('Ошибка getServiceCenters: $e');
    // Возвращаем тестовые данные при любой ошибке
    return _getTestCenters();
  }
}

// Метод для возврата тестовых данных
List<ServiceCenter> _getTestCenters() {
  return [
    ServiceCenter(
      id: 1,
      name: 'Автосервис "Мастер"',
      address: 'ул. Ленина, 10',
      phone: '+7 (495) 123-45-67',
      lat: 55.751244,
      lon: 37.618423,
    ),
    ServiceCenter(
      id: 2,
      name: 'СТО "Автодоктор"',
      address: 'пр. Мира, 25',
      phone: '+7 (495) 987-65-43',
      lat: 55.755244,
      lon: 37.628423,
    ),
    ServiceCenter(
      id: 3,
      name: 'Сервис "Быстрый ремонт"',
      address: 'ул. Пушкина, 15',
      phone: '+7 (495) 555-44-33',
      lat: 55.761244,
      lon: 37.608423,
    ),
  ];
}

  // Создание заявки на сервис
  Future<bool> createServiceRequest({
    required int customerId,
    required int carId,
    required int serviceCenterId,
    required String description,
  }) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/requests?customerId=$customerId&carId=$carId&serviceCenterId=$serviceCenterId&description=${Uri.encodeComponent(description)}"),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        return true;
      } else {
        print('Ошибка создания заявки: ${response.statusCode} - ${response.body}');
        return false;
      }
    } catch (e) {
      print('Ошибка createServiceRequest: $e');
      return false;
    }
  }

  Future<List<dynamic>> getServiceRequests() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    final customerId = prefs.getInt('customer_id');
    
    if (customerId == null) {
      throw Exception("User ID not found");
    }

    final response = await http.get(
      Uri.parse("$baseUrl/requests/customer/$customerId"),
      headers: {'Authorization': 'Bearer $token'},
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception("Ошибка загрузки заявок: ${response.statusCode}");
    }
  }

  Future<int?> registerPhone(String phone) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/auth/register/phone"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({'phone': phone}),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final tempUserId = data['tempUserId'];
        return tempUserId;
      }
      return null;
    } catch (e) {
      print('💥 Ошибка регистрации телефона: $e');
      return null;
    }
  }

  Future<bool> registerUserData(String email, String firstName, String lastName, String phone) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final tempUserId = prefs.getInt('tempUserId');

      if (tempUserId == null) {
        print('tempUserId не найден в SharedPreferences');
        return false;
      }

      final response = await http.post(
        Uri.parse("$baseUrl/auth/register/profile"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': tempUserId,
          'email': email,
          'firstName': firstName,
          'lastName': lastName,
        }),
      );

      return response.statusCode == 200;
    } catch (e) {
      print('Исключение: $e');
      return false;
    }
  }

  Future<bool> registerPassword(String email, String password) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final tempUserId = prefs.getInt('tempUserId');

      if (tempUserId == null) {
        print('tempUserId не найден');
        return false;
      }

      final response = await http.post(
        Uri.parse("$baseUrl/auth/register/password"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': tempUserId,
          'password': password,
          'repeatPassword': password,
        }),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final token = data['token'];
        final userId = data['userId'];
        
        await prefs.setString('token', token);
        await prefs.setInt('customer_id', userId);
        await prefs.remove('tempUserId');
        
        return true;
      } else {
        return false;
      }
    } catch (e) {
      print('💥 Ошибка установки пароля: $e');
      return false;
    }
  }
}