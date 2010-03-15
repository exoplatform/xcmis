/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Tests the existence of a property.
 * <p>
 * A node-tuple satisfies the constraint if the selector node has a property
 * named {@link #getPropertyName property}.
 *
 * @since JCR 2.0
 */
public interface PropertyExistence
        extends Constraint {
    /**
     * Gets the name of the selector against which to apply this constraint.
     *
     * @return the selector name; non-null
     */
    public String getSelectorName();

    /**
     * Gets the name of the property.
     *
     * @return the property name; non-null
     */
    public String getPropertyName();
}
