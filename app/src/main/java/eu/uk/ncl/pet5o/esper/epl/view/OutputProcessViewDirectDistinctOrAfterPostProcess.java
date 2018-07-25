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
import eu.uk.ncl.pet5o.esper.core.service.UpdateDispatchView;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;

/**
 * Output process view that does not enforce any output policies and may simply
 * hand over events to child views, but works with distinct and after-output policies
 */
public class OutputProcessViewDirectDistinctOrAfterPostProcess extends OutputProcessViewDirectDistinctOrAfter {
    private final OutputStrategyPostProcess postProcessor;

    public OutputProcessViewDirectDistinctOrAfterPostProcess(ResultSetProcessorHelperFactory resultSetProcessorHelperFactory, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor, Long afterConditionTime, Integer afterConditionNumberOfEvents, boolean afterConditionSatisfied, OutputProcessViewDirectDistinctOrAfterFactory parent, OutputStrategyPostProcess postProcessor) {
        super(resultSetProcessorHelperFactory, agentInstanceContext, resultSetProcessor, afterConditionTime, afterConditionNumberOfEvents, afterConditionSatisfied, parent);
        this.postProcessor = postProcessor;
    }

    @Override
    protected void postProcess(boolean force, UniformPair<EventBean[]> newOldEvents, UpdateDispatchView childView) {
        postProcessor.output(force, newOldEvents, childView);
    }
}
