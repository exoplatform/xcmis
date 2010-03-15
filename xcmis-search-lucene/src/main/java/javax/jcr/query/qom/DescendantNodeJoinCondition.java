/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Tests whether the {@link #getDescendantSelectorName descendantSelector} node
 * is a descendant of the {@link #getAncestorSelectorName ancestorSelector}
 * node.  A node-tuple satisfies the constraint only if:
 * <pre>  descendantSelectorNode.getAncestor(n).isSame(ancestorSelectorNode) &&
 *     descendantSelectorNode.getDepth() > n</pre>
 * would return true some some non-negative integer <code>n</code>, where
 * <code>descendantSelectorNode</code> is the node for {@link
 * #getDescendantSelectorName descendantSelector} and <code>ancestorSelectorNode</code>
 * is the node for {@link #getAncestorSelectorName ancestorSelector}.
 *
 * @since JCR 2.0
 */
public interface DescendantNodeJoinCondition
        extends JoinCondition {
    /**
     * Gets the name of the descendant selector.
     *
     * @return the selector name; non-null
     */
    public String getDescendantSelectorName();

    /**
     * Gets the name of the ancestor selector.
     *
     * @return the selector name; non-null
     */
    public String getAncestorSelectorName();
}
