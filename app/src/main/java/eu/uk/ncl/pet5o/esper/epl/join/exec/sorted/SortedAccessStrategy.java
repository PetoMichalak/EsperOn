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
package eu.uk.ncl.pet5o.esper.epl.join.exec.sorted;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertySortedEventTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public interface SortedAccessStrategy {
    public Set<EventBean> lookup(EventBean theEvent, PropertySortedEventTable index, ExprEvaluatorContext context);

    public Set<EventBean> lookupCollectKeys(EventBean theEvent, PropertySortedEventTable index, ExprEvaluatorContext context, ArrayList<Object> keys);

    public Collection<EventBean> lookup(EventBean[] eventsPerStream, PropertySortedEventTable index, ExprEvaluatorContext context);

    public Collection<EventBean> lookupCollectKeys(EventBean[] eventsPerStream, PropertySortedEventTable index, ExprEvaluatorContext context, ArrayList<Object> keys);

    public String toQueryPlan();
}
