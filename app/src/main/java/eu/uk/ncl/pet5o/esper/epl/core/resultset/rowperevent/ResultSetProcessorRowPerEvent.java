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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent;

import com.espertech.esper.epl.core.resultset.core.ResultSetProcessor;
import com.espertech.esper.epl.core.select.SelectExprProcessor;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

public interface ResultSetProcessorRowPerEvent extends ResultSetProcessor {
    SelectExprProcessor getSelectExprProcessor();

    boolean hasHavingClause();

    boolean evaluateHavingClause(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext);
}
