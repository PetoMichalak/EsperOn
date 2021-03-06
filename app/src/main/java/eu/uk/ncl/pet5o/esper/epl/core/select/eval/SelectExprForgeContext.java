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
package eu.uk.ncl.pet5o.esper.epl.core.select.eval;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

public class SelectExprForgeContext {
    private final ExprForge[] exprForges;
    private final String[] columnNames;
    private final EventAdapterService eventAdapterService;
    private final EventType[] eventTypes;

    public SelectExprForgeContext(ExprForge[] exprForges, String[] columnNames, EventAdapterService eventAdapterService, EventType[] eventTypes) {
        this.exprForges = exprForges;
        this.columnNames = columnNames;
        this.eventAdapterService = eventAdapterService;
        this.eventTypes = eventTypes;
    }

    public ExprForge[] getExprForges() {
        return exprForges;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public EventAdapterService getEventAdapterService() {
        return eventAdapterService;
    }

    public int getNumStreams() {
        return eventTypes.length;
    }

    public EventType[] getEventTypes() {
        return eventTypes;
    }
}
