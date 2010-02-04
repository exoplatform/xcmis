/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

/**
 * Evaluates to the value of a bind variable.
 * <p>
 *
 * @since JCR 2.0
 */
public interface BindVariableValue
        extends StaticOperand {
    /**
     * Gets the name of the bind variable.
     *
     * @return the bind variable name; non-null
     */
    public String getBindVariableName();
}
