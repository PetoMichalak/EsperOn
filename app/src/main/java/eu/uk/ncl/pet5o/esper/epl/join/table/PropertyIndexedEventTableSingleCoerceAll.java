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
package eu.uk.ncl.pet5o.esper.epl.join.table;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

import java.util.Set;

public class PropertyIndexedEventTableSingleCoerceAll extends PropertyIndexedEventTableSingleCoerceAdd {
    private final Class coercionType;

    public PropertyIndexedEventTableSingleCoerceAll(EventPropertyGetter propertyGetter, EventTableOrganization organization, SimpleNumberCoercer coercer, Class coercionType) {
        super(propertyGetter, organization, coercer, coercionType);
        this.coercionType = coercionType;
    }

    /**
     * Returns the set of events that have the same property value as the given event.
     *
     * @return set of events with property value, or null if none found (never returns zero-sized set)
     */
    public Set<EventBean> lookup(Object key) {
        key = EventBeanUtility.coerce(key, coercionType);
        return propertyIndex.get(key);
    }
}
