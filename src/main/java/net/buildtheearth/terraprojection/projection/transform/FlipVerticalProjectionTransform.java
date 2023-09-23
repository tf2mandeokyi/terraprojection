package net.buildtheearth.terraprojection.projection.transform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import net.buildtheearth.terraprojection.projection.GeographicProjection;
import net.buildtheearth.terraprojection.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraprojection.util.JsonUtils;

import java.lang.reflect.Type;

/**
 * Mirrors the warped projection vertically.
 * I.E. x' = x and y' = -y
 */
@JsonAdapter(FlipVerticalProjectionTransform.Deserializer.class)
public class FlipVerticalProjectionTransform extends ProjectionTransform {
    /**
     * @param delegate - projection to transform
     */
    public FlipVerticalProjectionTransform(GeographicProjection delegate) {
        super(delegate);
    }

    static class Deserializer implements JsonDeserializer<FlipVerticalProjectionTransform> {
        public FlipVerticalProjectionTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new FlipVerticalProjectionTransform(JsonUtils.getProjectionDelegate(json.getAsJsonObject(), context));
        }
    }

    @Override
    public double[] toGeo(double x, double y) throws OutOfProjectionBoundsException {
        return this.delegate.toGeo(x, -y);
    }

    @Override
    public double[] fromGeo(double longitude, double latitude) throws OutOfProjectionBoundsException {
        double[] p = this.delegate.fromGeo(longitude, latitude);
        p[1] = -p[1];
        return p;
    }

    @Override
    public boolean upright() {
        return !this.delegate.upright();
    }

    @Override
    public double[] bounds() {
        double[] b = this.delegate.bounds();
        return new double[]{ b[0], -b[3], b[2], -b[1] };
    }

    @Override
    public String toString() {
        return "Vertical Flip (" + super.delegate + ')';
    }
}