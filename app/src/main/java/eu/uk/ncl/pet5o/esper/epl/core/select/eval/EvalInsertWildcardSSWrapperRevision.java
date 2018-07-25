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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.DecoratingEventBean;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventProcessor;

import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class EvalInsertWildcardSSWrapperRevision extends EvalBaseMap implements SelectExprProcessor {

    private final ValueAddEventProcessor vaeProcessor;

    public EvalInsertWildcardSSWrapperRevision(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, ValueAddEventProcessor vaeProcessor) {
        super(selectExprForgeContext, resultEventType);
        this.vaeProcessor = vaeProcessor;
    }

    protected void initSelectExprProcessorSpecific(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
    }

    // In case of a wildcard and single stream that is itself a
    // wrapper bean, we also need to add the map properties
    public eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        return selectExprInsertWildcardSSWrapRevision(eventsPerStream, evaluators.length, props, vaeProcessor);
    }

    protected CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodNode methodNode, SelectExprProcessorCodegenSymbol selectEnv, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember member = codegenClassScope.makeAddMember(ValueAddEventProcessor.class, vaeProcessor);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        return staticMethod(EvalInsertWildcardSSWrapperRevision.class, "selectExprInsertWildcardSSWrapRevision", refEPS, constant(evaluators.length), props, CodegenExpressionBuilder.member(member.getMemberId()));
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param eventsPerStream events
     * @param numEvaluators num evals
     * @param props props
     * @param vaeProcessor processor
     * @return bean
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean selectExprInsertWildcardSSWrapRevision(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, int numEvaluators, Map<String, Object> props, ValueAddEventProcessor vaeProcessor) {
        DecoratingEventBean wrapper = (DecoratingEventBean) eventsPerStream[0];
        if (wrapper != null) {
            Map<String, Object> map = wrapper.getDecoratingProperties();
            if ((numEvaluators == 0) && (!map.isEmpty())) {
                // no action
            } else {
                props.putAll(map);
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[0];
        return vaeProcessor.getValueAddEventBean(theEvent);
    }
}
