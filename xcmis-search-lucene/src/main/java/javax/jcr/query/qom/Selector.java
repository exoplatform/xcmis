/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Selects a subset of the nodes in the repository based on node type.
 * <p>
 * A selector selects every node in the repository, subject to access control
 * constraints, that satisfies at least one of the following conditions: <ul>
 * <li>the node's primary node type is {@link #getNodeTypeName nodeType},
 * or</li> <li>the node's primary node type is a subtype of {@link
 * #getNodeTypeName nodeType}, or</li> <li>the node has a mixin node type that
 * is {@link #getNodeTypeName nodeType}, or</li> <li>the node has a mixin node
 * type that is a subtype of {@link #getNodeTypeName nodeType}.</li> </ul>
 *
 * @since JCR 2.0
 */
public interface Selector
        extends Source {
    /**
     * Gets the name of the required node type.
     *
     * @return the node type name; non-null
     */
    public String getNodeTypeName();

    /**
     * Gets the selector name.
     * <p>
     * A selector's name can be used elsewhere in the query to identify the
     * selector.
     *
     * @return the selector name; non-null
     */
    public String getSelectorName();
}
