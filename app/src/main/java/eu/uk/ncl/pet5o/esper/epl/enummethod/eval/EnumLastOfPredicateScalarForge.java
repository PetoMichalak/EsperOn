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
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;

public class EnumLastOfPredicateScalarForge extends EnumForgeBaseScalar {

    protected final EPType resultType;

    public EnumLastOfPredicateScalarForge(ExprForge innerExpression, int streamCountIncoming, ObjectArrayEventType type, EPType resultType) {
        super(innerExpression, streamCountIncoming, type);
        this.resultType = resultType;
    }

    public EnumEval getEnumEvaluator() {
        return new EnumLastOfPredicateScalarForgeEval(this, innerExpression.getExprEvaluator());
    }

    public CodegenExpression codegen(EnumForgeCodegenParams premade, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return EnumLastOfPredicateScalarForgeEval.codegen(this, premade, codegenMethodScope, codegenClassScope);
    }
}
