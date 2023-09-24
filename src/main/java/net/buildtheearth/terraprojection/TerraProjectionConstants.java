package net.buildtheearth.terraprojection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.buildtheearth.terraprojection.projection.GeographicProjection;
import net.daporkchop.lib.binary.oio.StreamUtil;

import java.io.IOException;
import java.io.InputStream;

public class TerraProjectionConstants {

    public static final Gson GSON = new GsonBuilder().create();

    /**
     * Earth's circumference around the equator, in meters.
     */
    public static final double EARTH_CIRCUMFERENCE = 40075017;

    /**
     * Earth's circumference around the poles, in meters.
     */
    public static final double EARTH_POLAR_CIRCUMFERENCE = 40008000;

    public static final GeographicProjection BTE_PROJECTION;

    static {
        try(InputStream in = TerraProjectionConstants.class.getResourceAsStream("bte_projection_settings.json5")) {
            if(in == null) throw new IOException("Resource bte_projection_settings.json5 not found");
            BTE_PROJECTION = GeographicProjection.parse(new String(StreamUtil.toByteArray(in)));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

}
