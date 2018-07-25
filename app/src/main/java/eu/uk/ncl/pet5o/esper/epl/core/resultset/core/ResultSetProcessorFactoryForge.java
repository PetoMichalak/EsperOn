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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.core;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenInstanceAux;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;

import java.util.List;

public interface ResultSetProcessorFactoryForge {
    ResultSetProcessorFactory getResultSetProcessorFactory(StatementContext stmtContext, boolean isFireAndForget);

    Class getInterfaceClass();

    void instanceCodegen(CodegenInstanceAux instance, CodegenClassScope classScope, CodegenCtor factoryCtor, List<CodegenTypedParam> factoryMembers);

    void processViewResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void processJoinResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void getIteratorViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void getIteratorJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void processOutputLimitedViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void processOutputLimitedJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void applyViewResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void applyJoinResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void continueOutputLimitedLastAllNonBufferedViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void continueOutputLimitedLastAllNonBufferedJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void processOutputLimitedLastAllNonBufferedViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void processOutputLimitedLastAllNonBufferedJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void acceptHelperVisitorCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void stopMethodCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance);

    void clearMethodCodegen(CodegenClassScope classScope, CodegenMethodNode method);
}
