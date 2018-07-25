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
package eu.uk.ncl.pet5o.esper.epl.table.strategy;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowPair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

public class ExprTableExprEvaluatorMethod extends ExprTableExprEvaluatorBase implements ExprEvaluator {

    private final int methodNum;

    public ExprTableExprEvaluatorMethod(ExprNode exprNode, String tableName, String subpropName, int streamNum, Class returnType, int methodNum) {
        super(exprNode, tableName, subpropName, streamNum, returnType);
        this.methodNum = methodNum;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprTableSubproperty(exprNode, tableName, subpropName);
        }

        ObjectArrayBackedEventBean oa = (ObjectArrayBackedEventBean) eventsPerStream[streamNum];
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow(oa);
        Object result = row.getMethods()[methodNum].getValue();

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprTableSubproperty(result);
        }
        return result;
    }

}
