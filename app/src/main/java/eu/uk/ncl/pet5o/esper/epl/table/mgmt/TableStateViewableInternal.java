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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.util.FilteredEventIterator;
import com.espertech.esper.view.ViewSupport;

import java.util.Iterator;

public class TableStateViewableInternal extends ViewSupport {

    private final TableMetadata tableMetadata;
    private final TableStateInstance tableStateInstance;
    private final ExprEvaluator[] optionalTableFilters;

    public TableStateViewableInternal(TableMetadata tableMetadata, TableStateInstance tableStateInstance, ExprEvaluator[] optionalTableFilters) {
        this.tableMetadata = tableMetadata;
        this.tableStateInstance = tableStateInstance;
        this.optionalTableFilters = optionalTableFilters;
    }

    public void update(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData) {
        // no action required
    }

    public EventType getEventType() {
        return tableMetadata.getInternalEventType();
    }

    public Iterator<EventBean> iterator() {
        Iterator<EventBean> it = tableStateInstance.getEventCollection().iterator();
        if (optionalTableFilters != null) {
            return new FilteredEventIterator(optionalTableFilters, it, tableStateInstance.getAgentInstanceContext());
        }
        return tableStateInstance.getEventCollection().iterator();
    }
}
