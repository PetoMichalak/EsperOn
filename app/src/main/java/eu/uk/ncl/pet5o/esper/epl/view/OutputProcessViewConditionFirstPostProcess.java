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
 * Handles output rate limiting for FIRST, only applicable with a having-clause and no group-by clause.
 * <p>
 * Without having-clause the order of processing won't matter therefore its handled by the
 * {@link OutputProcessViewConditionDefault}. With group-by the {@link ResultSetProcessor} handles the per-group first criteria.
 */
public class OutputProcessViewConditionFirstPostProcess extends OutputProcessViewConditionFirst {
    private final OutputStrategyPostProcess postProcessor;

    public OutputProcessViewConditionFirstPostProcess(ResultSetProcessorHelperFactory resultSetProcessorHelperFactory, ResultSetProcessor resultSetProcessor, Long afterConditionTime, Integer afterConditionNumberOfEvents, boolean afterConditionSatisfied, OutputProcessViewConditionFactory parent, AgentInstanceContext agentInstanceContext, OutputStrategyPostProcess postProcessor) {
        super(resultSetProcessorHelperFactory, resultSetProcessor, afterConditionTime, afterConditionNumberOfEvents, afterConditionSatisfied, parent, agentInstanceContext);
        this.postProcessor = postProcessor;
    }

    public void output(boolean forceUpdate, UniformPair<EventBean[]> results) {
        // Child view can be null in replay from named window
        if (childView != null) {
            postProcessor.output(forceUpdate, results, childView);
        }
    }
}
