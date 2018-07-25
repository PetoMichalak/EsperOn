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
package eu.uk.ncl.pet5o.esper.pattern;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSetParam;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMapMeta;

import java.util.IdentityHashMap;

/**
 * Default pattern context factory.
 */
public class PatternContextFactoryDefault implements PatternContextFactory {
    public PatternContext createContext(StatementContext statementContext,
                                        int streamId,
                                        EvalRootFactoryNode rootNode,
                                        MatchedEventMapMeta matchedEventMapMeta,
                                        boolean allowResilient) {
        return new PatternContext(statementContext, streamId, matchedEventMapMeta, false);
    }

    public PatternAgentInstanceContext createPatternAgentContext(PatternContext patternContext, AgentInstanceContext agentInstanceContext, boolean hasConsumingFilter, IdentityHashMap<FilterSpecCompiled, FilterValueSetParam[][]> filterAddendum) {
        return new PatternAgentInstanceContext(patternContext, agentInstanceContext, hasConsumingFilter, filterAddendum);
    }
}
