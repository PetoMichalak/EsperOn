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

import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoCast;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.event.EventTypeSPI;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.io.StringWriter;
import java.util.Arrays;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Represents an stream property identifier in a filter expressiun tree.
 */
public class ExprContextPropertyNodeImpl extends ExprNodeBase implements ExprContextPropertyNode, ExprEvaluator, ExprForge {
    private static final long serialVersionUID = 2816977190089087618L;
    private final String propertyName;
    private Class returnType;
    private transient EventPropertyGetterSPI getter;

    public ExprContextPropertyNodeImpl(String propertyName) {
        this.propertyName = propertyName;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class getEvaluationType() {
        return returnType;
    }

    public ExprForge getForge() {
        return this;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public ExprNode validate(ExprValidationContext validationContext) throws ExprValidationException {
        if (validationContext.getContextDescriptor() == null) {
            throw new ExprValidationException("Context property '" + propertyName + "' cannot be used in the expression as provided");
        }
        EventTypeSPI eventType = (EventTypeSPI) validationContext.getContextDescriptor().getContextPropertyRegistry().getContextEventType();
        if (eventType == null) {
            throw new ExprValidationException("Context property '" + propertyName + "' cannot be used in the expression as provided");
        }
        getter = eventType.getGetterSPI(propertyName);
        if (getter == null) {
            throw new ExprValidationException("Context property '" + propertyName + "' is not a known property, known properties are " + Arrays.toString(eventType.getPropertyNames()));
        }
        returnType = JavaClassHelper.getBoxedType(eventType.getPropertyType(propertyName));
        return null;
    }

    public boolean isConstantResult() {
        return false;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprContextProp(this);
        }
        eu.uk.ncl.pet5o.esper.client.EventBean props = context.getContextProperties();
        Object result = props != null ? getter.get(props) : null;
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprContextProp(result);
        }
        return result;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(getEvaluationType(), ExprContextPropertyNodeImpl.class, codegenClassScope);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "props", exprDotMethod(refExprEvalCtx, "getContextProperties"))
                .ifRefNullReturnNull("props");
        block.methodReturn(CodegenLegoCast.castSafeFromObjectType(returnType, getter.eventBeanGetCodegen(ref("props"), methodNode, codegenClassScope)));
        return localMethod(methodNode);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SINGLE;
    }

    public Class getType() {
        return returnType;
    }

    public void toPrecedenceFreeEPL(StringWriter writer) {
        writer.append(propertyName);
    }

    public ExprPrecedenceEnum getPrecedence() {
        return ExprPrecedenceEnum.UNARY;
    }

    public EventPropertyGetter getGetter() {
        return getter;
    }

    public boolean equalsNode(ExprNode node, boolean ignoreStreamPrefix) {
        if (this == node) return true;
        if (node == null || getClass() != node.getClass()) return false;

        ExprContextPropertyNodeImpl that = (ExprContextPropertyNodeImpl) node;
        return propertyName.equals(that.propertyName);
    }
}
