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
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.ExpressionResultCacheEntryBeanAndCollBean;
import eu.uk.ncl.pet5o.esper.core.service.ExpressionResultCacheForPropUnwrap;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class PropertyDotEventCollectionForge implements ExprEnumerationForge, ExprEnumerationEval, ExprEnumerationGivenEvent, ExprNodeRenderable {

    private final String propertyNameCache;
    private final int streamId;
    private final EventType fragmentType;
    private final EventPropertyGetterSPI getter;
    private final boolean disablePropertyExpressionEventCollCache;

    public PropertyDotEventCollectionForge(String propertyNameCache, int streamId, EventType fragmentType, EventPropertyGetterSPI getter, boolean disablePropertyExpressionEventCollCache) {
        this.propertyNameCache = propertyNameCache;
        this.streamId = streamId;
        this.fragmentType = fragmentType;
        this.getter = getter;
        this.disablePropertyExpressionEventCollCache = disablePropertyExpressionEventCollCache;
    }

    public ExprEnumerationEval getExprEvaluatorEnumeration() {
        return this;
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        EventBean eventInQuestion = eventsPerStream[streamId];
        if (eventInQuestion == null) {
            return null;
        }
        return evaluateInternal(eventInQuestion, context);
    }

    public CodegenExpression evaluateGetROCollectionEventsCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Collection.class, PropertyDotEventCollectionForge.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);

        methodNode.getBlock()
                .declareVar(EventBean.class, "event", arrayAtIndex(refEPS, constant(streamId)))
                .ifRefNullReturnNull("event")
                .methodReturn(codegenEvaluateInternal(ref("event"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode);
    }

    public Collection<EventBean> evaluateEventGetROCollectionEvents(EventBean event, ExprEvaluatorContext context) {
        if (event == null) {
            return null;
        }
        return evaluateInternal(event, context);
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) {
        return fragmentType;
    }

    public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public CodegenExpression evaluateGetROCollectionScalarCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return null;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return null;
    }

    public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateEventGetROCollectionScalar(EventBean event, ExprEvaluatorContext context) {
        return null;
    }

    public EventBean evaluateEventGetEventBean(EventBean event, ExprEvaluatorContext context) {
        return null;
    }

    public CodegenExpression evaluateGetEventBeanCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    private Collection<EventBean> evaluateInternal(EventBean eventInQuestion, ExprEvaluatorContext context) {

        if (disablePropertyExpressionEventCollCache) {
            EventBean[] events = (EventBean[]) getter.getFragment(eventInQuestion);
            return events == null ? null : Arrays.asList(events);
        }

        ExpressionResultCacheForPropUnwrap cache = context.getExpressionResultCacheService().getAllocateUnwrapProp();
        ExpressionResultCacheEntryBeanAndCollBean cacheEntry = cache.getPropertyColl(propertyNameCache, eventInQuestion);
        if (cacheEntry != null) {
            return cacheEntry.getResult();
        }

        EventBean[] events = (EventBean[]) getter.getFragment(eventInQuestion);
        Collection<EventBean> coll = events == null ? null : Arrays.asList(events);
        cache.savePropertyColl(propertyNameCache, eventInQuestion, coll);
        return coll;
    }

    private CodegenExpression codegenEvaluateInternal(CodegenExpressionRef event, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (disablePropertyExpressionEventCollCache) {
            CodegenMethodNode methodNode = codegenMethodScope.makeChild(Collection.class, PropertyDotEventCollectionForge.class, codegenClassScope).addParam(EventBean.class, "event");

            methodNode.getBlock()
                    .declareVar(EventBean[].class, "events", cast(EventBean[].class, getter.eventBeanFragmentCodegen(ref("event"), methodNode, codegenClassScope)))
                    .ifRefNullReturnNull("events")
                    .methodReturn(staticMethod(Arrays.class, "asList", ref("events")));
            return localMethod(methodNode, event);
        }
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Collection.class, PropertyDotEventCollectionForge.class, codegenClassScope).addParam(EventBean.class, "event");
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);

        methodNode.getBlock()
                .declareVar(ExpressionResultCacheForPropUnwrap.class, "cache", exprDotMethodChain(refExprEvalCtx).add("getExpressionResultCacheService").add("getAllocateUnwrapProp"))
                .declareVar(ExpressionResultCacheEntryBeanAndCollBean.class, "cacheEntry", exprDotMethod(ref("cache"), "getPropertyColl", constant(propertyNameCache), ref("event")))
                .ifCondition(notEqualsNull(ref("cacheEntry")))
                .blockReturn(exprDotMethod(ref("cacheEntry"), "getResult"))
                .declareVar(EventBean[].class, "events", cast(EventBean[].class, getter.eventBeanFragmentCodegen(ref("event"), methodNode, codegenClassScope)))
                .declareVarNoInit(Collection.class, "coll")
                .ifRefNull("events")
                .assignRef("coll", constantNull())
                .ifElse()
                .assignRef("coll", staticMethod(Arrays.class, "asList", ref("events")))
                .blockEnd()
                .expression(exprDotMethod(ref("cache"), "savePropertyColl", constant(propertyNameCache), ref("event"), ref("coll")))
                .methodReturn(ref("coll"));
        return localMethod(methodNode, event);
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public void toEPL(StringWriter writer, ExprPrecedenceEnum parentPrecedence) {
        writer.append(this.getClass().getSimpleName());
    }
}
