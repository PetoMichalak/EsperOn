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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationEval;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorEnumerationSingleToCollEval implements ExprEvaluator {
    private final SelectExprProcessorEnumerationSingleToCollForge forge;
    private final ExprEnumerationEval enumeration;

    public SelectExprProcessorEnumerationSingleToCollEval(SelectExprProcessorEnumerationSingleToCollForge forge, ExprEnumerationEval enumeration) {
        this.forge = forge;
        this.enumeration = enumeration;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = enumeration.evaluateGetEventBean(eventsPerStream, isNewData, exprEvaluatorContext);
        if (event == null) {
            return null;
        }
        return new eu.uk.ncl.pet5o.esper.client.EventBean[]{event};
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationSingleToCollForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, SelectExprProcessorEnumerationSingleToCollEval.class, codegenClassScope);

        methodNode.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", forge.enumerationForge.evaluateGetEventBeanCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("event")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .assignArrayElement(ref("events"), constant(0), ref("event"))
                .methodReturn(ref("events"));
        return localMethod(methodNode);
    }

}
