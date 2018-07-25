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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessorOutputHelper;

import java.util.Set;

public interface ResultSetProcessorRowPerGroupRollupOutputLastHelper extends ResultSetProcessorOutputHelper {
    void processView(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isGenerateSynthetic);

    void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic);

    UniformPair<com.espertech.esper.client.EventBean[]> outputView(boolean isSynthesize);

    UniformPair<com.espertech.esper.client.EventBean[]> outputJoin(boolean isSynthesize);

    void destroy();
}
