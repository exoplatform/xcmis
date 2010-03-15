/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Evaluates to the length (or lengths, if multi-valued) of a property.
 * <p>
 * The length should be computed as though the <code>getLength</code> method (or
 * <code>getLengths</code>, if multi-valued) of <code>javax.jcr.Property</code>
 * were called.
 * <p>
 * If {@link #getPropertyValue propertyValue} evaluates to null, the
 * <code>Length</code> operand also evaluates to null.
 *
 * @since JCR 2.0
 */
public interface Length
        extends DynamicOperand {
    /**
     * Gets the property value for which to compute the length.
     *
     * @return the property value; non-null
     */
    public PropertyValue getPropertyValue();
}
