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

import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRefWCol;

import java.util.LinkedHashMap;

public class CodegenMembersColumnized {
    private final LinkedHashMap<CodegenExpressionRefWCol, Class> members = new LinkedHashMap<>();

    public CodegenMembersColumnized addMember(int column, Class type, String name) {
        members.put(new CodegenExpressionRefWCol(name, column), type);
        return this;
    }

    public LinkedHashMap<CodegenExpressionRefWCol, Class> getMembers() {
        return members;
    }
}
