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
import eu.uk.ncl.pet5o.esper.client.annotation.AuditEnum;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.util.AuditPath;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.enumValue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.util.AuditPath.METHOD_AUDITLOG;

public class ExprIdentNodeEvaluatorLogging extends ExprIdentNodeEvaluatorImpl {
    private final String engineURI;
    private final String propertyName;
    private final String statementName;

    public ExprIdentNodeEvaluatorLogging(int streamNum, EventPropertyGetterSPI propertyGetter, Class returnType, ExprIdentNode identNode, EventType eventType, boolean optionalEvent, String engineURI, String propertyName, String statementName) {
        super(streamNum, propertyGetter, returnType, identNode, eventType, optionalEvent);
        this.engineURI = engineURI;
        this.propertyName = propertyName;
        this.statementName = statementName;
    }

    @Override
    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object result = super.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (AuditPath.isInfoEnabled()) {
            AuditPath.auditLog(engineURI, statementName, AuditEnum.PROPERTY, propertyName + " value " + result);
        }
        return result;
    }

    @Override
    public CodegenExpression codegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (returnType == null) {
            return constantNull();
        }
        Class castTargetType = getCodegenReturnType(requiredType);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(castTargetType, this.getClass(), codegenClassScope);
        methodNode.getBlock()
                .declareVar(castTargetType, "result", super.codegen(requiredType, methodNode, exprSymbol, codegenClassScope))
                .ifCondition(staticMethod(AuditPath.class, "isInfoEnabled"))
                .staticMethod(AuditPath.class, METHOD_AUDITLOG, constant(engineURI), constant(statementName), enumValue(AuditEnum.class, "PROPERTY"), op(constant(propertyName + " value "), "+", ref("result")))
                .blockEnd()
                .methodReturn(ref("result"));
        return localMethod(methodNode);
    }
}
