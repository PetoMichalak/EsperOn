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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

public class ExprDotMethodForgeDuck implements ExprDotForge {
    private final String statementName;
    private final EngineImportService engineImportService;
    private final String methodName;
    private final Class[] parameterTypes;
    private final ExprForge[] parameters;

    public ExprDotMethodForgeDuck(String statementName, EngineImportService engineImportService, String methodName, Class[] parameterTypes, ExprForge[] parameters) {
        this.statementName = statementName;
        this.engineImportService = engineImportService;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    public EPType getTypeInfo() {
        return EPTypeHelper.singleValue(Object.class);
    }

    public void visit(ExprDotEvalVisitor visitor) {
        visitor.visitMethod(methodName);
    }

    public ExprDotEval getDotEvaluator() {
        return new ExprDotMethodForgeDuckEval(this, ExprNodeUtilityCore.getEvaluatorsNoCompile(parameters));
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return ExprDotMethodForgeDuckEval.codegen(this, inner, innerType, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public String getStatementName() {
        return statementName;
    }

    public EngineImportService getEngineImportService() {
        return engineImportService;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public ExprForge[] getParameters() {
        return parameters;
    }
}
