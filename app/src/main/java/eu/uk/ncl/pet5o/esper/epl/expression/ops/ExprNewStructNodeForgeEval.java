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
package eu.uk.ncl.pet5o.esper.epl.expression.ops;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.HashMap;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprNewStructNodeForgeEval implements ExprTypableReturnEval {

    private final ExprNewStructNodeForge forge;
    private final ExprEvaluator[] evaluators;

    public ExprNewStructNodeForgeEval(ExprNewStructNodeForge forge, ExprEvaluator[] evaluators) {
        this.forge = forge;
        this.evaluators = evaluators;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprNew(forge.getForgeRenderable());
        }
        String[] columnNames = forge.getForgeRenderable().getColumnNames();
        Map<String, Object> props = new HashMap<String, Object>();
        for (int i = 0; i < evaluators.length; i++) {
            props.put(columnNames[i], evaluators[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext));
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprNew(props);
        }
        return props;
    }

    public static CodegenExpression codegen(ExprNewStructNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Map.class, ExprNewStructNodeForgeEval.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Map.class, "props", newInstance(HashMap.class));
        ExprNode[] nodes = forge.getForgeRenderable().getChildNodes();
        String[] columnNames = forge.getForgeRenderable().getColumnNames();
        for (int i = 0; i < nodes.length; i++) {
            ExprForge child = nodes[i].getForge();
            block.exprDotMethod(ref("props"), "put", constant(columnNames[i]), child.evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope));
        }
        block.methodReturn(ref("props"));
        return localMethod(methodNode);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        String[] columnNames = forge.getForgeRenderable().getColumnNames();
        Object[] rows = new Object[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            rows[i] = evaluators[i].evaluate(eventsPerStream, isNewData, context);
        }
        return rows;
    }

    public static CodegenExpression codegenTypeableSingle(ExprNewStructNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Object[].class, ExprNewStructNodeForgeEval.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Object[].class, "rows", newArrayByLength(Object.class, constant(forge.getForgeRenderable().getColumnNames().length)));
        for (int i = 0; i < forge.getForgeRenderable().getColumnNames().length; i++) {
            block.assignArrayElement("rows", constant(i), forge.getForgeRenderable().getChildNodes()[i].getForge().evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope));
        }
        block.methodReturn(ref("rows"));
        return localMethod(methodNode);
    }

    public Object[][] evaluateTypableMulti(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

}
