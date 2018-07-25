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
package eu.uk.ncl.pet5o.esper.codegen.model.statement;

import java.util.Map;
import java.util.Set;

public class CodegenStatementBreakLoop extends CodegenStatementBase {
    public final static CodegenStatementBreakLoop INSTANCE = new CodegenStatementBreakLoop();

    public void renderStatement(StringBuilder builder, Map<Class, String> imports, boolean isInnerClass) {
        builder.append("break");
    }

    public void mergeClasses(Set<Class> classes) {
    }
}
