/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Performs a join between two node-tuple sources.
 *
 * @since JCR 2.0
 */
public interface Join
        extends Source {
    /**
     * Gets the left node-tuple source.
     *
     * @return the left source; non-null
     */
    public Source getLeft();

    /**
     * Gets the right node-tuple source.
     *
     * @return the right source; non-null
     */
    public Source getRight();

    /**
     * Gets the join type.
     *
     * @return either <ul> <li>{@link QueryObjectModelConstants#JCR_JOIN_TYPE_INNER},</li>
     *         <li>{@link QueryObjectModelConstants#JCR_JOIN_TYPE_LEFT_OUTER},</li>
     *         <li>{@link QueryObjectModelConstants#JCR_JOIN_TYPE_RIGHT_OUTER}</li>
     *         </ul>
     */
    public String getJoinType();

    /**
     * Gets the join condition.
     *
     * @return the join condition; non-null
     */
    public JoinCondition getJoinCondition();
}
