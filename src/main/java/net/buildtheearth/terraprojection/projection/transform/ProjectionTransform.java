package net.buildtheearth.terraprojection.projection.transform;

import lombok.Getter;
import net.buildtheearth.terraprojection.projection.GeographicProjection;

/**
 * Warps a Geographic projection and applies a transformation to it.
 */
@Getter
public abstract class ProjectionTransform implements GeographicProjection {
    protected final GeographicProjection delegate;

    /**
     * @param delegate - projection to transform
     */
    public ProjectionTransform(GeographicProjection delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean upright() {
        return this.delegate.upright();
    }

    @Override
    public double[] bounds() {
        return this.delegate.bounds();
    }

    @Override
    public double metersPerUnit() {
        return this.delegate.metersPerUnit();
    }
}