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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationEval;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.ArrayList;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class EnumUnionForgeEval implements EnumEval {

    private final EnumUnionForge forge;
    private final ExprEnumerationEval evaluator;

    public EnumUnionForgeEval(EnumUnionForge forge, ExprEnumerationEval evaluator) {
        this.forge = forge;
        this.evaluator = evaluator;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {
        Collection other;
        if (forge.scalar) {
            other = evaluator.evaluateGetROCollectionScalar(eventsLambda, isNewData, context);
        } else {
            other = evaluator.evaluateGetROCollectionEvents(eventsLambda, isNewData, context);
        }

        if (other == null || other.isEmpty()) {
            return enumcoll;
        }

        ArrayList<Object> result = new ArrayList<Object>(enumcoll.size() + other.size());
        result.addAll(enumcoll);
        result.addAll(other);

        return result;
    }

    public static CodegenExpression codegen(EnumUnionForge forge, EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        ExprForgeCodegenSymbol scope = new ExprForgeCodegenSymbol(false, null);
        CodegenMethodNode methodNode = codegenMethodScope.makeChildWithScope(Collection.class, EnumUnionForgeEval.class, scope, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS);

        CodegenBlock block = methodNode.getBlock();
        if (forge.scalar) {
            block.declareVar(Collection.class, "other", forge.evaluatorForge.evaluateGetROCollectionScalarCodegen(methodNode, scope, codegenClassScope));
        } else {
            block.declareVar(Collection.class, "other", forge.evaluatorForge.evaluateGetROCollectionEventsCodegen(methodNode, scope, codegenClassScope));
        }
        block.ifCondition(or(equalsNull(ref("other")), exprDotMethod(ref("other"), "isEmpty")))
                .blockReturn(EnumForgeCodegenNames.REF_ENUMCOLL);
        block.declareVar(ArrayList.class, "result", newInstance(ArrayList.class, op(exprDotMethod(EnumForgeCodegenNames.REF_ENUMCOLL, "size"), "+", exprDotMethod(ref("other"), "size"))))
                .expression(exprDotMethod(ref("result"), "addAll", EnumForgeCodegenNames.REF_ENUMCOLL))
                .expression(exprDotMethod(ref("result"), "addAll", ref("other")))
                .methodReturn(ref("result"));
        return localMethod(methodNode, args.getEps(), args.getEnumcoll(), args.getIsNewData(), args.getExprCtx());
    }
}
