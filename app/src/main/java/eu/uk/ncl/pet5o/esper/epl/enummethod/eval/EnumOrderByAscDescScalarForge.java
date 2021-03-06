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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class EnumOrderByAscDescScalarForge extends EnumForgeBase implements EnumEval {

    private final boolean descending;

    public EnumOrderByAscDescScalarForge(int streamCountIncoming, boolean descending) {
        super(streamCountIncoming);
        this.descending = descending;
    }

    public EnumEval getEnumEvaluator() {
        return this;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {

        if (enumcoll == null || enumcoll.isEmpty()) {
            return enumcoll;
        }

        List list = new ArrayList(enumcoll);
        if (descending) {
            Collections.sort(list, Collections.reverseOrder());
        } else {
            Collections.sort(list);
        }
        return list;
    }

    public CodegenExpression codegen(EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenBlock block = codegenMethodScope.makeChild(Collection.class, EnumOrderByAscDescScalarForge.class, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS).getBlock()
                .ifCondition(or(equalsNull(EnumForgeCodegenNames.REF_ENUMCOLL), exprDotMethod(EnumForgeCodegenNames.REF_ENUMCOLL, "isEmpty")))
                .blockReturn(EnumForgeCodegenNames.REF_ENUMCOLL)
                .declareVar(List.class, "list", newInstance(ArrayList.class, EnumForgeCodegenNames.REF_ENUMCOLL));
        if (descending) {
            block.staticMethod(Collections.class, "sort", ref("list"), staticMethod(Collections.class, "reverseOrder"));
        } else {
            block.staticMethod(Collections.class, "sort", ref("list"));
        }
        CodegenMethodNode method = block.methodReturn(ref("list"));
        return localMethod(method, args.getExpressions());
    }
}
