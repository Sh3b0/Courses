import 'package:flutter/material.dart';
import 'package:share_plus/share_plus.dart';
import 'joke.dart';

void main() {
  runApp(const Main());
}

class Main extends StatefulWidget {
  const Main({super.key});

  @override
  State<Main> createState() => _MainState();
}

class _MainState extends State<Main> {
  String joke = 'Swipe Left/Right for a random joke';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.green,
          title: const Text('Facts about Chuck Norris'),
          actions: [
            Builder(builder: (context) {
              return IconButton(
                icon: const Icon(Icons.info),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (_) => const AboutScreen()),
                  );
                },
              );
            }),
          ],
        ),
        body: SafeArea(
          child: PageView.builder(
            scrollDirection: Axis.horizontal,
            itemBuilder: (_, index) => Center(
              widthFactor: 0.5,
              heightFactor: 0.5,
              child: Column(
                children: [
                  Card(
                    margin: const EdgeInsets.all(50),
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Text(joke),
                    ),
                  ),
                  ElevatedButton(
                    onPressed: () => Share.share(joke),
                    child: const Text('Share'),
                  ),
                ],
              ),
            ),
            onPageChanged: (index) async {
              setState(() {
                joke = '...';
              });
              String text = await getJoke();
              setState(() {
                joke = text;
              });
            },
          ),
        ),
      ),
    );
  }
}

class AboutScreen extends StatelessWidget {
  const AboutScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Facts about me'),
          leading: IconButton(
            icon: const Icon(Icons.arrow_back),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
        ),
        body: const Center(
          child: Card(
            margin: EdgeInsets.all(50),
            child: Padding(
              padding: EdgeInsets.all(20),
              child: Text('I hate frontend development'),
            ),
          ),
        ),
      ),
    );
  }
}
