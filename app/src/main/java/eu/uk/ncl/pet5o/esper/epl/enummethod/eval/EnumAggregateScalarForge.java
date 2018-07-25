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

public class EnumAggregateScalarForge extends EnumAggregateForge {
    protected final ObjectArrayEventType evalEventType;

    public EnumAggregateScalarForge(ExprForge initialization, ExprForge innerExpression, int streamNumLambda, ObjectArrayEventType resultEventType, ObjectArrayEventType evalEventType) {
        super(initialization, innerExpression, streamNumLambda, resultEventType);
        this.evalEventType = evalEventType;
    }

    public EnumEval getEnumEvaluator() {
        return new EnumAggregateScalarForgeEval(this, initialization.getExprEvaluator(), innerExpression.getExprEvaluator());
    }

    public CodegenExpression codegen(EnumForgeCodegenParams premade, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return EnumAggregateScalarForgeEval.codegen(this, premade, codegenMethodScope, codegenClassScope);
    }

    public ObjectArrayEventType getEvalEventType() {
        return evalEventType;
    }
}
