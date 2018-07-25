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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;

public class EnumFirstOfNoPredicateForge extends EnumForgeBase implements EnumForge, EnumEval {

    private final EPType resultType;

    public EnumFirstOfNoPredicateForge(int streamCountIncoming, EPType resultType) {
        super(streamCountIncoming);
        this.resultType = resultType;
    }

    public EnumEval getEnumEvaluator() {
        return this;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {
        if (enumcoll == null || enumcoll.isEmpty()) {
            return null;
        }
        return enumcoll.iterator().next();
    }

    public CodegenExpression codegen(EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        Class type = EPTypeHelper.getCodegenReturnType(resultType);
        CodegenMethodNode method = codegenMethodScope.makeChild(type, EnumFirstOfNoPredicateForge.class, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS).getBlock()
                .ifCondition(or(equalsNull(EnumForgeCodegenNames.REF_ENUMCOLL), exprDotMethod(EnumForgeCodegenNames.REF_ENUMCOLL, "isEmpty")))
                .blockReturn(constantNull())
                .methodReturn(cast(type, exprDotMethodChain(EnumForgeCodegenNames.REF_ENUMCOLL).add("iterator").add("next")));
        return localMethod(method, args.getExpressions());
    }
}
