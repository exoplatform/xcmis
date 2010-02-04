/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Tests whether the {@link #getChildSelectorName childSelector} node is a child
 * of the {@link #getParentSelectorName parentSelector} node.  A node-tuple
 * satisfies the constraint only if:
 * <pre>  childSelectorNode.getParent().isSame(parentSelectorNode)</pre>
 * would return true, where <code>childSelectorNode</code> is the node for
 * {@link #getChildSelectorName childSelector} and <code>parentSelectorNode</code>
 * is the node for {@link #getParentSelectorName parentSelector}.
 *
 * @since JCR 2.0
 */
public interface ChildNodeJoinCondition
        extends JoinCondition {
    /**
     * Gets the name of the child selector.
     *
     * @return the selector name; non-null
     */
    public String getChildSelectorName();

    /**
     * Gets the name of the parent selector.
     *
     * @return the selector name; non-null
     */
    public String getParentSelectorName();
}
