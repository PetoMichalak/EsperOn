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
package eu.uk.ncl.pet5o.esper.core.context.factory;

import eu.uk.ncl.pet5o.esper.core.context.activator.ViewableActivationResult;
import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyHolder;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousNode;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorNode;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowProcessorInstance;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.Collections;

public class StatementAgentInstanceFactoryCreateWindowResult extends StatementAgentInstanceFactoryResult {

    private final Viewable eventStreamParentViewable;
    private final StatementAgentInstancePostLoad postLoad;
    private final Viewable topView;
    private final NamedWindowProcessorInstance processorInstance;
    private final ViewableActivationResult viewableActivationResult;

    public StatementAgentInstanceFactoryCreateWindowResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, Viewable eventStreamParentViewable, StatementAgentInstancePostLoad postLoad, Viewable topView, NamedWindowProcessorInstance processorInstance, ViewableActivationResult viewableActivationResult) {
        super(finalView, stopCallback, agentInstanceContext,
                null, Collections.<ExprSubselectNode, SubSelectStrategyHolder>emptyMap(),
                Collections.<ExprPriorNode, ExprPriorEvalStrategy>emptyMap(),
                Collections.<ExprPreviousNode, ExprPreviousEvalStrategy>emptyMap(),
                null,
                Collections.<ExprTableAccessNode, ExprTableAccessEvalStrategy>emptyMap(),
                Collections.<StatementAgentInstancePreload>emptyList()
        );
        this.eventStreamParentViewable = eventStreamParentViewable;
        this.postLoad = postLoad;
        this.topView = topView;
        this.processorInstance = processorInstance;
        this.viewableActivationResult = viewableActivationResult;
    }

    public Viewable getEventStreamParentViewable() {
        return eventStreamParentViewable;
    }

    public StatementAgentInstancePostLoad getPostLoad() {
        return postLoad;
    }

    public Viewable getTopView() {
        return topView;
    }

    public NamedWindowProcessorInstance getProcessorInstance() {
        return processorInstance;
    }

    public ViewableActivationResult getViewableActivationResult() {
        return viewableActivationResult;
    }
}
