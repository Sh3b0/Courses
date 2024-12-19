import 'dart:io';
import 'package:path_provider/path_provider.dart';

class FileStore {
  String filename;
  FileStore({required this.filename});

  Future<File> get _localFile async {
    final directory = await getApplicationDocumentsDirectory();
    final path = directory.path;
    return File('$path/$filename');
  }

  Future<List<String>> loadEntries() async {
    final file = await _localFile;
    if (!file.existsSync()) return [];
    return await file.readAsLines();
  }

  void addEntry(String line) async {
    final file = await _localFile;
    file.writeAsString('$line\n', mode: FileMode.append, flush: true);
  }

  void removeEntry(String line) async {
    List<String> lines = await loadEntries();
    lines.remove(line);
    final file = await _localFile;
    file.writeAsString(lines.join('\n'));
  }

  Future<bool> checkEntry(String line) async {
    List<String> lines = await loadEntries();
    return lines.contains(line);
  }

  void clear() async {
    final file = await _localFile;
    file.delete();
  }
}
