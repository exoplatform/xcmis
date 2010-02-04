/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Tests whether the {@link #getSelectorName selector} node is reachable by
 * absolute path {@link #getPath path}.
 * <p>
 * A node-tuple satisfies the constraint only if:
 * <pre>  selectorNode.isSame(session.getNode(path))</pre>
 * would return true, where <code>selectorNode</code> is the node for the
 * specified selector.
 *
 * @since JCR 2.0
 */
public interface SameNode
        extends Constraint {
    /**
     * Gets the name of the selector against which to apply this constraint.
     *
     * @return the selector name; non-null
     */
    public String getSelectorName();

    /**
     * Gets the absolute path.
     *
     * @return the path; non-null
     */
    public String getPath();
}
