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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

public class ExprNodeUtilExprStreamNumEnumSingleEval implements ExprEvaluator {
    private final ExprEnumerationEval enumeration;

    public ExprNodeUtilExprStreamNumEnumSingleEval(ExprEnumerationEval enumeration) {
        this.enumeration = enumeration;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return enumeration.evaluateGetEventBean(eventsPerStream, isNewData, context);
    }

}
