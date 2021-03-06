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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenIndent;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;

import java.util.Map;
import java.util.Set;

public class CodegenStatementWhile extends CodegenStatementWBlockBase {
    private final CodegenExpression condition;

    private CodegenBlock block;

    public CodegenStatementWhile(CodegenBlock parent, CodegenExpression condition) {
        super(parent);
        this.condition = condition;
    }

    public void setBlock(CodegenBlock block) {
        this.block = block;
    }

    public void render(StringBuilder builder, Map<Class, String> imports, boolean isInnerClass, int level, CodegenIndent indent) {
        builder.append("while (");
        condition.render(builder, imports, isInnerClass);
        builder.append(") {\n");
        block.render(builder, imports, isInnerClass, level + 1, indent);
        indent.indent(builder, level);
        builder.append("}\n");
    }

    public void mergeClasses(Set<Class> classes) {
        block.mergeClasses(classes);
        condition.mergeClasses(classes);
    }
}
