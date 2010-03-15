/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Performs a logical conjunction of two other constraints.
 * <p>
 * To satisfy the <code>And</code> constraint, a node-tuple must satisfy both
 * {@link #getConstraint1 constraint1} and {@link #getConstraint2 constraint2}.
 *
 * @since JCR 2.0
 */
public interface And
        extends Constraint {
    /**
     * Gets the first constraint.
     *
     * @return the constraint; non-null
     */
    public Constraint getConstraint1();

    /**
     * Gets the second constraint.
     *
     * @return the constraint; non-null
     */
    public Constraint getConstraint2();
}
