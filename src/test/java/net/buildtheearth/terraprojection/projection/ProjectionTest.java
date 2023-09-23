package net.buildtheearth.terraprojection.projection;

import net.buildtheearth.terraprojection.TerraProjectionConstants;
import net.buildtheearth.terraprojection.projection.dymaxion.BTEDymaxionProjection;
import net.buildtheearth.terraprojection.projection.dymaxion.ConformalDynmaxionProjection;
import net.buildtheearth.terraprojection.projection.dymaxion.DymaxionProjection;
import net.buildtheearth.terraprojection.projection.mercator.CenteredMercatorProjection;
import net.buildtheearth.terraprojection.projection.mercator.TransverseMercatorProjection;
import net.buildtheearth.terraprojection.projection.mercator.WebMercatorProjection;
import net.buildtheearth.terraprojection.projection.transform.*;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectionTest {

    @Test
    public void testBteProjection() throws OutOfProjectionBoundsException {
        final String bteProjectionJson = "{\n"
                + "        \"scale\": {\n"
                + "            \"delegate\": {\n"
                + "                \"flip_vertical\": {\n"
                + "                    \"delegate\": {\n"
                + "                        \"bte_conformal_dymaxion\": {}\n"
                + "                    }\n"
                + "                }\n"
                + "            },\n"
                + "            \"x\": 7318261.522857145,\n"
                + "            \"y\": 7318261.522857145\n"
                + "        }\n"
                + "    }";
        GeographicProjection projection = TerraProjectionConstants.GSON.fromJson(bteProjectionJson, GeographicProjection.class);

        double[] geos = {
                2.350987d, 48.856667d,
                -74.005974d, 40.714268d,
                -0.166670d, 51.500000d,
                116.397230d, 39.907500d,
                -122.332070, 47.606210d,
                151.208666d, -33.875113d,
                2.295026d, 48.87378100000001d,
                2.236214, 48.8926507,
                2.349270d, 48.853474d,
                2.348969d, 48.853065d
        };
        double[] mcs = {
                2851660.278582057, -5049718.243628887,
                -8526456.75523275, -6021812.714103152,
                2774758.1546624764, -5411708.236500686,
                11571988.173618957, -6472387.375809908,
                -12410431.110669583, -6894851.702710003,
                20001061.636216827, -2223355.8371363534,
                2848192.3338641203, -5053053.018157968,
                2844585.5271490104, -5056657.959395678,
                2851410.680220599, -5049403.7778784195,
                2851372.726732094, -5049365.549214174
        };

        for (int i = 0; i < geos.length / 2; i++) {
            double lon = geos[i * 2];
            double lat = geos[i*2 + 1];
            double x = mcs[i * 2];
            double z = mcs[i * 2 + 1];
            double[] lola = projection.toGeo(x, z);
            double[] xz = projection.fromGeo(lon, lat);
            assertEquals(lon, lola[0], .1d);
            assertEquals(lat, lola[1], .1d);
            assertEquals(x, xz[0], .1d);
            assertEquals(z, xz[1], .1d);
        }
    }

    @Test
    public void givenProjectionJsonLists_testReadability() {
        final double DELTA = 0.000001;

        final String projectionLists = "[" +
                //normal projections
                "    { \"centered_mercator\": {} }," +
                "    { \"web_mercator\": { \"zoom\": 69 } }," +
                "    { \"transverse_mercator\": {} }," +
                "    { \"equirectangular\": {} }," +
                "    { \"sinusoidal\": {} }," +
                "    { \"equal_earth\": {} }," +
                "    { \"bte_conformal_dymaxion\": {} }," +
                "    { \"dymaxion\": {} }," +
                "    { \"conformal_dymaxion\": {} }," +
                "    { \"lambert_azimuthal\": { \"centerX\": 6.9, \"centerY\": 42.0 } }," +
                "    { \"azimuthal_equidistant\": { \"centerX\": 6.9, \"centerY\": 42.0 } }," +
                "    { \"stereographic\": { \"centerX\": 6.9, \"centerY\": 42.0, \"radius\": 123.4 } }," +
                //transformations
                "    { \"clamp\": { \"delegate\": { \"equirectangular\": {} }, \"minX\": 0, \"minY\": 1, \"maxX\": 2, \"maxY\": 3 } }," +
                "    { \"flip_horizontal\": { \"delegate\": { \"equirectangular\": {} } } }," +
                "    { \"flip_vertical\": { \"delegate\": { \"equirectangular\": {} } } }," +
                "    { \"offset\": { \"delegate\": { \"equirectangular\": {} }, \"dx\": 0, \"dy\": 1 } }," +
                "    { \"rotate\": { \"delegate\": { \"equirectangular\": {} }, \"by\": 90 } }," +
                "    { \"scale\": { \"delegate\": { \"equirectangular\": {} }, \"x\": 4, \"y\": 4 } }," +
                "    { \"swap_axes\": { \"delegate\": { \"equirectangular\": {} } } }," +
                "]";
        GeographicProjection[] projection = TerraProjectionConstants.GSON.fromJson(projectionLists, GeographicProjection[].class);

        //normal projections
        assertInstanceOf(projection[0], CenteredMercatorProjection.class);
        assertInstanceOf(projection[1], WebMercatorProjection.class, p -> assertEquals(p.getZoom(), 69));
        assertInstanceOf(projection[2], TransverseMercatorProjection.class);
        assertInstanceOf(projection[3], EquirectangularProjection.class);
        assertInstanceOf(projection[4], SinusoidalProjection.class);
        assertInstanceOf(projection[5], EqualEarthProjection.class);
        assertInstanceOf(projection[6], BTEDymaxionProjection.class);
        assertInstanceOf(projection[7], DymaxionProjection.class);
        assertInstanceOf(projection[8], ConformalDynmaxionProjection.class);
        assertInstanceOf(projection[9], LambertAzimuthalProjection.class, p -> {
            assertEquals(p.getCenterX(), 6.9, DELTA);
            assertEquals(p.getCenterY(), 42.0, DELTA);
        });
        assertInstanceOf(projection[10], AzimuthalEquidistantProjection.class, p -> {
            assertEquals(p.getCenterX(), 6.9, DELTA);
            assertEquals(p.getCenterY(), 42.0, DELTA);
        });
        assertInstanceOf(projection[11], StereographicProjection.class, p -> {
            assertEquals(p.getCenterX(), 6.9, DELTA);
            assertEquals(p.getCenterY(), 42.0, DELTA);
            assertEquals(p.getRadius(), 123.4, DELTA);
        });

        //transformations
        assertInstanceOf(projection[12], ClampProjectionTransform.class, p -> {
            assertEquals(p.getMinX(), 0, DELTA);
            assertEquals(p.getMinY(), 1, DELTA);
            assertEquals(p.getMaxX(), 2, DELTA);
            assertEquals(p.getMaxY(), 3, DELTA);
        });
        assertInstanceOf(projection[13], FlipHorizontalProjectionTransform.class);
        assertInstanceOf(projection[14], FlipVerticalProjectionTransform.class);
        assertInstanceOf(projection[15], OffsetProjectionTransform.class, p -> {
            assertEquals(p.getDx(), 0, DELTA);
            assertEquals(p.getDy(), 1, DELTA);
        });
        assertInstanceOf(projection[16], RotateProjectionTransform.class, p -> assertEquals(p.getBy(), 90, DELTA));
        assertInstanceOf(projection[17], ScaleProjectionTransform.class, p -> {
            assertEquals(p.getX(), 4, DELTA);
            assertEquals(p.getY(), 4, DELTA);
        });
        assertInstanceOf(projection[18], SwapAxesProjectionTransform.class);
    }

    @SuppressWarnings("unchecked")
    private <T> void assertInstanceOf(Object object, Class<T> clazz, Consumer<T> assertions) {
        assertTrue(clazz.isInstance(object));
        assertions.accept((T) object);
    }

    private <T> void assertInstanceOf(Object object, Class<T> clazz) {
        this.assertInstanceOf(object, clazz, t -> {});
    }

}
