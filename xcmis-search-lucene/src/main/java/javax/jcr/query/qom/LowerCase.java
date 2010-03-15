/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Evaluates to the lower-case string value (or values, if multi-valued) of
 * {@link #getOperand operand}.
 * <p>
 * If {@link #getOperand operand} does not evaluate to a string value, its value
 * is first converted to a string.  The lower-case string value is computed as
 * though the <code>toLowerCase()</code> method of <code>java.lang.String</code>
 * were called.
 * <p>
 * If {@link #getOperand operand} evaluates to null, the <code>LowerCase</code>
 * operand also evaluates to null.
 *
 * @since JCR 2.0
 */
public interface LowerCase
        extends DynamicOperand {
    /**
     * Gets the operand whose value is converted to a lower-case string.
     *
     * @return the operand; non-null
     */
    public DynamicOperand getOperand();
}
