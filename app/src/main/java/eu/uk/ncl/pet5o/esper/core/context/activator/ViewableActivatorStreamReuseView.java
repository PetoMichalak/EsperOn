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
package eu.uk.ncl.pet5o.esper.core.context.activator;

import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.ExprEvaluatorContextStatement;
import eu.uk.ncl.pet5o.esper.core.service.StatementAgentInstanceLock;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.spec.FilterStreamSpecCompiled;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.EventStream;

public class ViewableActivatorStreamReuseView implements ViewableActivator, StopCallback {

    private final EPServicesContext services;
    private final StatementContext statementContext;
    private final StatementSpecCompiled statementSpec;
    private final FilterStreamSpecCompiled filterStreamSpec;
    private final boolean join;
    private final ExprEvaluatorContextStatement evaluatorContextStmt;
    private final boolean filterSubselectSameStream;
    private final int streamNum;
    private final boolean isCanIterateUnbound;

    protected ViewableActivatorStreamReuseView(EPServicesContext services, StatementContext statementContext, StatementSpecCompiled statementSpec, FilterStreamSpecCompiled filterStreamSpec, boolean join, ExprEvaluatorContextStatement evaluatorContextStmt, boolean filterSubselectSameStream, int streamNum, boolean isCanIterateUnbound) {
        this.services = services;
        this.statementContext = statementContext;
        this.statementSpec = statementSpec;
        this.filterStreamSpec = filterStreamSpec;
        this.join = join;
        this.evaluatorContextStmt = evaluatorContextStmt;
        this.filterSubselectSameStream = filterSubselectSameStream;
        this.streamNum = streamNum;
        this.isCanIterateUnbound = isCanIterateUnbound;
    }

    public ViewableActivationResult activate(AgentInstanceContext agentInstanceContext, boolean isSubselect, boolean isRecoveringResilient) {
        Pair<EventStream, StatementAgentInstanceLock> pair = services.getStreamService().createStream(statementContext.getStatementId(), filterStreamSpec.getFilterSpec(),
                statementContext.getFilterService(),
                agentInstanceContext.getEpStatementAgentInstanceHandle(),
                join,
                agentInstanceContext,
                statementSpec.getOrderByList().length > 0,
                filterSubselectSameStream,
                statementContext.getAnnotations(),
                statementContext.isStatelessSelect(),
                streamNum,
                isCanIterateUnbound);
        return new ViewableActivationResult(pair.getFirst(), this, pair.getSecond(), null, null, false, false, null);
    }

    public void stop() {
        services.getStreamService().dropStream(filterStreamSpec.getFilterSpec(), statementContext.getFilterService(), join, statementSpec.getOrderByList().length > 0, filterSubselectSameStream, statementContext.isStatelessSelect());
    }

    public FilterStreamSpecCompiled getFilterStreamSpec() {
        return filterStreamSpec;
    }
}
