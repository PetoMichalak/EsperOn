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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

import java.lang.reflect.Array;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LE;

public class ExprDotForgeArrayGetEval implements ExprDotEval {
    private final ExprDotForgeArrayGet forge;
    private final ExprEvaluator indexExpression;

    public ExprDotForgeArrayGetEval(ExprDotForgeArrayGet forge, ExprEvaluator indexExpression) {
        this.forge = forge;
        this.indexExpression = indexExpression;
    }

    public Object evaluate(Object target, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }

        Object index = indexExpression.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (index == null) {
            return null;
        }
        if (!(index instanceof Integer)) {
            return null;
        }
        int indexNum = (Integer) index;

        if (Array.getLength(target) <= indexNum) {
            return null;
        }
        return Array.get(target, indexNum);
    }

    public EPType getTypeInfo() {
        return forge.getTypeInfo();
    }

    public ExprDotForge getDotForge() {
        return forge;
    }

    public static CodegenExpression codegen(ExprDotForgeArrayGet forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(EPTypeHelper.getNormalizedClass(forge.getTypeInfo()), ExprDotForgeArrayGetEval.class, codegenClassScope).addParam(innerType, "target");


        CodegenBlock block = methodNode.getBlock();
        if (!innerType.isPrimitive()) {
            block.ifRefNullReturnNull("target");
        }
        block.declareVar(int.class, "index", forge.getIndexExpression().evaluateCodegen(int.class, methodNode, exprSymbol, codegenClassScope));
        block.ifCondition(relational(arrayLength(ref("target")), LE, ref("index")))
                .blockReturn(constantNull())
                .methodReturn(arrayAtIndex(ref("target"), ref("index")));
        return localMethod(methodNode, inner);
    }
}
