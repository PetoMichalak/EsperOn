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
package eu.uk.ncl.pet5o.esper.epl.expression.codegen;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class CodegenLegoCast {
    public static CodegenExpression castSafeFromObjectType(Class targetType, CodegenExpression value) {
        if (targetType == null) {
            return constantNull();
        }
        if (targetType == Object.class) {
            return value;
        }
        if (targetType == void.class) {
            throw new IllegalArgumentException("Invalid void target type for cast");
        }
        if (targetType.isPrimitive()) {
            return cast(JavaClassHelper.getBoxedType(targetType), value);
        }
        return cast(targetType, value);
    }

    public static void asDoubleNullReturnNull(CodegenBlock block, String variable, ExprForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class type = forge.getEvaluationType();
        if (type == double.class) {
            block.declareVar(type, variable, forge.evaluateCodegen(type, codegenMethodScope, exprSymbol, codegenClassScope));
            return;
        }

        String holder = variable + "_";
        block.declareVar(type, holder, forge.evaluateCodegen(type, codegenMethodScope, exprSymbol, codegenClassScope));
        if (!type.isPrimitive()) {
            block.ifRefNullReturnNull(holder);
        }
        block.declareVar(double.class, variable, SimpleNumberCoercerFactory.SimpleNumberCoercerDouble.codegenDouble(ref(holder), type));
    }
}
