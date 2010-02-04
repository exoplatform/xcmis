/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Performs a logical negation of another constraint.
 * <p>
 * To satisfy the <code>Not</code> constraint, the node-tuple must <i>not</i>
 * satisfy {@link #getConstraint constraint}.
 *
 * @since JCR 2.0
 */
public interface Not
        extends Constraint {
    /**
     * Gets the constraint negated by this <code>Not</code> constraint.
     *
     * @return the constraint; non-null
     */
    public Constraint getConstraint();
}
