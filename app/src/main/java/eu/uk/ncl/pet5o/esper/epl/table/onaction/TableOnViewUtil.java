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
package eu.uk.ncl.pet5o.esper.epl.table.onaction;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;

public class TableOnViewUtil {
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] toPublic(eu.uk.ncl.pet5o.esper.client.EventBean[] matching, TableMetadata tableMetadata, eu.uk.ncl.pet5o.esper.client.EventBean[] triggers, boolean isNewData, ExprEvaluatorContext context) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[2];
        eventsPerStream[0] = triggers[0];

        eu.uk.ncl.pet5o.esper.client.EventBean[] events = new eu.uk.ncl.pet5o.esper.client.EventBean[matching.length];
        for (int i = 0; i < events.length; i++) {
            eventsPerStream[1] = matching[i];
            events[i] = tableMetadata.getEventToPublic().convert(matching[i], eventsPerStream, isNewData, context);
        }
        return events;
    }
}
