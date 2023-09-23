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
 * Scales the warps projection's projected space up or down.
 * More specifically, it multiplies x and y by there respective scale factors.
 */
@Getter
@JsonAdapter(ScaleProjectionTransform.Deserializer.class)
public class ScaleProjectionTransform extends ProjectionTransform {
    private final double x;
    private final double y;

    /**
     * Creates a new ScaleProjection with different scale factors for the x and y axis.
     *
     * @param delegate - projection to transform
     * @param x        - scaling to apply along the x axis
     * @param y        - scaling to apply along the y axis
     */
    public ScaleProjectionTransform(GeographicProjection delegate, double x, double y) {
        super(delegate);
        Preconditions.checkArgument(Double.isFinite(x) && Double.isFinite(y), "Projection scales should be finite");
        Preconditions.checkArgument(x != 0 && y != 0, "Projection scale cannot be 0!");
        this.x = x;
        this.y = y;
    }

    static class Deserializer implements JsonDeserializer<ScaleProjectionTransform> {
        public ScaleProjectionTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ScaleProjectionTransform(
                    JsonUtils.getProjectionDelegate(jsonObject, context),
                    JsonUtils.getDouble(jsonObject, "x"),
                    JsonUtils.getDouble(jsonObject, "y")
            );
        }
    }

    @Override
    public double[] toGeo(double x, double y) throws OutOfProjectionBoundsException {
        return this.delegate.toGeo(x / this.x, y / this.y);
    }

    @Override
    public double[] fromGeo(double lon, double lat) throws OutOfProjectionBoundsException {
        double[] p = this.delegate.fromGeo(lon, lat);
        p[0] *= this.x;
        p[1] *= this.y;
        return p;
    }

    @Override
    public boolean upright() {
        return (this.y < 0) ^ this.delegate.upright();
    }

    @Override
    public double[] bounds() {
        double[] b = this.delegate.bounds();
        b[0] *= this.x;
        b[1] *= this.y;
        b[2] *= this.x;
        b[3] *= this.y;
        return b;
    }

    @Override
    public double metersPerUnit() {
        return this.delegate.metersPerUnit() / Math.sqrt((this.x * this.x + this.y * this.y) / 2); //TODO: better transform
    }

    @Override
    public String toString() {
        return "Scale (" + super.delegate + ") by " + this.x + ", " + this.y;
    }
}