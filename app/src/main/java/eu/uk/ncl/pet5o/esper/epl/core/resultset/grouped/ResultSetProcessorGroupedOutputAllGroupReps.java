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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessorOutputHelper;

import java.util.Iterator;
import java.util.Map;

public interface ResultSetProcessorGroupedOutputAllGroupReps extends ResultSetProcessorOutputHelper {

    Object put(Object mk, com.espertech.esper.client.EventBean[] array);

    void remove(Object key);

    Iterator<Map.Entry<Object, EventBean[]>> entryIterator();

    void destroy();
}
