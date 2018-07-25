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
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;

import java.util.Collection;

public class AggregationServiceTable implements AggregationService {

    private final TableStateInstance tableState;

    public AggregationServiceTable(TableStateInstance tableState) {
        this.tableState = tableState;
    }

    public TableStateInstance getTableState() {
        return tableState;
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext) {
        throw handleNotSupported();
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext) {
        throw handleNotSupported();
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId, AggregationGroupByRollupLevel rollupLevel) {
        throw handleNotSupported();
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext) {
        throw handleNotSupported();
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        throw handleNotSupported();
    }

    public void accept(AggregationServiceVisitor visitor) {
        // no action
    }

    public void acceptGroupDetail(AggregationServiceVisitorWGroupDetail visitor) {
        // no action
    }

    public boolean isGrouped() {
        return false;
    }

    public Object getValue(int column, int agentInstanceId, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        throw handleNotSupported();
    }

    public Collection<EventBean> getCollectionOfEvents(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        throw handleNotSupported();
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEventBean(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        throw handleNotSupported();
    }

    public Object getGroupKey(int agentInstanceId) {
        throw handleNotSupported();
    }

    public Collection<Object> getGroupKeys(ExprEvaluatorContext exprEvaluatorContext) {
        throw handleNotSupported();
    }

    public Collection<Object> getCollectionScalar(int column, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        throw handleNotSupported();
    }

    private UnsupportedOperationException handleNotSupported() {
        return new UnsupportedOperationException("Operation not supported, aggregation server for reporting only");
    }

    public void stop() {
    }

    public AggregationService getContextPartitionAggregationService(int agentInstanceId) {
        return this;
    }
}
