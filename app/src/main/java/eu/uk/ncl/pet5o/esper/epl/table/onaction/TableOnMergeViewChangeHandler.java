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

import com.espertech.esper.collection.OneEventCollection;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.table.mgmt.TableMetadata;
import com.espertech.esper.event.NaturalEventBean;

public class TableOnMergeViewChangeHandler {
    private final TableMetadata tableMetadata;
    private OneEventCollection coll;

    public TableOnMergeViewChangeHandler(TableMetadata tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    public com.espertech.esper.client.EventBean[] getEvents() {
        if (coll == null) {
            return null;
        }
        return coll.toArray();
    }

    public void add(com.espertech.esper.client.EventBean theEvent, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (coll == null) {
            coll = new OneEventCollection();
        }
        if (theEvent instanceof NaturalEventBean) {
            theEvent = ((NaturalEventBean) theEvent).getOptionalSynthetic();
        }
        coll.add(tableMetadata.getEventToPublic().convert(theEvent, eventsPerStream, isNewData, context));
    }
}
