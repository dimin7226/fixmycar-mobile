import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'api_service.dart';
import 'registration_user_data_screen.dart';
import 'login_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:google_fonts/google_fonts.dart';

class RegistrationPhoneScreen extends StatefulWidget {
  const RegistrationPhoneScreen({super.key});

  @override
  State<RegistrationPhoneScreen> createState() => _RegistrationPhoneScreenState();
}

class _RegistrationPhoneScreenState extends State<RegistrationPhoneScreen> {
  final _phoneController = TextEditingController();
  final _api = ApiService();
  String? _error;
  bool _loading = false;

  Future<void> _submit() async {
    setState(() { _loading = true; _error = null; });

    try {
      final tempUserId = await _api.registerPhone(_phoneController.text.trim());
      
      if (!mounted) return;

      if (tempUserId != null) {
        // Сохраняем tempUserId в SharedPreferences
        final prefs = await SharedPreferences.getInstance();
        await prefs.setInt('tempUserId', tempUserId);
        
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => RegistrationUserDataScreen(
              phone: _phoneController.text.trim(),
            ),
          ),
        );
      } else {
        setState(() => _error = "Ошибка регистрации телефона");
      }
    } catch (e) {
      setState(() => _error = e.toString());
    }

    setState(() => _loading = false);
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
                      'Регистрация',
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
                  
                  SizedBox(height: size.height * 0.05),
                  
                  // Кнопка регистрации
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
                              'ЗАРЕГИСТРИРОВАТЬСЯ',
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
                  
                  // Ссылка на вход
                  Center(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          'Уже есть аккаунт?',
                          style: GoogleFonts.balsamiqSans(
                            color: Colors.black,
                            fontSize: isSmallScreen ? 11 : 12,
                            height: 1.3,
                          ),
                        ),
                        const SizedBox(width: 4),
                        InkWell(
                          onTap: _loading
                              ? null
                              : () {
                                  Navigator.pushReplacement(
                                    context,
                                    MaterialPageRoute(
                                      builder: (_) => const LoginScreen(),
                                    ),
                                  );
                                },
                          child: Text(
                            'ВОЙТИ',
                            style: GoogleFonts.balsamiqSans(
                              color: const Color(0xFF2F6EE2),
                              fontSize: isSmallScreen ? 13 : 15,
                              fontWeight: FontWeight.bold,
                              height: 1.1,
                              decoration: TextDecoration.underline,
                            ),
                          ),
                        ),
                      ],
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
    super.dispose();
  }
}