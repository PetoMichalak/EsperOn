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
package eu.uk.ncl.pet5o.esper.epl.agg.service.common;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

/**
 * A null object implementation of the AggregationService
 * interface.
 */
public class AggregationServiceNull implements AggregationService {

    public final static AggregationServiceNull INSTANCE = new AggregationServiceNull();

    private AggregationServiceNull() {
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream,
                           Object optionalGroupKeyPerRow,
                           ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream,
                           Object optionalGroupKeyPerRow,
                           ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId, AggregationGroupByRollupLevel rollupLevel) {
    }

    public Object getValue(int column, int agentInstanceId, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public Collection<EventBean> getCollectionOfEvents(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection<Object> getCollectionScalar(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEventBean(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext) {
        // no state to clear
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

    public void accept(AggregationServiceVisitor visitor) {
    }

    public void acceptGroupDetail(AggregationServiceVisitorWGroupDetail visitor) {
    }

    public boolean isGrouped() {
        return false;
    }

    public Object getGroupKey(int agentInstanceId) {
        return null;
    }

    public Collection<Object> getGroupKeys(ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public void stop() {
    }

    public AggregationService getContextPartitionAggregationService(int agentInstanceId) {
        return this;
    }
}
