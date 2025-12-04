import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'api_service.dart';
import 'main.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:google_fonts/google_fonts.dart';
import 'registration_phone_screen.dart';
import 'car_list_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _phoneController = TextEditingController();
  final _passwordController = TextEditingController();
  final _api = ApiService();
  bool _isLoading = false;
  String? _error;

  Future<void> _login() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final token = await _api.login(
        _phoneController.text.trim(),
        _passwordController.text.trim(),
      );
      if (token != null) {
        final prefs = await SharedPreferences.getInstance();
        if (!mounted) return;
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (_) => const CarListScreen()),
        );
      } else {
        setState(() => _error = "Неверный номер телефона или пароль");
      }
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _navigateToRegistration() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const RegistrationPhoneScreen()),
    );
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
        automaticallyImplyLeading: false,
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
                      'Вход',
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
                  
                  // Поле для номера телефона
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
                      controller: _phoneController,
                      decoration: InputDecoration(
                        hintText: 'Номер телефона',
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
                  
                  // Поле для пароля
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
                      controller: _passwordController,
                      obscureText: true,
                      decoration: InputDecoration(
                        hintText: 'Пароль',
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
                  
                  SizedBox(height: size.height * 0.02),
                  
                  // Ссылка "Забыли пароль?"
                  Align(
                    alignment: Alignment.centerRight,
                    child: GestureDetector(
                      onTap: () {
                        // Добавьте навигацию на экран восстановления пароля
                      },
                      child: Text(
                        'Забыли пароль?',
                        style: GoogleFonts.balsamiqSans(
                          color: const Color(0xFF2F6EE2),
                          fontSize: isSmallScreen ? 11 : 12,
                          fontWeight: FontWeight.bold,
                          height: 1.3,
                        ),
                      ),
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.05),
                  
                  // Кнопка входа
                  SizedBox(
                    width: double.infinity,
                    height: 48,
                    child: ElevatedButton(
                      onPressed: _isLoading ? null : _login,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFFE1E1E1),
                        foregroundColor: const Color(0xFF303030),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(4),
                        ),
                        elevation: 0,
                      ),
                      child: _isLoading
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
                              'ВОЙТИ',
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
                      style: const TextStyle(
                        color: Colors.red,
                        fontSize: 14,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                  
                  SizedBox(height: size.height * 0.05),
                  
                  // Текст "Ещё нет аккаунта?"
                  Center(
                    child: Text(
                      'Ещё нет аккаунта?',
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.black,
                        fontSize: isSmallScreen ? 11 : 12,
                        height: 1.3,
                      ),
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.01),
                  
                  // Кнопка "ЗАРЕГИСТРИРОВАТЬСЯ"
                  Center(
                    child: InkWell(
                      onTap: _navigateToRegistration,
                      child: Text(
                        'ЗАРЕГИСТРИРОВАТЬСЯ',
                        style: GoogleFonts.balsamiqSans(
                          color: const Color(0xFF2F6EE2),
                          fontSize: isSmallScreen ? 13 : 15,
                          fontWeight: FontWeight.bold,
                          height: 1.1,
                          decoration: TextDecoration.underline,
                        ),
                      ),
                    ),
                  ),
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
    _phoneController.dispose();
    _passwordController.dispose();
    super.dispose();
  }
}