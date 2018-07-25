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
package eu.uk.ncl.pet5o.esper.epl.core.select.eval;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMayVoid;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public abstract class EvalBaseMap extends EvalBase implements SelectExprProcessor, SelectExprProcessorForge {

    protected ExprEvaluator[] evaluators;

    protected EvalBaseMap(SelectExprForgeContext selectExprForgeContext, EventType resultEventType) {
        super(selectExprForgeContext, resultEventType);
    }

    protected abstract void initSelectExprProcessorSpecific(EngineImportService engineImportService, boolean isFireAndForget, String statementName);

    protected abstract eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext);

    protected abstract CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodNode methodNode, SelectExprProcessorCodegenSymbol selectEnv, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        String[] columnNames = context.getColumnNames();

        // Evaluate all expressions and build a map of name-value pairs
        Map<String, Object> props;
        if (evaluators.length == 0) {
            props = Collections.emptyMap();
        } else {
            props = new HashMap<>(CollectionUtil.capacityHashMap(evaluators.length));
            for (int i = 0; i < evaluators.length; i++) {
                Object evalResult = evaluators[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                props.put(columnNames[i], evalResult);
            }
        }

        return processSpecific(props, eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenBlock block = methodNode.getBlock();
        if (this.context.getExprForges().length == 0) {
            block.declareVar(Map.class, "props", staticMethod(Collections.class, "emptyMap"));
        } else {
            block.declareVar(Map.class, "props", newInstance(HashMap.class, constant(CollectionUtil.capacityHashMap(this.context.getColumnNames().length))));
        }
        for (int i = 0; i < this.context.getColumnNames().length; i++) {
            CodegenExpression expression = CodegenLegoMayVoid.expressionMayVoid(Object.class, this.context.getExprForges()[i], methodNode, exprSymbol, codegenClassScope);
            block.expression(exprDotMethod(ref("props"), "put", constant(this.context.getColumnNames()[i]), expression));
        }
        block.methodReturn(processSpecificCodegen(memberResultEventType, memberEventAdapterService, ref("props"), methodNode, selectSymbol, exprSymbol, codegenClassScope));
        return methodNode;
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        if (evaluators == null) {
            evaluators = ExprNodeUtilityRich.getEvaluatorsMayCompile(context.getExprForges(), engineImportService, this.getClass(), isFireAndForget, statementName);
        }
        initSelectExprProcessorSpecific(engineImportService, isFireAndForget, statementName);
        return this;
    }
}
