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
package eu.uk.ncl.pet5o.esper.epl.expression.subquery;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

/**
 * Strategy for subselects with "=/!=/&gt;&lt; ALL".
 */
public abstract class SubselectEvalStrategyNREqualsBase extends SubselectEvalStrategyNRBase {
    protected final boolean isNot;
    protected final SimpleNumberCoercer coercer;

    public SubselectEvalStrategyNREqualsBase(ExprEvaluator valueEval, ExprEvaluator selectEval, boolean resultWhenNoMatchingEvents, boolean notIn, SimpleNumberCoercer coercer) {
        super(valueEval, selectEval, resultWhenNoMatchingEvents);
        this.isNot = notIn;
        this.coercer = coercer;
    }
}
