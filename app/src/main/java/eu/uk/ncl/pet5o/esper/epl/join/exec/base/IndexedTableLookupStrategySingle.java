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
package eu.uk.ncl.pet5o.esper.epl.join.exec.base;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.rep.Cursor;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableSingle;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyDesc;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyType;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.Set;

public class IndexedTableLookupStrategySingle implements JoinExecTableLookupStrategy {
    private final EventType eventType;
    private final String property;
    private final PropertyIndexedEventTableSingle index;
    private final EventPropertyGetter propertyGetter;

    /**
     * Ctor.
     *
     * @param eventType - event type to expect for lookup
     * @param index     - index to look up in
     * @param property  property name
     */
    public IndexedTableLookupStrategySingle(EventType eventType, String property, PropertyIndexedEventTableSingle index) {
        this.eventType = eventType;
        this.property = property;
        if (index == null) {
            throw new IllegalArgumentException("Unexpected null index received");
        }
        this.index = index;
        propertyGetter = EventBeanUtility.getAssertPropertyGetter(eventType, property);
    }

    /**
     * Returns event type of the lookup event.
     *
     * @return event type of the lookup event
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Returns index to look up in.
     *
     * @return index to use
     */
    public PropertyIndexedEventTableSingle getIndex() {
        return index;
    }

    public Set<EventBean> lookup(EventBean theEvent, Cursor cursor, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qIndexJoinLookup(this, index);
        }

        Object key = getKey(theEvent);

        if (InstrumentationHelper.ENABLED) {
            Set<EventBean> result = index.lookup(key);
            InstrumentationHelper.get().aIndexJoinLookup(result, key);
            return result;
        }
        return index.lookup(key);
    }

    public LookupStrategyDesc getStrategyDesc() {
        return new LookupStrategyDesc(LookupStrategyType.SINGLEPROP, new String[]{property});
    }

    private Object getKey(EventBean theEvent) {
        return propertyGetter.get(theEvent);
    }

    public String toString() {
        return "IndexedTableLookupStrategy indexProp=" + property +
                " index=(" + index + ')';
    }
}
