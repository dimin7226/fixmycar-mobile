class ServiceCenter {
  final int id;
  final String name;
  final String address;
  final String phone;
  final double lat;
  final double lon;
  
  ServiceCenter({
    required this.id,
    required this.name,
    required this.address,
    required this.phone,
    required this.lat,
    required this.lon,
  });
  
  factory ServiceCenter.fromJson(Map<String, dynamic> json) {
    // ДЕБАГ: выводим что приходит от сервера
    print('ServiceCenter JSON: $json');
    
    // Пробуем разные варианты названий полей
    final lat = json['latitude'] ?? json['lat'] ?? json['location']?['lat'] ?? 55.751244;
    final lon = json['longitude'] ?? json['lon'] ?? json['location']?['lon'] ?? 37.618423;
    
    return ServiceCenter(
      id: json['id']?.toInt() ?? 0,
      name: json['name'] ?? 'Неизвестный сервис',
      address: json['address'] ?? 'Адрес не указан',
      phone: json['phone'] ?? 'Телефон не указан',
      lat: (lat is num) ? lat.toDouble() : 55.751244,
      lon: (lon is num) ? lon.toDouble() : 37.618423,
    );
  }
}