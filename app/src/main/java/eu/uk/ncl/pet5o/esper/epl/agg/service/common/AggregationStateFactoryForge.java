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
package eu.uk.ncl.pet5o.esper.epl.agg.service.common;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

public interface AggregationStateFactoryForge {
    AggregationStateFactory makeFactory(EngineImportService engineImportService, boolean isFireAndForget, String statementName);

    void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, CodegenClassScope classScope);

    void applyEnterCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void applyLeaveCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods);

    void clearCodegen(int stateNumber, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods);
}
