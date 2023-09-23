package net.buildtheearth.terraprojection.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.buildtheearth.terraprojection.TerraProjectionConstants;

import java.io.IOException;
import java.util.Map;

public abstract class TypedJsonAdapter<T> extends TypeAdapter<T> {
    @Override
    public T read(JsonReader r) throws IOException {
        r.beginObject();

        String name = r.nextName();
        Class<? extends T> clazz = this.nameToClassRegistry().get(name);
        T value = TerraProjectionConstants.GSON.fromJson(r, clazz);

        r.endObject();
        return value;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if(value == null) {
            out.nullValue();
            return;
        }

        String name = this.classToNameRegistry().get(value.getClass());
        if(name == null) {
            throw new IOException("invalid value type: " + value.getClass());
        }

        out.beginObject();
        out.name(name);
        TerraProjectionConstants.GSON.toJson(value, value.getClass(), out);
        out.endObject();
    }

    protected abstract Map<String, Class<? extends T>> nameToClassRegistry();
    protected abstract Map<Class<? extends T>, String> classToNameRegistry();
}
