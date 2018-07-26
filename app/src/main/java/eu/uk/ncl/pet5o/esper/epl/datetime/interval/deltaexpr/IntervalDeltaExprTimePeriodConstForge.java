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
package eu.uk.ncl.pet5o.esper.epl.datetime.interval.deltaexpr;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalDeltaExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalDeltaExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodEvalDeltaConst;

public class IntervalDeltaExprTimePeriodConstForge implements IntervalDeltaExprForge, IntervalDeltaExprEvaluator {

    private final ExprTimePeriodEvalDeltaConst timerPeriodConst;

    public IntervalDeltaExprTimePeriodConstForge(ExprTimePeriodEvalDeltaConst timerPeriodConst) {
        this.timerPeriodConst = timerPeriodConst;
    }

    public IntervalDeltaExprEvaluator makeEvaluator() {
        return this;
    }

    public long evaluate(long reference, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return timerPeriodConst.deltaAdd(reference);
    }

    public CodegenExpression codegen(CodegenExpression reference, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return timerPeriodConst.deltaAddCodegen(reference, codegenMethodScope, codegenClassScope);
    }
}
