package com.example.chatsystem.model;

import com.google.gson.*;
import javafx.scene.image.Image;

import java.lang.reflect.Type;

public class ImageAdapter implements JsonSerializer<Image>, JsonDeserializer<Image>
{
    @Override
    public JsonElement serialize(Image src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getUrl());
    }

    @Override
    public Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        String url = json.getAsJsonPrimitive().getAsString();
        return new Image(url);
    }
}
