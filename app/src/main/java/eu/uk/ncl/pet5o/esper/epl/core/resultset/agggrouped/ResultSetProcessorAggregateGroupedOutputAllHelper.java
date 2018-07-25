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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.agggrouped;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputHelper;

import java.util.Set;

public interface ResultSetProcessorAggregateGroupedOutputAllHelper extends ResultSetProcessorOutputHelper {
    void processView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic);

    void processJoin(Set<MultiKey<EventBean>> newData, Set<MultiKey<EventBean>> oldData, boolean isGenerateSynthetic);

    UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputView(boolean isSynthesize);

    UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputJoin(boolean isSynthesize);

    void remove(Object key);

    void destroy();
}
