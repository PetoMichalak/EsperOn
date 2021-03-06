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
package eu.uk.ncl.pet5o.esper.view.window;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodEvalDeltaConst;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodEvalDeltaConstFactory;
import eu.uk.ncl.pet5o.esper.view.*;

import java.util.List;

/**
 * Factory for {@link FirstTimeView}.
 */
public class FirstTimeViewFactory implements AsymetricDataWindowViewFactory, DataWindowBatchingViewFactory {
    private EventType eventType;

    /**
     * Number of msec before expiry.
     */
    protected ExprTimePeriodEvalDeltaConstFactory timeDeltaComputationFactory;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        if (expressionParameters.size() != 1) {
            throw new ViewParameterException(getViewParamMessage());
        }
        timeDeltaComputationFactory = ViewFactoryTimePeriodHelper.validateAndEvaluateTimeDeltaFactory(getViewName(), viewFactoryContext.getStatementContext(), expressionParameters.get(0), getViewParamMessage(), 0);
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        this.eventType = parentEventType;
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        ExprTimePeriodEvalDeltaConst timeDeltaComputation = timeDeltaComputationFactory.make(getViewName(), "view", agentInstanceViewFactoryContext.getAgentInstanceContext(), agentInstanceViewFactoryContext.getTimeAbacus());
        return new FirstTimeView(this, agentInstanceViewFactoryContext, timeDeltaComputation);
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        if (!(view instanceof FirstTimeView)) {
            return false;
        }

        FirstTimeView myView = (FirstTimeView) view;
        ExprTimePeriodEvalDeltaConst delta = timeDeltaComputationFactory.make(getViewName(), "view", agentInstanceContext, agentInstanceContext.getTimeAbacus());
        if (!delta.equalsTimePeriod(myView.getTimeDeltaComputation())) {
            return false;
        }
        return myView.isEmpty();
    }

    public String getViewName() {
        return "First-Time";
    }

    private String getViewParamMessage() {
        return getViewName() + " view requires a single numeric or time period parameter";
    }
}
