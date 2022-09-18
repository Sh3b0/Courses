import 'package:json_annotation/json_annotation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

part 'joke.g.dart';

@JsonSerializable()
class Joke {
  @JsonKey(name: 'icon_url')
  String iconUrl;
  String id;
  String url;
  String value;

  Joke(
    this.iconUrl,
    this.id,
    this.url,
    this.value,
  );

  factory Joke.fromJson(Map<String, dynamic> json) => _$JokeFromJson(json);
  Map<String, dynamic> toJson() => _$JokeToJson(this);
}

Future<String> getJoke() async {
  http.Response response =
      await http.get(Uri.parse('https://api.chucknorris.io/jokes/random'));
  return Joke.fromJson(jsonDecode(response.body)).value;
}
