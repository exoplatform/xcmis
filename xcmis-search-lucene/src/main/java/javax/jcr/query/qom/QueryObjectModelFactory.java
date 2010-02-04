/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;


/**
 * A <code>QueryObjectModelFactory</code> creates instances of the JCR query
 * object model.
 * <p>
 * Refer to {@link QueryObjectModel} for a description of the query object
 * model.
 *
 * @since JCR 2.0
 */
public interface QueryObjectModelFactory
        extends QueryObjectModelConstants {
    ///
    /// QUERY
    ///

    /**
     * Creates a query with one or more selectors.
     *
     * @param source     the node-tuple source; non-null
     * @param constraint the constraint, or null if none
     * @param orderings  zero or more orderings; null is equivalent to a
     *                   zero-length array
     * @param columns    the columns; null is equivalent to a zero-length array
     * @return the query; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test and the
     *                               parameters given fail that test. See the individual QOM factory methods
     *                               for the validity criteria of each query element.
     * @throws RepositoryException   if another error occurs.
     */
    public QueryObjectModel createQuery
            (
                    Source source,
                    Constraint constraint,
                    Ordering[] orderings,
                    Column[] columns
            ) throws InvalidQueryException, RepositoryException;

    ///
    /// SELECTOR
    ///

    /**
     * Selects a subset of the nodes in the repository based on node type.
     * <p>
     * The query is invalid if <code>nodeTypeName</code> or
     * <code>selectorName</code> is not a syntactically valid JCR name.
     * <p>
     * The query is invalid if <code>selectorName</code>} is identical to the
     * name of another selector in the query.
     * <p>
     * If <code>nodeTypeName</code> is a valid JCR name but not the name of a
     * node type available in the repository, the query is valid but the
     * selector selects no nodes.
     *
     * @param nodeTypeName the name of the required node type; non-null
     * @param selectorName the selector name; non-null
     * @return the selector; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Selector selector(String nodeTypeName, String selectorName)
            throws InvalidQueryException, RepositoryException;

    ///
    /// JOIN
    ///

    /**
     * Performs a join between two node-tuple sources.
     * <p>
     * The query is invalid if <code>left</code> is the same source as
     * <code>right</code>.
     *
     * @param left          the left node-tuple source; non-null
     * @param right         the right node-tuple source; non-null
     * @param joinType      either <ul> <li>{@link QueryObjectModelConstants#JCR_JOIN_TYPE_INNER},</li>
     *                      <li>{@link QueryObjectModelConstants#JCR_JOIN_TYPE_LEFT_OUTER},</li>
     *                      <li>{@link QueryObjectModelConstants#JCR_JOIN_TYPE_RIGHT_OUTER}</li>
     *                      </ul>
     * @param joinCondition the join condition; non-null
     * @return the join; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Join join
            (
                    Source left,
                    Source right,
                    String joinType,
                    JoinCondition joinCondition
            ) throws InvalidQueryException, RepositoryException;

    ///
    /// JOIN CONDITION
    ///

    /**
     * Tests whether the value of a property in a first selector is equal to the
     * value of a property in a second selector.
     * <p>
     * <p>
     * The query is invalid if: <ul> <li><code>selector1</code> is not the name
     * of a selector in the query, or</li> <li><code>selector2</code> is not the
     * name of a selector in the query, or</li> <li><code>selector1</code> is
     * the same as <code>selector2</code>, or</li> <li><code>property1</code> is
     * not a syntactically valid JCR name, or</li> <li><code>property2</code> is
     * not a syntactically valid JCR name, or</li> <li>the value of
     * <code>property1</code> is not the same property type as the value of
     * <code>property2</code>, or</li> <li><code>property1</code> is a
     * multi-valued property, or</li> <li><code>property2</code> is a
     * multi-valued property, or</li> <li><code>property1</code> is a
     * <code>BINARY</code> property, or</li> <li><code>property2</code> is a
     * <code>BINARY</code> property.</li> </ul>
     *
     * @param selector1Name the name of the first selector; non-null
     * @param property1Name the property name in the first selector; non-null
     * @param selector2Name the name of the second selector; non-null
     * @param property2Name the property name in the second selector; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implementation chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public EquiJoinCondition equiJoinCondition
            (
                    String selector1Name,
                    String property1Name,
                    String selector2Name,
                    String property2Name
            ) throws InvalidQueryException, RepositoryException;


    /**
     * Tests whether a first selector's node is the same as a node identified by
     * relative path from a second selector's node.
     * <p>
     * The query is invalid if: <ul> <li><code>selector1</code> is not the name
     * of a selector in the query, or</li> <li><code>selector2</code> is not the
     * name of a selector in the query, or</li> <li><code>selector1</code> is
     * the same as selector2, or</li> <li><code>selector2Path</code> is not a
     * syntactically valid relative path.  Note, however, that if the path is
     * syntactically valid but does not identify a node visible to the current
     * session, the query is valid but the constraint is not satisfied.</li>
     * </ul>
     *
     * @param selector1Name the name of the first selector; non-null
     * @param selector2Name the name of the second selector; non-null
     * @param selector2Path the path relative to the second selector; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public SameNodeJoinCondition sameNodeJoinCondition
            (
                    String selector1Name,
                    String selector2Name,
                    String selector2Path
            ) throws InvalidQueryException, RepositoryException;

    /**
     * Tests whether a first selector's node is a child of a second selector's
     * node.
     * <p>
     * The query is invalid if: <ul> <li><code>childSelector</code> is not the
     * name of a selector in the query, or</li> <li><code>parentSelector</code>
     * is not the name of a selector in the query, or</li>
     * <li><code>childSelector</code> is the same as <code>parentSelector</code>.
     * </ul>
     *
     * @param childSelectorName  the name of the child selector; non-null
     * @param parentSelectorName the name of the parent selector; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public ChildNodeJoinCondition childNodeJoinCondition
            (
                    String childSelectorName,
                    String parentSelectorName
            ) throws InvalidQueryException, RepositoryException;

    /**
     * Tests whether a first selector's node is a descendant of a second
     * selector's node.
     * <p>
     * The query is invalid if: <ul> <li><code>descendantSelector</code> is not
     * the name of a selector in the query, or</li> <li><code>ancestorSelector</code>
     * is not the name of a selector in the query, or</li>
     * <li><code>descendantSelector</code> is the same as
     * <code>ancestorSelector</code>. </ul>
     *
     * @param descendantSelectorName the name of the descendant selector;
     *                               non-null
     * @param ancestorSelectorName   the name of the ancestor selector; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public DescendantNodeJoinCondition descendantNodeJoinCondition
            (
                    String descendantSelectorName,
                    String ancestorSelectorName
            ) throws InvalidQueryException, RepositoryException;

    ///
    /// CONSTRAINT
    ///

    /**
     * Performs a logical conjunction of two other constraints.
     *
     * @param constraint1 the first constraint; non-null
     * @param constraint2 the second constraint; non-null
     * @return the <code>And</code> constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public And and(Constraint constraint1, Constraint constraint2)
            throws InvalidQueryException, RepositoryException;

    /**
     * Performs a logical disjunction of two other constraints.
     *
     * @param constraint1 the first constraint; non-null
     * @param constraint2 the second constraint; non-null
     * @return the <code>Or</code> constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Or or(Constraint constraint1, Constraint constraint2)
            throws InvalidQueryException, RepositoryException;

    /**
     * Performs a logical negation of another constraint.
     *
     * @param constraint the constraint to be negated; non-null
     * @return the <code>Not</code> constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Not not(Constraint constraint)
            throws InvalidQueryException, RepositoryException;

    /**
     * Filters node-tuples based on the outcome of a binary operation.
     *
     * @param operand1 the first operand; non-null
     * @param operator the operator; either <ul> <li>{@link
     *                 QueryObjectModelConstants#JCR_OPERATOR_EQUAL_TO},</li> <li>{@link
     *                 QueryObjectModelConstants#JCR_OPERATOR_NOT_EQUAL_TO},</li> <li>{@link
     *                 QueryObjectModelConstants#JCR_OPERATOR_LESS_THAN},</li> <li>{@link
     *                 QueryObjectModelConstants#JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO},</li>
     *                 <li>{@link QueryObjectModelConstants#JCR_OPERATOR_GREATER_THAN},</li>
     *                 <li>{@link QueryObjectModelConstants#JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO},
     *                 or</li> <li>{@link QueryObjectModelConstants#JCR_OPERATOR_LIKE}</li>
     *                 </ul>
     * @param operand2 the second operand; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Comparison comparison
            (
                    DynamicOperand operand1,
                    String operator,
                    StaticOperand operand2
            ) throws InvalidQueryException, RepositoryException;


    /**
     * Tests the existence of a property in the specified selector.
     * <p>
     * The query is invalid if: <ul> <li><code>selector</code> is not the name
     * of a selector in the query, or</li> <li><code>property</code> is not a
     * syntactically valid JCR name.</li> </ul>
     *
     * @param selectorName the selector name; non-null
     * @param propertyName the property name; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public PropertyExistence propertyExistence
            (
                    String selectorName,
                    String propertyName
            ) throws InvalidQueryException, RepositoryException;


    /**
     * Performs a full-text search against the specified selector.
     * <p>
     * The query is invalid if: <ul> <li><code>selector</code> is not the name
     * of a selector in the query, or</li> <li><code>property</code> is
     * specified but is not a syntactically valid JCR name, or</li>
     * <li><code>fullTextSearchExpression</code> does not evaluate to a JCR
     * STRING Value (or convertible to STRING) that conforms to the full text
     * search grammar.</li> </ul>
     * <p>
     * If <code>property</code> is specified but, for a node-tuple, the selector
     * node does not have a property named <code>property</code>, the query is
     * valid but the constraint is not satisfied.</li>
     *
     * @param selectorName             the selector name; non-null
     * @param propertyName             the property name, or null to search all full-text
     *                                 indexed properties of the node (or node subgraph, in some
     *                                 implementations)
     * @param fullTextSearchExpression the full-text search expression as a
     *                                 static operand; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public FullTextSearch fullTextSearch
            (
                    String selectorName,
                    String propertyName,
                    StaticOperand fullTextSearchExpression
            ) throws InvalidQueryException, RepositoryException;


    /**
     * Tests whether a node in the specified selector is reachable by a
     * specified absolute path.
     * <p>
     * The query is invalid if: <ul> <li><code>selector</code> is not the name
     * of a selector in the query, or</li> <li><code>path</code> is not a
     * syntactically valid absolute path.  Note, however, that if the path is
     * syntactically valid but does not identify a node in the repository (or
     * the node is not visible to this session, because of access control
     * constraints), the query is valid but the constraint is not
     * satisfied.</li> </ul>
     *
     * @param selectorName the selector name; non-null
     * @param path         an absolute path; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public SameNode sameNode(String selectorName, String path)
            throws InvalidQueryException, RepositoryException;

    /**
     * Tests whether a node in the specified selector is a child of a node
     * reachable by a specified absolute path. The query is invalid if: <ul>
     * <li><code>selector</code> is not the name of a selector in the query,
     * or</li> <li><code>path</code> is not a syntactically valid absolute path.
     * Note, however, that if the path is syntactically valid but does not
     * identify a node in the repository (or the node is not visible to this
     * session, because of access control constraints), the query is valid but
     * the constraint is not satisfied.</li> </ul>
     *
     * @param selectorName the selector name; non-null
     * @param path         an absolute path; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public ChildNode childNode(String selectorName, String path)
            throws InvalidQueryException, RepositoryException;

    /**
     * Tests whether a node in the specified selector is a descendant of a node
     * reachable by a specified absolute path.
     * <p>
     * The query is invalid if: <ul> <li><code>selector</code> is not the name
     * of a selector in the query, or</li> <li><code>path</code> is not a
     * syntactically valid absolute path.  Note, however, that if the path is
     * syntactically valid but does not identify a node in the repository (or
     * the node is not visible to this session, because of access control
     * constraints), the query is valid but the constraint is not
     * satisfied.</li> </ul>
     *
     * @param selectorName the selector name; non-null
     * @param path         an absolute path; non-null
     * @return the constraint; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public DescendantNode descendantNode(String selectorName, String path)
            throws InvalidQueryException, RepositoryException;

    ///
    /// OPERAND
    ///

    /**
     * Evaluates to the value (or values, if multi-valued) of a property in the
     * specified selector.
     * <p>
     * The query is invalid if: <ul> <li><code>selector</code> is not the name
     * of a selector in the query, or</li> <li><code>property</code> is not a
     * syntactically valid JCR name.</li> </ul>
     *
     * @param selectorName the selector name; non-null
     * @param propertyName the property name; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public PropertyValue propertyValue(String selectorName, String propertyName)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to the length (or lengths, if multi-valued) of a property.
     *
     * @param propertyValue the property value for which to compute the length;
     *                      non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Length length(PropertyValue propertyValue)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to a <code>NAME</code> value equal to the prefix-qualified name
     * of a node in the specified selector.
     * <p>
     * The query is invalid if <code>selector</code> is not the name of a
     * selector in the query.
     *
     * @param selectorName the selector name; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public NodeName nodeName(String selectorName)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to a <code>NAME</code> value equal to the local (unprefixed)
     * name of a node in the specified selector.
     * <p>
     * The query is invalid if <code>selector</code> is not the name of a
     * selector in the query.
     *
     * @param selectorName the selector name; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public NodeLocalName nodeLocalName(String selectorName)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to a <code>DOUBLE</code> value equal to the full-text search
     * score of a node in the specified selector.
     * <p>
     * The query is invalid if <code>selector</code> is not the name of a
     * selector in the query.
     *
     * @param selectorName the selector name; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public FullTextSearchScore fullTextSearchScore(String selectorName)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to the lower-case string value (or values, if multi-valued) of
     * an operand.
     *
     * @param operand the operand whose value is converted to a lower-case
     *                string; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public LowerCase lowerCase(DynamicOperand operand)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to the upper-case string value (or values, if multi-valued) of
     * an operand.
     *
     * @param operand the operand whose value is converted to a upper-case
     *                string; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public UpperCase upperCase(DynamicOperand operand)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to the value of a bind variable.
     * <p>
     * The query is invalid if <code>bindVariableName</code> is not a valid JCR
     * prefix.
     *
     * @param bindVariableName the bind variable name; non-null
     * @return the operand; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test.
     * @throws RepositoryException   if the operation otherwise fails
     */
    public BindVariableValue bindVariable(String bindVariableName)
            throws InvalidQueryException, RepositoryException;

    /**
     * Evaluates to a literal value.
     *
     * @param literalValue the value
     * @return the literal; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses
     *                               to perform that test (and not leave it
     *                               until later, on {@link #createQuery}),
     *                               and the parameter given fails that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Literal literal(Value literalValue)
            throws InvalidQueryException, RepositoryException;
    ///
    /// ORDERING
    ///

    /**
     * Orders by the value of the specified operand, in ascending order. The
     * query is invalid if <code>operand</code> does not evaluate to a scalar
     * value.
     *
     * @param operand the operand by which to order; non-null
     * @return the ordering
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Ordering ascending(DynamicOperand operand)
            throws InvalidQueryException, RepositoryException;

    /**
     * Orders by the value of the specified operand, in descending order. The
     * query is invalid if <code>operand</code> does not evaluate to a scalar
     * value.
     *
     * @param operand the operand by which to order; non-null
     * @return the ordering
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Ordering descending(DynamicOperand operand)
            throws InvalidQueryException, RepositoryException;

    ///
    /// COLUMN
    ///

    /**
     * Identifies a property in the specified selector to include in the tabular
     * view of query results.
     * <p>
     * The query is invalid if: <ul> <li><code>selectorName</code> is not the name
     * of a selector in the query, or</li>
     * <li><code>propertyName</code> is specified but does not evaluate to a scalar
     * value, or</li> <li><code>propertyName</code> is specified but
     * <code>columnName</code> is omitted, or</li> <li><code>propertyName</code> is
     * omitted but <code>columnName</code> is specified, or</li> <li>the columns
     * in the tabular view are not uniquely named, whether those column names
     * are specified by <code>columnName</code> (if <code>propertyName</code> is
     * specified) or generated as described above (if <code>propertyName</code> is
     * omitted).</li> </ul> If <code>propertyName</code> is specified but, for a
     * node-tuple, the selector node does not have a property named
     * <code>propertyName</code>, the query is valid and the column has null value.
     *
     * @param selectorName the selector name; non-null
     * @param propertyName the property name, or null to include a column for
     *                     each single-value non-residual property of the selector's node type
     * @param columnName   the column name; must be null if <code>propertyName</code> is null;
     *                     must be non-null if <code>propertyName</code> is non-null.
     * @return the column; non-null
     * @throws InvalidQueryException if a particular validity test is possible
     *                               on this method, the implemention chooses to perform that test (and not
     *                               leave it until later, on {@link #createQuery}), and the parameters given
     *                               fail that test
     * @throws RepositoryException   if the operation otherwise fails
     */
    public Column column(String selectorName,
                         String propertyName,
                         String columnName)
            throws InvalidQueryException, RepositoryException;
}
