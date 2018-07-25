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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.SelectClauseStreamCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseElementCompiled;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseElementWildcard;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseExprCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableService;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;

import java.util.ArrayList;

/**
 * Works in conjunction with {@link SelectExprResultProcessor} to present
 * a result as an object array for 'natural' delivery.
 */
public class BindProcessorForge {
    private ExprForge[] expressionForges;
    private Class[] expressionTypes;
    private String[] columnNamesAssigned;

    public BindProcessorForge(SelectClauseElementCompiled[] selectionList,
                              EventType[] typesPerStream,
                              String[] streamNames,
                              TableService tableService)
            throws ExprValidationException {
        ArrayList<ExprForge> expressions = new ArrayList<>();
        ArrayList<Class> types = new ArrayList<Class>();
        ArrayList<String> columnNames = new ArrayList<String>();

        for (SelectClauseElementCompiled element : selectionList) {
            // handle wildcards by outputting each stream's underlying event
            if (element instanceof SelectClauseElementWildcard) {
                for (int i = 0; i < typesPerStream.length; i++) {
                    Class returnType = typesPerStream[i].getUnderlyingType();
                    TableMetadata tableMetadata = tableService.getTableMetadataFromEventType(typesPerStream[i]);
                    ExprForge forge;
                    if (tableMetadata != null) {
                        forge = new BindProcessorEvaluatorStreamTable(i, returnType, tableMetadata);
                    } else {
                        forge = new BindProcessorStream(i, returnType);
                    }
                    expressions.add(forge);
                    types.add(returnType);
                    columnNames.add(streamNames[i]);
                }
            } else if (element instanceof SelectClauseStreamCompiledSpec) {
                // handle stream wildcards by outputting the stream underlying event
                final SelectClauseStreamCompiledSpec streamSpec = (SelectClauseStreamCompiledSpec) element;
                EventType type = typesPerStream[streamSpec.getStreamNumber()];
                final Class returnType = type.getUnderlyingType();

                final TableMetadata tableMetadata = tableService.getTableMetadataFromEventType(type);
                ExprForge forge;
                if (tableMetadata != null) {
                    forge = new BindProcessorEvaluatorStreamTable(streamSpec.getStreamNumber(), returnType, tableMetadata);
                } else {
                    forge = new BindProcessorStream(streamSpec.getStreamNumber(), returnType);
                }
                expressions.add(forge);
                types.add(returnType);
                columnNames.add(streamNames[streamSpec.getStreamNumber()]);
            } else if (element instanceof SelectClauseExprCompiledSpec) {
                // handle expressions
                SelectClauseExprCompiledSpec expr = (SelectClauseExprCompiledSpec) element;
                ExprForge forge = expr.getSelectExpression().getForge();
                expressions.add(forge);
                types.add(forge.getEvaluationType());
                if (expr.getAssignedName() != null) {
                    columnNames.add(expr.getAssignedName());
                } else {
                    columnNames.add(ExprNodeUtilityCore.toExpressionStringMinPrecedenceSafe(expr.getSelectExpression()));
                }
            } else {
                throw new IllegalStateException("Unrecognized select expression element of type " + element.getClass());
            }
        }

        expressionForges = expressions.toArray(new ExprForge[expressions.size()]);
        expressionTypes = types.toArray(new Class[types.size()]);
        columnNamesAssigned = columnNames.toArray(new String[columnNames.size()]);
    }

    public BindProcessor getBindProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator[] evaluators = ExprNodeUtilityRich.getEvaluatorsMayCompile(expressionForges, engineImportService, this.getClass(), isFireAndForget, statementName);
        return new BindProcessor(this, evaluators);
    }

    public CodegenMethodNode processCodegen(CodegenMethodNode methodNode, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return BindProcessor.processCodegen(this, methodNode, exprSymbol, codegenClassScope);
    }

    /**
     * Returns the expression types generated by the select-clause expressions.
     *
     * @return types
     */
    public Class[] getExpressionTypes() {
        return expressionTypes;
    }

    /**
     * Returns the column names of select-clause expressions.
     *
     * @return column names
     */
    public String[] getColumnNamesAssigned() {
        return columnNamesAssigned;
    }

    public ExprForge[] getExpressionForges() {
        return expressionForges;
    }
}
