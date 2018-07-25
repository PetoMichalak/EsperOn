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
package eu.uk.ncl.pet5o.esper.epl.table.upd;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateHelper;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventBean;

import java.util.Collection;
import java.util.Set;

public class TableUpdateStrategyIndexNonUnique implements TableUpdateStrategy {

    private final EventBeanUpdateHelper updateHelper;
    private final Set<String> affectedIndexNames;

    public TableUpdateStrategyIndexNonUnique(EventBeanUpdateHelper updateHelper, Set<String> affectedIndexNames) {
        this.updateHelper = updateHelper;
        this.affectedIndexNames = affectedIndexNames;
    }

    public void updateTable(Collection<EventBean> eventsUnsafeIter, TableStateInstance instance, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        // copy references to array - as it is allowed to pass an index-originating collection
        // and those same indexes are being changed now
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = new eu.uk.ncl.pet5o.esper.client.EventBean[eventsUnsafeIter.size()];
        int count = 0;
        for (eu.uk.ncl.pet5o.esper.client.EventBean event : eventsUnsafeIter) {
            events[count++] = event;
        }

        // remove from affected indexes
        for (String affectedIndexName : affectedIndexNames) {
            EventTable index = instance.getIndex(affectedIndexName);
            index.remove(events, instance.getAgentInstanceContext());
        }

        // update (no-copy unless original values required)
        for (eu.uk.ncl.pet5o.esper.client.EventBean event : events) {
            eventsPerStream[0] = event;
            ObjectArrayBackedEventBean updatedEvent = (ObjectArrayBackedEventBean) event;

            // if "initial.property" is part of the assignment expressions, provide initial value event
            if (updateHelper.isRequiresStream2InitialValueEvent()) {
                Object[] prev = new Object[updatedEvent.getProperties().length];
                System.arraycopy(updatedEvent.getProperties(), 0, prev, 0, prev.length);
                eventsPerStream[2] = new ObjectArrayEventBean(prev, updatedEvent.getEventType());
            }

            // apply in-place updates
            updateHelper.updateNoCopy(updatedEvent, eventsPerStream, exprEvaluatorContext);
            instance.handleRowUpdated(updatedEvent);
        }

        // add to affected indexes
        for (String affectedIndexName : affectedIndexNames) {
            EventTable index = instance.getIndex(affectedIndexName);
            index.add(events, instance.getAgentInstanceContext());
        }
    }
}
