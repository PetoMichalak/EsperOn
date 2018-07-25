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

public class EnumTakeLastForge implements EnumForge {

    protected ExprForge sizeEval;
    protected int numStreams;

    public EnumTakeLastForge(ExprForge sizeEval, int numStreams) {
        this.sizeEval = sizeEval;
        this.numStreams = numStreams;
    }

    public int getStreamNumSize() {
        return numStreams;
    }

    public EnumEval getEnumEvaluator() {
        return new EnumTakeLastForgeEval(sizeEval.getExprEvaluator());
    }

    public CodegenExpression codegen(EnumForgeCodegenParams premade, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return EnumTakeLastForgeEval.codegen(this, premade, codegenMethodScope, codegenClassScope);
    }
}
