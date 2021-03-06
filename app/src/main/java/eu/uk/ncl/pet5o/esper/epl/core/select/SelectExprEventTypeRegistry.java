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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.service.StatementEventTypeRef;
import eu.uk.ncl.pet5o.esper.event.EventTypeSPI;

/**
 * Registry for event types creates as part of the select expression analysis.
 */
public class SelectExprEventTypeRegistry {
    private final String statementName;
    private final StatementEventTypeRef statementEventTypeRef;

    public SelectExprEventTypeRegistry(String statementName, StatementEventTypeRef statementEventTypeRef) {
        this.statementName = statementName;
        this.statementEventTypeRef = statementEventTypeRef;
    }

    /**
     * Adds an event type.
     *
     * @param eventType to add
     */
    public void add(EventType eventType) {
        if (!(eventType instanceof EventTypeSPI)) {
            return;
        }
        statementEventTypeRef.addReferences(statementName, new String[]{((EventTypeSPI) eventType).getMetadata().getPrimaryName()});
    }
}
