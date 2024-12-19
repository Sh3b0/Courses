import 'package:flutter/material.dart';
import 'package:share_plus/share_plus.dart';
import 'package:project/utils/filestore.dart';

class FavoritesScreen extends StatefulWidget {
  const FavoritesScreen({super.key});

  @override
  State<FavoritesScreen> createState() => _FavoritesScreenState();
}

class _FavoritesScreenState extends State<FavoritesScreen> {
  List<String> jokes = [];
  FileStore db = FileStore(filename: "favs.txt");

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      List<String> tmp = await db.loadEntries();
      setState(() {
        jokes = tmp;
      });
    });
  }

  void showClearDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Clear Favorites"),
          content: const Text("Are you sure you want clear favorites list?"),
          actions: [
            ElevatedButton(
              child: const Text("Clear"),
              onPressed: () {
                db.clear();
                Navigator.popUntil(context, ModalRoute.withName('/'));
              },
            ),
            ElevatedButton(
              child: const Text("Cancel"),
              onPressed: () => Navigator.pop(context),
            ),
          ],
        );
      },
    );
  }

  PreferredSizeWidget appBar(BuildContext context) {
    return AppBar(
      title: const Text('Favorite Jokes'),
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () => Navigator.pop(context),
      ),
      backgroundColor: Colors.orange,
      actions: [
        IconButton(
          onPressed: () => showClearDialog(context),
          icon: const Icon(Icons.delete_rounded),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    if (jokes.isEmpty) {
      return MaterialApp(
        home: Scaffold(
          appBar: appBar(context),
          body: const Center(
            child: Text("Jokes marked as favorite will show up here."),
          ),
        ),
      );
    }
    return MaterialApp(
      home: Scaffold(
        appBar: appBar(context),
        body: ListView.builder(
          itemCount: jokes.length,
          itemBuilder: (context, index) => ListTile(
            contentPadding: const EdgeInsets.only(left: 20, right: 10, top: 10),
            title: Text(jokes[index]),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                IconButton(
                  onPressed: () => {Share.share(jokes[index])},
                  icon: const Icon(Icons.share_rounded),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
