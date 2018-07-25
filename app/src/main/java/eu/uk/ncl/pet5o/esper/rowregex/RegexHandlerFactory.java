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
package eu.uk.ncl.pet5o.esper.rowregex;

import eu.uk.ncl.pet5o.esper.client.ConfigurationEngineDefaults;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.MatchRecognizeSpec;
import eu.uk.ncl.pet5o.esper.view.ViewFactoryChain;

import java.lang.annotation.Annotation;

/**
 * Service for creating match-recognize factory and state services.
 */
public interface RegexHandlerFactory {
    EventRowRegexNFAViewFactory makeViewFactory(ViewFactoryChain viewFactoryChain, MatchRecognizeSpec matchRecognizeSpec, AgentInstanceContext agentInstanceContext, boolean isUnbound, Annotation[] annotations, ConfigurationEngineDefaults.MatchRecognize matchRecognizeConfigs) throws ExprValidationException;

    RegexPartitionStateRepo makeSingle(RegexPartitionStateRandomAccessGetter prevGetter, AgentInstanceContext agentInstanceContext, EventRowRegexNFAView view, boolean keepScheduleState, RegexPartitionTerminationStateComparator terminationStateCompare);

    RegexPartitionStateRepo makePartitioned(RegexPartitionStateRandomAccessGetter prevGetter, RegexPartitionStateRepoGroupMeta stateRepoGroupMeta, AgentInstanceContext agentInstanceContext, EventRowRegexNFAView view, boolean keepScheduleState, RegexPartitionTerminationStateComparator terminationStateCompare);
}
