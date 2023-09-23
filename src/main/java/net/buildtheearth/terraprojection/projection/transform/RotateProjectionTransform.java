package net.buildtheearth.terraprojection.projection.transform;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import net.buildtheearth.terraprojection.projection.GeographicProjection;
import net.buildtheearth.terraprojection.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraprojection.util.JsonUtils;

import java.lang.reflect.Type;

import static java.lang.Math.*;

/**
 * @author DaPorkchop_
 */
@JsonAdapter(RotateProjectionTransform.Deserializer.class)
public class RotateProjectionTransform extends ProjectionTransform {
    @Getter
    private final double by;

    private transient final double sin;
    private transient final double cos;
    private transient final double sinBackwards;
    private transient final double cosBackwards;

    /**
     * @param delegate - Input projection
     * @param by       - how much to rotate the projection by
     */
    public RotateProjectionTransform(GeographicProjection delegate, double by) {
        super(delegate);
        Preconditions.checkArgument(Double.isFinite(by), "Projection rotation must be a finite double");
        this.by = by;

        this.sin = Math.sin(toRadians(by));
        this.cos = Math.cos(toRadians(by));
        this.sinBackwards = Math.sin(toRadians(-by));
        this.cosBackwards = Math.cos(toRadians(-by));
    }

    static class Deserializer implements JsonDeserializer<RotateProjectionTransform> {
        public RotateProjectionTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new RotateProjectionTransform(
                    JsonUtils.getProjectionDelegate(jsonObject, context),
                    JsonUtils.getDouble(jsonObject, "by")
            );
        }
    }

    @Override
    public double[] bounds() {
        double[] bounds = super.bounds();

        double x0 = bounds[0] * this.cos - bounds[1] * this.sin;
        double x1 = bounds[0] * this.cos - bounds[3] * this.sin;
        double x2 = bounds[2] * this.cos - bounds[1] * this.sin;
        double x3 = bounds[2] * this.cos - bounds[3] * this.sin;

        double y0 = bounds[0] * this.sin + bounds[1] * this.cos;
        double y1 = bounds[0] * this.sin + bounds[3] * this.cos;
        double y2 = bounds[2] * this.sin + bounds[1] * this.cos;
        double y3 = bounds[2] * this.sin + bounds[3] * this.cos;

        return new double[] {
                min(min(x0, x1), min(x2, x3)),
                min(min(y0, y1), min(y2, y3)),
                max(max(x0, x1), max(x2, x3)),
                max(max(y0, y1), max(y2, y3))
        };
    }

    @Override
    public double[] toGeo(double x, double y) throws OutOfProjectionBoundsException {
        return super.delegate.toGeo(
                x * this.cos - y * this.sin,
                x * this.sin + y * this.cos
        );
    }

    @Override
    public double[] fromGeo(double longitude, double latitude) throws OutOfProjectionBoundsException {
        double[] pos = super.delegate.fromGeo(longitude, latitude);
        return new double[] {
                pos[0] * this.cosBackwards - pos[1] * this.sinBackwards,
                pos[0] * this.sinBackwards + pos[1] * this.cosBackwards,
        };
    }

    @Override
    public String toString() {
        return "Rotate (" + super.delegate + ") by " + this.by + " degrees";
    }
}