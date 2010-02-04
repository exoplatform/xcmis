/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Tests whether the {@link #getSelectorName selector} node is a descendant of a
 * node reachable by absolute path {@link #getAncestorPath path}.
 * <p>
 * A node-tuple satisfies the constraint only if:
 * <pre>  selectorNode.getAncestor(n).isSame(session.getNode(path)) &&
 *     selectorNode.getDepth() > n</pre>
 * would return true for some non-negative integer <code>n</code>, where {@link
 * #getSelectorName selectorNode} is the node for the specified selector.
 *
 * @since JCR 2.0
 */
public interface DescendantNode
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
    public String getAncestorPath();
}
