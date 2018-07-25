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
package eu.uk.ncl.pet5o.esper.view;

import eu.uk.ncl.pet5o.esper.core.service.ExprEvaluatorContextStatement;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeService;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.time.*;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

public class ViewFactoryTimePeriodHelper {
    public static ExprTimePeriodEvalDeltaConstFactory validateAndEvaluateTimeDeltaFactory(String viewName,
                                                                                          StatementContext statementContext,
                                                                                          ExprNode expression,
                                                                                          String expectedMessage,
                                                                                          int expressionNumber)
            throws ViewParameterException {
        StreamTypeService streamTypeService = new StreamTypeServiceImpl(statementContext.getEngineURI(), false);
        ExprTimePeriodEvalDeltaConstFactory factory;
        if (expression instanceof ExprTimePeriod) {
            ExprTimePeriod validated = (ExprTimePeriod) ViewFactorySupport.validateExpr(viewName, statementContext, expression, streamTypeService, expressionNumber);
            factory = validated.constEvaluator(new ExprEvaluatorContextStatement(statementContext, false));
        } else {
            ExprNode validated = ViewFactorySupport.validateExpr(viewName, statementContext, expression, streamTypeService, expressionNumber);
            ExprEvaluator secondsEvaluator = validated.getForge().getExprEvaluator();
            Class returnType = JavaClassHelper.getBoxedType(validated.getForge().getEvaluationType());
            if (!JavaClassHelper.isNumeric(returnType)) {
                throw new ViewParameterException(expectedMessage);
            }
            if (validated.isConstantResult()) {
                Number time = (Number) ViewFactorySupport.evaluate(secondsEvaluator, 0, viewName, statementContext);
                if (!ExprTimePeriodUtil.validateTime(time, statementContext.getTimeAbacus())) {
                    throw new ViewParameterException(ExprTimePeriodUtil.getTimeInvalidMsg(viewName, "view", time));
                }
                long msec = statementContext.getTimeAbacus().deltaForSecondsNumber(time);
                factory = new ExprTimePeriodEvalDeltaConstGivenDelta(msec);
            } else {
                factory = new ExprTimePeriodEvalDeltaConstFactoryMsec(secondsEvaluator, statementContext.getTimeAbacus());
            }
        }
        return factory;
    }
}
