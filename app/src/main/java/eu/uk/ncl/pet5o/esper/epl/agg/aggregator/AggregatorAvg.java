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
package eu.uk.ncl.pet5o.esper.epl.agg.aggregator;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorCodegenUtil.cntRefCol;
import static eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorCodegenUtil.sumRefCol;

/**
 * Average that generates double-typed numbers.
 */
public class AggregatorAvg implements AggregationMethod {
    protected double sum;
    protected long cnt;

    public void clear() {
        sum = 0;
        cnt = 0;
    }

    public void enter(Object object) {
        if (object == null) {
            return;
        }
        cnt++;
        sum += ((Number) object).doubleValue();
    }

    public void leave(Object object) {
        if (object == null) {
            return;
        }
        if (cnt <= 1) {
            clear();
        } else {
            cnt--;
            sum -= ((Number) object).doubleValue();
        }
    }

    public Object getValue() {
        if (cnt == 0) {
            return null;
        }
        return sum / cnt;
    }

    public static void getValueCodegen(int column, CodegenMethodNode method) {
        method.getBlock()
                .ifCondition(equalsIdentity(cntRefCol(column), constant(0)))
                .blockReturn(constantNull())
                .methodReturn(op(sumRefCol(column), "/", cntRefCol(column)));
    }
}
