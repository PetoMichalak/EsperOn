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
package eu.uk.ncl.pet5o.esper.epl.lookup;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

/**
 * A lookup strategy that receives an additional match expression.
 */
public interface SubordWMatchExprLookupStrategy {
    /**
     * Determines the events.
     *
     * @param newData              is the correlation events
     * @param exprEvaluatorContext expression evaluation context
     * @return the events
     */
    public EventBean[] lookup(EventBean[] newData, ExprEvaluatorContext exprEvaluatorContext);

    public String toQueryPlan();
}
