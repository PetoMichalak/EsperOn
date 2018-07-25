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
package eu.uk.ncl.pet5o.esper.epl.agg.service.common;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntyped;
import eu.uk.ncl.pet5o.esper.epl.agg.service.groupbylocal.AggSvcGroupLocalGroupByBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_ISNEWDATA;

public class AggregationServiceCodegenUtil {
    public static CodegenMethodNode computeMultiKeyCodegen(int idNumber, ExprForge[] partitionForges, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        ExprForgeCodegenSymbol exprSymbol = new ExprForgeCodegenSymbol(true, null);
        Consumer<CodegenMethodNode> code = method -> {
            if (partitionForges.length == 1) {
                CodegenExpression expression = partitionForges[0].evaluateCodegen(Object.class, method, exprSymbol, classScope);
                exprSymbol.derivedSymbolsCodegen(method, method.getBlock(), classScope);
                method.getBlock().methodReturn(expression);
            } else {
                CodegenExpression[] expressions = new CodegenExpression[partitionForges.length];
                for (int i = 0; i < partitionForges.length; i++) {
                    expressions[i] = partitionForges[i].evaluateCodegen(Object.class, method, exprSymbol, classScope);
                }
                exprSymbol.derivedSymbolsCodegen(method, method.getBlock(), classScope);
                method.getBlock().declareVar(Object[].class, "keys", newArrayByLength(Object.class, constant(partitionForges.length)));
                for (int i = 0; i < expressions.length; i++) {
                    method.getBlock().assignArrayElement("keys", constant(i), expressions[i]);
                }
                method.getBlock().methodReturn(newInstance(MultiKeyUntyped.class, ref("keys")));
            }
        };

        return namedMethods.addMethodWithSymbols(Object.class, "computeKeyArrayCodegen_" + idNumber, CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, boolean.class, NAME_ISNEWDATA, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT), AggSvcGroupLocalGroupByBase.class, classScope, code, exprSymbol);
    }

    public static void generateRefCount(boolean hasRefcount, CodegenNamedMethods namedMethods, CodegenCtor rowCtor, List<CodegenTypedParam> rowMembers, CodegenClassScope classScope) {
        if (hasRefcount) {
            rowMembers.add(new CodegenTypedParam(int.class, "refcount"));
            rowCtor.getBlock().assignRef("refcount", constant(1));
        }
        namedMethods.addMethod(void.class, "increaseRefcount", Collections.emptyList(), AggregationServiceCodegenUtil.class, classScope,
                hasRefcount ? method -> method.getBlock().increment(ref("refcount")) : method -> method.getBlock().methodThrowUnsupported());
        namedMethods.addMethod(void.class, "decreaseRefcount", Collections.emptyList(), AggregationServiceCodegenUtil.class, classScope,
                hasRefcount ? method -> method.getBlock().decrement(ref("refcount")) : method -> method.getBlock().methodThrowUnsupported());
        namedMethods.addMethod(long.class, "getRefcount", Collections.emptyList(), AggregationServiceCodegenUtil.class, classScope,
                hasRefcount ? method -> method.getBlock().methodReturn(ref("refcount")) : method -> method.getBlock().methodThrowUnsupported());
    }
}
