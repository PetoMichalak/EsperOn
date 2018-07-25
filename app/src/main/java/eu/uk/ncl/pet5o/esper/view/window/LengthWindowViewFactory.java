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
import eu.uk.ncl.pet5o.esper.collection.ViewUpdatedCollection;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.view.*;

import java.util.List;

/**
 * Factory for {@link LengthWindowView}.
 */
public class LengthWindowViewFactory implements DataWindowViewFactory, DataWindowViewWithPrevious {
    /**
     * Size of length window.
     */
    protected ExprEvaluator sizeEvaluator;

    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        sizeEvaluator = ViewFactorySupport.validateSizeSingleParam(getViewName(), viewFactoryContext, expressionParameters);
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        this.eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        int size = ViewFactorySupport.evaluateSizeParam(getViewName(), sizeEvaluator, agentInstanceViewFactoryContext.getAgentInstanceContext());
        ViewUpdatedCollection randomAccess = agentInstanceViewFactoryContext.getStatementContext().getViewServicePreviousFactory().getOptPreviousExprRandomAccess(agentInstanceViewFactoryContext);
        if (agentInstanceViewFactoryContext.isRemoveStream()) {
            return new LengthWindowViewRStream(agentInstanceViewFactoryContext, this, size);
        } else {
            return new LengthWindowView(agentInstanceViewFactoryContext, this, size, randomAccess);
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        if (!(view instanceof LengthWindowView)) {
            return false;
        }

        LengthWindowView myView = (LengthWindowView) view;
        int size = ViewFactorySupport.evaluateSizeParam(getViewName(), sizeEvaluator, agentInstanceContext);
        return myView.getSize() == size && myView.isEmpty();
    }

    public String getViewName() {
        return "Length";
    }
}
