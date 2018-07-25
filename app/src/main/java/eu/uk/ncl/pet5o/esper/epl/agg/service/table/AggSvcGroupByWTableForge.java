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
package eu.uk.ncl.pet5o.esper.epl.agg.service.table;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPairForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgent;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationCodegenRowLevelDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.*;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstanceInnerClass;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.CLASSNAME_AGGREGATIONSERVICE;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.REF_GROUPKEY;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.table.AggSvcGroupByWTableCodegenUtil.REF_TABLESTATEINSTANCE;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.table.AggSvcTableGetterType.*;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.table.AggSvcTableGetterType.GETCOLLECTIONOFEVENTS;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.table.AggSvcTableGetterType.GETCOLLECTIONSCALAR;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.table.AggSvcTableGetterType.GETEVENTBEAN;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.table.AggSvcTableGetterType.GETVALUE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByWTableForge implements AggregationServiceFactoryForge {

    private final TableService tableService;
    private final TableMetadata tableMetadata;
    private final TableColumnMethodPair[] methodPairs;
    private final AggregationAccessorSlotPairForge[] accessorForges;
    private final AggregationAccessorSlotPair[] accessors;
    private final boolean isJoin;
    private final int[] targetStates;
    private final ExprNode[] accessStateExpr;
    private final AggregationAgentForge[] agentForges;
    private final AggregationAgent[] agents;
    private final AggregationGroupByRollupDesc groupByRollupDesc;
    private final boolean hasGroupBy;

    public AggSvcGroupByWTableForge(TableService tableService, TableMetadata tableMetadata, TableColumnMethodPair[] methodPairs, AggregationAccessorSlotPairForge[] accessorForges, AggregationAccessorSlotPair[] accessors, boolean join, int[] targetStates, ExprNode[] accessStateExpr, AggregationAgentForge[] agentForges, AggregationAgent[] agents, AggregationGroupByRollupDesc groupByRollupDesc, boolean hasGroupBy) {
        this.tableService = tableService;
        this.tableMetadata = tableMetadata;
        this.methodPairs = methodPairs;
        this.accessorForges = accessorForges;
        this.accessors = accessors;
        isJoin = join;
        this.targetStates = targetStates;
        this.accessStateExpr = accessStateExpr;
        this.agentForges = agentForges;
        this.agents = agents;
        this.groupByRollupDesc = groupByRollupDesc;
        this.hasGroupBy = hasGroupBy;
    }

    public AggregationServiceFactory getAggregationServiceFactory(StatementContext stmtContext, boolean isFireAndForget) {
        AggregationAgent[] agents = AggregatorUtil.getAgentForges(agentForges, stmtContext.getEngineImportService(), isFireAndForget, stmtContext.getStatementName());
        if (hasGroupBy) {
            return new AggSvcGroupByWTableFactory(tableMetadata, methodPairs, accessors, isJoin, targetStates, accessStateExpr, agents, groupByRollupDesc);
        } else {
            return new AggSvcGroupAllWTableFactory(tableMetadata, methodPairs, accessors, isJoin, targetStates, accessStateExpr, agents);
        }
    }

    public AggregationCodegenRowLevelDesc getRowLevelDesc() {
        return AggregationCodegenRowLevelDesc.EMPTY;
    }

    public void rowCtorCodegen(CodegenClassScope classScope, CodegenCtor rowCtor, List<CodegenTypedParam> rowMembers, CodegenNamedMethods namedMethods) {
        // no code
    }

    public void makeServiceCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(newInstanceInnerClass(CLASSNAME_AGGREGATIONSERVICE, ref("o"), REF_AGENTINSTANCECONTEXT));
    }

    public void ctorCodegen(CodegenCtor ctor, List<CodegenTypedParam> explicitMembers, CodegenClassScope classScope) {
        ctor.getCtorParams().add(new CodegenTypedParam(AgentInstanceContext.class, NAME_AGENTINSTANCECONTEXT));
        Class stateInstanceClass = hasGroupBy ? TableStateInstanceGrouped.class : TableStateInstanceUngrouped.class;
        explicitMembers.add(new CodegenTypedParam(stateInstanceClass, REF_TABLESTATEINSTANCE.getRef()));
        CodegenMember tableServiceMember = classScope.makeAddMember(TableService.class, tableService);
        ctor.getBlock().assignRef(REF_TABLESTATEINSTANCE, cast(stateInstanceClass, exprDotMethod(member(tableServiceMember.getMemberId()), "getState", constant(tableMetadata.getTableName()), exprDotMethod(REF_AGENTINSTANCECONTEXT, "getAgentInstanceId"))));
        if (hasGroupBy) {
            AggSvcGroupByWTableBase.ctorCodegen(ctor, explicitMembers, classScope);
        }
    }

    public void getValueCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        getCodegen(GETVALUE, method, classScope, namedMethods);
    }

    public void getCollectionOfEventsCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        getCodegen(GETCOLLECTIONOFEVENTS, method, classScope, namedMethods);
    }

    public void getEventBeanCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        getCodegen(GETEVENTBEAN, method, classScope, namedMethods);
    }

    public void getCollectionScalarCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        getCodegen(GETCOLLECTIONSCALAR, method, classScope, namedMethods);
    }

    public void applyEnterCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        applyCodegen(true, method, classScope, namedMethods);
    }

    public void applyLeaveCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        applyCodegen(false, method, classScope, namedMethods);
    }

    public void stopMethodCodegen(AggregationServiceFactoryForge forge, CodegenMethodNode method) {
        // no code
    }

    public void setRemovedCallbackCodegen(CodegenMethodNode method) {
        // not applicable
    }

    public void setCurrentAccessCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        if (hasGroupBy) {
            if (groupByRollupDesc == null || tableMetadata.getKeyTypes().length == 1) {
                AggSvcGroupByWTableBase.setCurrentAccessCodegen(method, classScope);
            } else {
                AggSvcGroupByWTableRollupMultiKeyImpl.setCurrentAccessRollupCodegen(method, classScope, tableMetadata.getKeyTypes().length);
            }
        }
    }

    public void clearResultsCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        // not applicable
    }

    public void acceptCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        // not applicable
    }

    public void getGroupKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(hasGroupBy ? exprDotMethod(REF_TABLESTATEINSTANCE, "getGroupKeys") : constantNull());
    }

    public void getGroupKeyCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(hasGroupBy ? AggSvcGroupByWTableRollupMultiKeyImpl.REF_CURRENTGROUPKEY : constantNull());
    }

    public void acceptGroupDetailCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        // not applicable
    }

    public void isGroupedCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constant(hasGroupBy));
    }

    private void applyCodegen(boolean enter, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggSvcGroupByWTableCodegenUtil.obtainWriteLockCodegen(method);
        if (!hasGroupBy) {
            method.getBlock().localMethod(AggSvcGroupAllWTableImpl.applyCodegen(enter, method, classScope, methodPairs, agentForges, agents, targetStates), REF_EPS, REF_EXPREVALCONTEXT);
        } else if (groupByRollupDesc == null) {
            method.getBlock().localMethod(AggSvcGroupByWTableBase.applyGroupKeyCodegen(enter, method, classScope, methodPairs, agentForges, agents, targetStates), REF_EPS, REF_GROUPKEY, REF_EXPREVALCONTEXT);
        } else {
            if (tableMetadata.getKeyTypes().length == 1) {
                method.getBlock().localMethod(AggSvcGroupByWTableRollupSingleKeyImpl.applyRollupCodegen(enter, method, classScope, namedMethods, methodPairs, agentForges, agents, targetStates, groupByRollupDesc, tableMetadata.getKeyTypes().length), REF_EPS, REF_GROUPKEY, REF_EXPREVALCONTEXT);
            } else {
                method.getBlock().localMethod(AggSvcGroupByWTableRollupMultiKeyImpl.applyRollupCodegen(enter, method, classScope, namedMethods, methodPairs, agentForges, agents, targetStates, groupByRollupDesc, tableMetadata.getKeyTypes().length), REF_EPS, REF_GROUPKEY, REF_EXPREVALCONTEXT);
            }
        }
    }

    public void getCodegen(AggSvcTableGetterType getterType, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggSvcGroupByWTableCodegenUtil.obtainWriteLockCodegen(method);
        if (!hasGroupBy) {
            AggSvcGroupAllWTableImpl.getGroupAllValueCodegen(getterType, method, classScope, accessors);
        } else {
            AggSvcGroupByWTableBase.getGroupByValueCodegen(getterType, method, classScope, namedMethods, methodPairs.length, accessors);
        }
    }
}
