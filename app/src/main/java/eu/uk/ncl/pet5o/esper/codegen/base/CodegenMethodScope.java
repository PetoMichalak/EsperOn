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
package eu.uk.ncl.pet5o.esper.codegen.base;

import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;

public interface CodegenMethodScope {
    CodegenMethodNode makeChild(Class returnType, Class generator, CodegenClassScope codegenClassScope);
    CodegenMethodNode makeChildWithScope(Class returnType, Class generator, CodegenSymbolProvider symbolProvider, CodegenClassScope codegenClassScope);
    CodegenMethodScope addSymbol(CodegenExpressionRef symbol);
}
