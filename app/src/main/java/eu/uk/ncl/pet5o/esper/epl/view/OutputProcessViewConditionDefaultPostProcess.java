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
package eu.uk.ncl.pet5o.esper.epl.view;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;

/**
 * A view that prepares output events, batching incoming
 * events and invoking the result set processor as necessary.
 * <p>
 * Handles output rate limiting or stabilizing.
 */
public class OutputProcessViewConditionDefaultPostProcess extends OutputProcessViewConditionDefault {
    private final OutputStrategyPostProcess postProcessor;

    public OutputProcessViewConditionDefaultPostProcess(ResultSetProcessor resultSetProcessor, Long afterConditionTime, Integer afterConditionNumberOfEvents, boolean afterConditionSatisfied, OutputProcessViewConditionFactory parent, AgentInstanceContext agentInstanceContext, OutputStrategyPostProcess postProcessor, boolean isJoin, ResultSetProcessorHelperFactory resultSetProcessorHelperFactory) {
        super(resultSetProcessorHelperFactory, resultSetProcessor, afterConditionTime, afterConditionNumberOfEvents, afterConditionSatisfied, parent, agentInstanceContext, isJoin);
        this.postProcessor = postProcessor;
    }

    @Override
    public void output(boolean forceUpdate, UniformPair<EventBean[]> results) {
        // Child view can be null in replay from named window
        if (childView != null) {
            postProcessor.output(forceUpdate, results, childView);
        }
    }
}
