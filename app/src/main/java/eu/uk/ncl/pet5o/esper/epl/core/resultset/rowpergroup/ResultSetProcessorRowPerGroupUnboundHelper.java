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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowRemovedCallback;

import java.util.Iterator;

public interface ResultSetProcessorRowPerGroupUnboundHelper extends AggregationRowRemovedCallback {

    void put(Object key, eu.uk.ncl.pet5o.esper.client.EventBean event);

    void removedAggregationGroupKey(Object key);

    Iterator<EventBean> valueIterator();

    void destroy();
}
