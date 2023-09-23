package net.buildtheearth.terraprojection.projection.transform;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import net.buildtheearth.terraprojection.projection.GeographicProjection;
import net.buildtheearth.terraprojection.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraprojection.util.JsonUtils;

import java.lang.reflect.Type;

/**
 * Applies a simple translation to the projected space, such that:
 * x' = x + offsetX and y' = y + offsetY
 */
@Getter
@JsonAdapter(OffsetProjectionTransform.Deserializer.class)
public class OffsetProjectionTransform extends ProjectionTransform {
    private final double dx;
    private final double dy;

    /**
     * @param delegate - Input projection
     * @param dx       - how much to move along the X axis
     * @param dy       - how much to move along the Y axis
     */
    public OffsetProjectionTransform(GeographicProjection delegate, double dx, double dy) {
        super(delegate);
        Preconditions.checkArgument(Double.isFinite(dx) && Double.isFinite(dy), "Projection offsets have to be finite doubles");
        this.dx = dx;
        this.dy = dy;
    }

    static class Deserializer implements JsonDeserializer<OffsetProjectionTransform> {
        public OffsetProjectionTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new OffsetProjectionTransform(
                    JsonUtils.getProjectionDelegate(jsonObject, context),
                    JsonUtils.getDouble(jsonObject, "dx"),
                    JsonUtils.getDouble(jsonObject, "dy")
            );
        }
    }

    @Override
    public double[] bounds() {
        double[] b = this.delegate.bounds();
        b[0] += this.dx;
        b[1] += this.dy;
        b[2] += this.dx;
        b[3] += this.dy;
        return b;
    }

    @Override
    public double[] toGeo(double x, double y) throws OutOfProjectionBoundsException {
        return this.delegate.toGeo(x - this.dx, y - this.dy);
    }

    @Override
    public double[] fromGeo(double longitude, double latitude) throws OutOfProjectionBoundsException {
        double[] pos = this.delegate.fromGeo(longitude, latitude);
        pos[0] += this.dx;
        pos[1] += this.dy;
        return pos;
    }

    @Override
    public String toString() {
        return "Offset (" + super.delegate + ") by " + this.dx + ", " + this.dy;
    }
}