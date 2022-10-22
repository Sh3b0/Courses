import 'package:flutter/material.dart';
import 'package:project/screens/home.dart';
import 'package:project/screens/favorites.dart';

void main() {
  runApp(
    MaterialApp(
      routes: {
        "/": (_) => const HomeScreen(),
        "/favorites": (_) => const FavoritesScreen(),
      },
    ),
  );
}
