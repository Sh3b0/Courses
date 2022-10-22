import 'package:flutter/material.dart';
import 'package:project/models/joke.dart';
import 'package:share_plus/share_plus.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:project/utils/filestore.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  Joke joke = Joke("", "");
  final PageController pageController = PageController(initialPage: 1000000);
  FileStore db = FileStore(filename: "favs.txt");
  Color heartColor = Colors.black;

  void showJoke() async {
    setState(() {
      joke.value = '...';
      heartColor = Colors.black;
    });
    Joke tmp = await getJoke();
    setState(() {
      joke = tmp;
    });
    if (await db.checkEntry(joke.value)) {
      setState(() {
        heartColor = Colors.red;
      });
    }
  }

  void toggleFavorite() {
    if (heartColor == Colors.red) {
      db.removeEntry(joke.value);
      Fluttertoast.showToast(
        msg: "Joke removed from favorites",
        toastLength: Toast.LENGTH_SHORT,
      );
      setState(() {
        heartColor = Colors.black;
      });
      return;
    }
    db.addEntry(joke.value);
    Fluttertoast.showToast(
      msg: "Joke added to favorites",
      toastLength: Toast.LENGTH_SHORT,
    );
    setState(() {
      heartColor = Colors.red;
    });
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      showJoke();
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.green,
          title: const Text('Chuck Norris Jokes'),
          actions: [
            IconButton(
              icon: const Icon(Icons.star_rounded),
              onPressed: () => Navigator.pushNamed(context, "/favorites"),
            ),
          ],
        ),
        body: SafeArea(
          child: PageView.builder(
            controller: pageController,
            scrollDirection: Axis.horizontal,
            onPageChanged: (_) => showJoke(),
            itemBuilder: (_, index) => Center(
              widthFactor: 0.5,
              heightFactor: 0.5,
              child: Column(
                children: [
                  Card(
                    margin: const EdgeInsets.all(50),
                    child: Column(
                      children: [
                        Padding(
                          padding: const EdgeInsets.all(30),
                          child: Text(joke.value),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            IconButton(
                              onPressed: () => Share.share(joke.value),
                              icon: const Icon(Icons.share_rounded),
                            ),
                            IconButton(
                              onPressed: toggleFavorite,
                              icon: Icon(
                                Icons.favorite_rounded,
                                color: heartColor,
                              ),
                            )
                          ],
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
