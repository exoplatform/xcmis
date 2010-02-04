/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Defines constants used in the query object model.
 *
 * @since JCR 2.0
 */
public interface QueryObjectModelConstants {
    /**
     * An inner join.
     */
    public static final String JCR_JOIN_TYPE_INNER = "jcr.join.type.inner";

    /**
     * A left-outer join.
     */
    public static final String JCR_JOIN_TYPE_LEFT_OUTER = "jcr.join.type.left.outer";

    /**
     * A right-outer join.
     */
    public static final String JCR_JOIN_TYPE_RIGHT_OUTER = "jcr.join.type.right.outer";

    /**
     * The "<code>=</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_EQUAL_TO = "jcr.operator.equal.to";

    /**
     * The "<code>!=</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_NOT_EQUAL_TO = "jcr.operator.not.equal.to";

    /**
     * The "<code>&lt;</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_LESS_THAN = "jcr.operator.less.than";

    /**
     * The "<code>&lt;=</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO = "jcr.operator.less.than.or.equal.to";

    /**
     * The "<code>&gt;</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_GREATER_THAN = "jcr.operator.greater.than";

    /**
     * The "<code>&gt;=</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO = "jcr.operator.greater.than.or.equal.to";

    /**
     * The "<code>like</code>" comparison operator.
     */
    public static final String JCR_OPERATOR_LIKE = "jcr.operator.like";

    /**
     * Ascending order.
     */
    public static final String JCR_ORDER_ASCENDING = "jcr.order.ascending";

    /**
     * Descending order.
     */
    public static final String JCR_ORDER_DESCENDING = "jcr.order.descending";
}