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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;

public class SelectExprProcessorCompilerResult {
    private final CodegenMethodNode topNode;
    private final CodegenClassScope codegenClassScope;

    public SelectExprProcessorCompilerResult(CodegenMethodNode topNode, CodegenClassScope codegenClassScope) {
        this.topNode = topNode;
        this.codegenClassScope = codegenClassScope;
    }

    public CodegenMethodNode getTopNode() {
        return topNode;
    }

    public CodegenClassScope getCodegenClassScope() {
        return codegenClassScope;
    }
}
