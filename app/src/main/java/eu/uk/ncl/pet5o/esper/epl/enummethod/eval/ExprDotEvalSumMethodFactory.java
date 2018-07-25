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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;

public interface ExprDotEvalSumMethodFactory {
    ExprDotEvalSumMethod getSumAggregator();
    Class getValueType();

    void codegenDeclare(CodegenBlock block);
    void codegenEnterNumberTypedNonNull(CodegenBlock block, CodegenExpressionRef value);
    void codegenEnterObjectTypedNonNull(CodegenBlock block, CodegenExpressionRef value);
    void codegenReturn(CodegenBlock block);
}