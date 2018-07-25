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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenSymbolProvider;
import eu.uk.ncl.pet5o.esper.codegen.compile.CodegenClassGenerator;
import eu.uk.ncl.pet5o.esper.codegen.compile.CodegenMessageUtil;
import eu.uk.ncl.pet5o.esper.codegen.core.CodeGenerationIDGenerator;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenClass;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenClassMethods;
import eu.uk.ncl.pet5o.esper.codegen.util.CodegenStackGenerator;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

public class SelectExprProcessorCompiler {
    private final static Logger log = LoggerFactory.getLogger(SelectExprProcessorCompiler.class);

    public static SelectExprProcessor allocateSelectExprEvaluator(EventAdapterService eventAdapterService, SelectExprProcessorForge forge, EngineImportService engineImportService, Class compiledByClass, boolean onDemandQuery, String statementName) {
        if (!engineImportService.getByteCodeGeneration().isEnableSelectClause() || onDemandQuery) {
            return forge.getSelectExprProcessor(engineImportService, onDemandQuery, statementName);
        }

        Supplier<String> debugInformationProvider = new Supplier<String>() {
            public String get() {
                StringWriter writer = new StringWriter();
                writer.append("statement '")
                        .append(statementName)
                        .append("' select-clause-processor");
                writer.append(" requestor-class '")
                        .append(compiledByClass.getSimpleName())
                        .append("'");
                return writer.toString();
            }
        };

        try {
            CodegenClassScope codegenClassScope = new CodegenClassScope(engineImportService.getByteCodeGeneration().isIncludeComments());
            SelectExprProcessorCompilerResult result = generate(codegenClassScope, forge, engineImportService, eventAdapterService);

            CodegenClassMethods methods = new CodegenClassMethods();
            CodegenStackGenerator.recursiveBuildStack(result.getTopNode(), "process", methods);

            // render and compile
            String className = CodeGenerationIDGenerator.generateClassName(SelectExprProcessor.class);
            CodegenClass clazz = new CodegenClass(SelectExprProcessor.class, engineImportService.getCodegenCompiler().getPackageName(), className, result.getCodegenClassScope(), Collections.emptyList(), null, methods, Collections.emptyList());
            return CodegenClassGenerator.compile(clazz, engineImportService, SelectExprProcessor.class, debugInformationProvider);
        } catch (Throwable t) {
            boolean fallback = engineImportService.getByteCodeGeneration().isEnableFallback();
            String message = CodegenMessageUtil.getFailedCompileLogMessageWithCode(t, debugInformationProvider, fallback);
            if (fallback) {
                log.warn(message, t);
            } else {
                log.error(message, t);
            }
            return handleThrowable(engineImportService, t, forge, debugInformationProvider, onDemandQuery, statementName);
        }
    }

    public static SelectExprProcessorCompilerResult generate(CodegenClassScope codegenClassScope, SelectExprProcessorForge forge, EngineImportService engineImportService, EventAdapterService eventAdapterService) {
        ExprForgeCodegenSymbol exprSymbol = new ExprForgeCodegenSymbol(true, null);
        SelectExprProcessorCodegenSymbol selectEnv = new SelectExprProcessorCodegenSymbol();
        CodegenSymbolProvider symbolProvider = new CodegenSymbolProvider() {
            public void provide(Map<String, Class> symbols) {
                exprSymbol.provide(symbols);
                selectEnv.provide(symbols);
            }
        };

        CodegenMember memberResultEventType = codegenClassScope.makeAddMember(EventType.class, forge.getResultEventType());
        CodegenMember memberEventAdapterService = codegenClassScope.makeAddMember(EventAdapterService.class, eventAdapterService);

        CodegenMethodNode topNode = CodegenMethodNode.makeParentNode(eu.uk.ncl.pet5o.esper.client.EventBean.class, SelectExprProcessorCompiler.class, symbolProvider, codegenClassScope)
                .addParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, ExprForgeCodegenNames.NAME_EPS)
                .addParam(boolean.class, ExprForgeCodegenNames.NAME_ISNEWDATA)
                .addParam(boolean.class, SelectExprProcessorCodegenSymbol.NAME_ISSYNTHESIZE)
                .addParam(ExprEvaluatorContext.class, ExprForgeCodegenNames.NAME_EXPREVALCONTEXT);
        CodegenMethodNode method = forge.processCodegen(memberResultEventType, memberEventAdapterService, topNode, selectEnv, exprSymbol, codegenClassScope);
        exprSymbol.derivedSymbolsCodegen(topNode, topNode.getBlock(), codegenClassScope);
        topNode.getBlock().methodReturn(localMethod(method));

        return new SelectExprProcessorCompilerResult(topNode, codegenClassScope);
    }

    private static SelectExprProcessor handleThrowable(EngineImportService engineImportService, Throwable t, SelectExprProcessorForge forge, Supplier<String> debugInformationProvider, boolean isFireAndForget, String statementName) {
        if (engineImportService.getByteCodeGeneration().isEnableFallback()) {
            return forge.getSelectExprProcessor(engineImportService, isFireAndForget, statementName);
        }
        throw new EPException("Fatal exception during code-generation for " + debugInformationProvider.get() + " (see error log for further details): " + t.getMessage(), t);
    }
}
