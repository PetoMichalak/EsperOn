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
package eu.uk.ncl.pet5o.esper.epl.agg.codegen;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

public interface AggregationRowGenerated {
    void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext);
    void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext);
    void clear();
    Object getValue(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext);
    Collection<EventBean> getCollectionOfEvents(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
    eu.uk.ncl.pet5o.esper.client.EventBean getEventBean(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
    Collection<Object> getCollectionScalar(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
    void increaseRefcount();
    void decreaseRefcount();
    long getRefcount();
}
