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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstanceInnerClass;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.CLASSNAME_ORDERBYPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorOrderedLimitForge.REF_ROWLIMITPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorRowLimitOnlyForge implements OrderByProcessorFactoryForge {

    private final RowLimitProcessorFactory rowLimitProcessorFactory;

    public OrderByProcessorRowLimitOnlyForge(RowLimitProcessorFactory rowLimitProcessorFactory) {
        this.rowLimitProcessorFactory = rowLimitProcessorFactory;
    }

    public OrderByProcessorFactory make(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return new OrderByProcessorRowLimitOnlyFactory(rowLimitProcessorFactory);
    }

    public void instantiateCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        CodegenMember rowLimitFactory = classScope.makeAddMember(RowLimitProcessorFactory.class, rowLimitProcessorFactory);
        method.getBlock().declareVar(RowLimitProcessor.class, REF_ROWLIMITPROCESSOR.getRef(), exprDotMethod(member(rowLimitFactory.getMemberId()), "instantiate", REF_AGENTINSTANCECONTEXT))
                .methodReturn(newInstanceInnerClass(CLASSNAME_ORDERBYPROCESSOR, ref("o"), REF_ROWLIMITPROCESSOR));
    }

    public void ctorCodegen(CodegenCtor ctor, List<CodegenTypedParam> members, CodegenClassScope classScope) {
        ctor.getCtorParams().add(new CodegenTypedParam(RowLimitProcessor.class, REF_ROWLIMITPROCESSOR.getRef()));
    }

    public void sortPlainCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        OrderByProcessorRowLimitOnly.sortPlainCodegen(method);
    }

    public void sortWGroupKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        OrderByProcessorRowLimitOnly.sortWGroupKeysCodegen(method);
    }

    public void sortRollupCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        OrderByProcessorRowLimitOnly.sortRollupCodegen(method);
    }

    public void getSortKeyCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(constantNull());
    }

    public void getSortKeyRollupCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(constantNull());
    }

    public void sortWOrderKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        OrderByProcessorRowLimitOnly.sortWOrderKeysCodegen(method);
    }

    public void sortTwoKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        OrderByProcessorRowLimitOnly.sortTwoKeysCodegen(method);
    }
}
