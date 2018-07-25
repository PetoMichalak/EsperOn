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
package eu.uk.ncl.pet5o.esper.view.ext;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodEvalDeltaConst;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodEvalDeltaConstFactory;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.view.*;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndexGetter;

import java.util.List;

/**
 * Factory for views for time-ordering events.
 */
public class TimeOrderViewFactory implements DataWindowViewFactory, DataWindowViewWithPrevious {
    private List<ExprNode> viewParameters;

    /**
     * The timestamp expression.
     */
    protected ExprNode timestampExpression;

    protected ExprEvaluator timestampExpressionEvaluator;

    /**
     * The interval to wait for newer events to arrive.
     */
    protected ExprTimePeriodEvalDeltaConstFactory timeDeltaComputationFactory;

    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        ExprNode[] validated = ViewFactorySupport.validate(getViewName(), parentEventType, statementContext, viewParameters, true);

        if (viewParameters.size() != 2) {
            throw new ViewParameterException(getViewParamMessage());
        }

        if (!JavaClassHelper.isNumeric(validated[0].getForge().getEvaluationType())) {
            throw new ViewParameterException(getViewParamMessage());
        }
        timestampExpression = validated[0];
        timeDeltaComputationFactory = ViewFactoryTimePeriodHelper.validateAndEvaluateTimeDeltaFactory(getViewName(), statementContext, viewParameters.get(1), getViewParamMessage(), 1);
        timestampExpressionEvaluator = ExprNodeCompiler.allocateEvaluator(timestampExpression.getForge(), statementContext.getEngineImportService(), TimeOrderViewFactory.class, false, statementContext.getStatementName());
        eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        ExprTimePeriodEvalDeltaConst timeDeltaComputation = timeDeltaComputationFactory.make(getViewName(), "view", agentInstanceViewFactoryContext.getAgentInstanceContext(), agentInstanceViewFactoryContext.getTimeAbacus());
        IStreamSortRankRandomAccess sortedRandomAccess = agentInstanceViewFactoryContext.getStatementContext().getViewServicePreviousFactory().getOptPreviousExprSortedRankedAccess(agentInstanceViewFactoryContext);
        return new TimeOrderView(agentInstanceViewFactoryContext, this, timestampExpression, timestampExpressionEvaluator, timeDeltaComputation, sortedRandomAccess);
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        if (!(view instanceof TimeOrderView)) {
            return false;
        }

        TimeOrderView other = (TimeOrderView) view;
        ExprTimePeriodEvalDeltaConst timeDeltaComputation = timeDeltaComputationFactory.make(getViewName(), "view", agentInstanceContext, agentInstanceContext.getTimeAbacus());
        if ((!timeDeltaComputation.equalsTimePeriod(other.getTimeDeltaComputation())) ||
                (!ExprNodeUtilityCore.deepEquals(other.getTimestampExpression(), timestampExpression, false))) {
            return false;
        }

        return other.isEmpty();
    }

    public String getViewName() {
        return "Time-Order";
    }

    public ExprEvaluator getTimestampExpressionEvaluator() {
        return timestampExpressionEvaluator;
    }

    private String getViewParamMessage() {
        return getViewName() + " view requires the expression supplying timestamp values, and a numeric or time period parameter for interval size";
    }
}
