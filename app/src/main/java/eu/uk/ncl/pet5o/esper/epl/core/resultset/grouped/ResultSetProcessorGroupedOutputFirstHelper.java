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

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputHelper;
import eu.uk.ncl.pet5o.esper.epl.view.OutputConditionPolled;
import eu.uk.ncl.pet5o.esper.epl.view.OutputConditionPolledFactory;

public interface ResultSetProcessorGroupedOutputFirstHelper extends ResultSetProcessorOutputHelper {
    OutputConditionPolled getOrAllocate(Object mk, AgentInstanceContext agentInstanceContext, OutputConditionPolledFactory optionalOutputFirstConditionFactory);

    void remove(Object key);

    void destroy();
}
