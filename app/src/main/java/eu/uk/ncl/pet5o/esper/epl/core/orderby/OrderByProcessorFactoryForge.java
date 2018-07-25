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
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;

import java.util.List;

public interface OrderByProcessorFactoryForge {

    OrderByProcessorFactory make(EngineImportService engineImportService, boolean isFireAndForget, String statementName);

    void instantiateCodegen(CodegenMethodNode method, CodegenClassScope classScope);

    void ctorCodegen(CodegenCtor ctor, List<CodegenTypedParam> members, CodegenClassScope classScope);

    void sortPlainCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void sortWGroupKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void sortRollupCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void getSortKeyCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void getSortKeyRollupCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void sortWOrderKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void sortTwoKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);
}
