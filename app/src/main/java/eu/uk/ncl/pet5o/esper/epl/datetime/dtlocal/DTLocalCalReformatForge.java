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
package eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.datetime.reformatop.ReformatForge;
import eu.uk.ncl.pet5o.esper.epl.datetime.reformatop.ReformatOp;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Calendar;

public class DTLocalCalReformatForge extends DTLocalReformatForgeBase {
    public DTLocalCalReformatForge(ReformatForge reformatForge) {
        super(reformatForge);
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalCalReformatEval(reformatForge.getOp());
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return reformatForge.codegenCal(inner, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    private static class DTLocalCalReformatEval extends DTLocalReformatEvalBase {
        private DTLocalCalReformatEval(ReformatOp reformatOp) {
            super(reformatOp);
        }

        public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
            return reformatOp.evaluate((Calendar) target, eventsPerStream, isNewData, exprEvaluatorContext);
        }
    }
}
