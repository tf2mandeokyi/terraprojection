package net.buildtheearth.terraprojection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];

}
