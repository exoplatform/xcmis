/*
 * Copyright 2009 Day Management AG, Switzerland. All rights reserved.
 */
package javax.jcr.query.qom;

import javax.jcr.Value;

/**
 * Evaluates to a literal value.
 *
 * @since JCR 2.0
 */
public interface Literal
        extends StaticOperand {
    /**
     * Gets the value of the literal.
     *
     * @return the value of the literal.
     */
    public Value getLiteralValue();
}
