package net.buildtheearth.terraprojection.projection;

import net.buildtheearth.terraprojection.TerraProjectionConstants;

/**
 * Implementation of the Sinusoidal projection.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Sinusoidal_projection"> Wikipedia's article on the sinusoidal projection</a>
 */
public class SinusoidalProjection implements GeographicProjection {
    @Override
    public double[] toGeo(double x, double y) {
        return new double[]{ x / Math.cos(Math.toRadians(y)), y };
    }

    @Override
    public double[] fromGeo(double longitude, double latitude) throws OutOfProjectionBoundsException {
    	OutOfProjectionBoundsException.checkLongitudeLatitudeInRange(longitude, latitude);
        return new double[]{ longitude * Math.cos(Math.toRadians(latitude)), latitude };
    }

    @Override
    public double metersPerUnit() {
        return TerraProjectionConstants.EARTH_CIRCUMFERENCE / 360.0; //gotta make good on that exact area
    }

    @Override
    public String toString() {
        return "Sinusoidal";
    }
}