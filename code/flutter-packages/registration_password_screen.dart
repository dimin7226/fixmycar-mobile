import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'api_service.dart';
import 'login_screen.dart';
import 'package:google_fonts/google_fonts.dart';

class RegistrationPasswordScreen extends StatefulWidget {
  final String email;

  const RegistrationPasswordScreen({super.key, required this.email});

  @override
  State<RegistrationPasswordScreen> createState() => _RegistrationPasswordScreenState();
}

class _RegistrationPasswordScreenState extends State<RegistrationPasswordScreen> {
  final _pass1 = TextEditingController();
  final _pass2 = TextEditingController();
  final _api = ApiService();

  bool _loading = false;
  String? _error;

  Future<void> _submit() async {
    if (_pass1.text != _pass2.text) {
      setState(() => _error = "Пароли не совпадают");
      return;
    }

    if (_pass1.text.isEmpty || _pass2.text.isEmpty) {
      setState(() => _error = "Заполните все поля");
      return;
    }

    setState(() { _loading = true; _error = null; });

    try {
      final ok = await _api.registerPassword(widget.email, _pass1.text);

      if (!mounted) return;

      if (ok) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (_) => const LoginScreen()),
        );
      } else {
        setState(() => _error = "Ошибка установки пароля");
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
                vertical: size.height * 0.02,
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Заголовок
                  SizedBox(
                    width: double.infinity,
                    child: Text(
                      'Создание пароля',
                      style: GoogleFonts.balsamiqSans(
                        color: Colors.black,
                        fontSize: isSmallScreen ? 32 : 40,
                        height: 1.2,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.left,
                    ),
                  ),
                  
                  SizedBox(height: size.height * 0.08),
                  
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
                      controller: _pass1,
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
                  
                  SizedBox(height: size.height * 0.03),
                  
                  // Поле для повторения пароля
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
                      controller: _pass2,
                      obscureText: true,
                      decoration: InputDecoration(
                        hintText: 'Повторите пароль',
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
                  
                  // Кнопка "Создать аккаунт"
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
                              'СОЗДАТЬ АККАУНТ',
                              style: GoogleFonts.balsamiqSans(
                                fontSize: 14,
                                height: 4.1,
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
    _pass1.dispose();
    _pass2.dispose();
    super.dispose();
  }
}