/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Tests whether two nodes are "the same" according to the <code>isSame</code>
 * method of <code>javax.jcr.Item</code>.
 * <p>
 * Tests whether the {@link #getSelector1Name selector1} node is the same as a node identified by
 * relative path from the {@link #getSelector2Name selector2} node.
 * A node-tuple satisfies the constraint only if:
 * <pre>  selector1Node.isSame(selector2Node.getNode(selector2Path))</pre>
 * would return true, where <code>selector1Node</code> is the node for {@link
 * #getSelector1Name selector1} and <code>selector2Node</code> is the node for
 * {@link #getSelector2Name selector2}.</li></ul>
 *
 * @since JCR 2.0
 */
public interface SameNodeJoinCondition
        extends JoinCondition {
    /**
     * Gets the name of the first selector.
     *
     * @return the selector name; non-null
     */
    public String getSelector1Name();

    /**
     * Gets the name of the second selector.
     *
     * @return the selector name; non-null
     */
    public String getSelector2Name();

    /**
     * Gets the path relative to the second selector.
     *
     * @return the relative path, or null for none
     */
    public String getSelector2Path();
}
