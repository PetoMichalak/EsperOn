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
package eu.uk.ncl.pet5o.esper.core.start;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstanceFactoryNoAgentInstance;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.view.ViewProcessingException;
import eu.uk.ncl.pet5o.esper.view.Viewable;
import eu.uk.ncl.pet5o.esper.view.ZeroDepthStreamNoIterate;

import java.util.Collections;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodCreateExpression extends EPStatementStartMethodBase {
    public EPStatementStartMethodCreateExpression(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        String expressionName = services.getExprDeclaredService().addExpressionOrScript(statementSpec.getCreateExpressionDesc());

        // define output event type
        String typeName = "EventType_Expression_" + expressionName;
        EventType resultType = services.getEventAdapterService().createAnonymousMapType(typeName, Collections.<String, Object>emptyMap(), true);

        EPStatementStopMethod stopMethod = new EPStatementStopMethod() {
            public void stop() {
                // no action
            }
        };

        EPStatementDestroyMethod destroyMethod = new EPStatementDestroyMethod() {
            public void destroy() {
                services.getExprDeclaredService().destroyedExpression(statementSpec.getCreateExpressionDesc());
            }
        };

        Viewable resultView = new ZeroDepthStreamNoIterate(resultType);
        statementContext.setStatementAgentInstanceFactory(new StatementAgentInstanceFactoryNoAgentInstance(resultView));

        return new EPStatementStartResult(resultView, stopMethod, destroyMethod);
    }
}
