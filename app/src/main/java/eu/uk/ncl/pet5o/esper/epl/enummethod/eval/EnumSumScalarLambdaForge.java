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
package eu.uk.ncl.pet5o.esper.epl.enummethod.eval;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;

public class EnumSumScalarLambdaForge extends EnumForgeBase {

    protected final ExprDotEvalSumMethodFactory sumMethodFactory;
    protected final ObjectArrayEventType resultEventType;

    public EnumSumScalarLambdaForge(ExprForge innerExpression, int streamCountIncoming, ExprDotEvalSumMethodFactory sumMethodFactory, ObjectArrayEventType resultEventType) {
        super(innerExpression, streamCountIncoming);
        this.sumMethodFactory = sumMethodFactory;
        this.resultEventType = resultEventType;
    }

    public EnumEval getEnumEvaluator() {
        return new EnumSumScalarLambdaForgeEval(this, innerExpression.getExprEvaluator());
    }

    public CodegenExpression codegen(EnumForgeCodegenParams premade, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return EnumSumScalarLambdaForgeEval.codegen(this, premade, codegenMethodScope, codegenClassScope);
    }
}
