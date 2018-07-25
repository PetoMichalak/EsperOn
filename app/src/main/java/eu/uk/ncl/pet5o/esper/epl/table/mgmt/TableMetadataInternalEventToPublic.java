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
package eu.uk.ncl.pet5o.esper.epl.table.mgmt;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowPair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.strategy.ExprTableEvalStrategyUtil;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;

public class TableMetadataInternalEventToPublic {

    private final ObjectArrayEventType publicEventType;
    private final TableMetadataColumnPairPlainCol[] plains;
    private final TableMetadataColumnPairAggMethod[] methods;
    private final TableMetadataColumnPairAggAccess[] accessors;
    private final EventAdapterService eventAdapterService;
    private final int numColumns;

    public TableMetadataInternalEventToPublic(ObjectArrayEventType publicEventType, TableMetadataColumnPairPlainCol[] plains, TableMetadataColumnPairAggMethod[] methods, TableMetadataColumnPairAggAccess[] accessors, EventAdapterService eventAdapterService) {
        this.publicEventType = publicEventType;
        this.plains = plains;
        this.methods = methods;
        this.accessors = accessors;
        this.eventAdapterService = eventAdapterService;
        this.numColumns = publicEventType.getPropertyDescriptors().length;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean convert(eu.uk.ncl.pet5o.esper.client.EventBean event, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object[] data = convertToUnd(event, eventsPerStream, isNewData, context);
        return eventAdapterService.adapterForType(data, publicEventType);
    }

    public Object[] convertToUnd(eu.uk.ncl.pet5o.esper.client.EventBean event, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean bean = (ObjectArrayBackedEventBean) event;
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow(bean);
        Object[] data = new Object[numColumns];
        for (TableMetadataColumnPairPlainCol plain : plains) {
            data[plain.getDest()] = bean.getProperties()[plain.getSource()];
        }
        int count = 0;
        for (TableMetadataColumnPairAggAccess access : accessors) {
            data[access.getDest()] = access.getAccessor().getValue(row.getStates()[count++], eventsPerStream, isNewData, context);
        }
        count = 0;
        for (TableMetadataColumnPairAggMethod method : methods) {
            data[method.getDest()] = row.getMethods()[count++].getValue();
        }
        return data;
    }
}
