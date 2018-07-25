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

import eu.uk.ncl.pet5o.esper.collection.ViewUpdatedCollection;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventBean;
import eu.uk.ncl.pet5o.esper.view.View;
import eu.uk.ncl.pet5o.esper.view.ViewFactoryContext;
import eu.uk.ncl.pet5o.esper.view.ViewParameterException;

import java.util.List;

/**
 * Factory for {@link eu.uk.ncl.pet5o.esper.view.window.ExpressionWindowView}.
 */
public class ExpressionWindowViewFactory extends ExpressionViewFactoryBase {
    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
        if (expressionParameters.size() != 1) {
            String errorMessage = getViewName() + " view requires a single expression as a parameter";
            throw new ViewParameterException(errorMessage);
        }
        expiryExpression = expressionParameters.get(0);
    }

    public View makeView(final AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        ObjectArrayEventBean builtinBean = new ObjectArrayEventBean(ExpressionViewOAFieldEnum.getPrototypeOA(), builtinMapType);
        ViewUpdatedCollection randomAccess = agentInstanceViewFactoryContext.getStatementContext().getViewServicePreviousFactory().getOptPreviousExprRandomAccess(agentInstanceViewFactoryContext);
        return new ExpressionWindowView(this, randomAccess, expiryExpressionEvaluator, aggregationServiceFactoryDesc, builtinBean, variableNames, agentInstanceViewFactoryContext);
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public String getViewName() {
        return "Expression";
    }
}
