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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodEvalDeltaConstZero;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.view.*;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndexGetter;

import java.util.List;

/**
 * Factory for views for time-ordering events.
 */
public class TimeToLiveViewFactory implements DataWindowViewFactory, DataWindowViewWithPrevious {
    private List<ExprNode> viewParameters;

    /**
     * The timestamp expression.
     */
    protected ExprNode timestampExpression;
    protected ExprEvaluator timestampExpressionEvaluator;

    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        ExprNode[] validated = ViewFactorySupport.validate(getViewName(), parentEventType, statementContext, viewParameters, true);

        if (viewParameters.size() != 1) {
            throw new ViewParameterException(getViewParamMessage());
        }
        if (JavaClassHelper.getBoxedType(validated[0].getForge().getEvaluationType()) != Long.class) {
            throw new ViewParameterException(getViewParamMessage());
        }
        timestampExpression = validated[0];
        eventType = parentEventType;
        timestampExpressionEvaluator = ExprNodeCompiler.allocateEvaluator(timestampExpression.getForge(), statementContext.getEngineImportService(), TimeToLiveViewFactory.class, false, statementContext.getStatementName());
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        IStreamSortRankRandomAccess sortedRandomAccess = agentInstanceViewFactoryContext.getStatementContext().getViewServicePreviousFactory().getOptPreviousExprSortedRankedAccess(agentInstanceViewFactoryContext);
        return new TimeOrderView(agentInstanceViewFactoryContext, this, timestampExpression, timestampExpressionEvaluator, ExprTimePeriodEvalDeltaConstZero.INSTANCE, sortedRandomAccess);
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        if (!(view instanceof TimeToLiveViewFactory)) {
            return false;
        }
        TimeToLiveViewFactory other = (TimeToLiveViewFactory) view;
        return ExprNodeUtilityCore.deepEquals(other.getTimestampExpression(), timestampExpression, false);
    }

    public String getViewName() {
        return "Time-To-Live";
    }

    public ExprNode getTimestampExpression() {
        return timestampExpression;
    }

    public ExprEvaluator getTimestampExpressionEvaluator() {
        return timestampExpressionEvaluator;
    }

    private String getViewParamMessage() {
        return getViewName() + " view requires a single expression supplying long-type timestamp values as a parameter";
    }
}
