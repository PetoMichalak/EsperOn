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
package eu.uk.ncl.pet5o.esper.epl.core.orderby;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenSymbolProviderEmpty;
import eu.uk.ncl.pet5o.esper.codegen.core.*;
import eu.uk.ncl.pet5o.esper.codegen.util.CodegenStackGenerator;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupLevel;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.CLASSNAME_ORDERBYPROCESSORFACTORY;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERKEYS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERROLLUPLEVEL;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_OUTGOINGEVENTS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTPLAIN_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTROLLUP_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTTWOKEYS_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTWGROUPKEYS_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

public class OrderByProcessorCompiler {

    public static void makeOrderByProcessors(OrderByProcessorFactoryForge forge, CodegenClassScope classScope, List<CodegenInnerClass> innerClasses, List<CodegenTypedParam> providerExplicitMembers, CodegenCtor providerCtor, String providerClassName, String memberOrderByFactory) {
        providerExplicitMembers.add(new CodegenTypedParam(OrderByProcessorFactory.class, memberOrderByFactory));
        if (forge == null) {
            providerCtor.getBlock().assignRef(memberOrderByFactory, constantNull());
            return;
        }

        makeFactory(forge, classScope, innerClasses, providerClassName);
        makeService(forge, classScope, innerClasses, providerClassName);

        providerCtor.getBlock().assignRef(memberOrderByFactory, newInstanceInnerClass(CLASSNAME_ORDERBYPROCESSORFACTORY, ref("this")));
    }

    private static void makeFactory(OrderByProcessorFactoryForge forge, CodegenClassScope classScope, List<CodegenInnerClass> innerClasses, String providerClassName) {
        CodegenMethodNode instantiateMethod = CodegenMethodNode.makeParentNode(OrderByProcessor.class, OrderByProcessorCompiler.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(AgentInstanceContext.class, REF_AGENTINSTANCECONTEXT.getRef());
        forge.instantiateCodegen(instantiateMethod, classScope);

        List<CodegenTypedParam> ctorParams = Collections.singletonList(new CodegenTypedParam(providerClassName, "o"));
        CodegenCtor ctor = new CodegenCtor(OrderByProcessorCompiler.class, classScope, ctorParams);

        CodegenClassMethods methods = new CodegenClassMethods();
        CodegenStackGenerator.recursiveBuildStack(instantiateMethod, "instantiate", methods);
        CodegenInnerClass innerClass = new CodegenInnerClass(CLASSNAME_ORDERBYPROCESSORFACTORY, OrderByProcessorFactory.class, ctor, Collections.emptyList(), Collections.emptyMap(), methods);
        innerClasses.add(innerClass);
    }

    private static void makeService(OrderByProcessorFactoryForge forge, CodegenClassScope classScope, List<CodegenInnerClass> innerClasses, String providerClassName) {
        CodegenNamedMethods namedMethods = new CodegenNamedMethods();

        CodegenMethodNode sortPlainMethod = CodegenMethodNode.makeParentNode(eu.uk.ncl.pet5o.esper.client.EventBean[].class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(SORTPLAIN_PARAMS);
        forge.sortPlainCodegen(sortPlainMethod, classScope, namedMethods);

        CodegenMethodNode sortWGroupKeysMethod = CodegenMethodNode.makeParentNode(eu.uk.ncl.pet5o.esper.client.EventBean[].class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(SORTWGROUPKEYS_PARAMS);
        forge.sortWGroupKeysCodegen(sortWGroupKeysMethod, classScope, namedMethods);

        CodegenMethodNode sortRollupMethod = CodegenMethodNode.makeParentNode(eu.uk.ncl.pet5o.esper.client.EventBean[].class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(SORTROLLUP_PARAMS);
        forge.sortRollupCodegen(sortRollupMethod, classScope, namedMethods);

        CodegenMethodNode getSortKeyMethod = CodegenMethodNode.makeParentNode(Object.class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_EPS.getRef()).addParam(boolean.class, REF_ISNEWDATA.getRef()).addParam(ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef());
        forge.getSortKeyCodegen(getSortKeyMethod, classScope, namedMethods);

        CodegenMethodNode getSortKeyRollupMethod = CodegenMethodNode.makeParentNode(Object.class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_EPS.getRef()).addParam(boolean.class, REF_ISNEWDATA.getRef()).addParam(ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef()).addParam(AggregationGroupByRollupLevel.class, REF_ORDERROLLUPLEVEL.getRef());
        forge.getSortKeyRollupCodegen(getSortKeyRollupMethod, classScope, namedMethods);

        CodegenMethodNode sortWOrderKeysMethod = CodegenMethodNode.makeParentNode(eu.uk.ncl.pet5o.esper.client.EventBean[].class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_OUTGOINGEVENTS.getRef()).addParam(Object[].class, REF_ORDERKEYS.getRef()).addParam(ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef());
        forge.sortWOrderKeysCodegen(sortWOrderKeysMethod, classScope, namedMethods);

        CodegenMethodNode sortTwoKeysMethod = CodegenMethodNode.makeParentNode(eu.uk.ncl.pet5o.esper.client.EventBean[].class, forge.getClass(), CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(SORTTWOKEYS_PARAMS);
        forge.sortTwoKeysCodegen(sortTwoKeysMethod, classScope, namedMethods);

        List<CodegenTypedParam> members = new ArrayList<>();
        List<CodegenTypedParam> ctorParams = new ArrayList<>();
        ctorParams.add(new CodegenTypedParam(providerClassName, "o"));
        CodegenCtor ctor = new CodegenCtor(OrderByProcessorCompiler.class, classScope, ctorParams);
        forge.ctorCodegen(ctor, members, classScope);

        CodegenClassMethods innerMethods = new CodegenClassMethods();
        CodegenStackGenerator.recursiveBuildStack(sortPlainMethod, "sortPlain", innerMethods);
        CodegenStackGenerator.recursiveBuildStack(sortWGroupKeysMethod, "sortWGroupKeys", innerMethods);
        CodegenStackGenerator.recursiveBuildStack(sortRollupMethod, "sortRollup", innerMethods);
        CodegenStackGenerator.recursiveBuildStack(getSortKeyMethod, "getSortKey", innerMethods);
        CodegenStackGenerator.recursiveBuildStack(getSortKeyRollupMethod, "getSortKeyRollup", innerMethods);
        CodegenStackGenerator.recursiveBuildStack(sortWOrderKeysMethod, "sortWOrderKeys", innerMethods);
        CodegenStackGenerator.recursiveBuildStack(sortTwoKeysMethod, "sortTwoKeys", innerMethods);
        for (Map.Entry<String, CodegenMethodNode> methodEntry : namedMethods.getMethods().entrySet()) {
            CodegenStackGenerator.recursiveBuildStack(methodEntry.getValue(), methodEntry.getKey(), innerMethods);
        }

        CodegenInnerClass innerClass = new CodegenInnerClass(OrderByProcessorCodegenNames.CLASSNAME_ORDERBYPROCESSOR, OrderByProcessor.class, ctor, members, Collections.emptyMap(), innerMethods);
        innerClasses.add(innerClass);
    }
}
