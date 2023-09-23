package net.buildtheearth.terraprojection.projection.transform;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import net.buildtheearth.terraprojection.projection.GeographicProjection;
import net.buildtheearth.terraprojection.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraprojection.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
@Getter
@JsonAdapter(ClampProjectionTransform.Deserializer.class)
public class ClampProjectionTransform extends ProjectionTransform {
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    /**
     * @param delegate - Input projection
     */
    public ClampProjectionTransform(GeographicProjection delegate,
                                    double minX, double maxX, double minY, double maxY) {
        super(delegate);
        Preconditions.checkArgument(Double.isFinite(minX) && Double.isFinite(maxX) && Double.isFinite(minY) && Double.isFinite(maxY), "Projection bounds must be finite doubles");
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    static class Deserializer implements JsonDeserializer<ClampProjectionTransform> {
        public ClampProjectionTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ClampProjectionTransform(
                    JsonUtils.getProjectionDelegate(jsonObject, context),
                    JsonUtils.getDouble(jsonObject, "minX"),
                    JsonUtils.getDouble(jsonObject, "maxX"),
                    JsonUtils.getDouble(jsonObject, "minY"),
                    JsonUtils.getDouble(jsonObject, "maxY")
            );
        }
    }

    @Override
    public double[] bounds() {
        return new double[] { this.minX, this.minY, this.maxX, this.maxY };
    }

    @Override
    public double[] toGeo(double x, double y) throws OutOfProjectionBoundsException {
        if (x < this.minX || x > this.maxX || y < this.minY || y > this.maxY) {
            throw OutOfProjectionBoundsException.get();
        }
        return super.delegate.toGeo(x, y);
    }

    @Override
    public double[] fromGeo(double longitude, double latitude) throws OutOfProjectionBoundsException {
        double[] pos = super.delegate.fromGeo(longitude, latitude);
        if (pos[0] < this.minX || pos[0] > this.maxX || pos[1] < this.minY || pos[1] > this.maxY) {
            throw OutOfProjectionBoundsException.get();
        }
        return pos;
    }

    @Override
    public String toString() {
        return "Clamp (" + super.delegate + ") to " + Arrays.toString(this.bounds());
    }
}