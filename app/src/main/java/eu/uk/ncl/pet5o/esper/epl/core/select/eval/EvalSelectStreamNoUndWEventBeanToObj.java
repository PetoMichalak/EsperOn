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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.SelectClauseStreamCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class EvalSelectStreamNoUndWEventBeanToObj extends EvalSelectStreamBaseMap implements SelectExprProcessor {

    private final Set<String> eventBeanToObjectProps;

    public EvalSelectStreamNoUndWEventBeanToObj(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, List<SelectClauseStreamCompiledSpec> namedStreams, boolean usingWildcard, Set<String> eventBeanToObjectProps) {
        super(selectExprForgeContext, resultEventType, namedStreams, usingWildcard);
        this.eventBeanToObjectProps = eventBeanToObjectProps;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return processSelectExprbeanToMap(props, eventBeanToObjectProps, super.getContext().getEventAdapterService(), super.resultEventType);
    }

    protected CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember indexes = codegenClassScope.makeAddMember(Set.class, eventBeanToObjectProps);
        return staticMethod(EvalSelectStreamNoUndWEventBeanToObj.class, "processSelectExprbeanToMap", props, CodegenExpressionBuilder.member(indexes.getMemberId()), CodegenExpressionBuilder.member(memberEventAdapterService.getMemberId()), CodegenExpressionBuilder.member(memberResultEventType.getMemberId()));
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param props props
     * @param eventBeanToObjectProps indexes
     * @param eventAdapterService svc
     * @param resultEventType type
     * @return bean
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean processSelectExprbeanToMap(Map<String, Object> props, Set<String> eventBeanToObjectProps, EventAdapterService eventAdapterService, EventType resultEventType) {
        for (String property : eventBeanToObjectProps) {
            Object value = props.get(property);
            if (value instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
                props.put(property, ((eu.uk.ncl.pet5o.esper.client.EventBean) value).getUnderlying());
            }
        }
        return eventAdapterService.adapterForTypedMap(props, resultEventType);
    }
}
