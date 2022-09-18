// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'joke.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Joke _$JokeFromJson(Map<String, dynamic> json) => Joke(
      json['icon_url'] as String,
      json['id'] as String,
      json['url'] as String,
      json['value'] as String,
    );

Map<String, dynamic> _$JokeToJson(Joke instance) => <String, dynamic>{
      'icon_url': instance.iconUrl,
      'id': instance.id,
      'url': instance.url,
      'value': instance.value,
    };
