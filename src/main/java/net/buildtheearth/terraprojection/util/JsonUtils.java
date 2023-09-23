package net.buildtheearth.terraprojection.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import net.buildtheearth.terraprojection.projection.GeographicProjection;

@UtilityClass
public class JsonUtils {

    public Double getNullableDouble(JsonObject jsonObject, String name) {
        JsonElement element = jsonObject.get(name);
        return element.isJsonNull() ? null : element.getAsDouble();
    }

    public double getDouble(JsonObject jsonObject, String name) {
        return jsonObject.get(name).getAsDouble();
    }

    public Integer getNullableInteger(JsonObject jsonObject, String name) {
        JsonElement element = jsonObject.get(name);
        return element.isJsonNull() ? null : element.getAsInt();
    }

    /**
     * Assumes that the name of the projection object is "delegate"
     */
    public GeographicProjection getProjectionDelegate(JsonObject jsonObject, JsonDeserializationContext context) {
        return getProjectionObject(jsonObject, "delegate", context);
    }

    public GeographicProjection getProjectionObject(JsonObject jsonObject, String name, JsonDeserializationContext context) {
        return context.deserialize(jsonObject.getAsJsonObject(name), GeographicProjection.class);
    }

}
