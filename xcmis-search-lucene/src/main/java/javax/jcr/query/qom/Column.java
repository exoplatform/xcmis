/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Defines a column to include in the tabular view of query results.
 * <p>
 * If {@link #getPropertyName property} is not specified, a column is included
 * for each single-valued non-residual property of the node type specified by
 * the <code>nodeType</code> attribute of {@link #getSelectorName selector}.
 * <p>
 * If {@link #getPropertyName property} is specified, {@link #getColumnName
 * columnName} is required and used to name the column in the tabular results.
 * If {@link #getPropertyName property} is not specified, {@link #getColumnName
 * columnName} must not be specified, and the included columns will be named
 * "{@link #getSelectorName selector}.<i>propertyName</i>".
 *
 * @since JCR 2.0
 */
public interface Column {
    /**
     * Gets the name of the selector.
     *
     * @return the selector name; non-null
     */
    public String getSelectorName();

    /**
     * Gets the name of the property.
     *
     * @return the property name, or null to include a column for each
     *         single-value non-residual property of the selector's node type
     */
    public String getPropertyName();

    /**
     * Gets the column name.
     * <p>
     *
     * @return the column name; must be null if <code>getPropertyName</code> is
     *         null and non-null otherwise
     */
    public String getColumnName();
}
