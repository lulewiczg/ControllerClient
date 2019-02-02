package com.github.lulewiczg.controller.ui.component;

import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.actions.impl.DisconnectAction;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * JSON mapper for Actions.
 */
public class GsonMapper implements JsonSerializer<Action>, JsonDeserializer<Action> {
    private static final String TYPE = "type";
    private static final String PROPS = "props";

    @Override
    public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add(TYPE, new JsonPrimitive(src.getClass().getSimpleName()));
        result.add(PROPS, context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get(TYPE).getAsString();
        JsonElement element = jsonObject.get(PROPS);
        try {
            return context.deserialize(element, Class.forName(DisconnectAction.class.getPackage().getName() + "." + type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }
}
