/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Performs a logical disjunction of two other constraints.
 * <p>
 * To satisfy the <code>Or</code> constraint, the node-tuple must either: <ul>
 * <li>satisfy {@link #getConstraint1 constraint1} but not {@link
 * #getConstraint2 constraint2}, or</li> <li>satisfy {@link #getConstraint2
 * constraint2} but not {@link #getConstraint1 constraint1}, or</li> <li>satisfy
 * both {@link #getConstraint1 constraint1} and {@link #getConstraint2
 * constraint2}.</li> </ul>
 *
 * @since JCR 2.0
 */
public interface Or
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
