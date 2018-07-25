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
package eu.uk.ncl.pet5o.esper.view.stat;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.view.*;

import java.util.List;

/**
 * Factory for {@link CorrelationView} instances.
 */
public class CorrelationViewFactory implements ViewFactory {
    private List<ExprNode> viewParameters;
    private int streamNumber;

    protected ExprNode expressionX;
    protected ExprEvaluator expressionXEval;
    protected ExprNode expressionY;
    protected ExprEvaluator expressionYEval;

    /**
     * Additional properties.
     */
    protected StatViewAdditionalProps additionalProps;

    /**
     * Event type.
     */
    protected EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        this.viewParameters = expressionParameters;
        this.streamNumber = viewFactoryContext.getStreamNum();
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        ExprNode[] validated = ViewFactorySupport.validate(getViewName(), parentEventType, statementContext, viewParameters, true);
        if (validated.length < 2) {
            throw new ViewParameterException(getViewParamMessage());
        }
        if ((!JavaClassHelper.isNumeric(validated[0].getForge().getEvaluationType())) || (!JavaClassHelper.isNumeric(validated[1].getForge().getEvaluationType()))) {
            throw new ViewParameterException(getViewParamMessage());
        }

        expressionX = validated[0];
        expressionXEval = ExprNodeCompiler.allocateEvaluator(expressionX.getForge(), statementContext.getEngineImportService(), RegressionLinestViewFactory.class, false, statementContext.getStatementName());
        expressionY = validated[1];
        expressionYEval = ExprNodeCompiler.allocateEvaluator(expressionY.getForge(), statementContext.getEngineImportService(), RegressionLinestViewFactory.class, false, statementContext.getStatementName());

        additionalProps = StatViewAdditionalProps.make(validated, 2, parentEventType, statementContext.getEngineImportService(), statementContext.getStatementName());
        eventType = CorrelationView.createEventType(statementContext, additionalProps, streamNumber);
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        return new CorrelationView(this, agentInstanceViewFactoryContext.getAgentInstanceContext(), expressionX, expressionXEval, expressionY, expressionYEval, eventType, additionalProps);
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        if (!(view instanceof CorrelationView)) {
            return false;
        }

        if (additionalProps != null) {
            return false;
        }

        CorrelationView other = (CorrelationView) view;
        if (!ExprNodeUtilityCore.deepEquals(other.getExpressionX(), expressionX, false) ||
                (!ExprNodeUtilityCore.deepEquals(other.getExpressionY(), expressionY, false))) {
            return false;
        }

        return true;
    }

    public String getViewName() {
        return "Correlation";
    }

    public StatViewAdditionalProps getAdditionalProps() {
        return additionalProps;
    }

    private String getViewParamMessage() {
        return getViewName() + " view requires two expressions providing x and y values as properties";
    }
}
