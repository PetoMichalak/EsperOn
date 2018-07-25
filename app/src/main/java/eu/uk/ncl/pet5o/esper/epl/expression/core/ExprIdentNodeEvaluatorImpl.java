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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoCast;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.event.WrapperEventType;
import eu.uk.ncl.pet5o.esper.event.vaevent.RevisionEventType;
import eu.uk.ncl.pet5o.esper.event.vaevent.VariantEventType;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprIdentNodeEvaluatorImpl implements ExprIdentNodeEvaluator {
    private final int streamNum;
    private final EventPropertyGetterSPI propertyGetter;
    protected final Class returnType;
    private final ExprIdentNode identNode;
    private final EventType eventType;
    private final boolean optionalEvent;

    public ExprIdentNodeEvaluatorImpl(int streamNum, EventPropertyGetterSPI propertyGetter, Class returnType, ExprIdentNode identNode, EventType eventType, boolean optionalEvent) {
        this.streamNum = streamNum;
        this.propertyGetter = propertyGetter;
        this.returnType = returnType;
        this.identNode = identNode;
        this.eventType = eventType;
        this.optionalEvent = optionalEvent;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprIdent(identNode.getFullUnresolvedName());
        }
        eu.uk.ncl.pet5o.esper.client.EventBean event = eventsPerStream[streamNum];
        if (event == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprIdent(null);
            }
            return null;
        }
        if (InstrumentationHelper.ENABLED) {
            Object result = propertyGetter.get(event);
            InstrumentationHelper.get().aExprIdent(result);
            return result;
        }

        return propertyGetter.get(event);
    }

    public Class getCodegenReturnType(Class requiredType) {
        return requiredType == Object.class ? Object.class : returnType;
    }

    public CodegenExpression codegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (returnType == null) {
            return constantNull();
        }

        Class castTargetType = getCodegenReturnType(requiredType);
        boolean useUnderlying = exprSymbol.isAllowUnderlyingReferences() && !identNode.getResolvedPropertyName().contains("?") && !(eventType instanceof WrapperEventType) && !(eventType instanceof VariantEventType) && !(eventType instanceof RevisionEventType);
        if (useUnderlying && !optionalEvent) {
            CodegenExpressionRef underlying = exprSymbol.getAddRequiredUnderlying(codegenMethodScope, streamNum, eventType, false);
            return CodegenLegoCast.castSafeFromObjectType(castTargetType, propertyGetter.underlyingGetCodegen(underlying, codegenMethodScope, codegenClassScope));
        }

        CodegenMethodNode method = codegenMethodScope.makeChild(castTargetType, this.getClass(), codegenClassScope);
        CodegenBlock block = method.getBlock();

        if (useUnderlying) {
            CodegenExpressionRef underlying = exprSymbol.getAddRequiredUnderlying(method, streamNum, eventType, true);
            block.ifRefNullReturnNull(underlying)
                  .methodReturn(CodegenLegoCast.castSafeFromObjectType(castTargetType, propertyGetter.underlyingGetCodegen(underlying, method, codegenClassScope)));
        } else {
            CodegenExpressionRef refEPS = exprSymbol.getAddEPS(method);
            method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", arrayAtIndex(refEPS, constant(streamNum)));
            if (optionalEvent) {
                block.ifRefNullReturnNull("event");
            }
            block.methodReturn(CodegenLegoCast.castSafeFromObjectType(castTargetType, propertyGetter.eventBeanGetCodegen(ref("event"), method, codegenClassScope)));

        }
        return localMethod(method);
    }

    public Class getEvaluationType() {
        return returnType;
    }

    public EventPropertyGetterSPI getGetter() {
        return propertyGetter;
    }

    /**
     * Returns true if the property exists, or false if not.
     *
     * @param eventsPerStream each stream's events
     * @param isNewData       if the stream represents insert or remove stream
     * @return true if the property exists, false if not
     */
    public boolean evaluatePropertyExists(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[streamNum];
        if (theEvent == null) {
            return false;
        }
        return propertyGetter.isExistsProperty(theEvent);
    }

    public int getStreamNum() {
        return streamNum;
    }

    public boolean isContextEvaluated() {
        return false;
    }

}
