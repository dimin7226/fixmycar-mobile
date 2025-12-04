import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'api_service.dart';
import 'registration_password_screen.dart';
import 'package:google_fonts/google_fonts.dart';

class RegistrationUserDataScreen extends StatefulWidget {
  final String phone;

  const RegistrationUserDataScreen({super.key, required this.phone});

  @override
  State<RegistrationUserDataScreen> createState() => _RegistrationUserDataScreenState();
}

class _RegistrationUserDataScreenState extends State<RegistrationUserDataScreen> {
  final _emailController = TextEditingController();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();

  final _api = ApiService();
  bool _loading = false;
  String? _error;
  
  bool _isValidEmail(String email) {
    return RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(email);
  }

  Future<void> _submit() async {
    // Валидация полей
    if (_emailController.text.isEmpty || 
        _firstNameController.text.isEmpty || 
        _lastNameController.text.isEmpty) {
      setState(() => _error = "Заполните все поля");
      return;
    }
    
    if (!_isValidEmail(_emailController.text.trim())) {
      setState(() => _error = "Введите корректный email");
      return;
    }

    setState(() { 
      _loading = true; 
      _error = null; 
    });

    try {
      final ok = await _api.registerUserData(
        _emailController.text.trim(),
        _firstNameController.text.trim(),
        _lastNameController.text.trim(),
        widget.phone,
      );

      if (!mounted) return;

      if (ok) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => RegistrationPasswordScreen(
              email: _emailController.text.trim(),
            ),
          ),
        );
      } else {
        setState(() => _error = "Ошибка сохранения данных. Проверьте введенные данные.");
      }
    } catch (e) {
      setState(() => _error = "Произошла ошибка: $e");
    } finally {
      if (mounted) {
        setState(() => _loading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    final isSmallScreen = size.width < 360;

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(
            Icons.arrow_back_ios,
            color: Colors.black,
            size: 20,
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
        automaticallyImplyLeading: true,
        systemOverlayStyle: const SystemUiOverlayStyle(
          statusBarColor: Colors.white,
          statusBarBrightness: Brightness.light,
          statusBarIconBrightness: Brightness.dark,
        ),
      ),

      body: SafeArea(
        child: Stack(
          children: [
            // Фон с белой заливкой и скругленными углами
            Positioned.fill(
              child: Container(
                margin: const EdgeInsets.all(4),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                ),
              ),
            ),
            
            // Основной контент
            SingleChildScrollView(
              padding: EdgeInsets.symmetric(
                horizontal: size.width * 0.08,
                vertical: size.height * 0.05,
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Заголовок
                  SizedBox(
                    width: double.infinity,
                    child: Text(
                      'Данные клиента',
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.black,
                        fontSize: isSmallScreen ? 32 : 40,
                        height: 0.4,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.left,
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.08),
                  
                  // Поле для email
                  Container(
                    width: double.infinity,
                    height: 48,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      border: Border.all(
                        color: const Color(0xFFB3B3B3),
                      ),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: TextField(
                      controller: _emailController,
                      keyboardType: TextInputType.emailAddress,
                      decoration: InputDecoration(
                        hintText: 'Email',
                        hintStyle: GoogleFonts.balsamiqSans(
                          color: const Color(0xFF666666),
                          fontSize: 16,
                          height: 1.5,
                        ),
                        border: InputBorder.none,
                        contentPadding: const EdgeInsets.symmetric(
                          horizontal: 15,
                          vertical: 11,
                        ),
                      ),
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.black,
                        fontSize: 16,
                        height: 1.5,
                      ),
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.03),
                  
                  // Поле для имени
                  Container(
                    width: double.infinity,
                    height: 48,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      border: Border.all(
                        color: const Color(0xFFB3B3B3),
                      ),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: TextField(
                      controller: _firstNameController,
                      decoration: InputDecoration(
                        hintText: 'Имя',
                        hintStyle: GoogleFonts.balsamiqSans(
                          color: const Color(0xFF666666),
                          fontSize: 16,
                          height: 1.5,
                        ),
                        border: InputBorder.none,
                        contentPadding: const EdgeInsets.symmetric(
                          horizontal: 15,
                          vertical: 11,
                        ),
                      ),
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.black,
                        fontSize: 16,
                        height: 1.5,
                      ),
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.03),
                  
                  // Поле для фамилии
                  Container(
                    width: double.infinity,
                    height: 48,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      border: Border.all(
                        color: const Color(0xFFB3B3B3),
                      ),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: TextField(
                      controller: _lastNameController,
                      decoration: InputDecoration(
                        hintText: 'Фамилия',
                        hintStyle: GoogleFonts.balsamiqSans(
                          color: const Color(0xFF666666),
                          fontSize: 16,
                          height: 1.5,
                        ),
                        border: InputBorder.none,
                        contentPadding: const EdgeInsets.symmetric(
                          horizontal: 15,
                          vertical: 11,
                        ),
                      ),
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.black,
                        fontSize: 16,
                        height: 1.5,
                      ),
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.05),
                  
                  // Кнопка "Далее"
                  SizedBox(
                    width: double.infinity,
                    height: 48,
                    child: ElevatedButton(
                      onPressed: _loading ? null : _submit,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFFE1E1E1),
                        foregroundColor: const Color(0xFF303030),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(4),
                        ),
                        elevation: 0,
                      ),
                      child: _loading
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                valueColor: AlwaysStoppedAnimation<Color>(
                                  Color(0xFF303030),
                                ),
                              ),
                            )
                          : Text(
                              'ДАЛЕЕ',
                              style: GoogleFonts.balsamiqSans(
                                fontSize: 14,
                                height: 1.1,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                    ),
                  ),
                  
                  // Сообщение об ошибке
                  if (_error != null) ...[
                    SizedBox(height: size.height * 0.02),
                    Text(
                      _error!,
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.red,
                        fontSize: 14,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _emailController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    super.dispose();
  }
}