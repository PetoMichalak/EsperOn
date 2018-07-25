/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.epl.join.base;

import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.view.internal.BufferView;

/**
 * Implements a method for pre-loading (initializing) join that does not return any events.
 */
public class JoinPreloadMethodNull implements JoinPreloadMethod {
    /**
     * Ctor.
     */
    public JoinPreloadMethodNull() {
    }

    public void preloadFromBuffer(int stream, ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void preloadAggregation(ResultSetProcessor resultSetProcessor) {
    }

    public void setBuffer(BufferView buffer, int i) {
    }

    @Override
    public boolean isPreloading() {
        return false;
    }
}
