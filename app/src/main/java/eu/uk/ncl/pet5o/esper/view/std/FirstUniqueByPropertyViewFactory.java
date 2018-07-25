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
package eu.uk.ncl.pet5o.esper.view.std;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;
import eu.uk.ncl.pet5o.esper.view.*;

import java.util.List;
import java.util.Set;

/**
 * Factory for {@link FirstUniqueByPropertyView} instances.
 */
public class FirstUniqueByPropertyViewFactory implements AsymetricDataWindowViewFactory, DataWindowViewFactoryUniqueCandidate {
    public static final String NAME = "First-Unique-By";

    /**
     * View parameters.
     */
    protected List<ExprNode> viewParameters;

    /**
     * Property name to evaluate unique values.
     */
    protected ExprNode[] criteriaExpressions;
    protected ExprEvaluator[] criteriaExpressionEvals;

    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        this.viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        criteriaExpressions = ViewFactorySupport.validate(getViewName(), parentEventType, statementContext, viewParameters, false);

        if (criteriaExpressions.length == 0) {
            String errorMessage = getViewName() + " view requires a one or more expressions provinding unique values as parameters";
            throw new ViewParameterException(errorMessage);
        }

        this.eventType = parentEventType;
        this.criteriaExpressionEvals = ExprNodeUtilityRich.getEvaluatorsMayCompile(criteriaExpressions, statementContext.getEngineImportService(), this.getClass(), false, statementContext.getStatementName());
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        return new FirstUniqueByPropertyView(this, agentInstanceViewFactoryContext);
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        if (!(view instanceof FirstUniqueByPropertyView)) {
            return false;
        }

        FirstUniqueByPropertyView myView = (FirstUniqueByPropertyView) view;
        if (!ExprNodeUtilityCore.deepEquals(criteriaExpressions, myView.getUniqueCriteria(), false)) {
            return false;
        }

        return myView.isEmpty();
    }

    public Set<String> getUniquenessCandidatePropertyNames() {
        return ExprNodeUtilityCore.getPropertyNamesIfAllProps(criteriaExpressions);
    }

    public String getViewName() {
        return NAME;
    }

    public ExprNode[] getCriteriaExpressions() {
        return criteriaExpressions;
    }

    public ExprEvaluator[] getCriteriaExpressionEvals() {
        return criteriaExpressionEvals;
    }
}
