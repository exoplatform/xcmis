/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Evaluates to the value (or values, if multi-valued) of a property.
 * <p>
 * If, for a node-tuple, the {@link #getSelectorName selector} node does not
 * have a property named {@link #getPropertyName property}, the operand
 * evaluates to null.
 *
 * @since JCR 2.0
 */
public interface PropertyValue
        extends DynamicOperand {
    /**
     * Gets the name of the selector against which to evaluate this operand.
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
