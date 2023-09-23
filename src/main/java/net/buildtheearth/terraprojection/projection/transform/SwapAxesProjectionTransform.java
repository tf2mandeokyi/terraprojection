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
 * Inverses the warped projection such that x becomes y and y becomes x.
 */
@JsonAdapter(SwapAxesProjectionTransform.Deserializer.class)
public class SwapAxesProjectionTransform extends ProjectionTransform {

    /**
     * @param delegate - projection to transform
     */
    public SwapAxesProjectionTransform(GeographicProjection delegate) {
        super(delegate);
    }

    static class Deserializer implements JsonDeserializer<SwapAxesProjectionTransform> {
        public SwapAxesProjectionTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SwapAxesProjectionTransform(JsonUtils.getProjectionDelegate(json.getAsJsonObject(), context));
        }
    }

    @Override
    public double[] toGeo(double x, double y) throws OutOfProjectionBoundsException {
        return this.delegate.toGeo(y, x);
    }

    @Override
    public double[] fromGeo(double lon, double lat) throws OutOfProjectionBoundsException {
        double[] p = this.delegate.fromGeo(lon, lat);
        double t = p[0];
        p[0] = p[1];
        p[1] = t;
        return p;
    }

    @Override
    public double[] bounds() {
        double[] b = this.delegate.bounds();
        return new double[]{ b[1], b[0], b[3], b[2] };
    }

    @Override
    public String toString() {
        return "Swap Axes(" + super.delegate + ')';
    }
}