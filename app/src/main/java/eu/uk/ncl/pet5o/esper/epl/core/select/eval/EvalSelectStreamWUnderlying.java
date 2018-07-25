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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.SelectClauseStreamCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;
import eu.uk.ncl.pet5o.esper.event.DecoratingEventBean;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.event.EventTypeSPI;

import java.util.List;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class EvalSelectStreamWUnderlying extends EvalSelectStreamBaseMap implements SelectExprProcessor {

    private final List<SelectExprStreamDesc> unnamedStreams;
    private final boolean singleStreamWrapper;
    private final boolean underlyingIsFragmentEvent;
    private final int underlyingStreamNumber;
    private final EventPropertyGetterSPI underlyingPropertyEventGetter;
    private final ExprForge underlyingExprForge;
    private final TableMetadata tableMetadata;
    private final EventType[] eventTypes;

    public EvalSelectStreamWUnderlying(SelectExprForgeContext selectExprForgeContext,
                                       EventType resultEventType,
                                       List<SelectClauseStreamCompiledSpec> namedStreams,
                                       boolean usingWildcard,
                                       List<SelectExprStreamDesc> unnamedStreams,
                                       boolean singleStreamWrapper,
                                       boolean underlyingIsFragmentEvent,
                                       int underlyingStreamNumber,
                                       EventPropertyGetterSPI underlyingPropertyEventGetter,
                                       ExprForge underlyingExprForge,
                                       TableMetadata tableMetadata,
                                       EventType[] eventTypes) {
        super(selectExprForgeContext, resultEventType, namedStreams, usingWildcard);
        this.unnamedStreams = unnamedStreams;
        this.singleStreamWrapper = singleStreamWrapper;
        this.underlyingIsFragmentEvent = underlyingIsFragmentEvent;
        this.underlyingStreamNumber = underlyingStreamNumber;
        this.underlyingPropertyEventGetter = underlyingPropertyEventGetter;
        this.underlyingExprForge = underlyingExprForge;
        this.tableMetadata = tableMetadata;
        this.eventTypes = eventTypes;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        // In case of a wildcard and single stream that is itself a
        // wrapper bean, we also need to add the map properties
        if (singleStreamWrapper) {
            DecoratingEventBean wrapper = (DecoratingEventBean) eventsPerStream[0];
            if (wrapper != null) {
                Map<String, Object> map = wrapper.getDecoratingProperties();
                props.putAll(map);
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = null;
        if (underlyingIsFragmentEvent) {
            eu.uk.ncl.pet5o.esper.client.EventBean eventBean = eventsPerStream[underlyingStreamNumber];
            theEvent = (eu.uk.ncl.pet5o.esper.client.EventBean) eventBean.getFragment(unnamedStreams.get(0).getStreamSelected().getStreamName());
        } else if (underlyingPropertyEventGetter != null) {
            Object value = underlyingPropertyEventGetter.get(eventsPerStream[underlyingStreamNumber]);
            if (value != null) {
                theEvent = super.getContext().getEventAdapterService().adapterForBean(value);
            }
        } else if (underlyingExprForge != null) {
            Object value = underlyingExprForge.getExprEvaluator().evaluate(eventsPerStream, true, exprEvaluatorContext);
            if (value != null) {
                theEvent = super.getContext().getEventAdapterService().adapterForBean(value);
            }
        } else {
            theEvent = eventsPerStream[underlyingStreamNumber];
            if (tableMetadata != null && theEvent != null) {
                theEvent = tableMetadata.getEventToPublic().convert(theEvent, eventsPerStream, isNewData, exprEvaluatorContext);
            }
        }

        // Using a wrapper bean since we cannot use the same event type else same-type filters match.
        // Wrapping it even when not adding properties is very inexpensive.
        return super.getContext().getEventAdapterService().adapterForTypedWrapper(theEvent, props, super.getResultEventType());
    }

    protected CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, EvalSelectStreamWUnderlying.class, codegenClassScope).addParam(Map.class, "props");

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        CodegenExpression refIsNewData = exprSymbol.getAddIsNewData(methodNode);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);

        CodegenBlock block = methodNode.getBlock();
        if (singleStreamWrapper) {
            block.declareVar(DecoratingEventBean.class, "wrapper", cast(DecoratingEventBean.class, arrayAtIndex(refEPS, constant(0))))
                    .ifRefNotNull("wrapper")
                    .exprDotMethod(props, "putAll", exprDotMethod(ref("wrapper"), "getDecoratingProperties"))
                    .blockEnd();
        }

        if (underlyingIsFragmentEvent) {
            CodegenExpression fragment = ((EventTypeSPI) eventTypes[underlyingStreamNumber]).getGetterSPI(unnamedStreams.get(0).getStreamSelected().getStreamName()).eventBeanFragmentCodegen(ref("eventBean"), methodNode, codegenClassScope);
            block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "eventBean", arrayAtIndex(refEPS, constant(underlyingStreamNumber)))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, fragment));
        } else if (underlyingPropertyEventGetter != null) {
            block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", constantNull())
                    .declareVar(Object.class, "value", underlyingPropertyEventGetter.eventBeanGetCodegen(arrayAtIndex(refEPS, constant(underlyingStreamNumber)), methodNode, codegenClassScope))
                    .ifRefNotNull("value")
                    .assignRef("theEvent", exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForBean", ref("value")))
                    .blockEnd();
        } else if (underlyingExprForge != null) {
            block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", constantNull())
                    .declareVar(Object.class, "value", underlyingExprForge.evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope))
                    .ifRefNotNull("value")
                    .assignRef("theEvent", exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForBean", ref("value")))
                    .blockEnd();
        } else {
            block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", arrayAtIndex(refEPS, constant(underlyingStreamNumber)));
            if (tableMetadata != null) {
                CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, tableMetadata.getEventToPublic());
                block.ifRefNotNull("theEvent")
                        .assignRef("theEvent", exprDotMethod(member(eventToPublic.getMemberId()), "convert", ref("theEvent"), refEPS, refIsNewData, refExprEvalCtx))
                        .blockEnd();
            }
        }
        block.methodReturn(exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedWrapper", ref("theEvent"), ref("props"), member(memberResultEventType.getMemberId())));
        return localMethod(methodNode, props);
    }
}
