import 'package:json_annotation/json_annotation.dart';
import 'package:internet_connection_checker/internet_connection_checker.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

part 'joke.g.dart';

@JsonSerializable()
class Joke {
  String id;
  String value;

  Joke(
    this.id,
    this.value,
  );

  factory Joke.fromJson(Map<String, dynamic> json) => _$JokeFromJson(json);
  Map<String, dynamic> toJson() => _$JokeToJson(this);
}

Future<Joke> getJoke() async {
  bool result = await InternetConnectionChecker().hasConnection;
  if (!result) {
    return Joke("", "Seems like Chuck Norris cut your internet connection");
  }
  http.Response response = await http.get(Uri.parse('https://api.chucknorris.io/jokes/random'));
  if (response.statusCode != 200) return Joke("", "Chuck won't tell jokes right now");
  return Joke.fromJson(jsonDecode(response.body));
}
