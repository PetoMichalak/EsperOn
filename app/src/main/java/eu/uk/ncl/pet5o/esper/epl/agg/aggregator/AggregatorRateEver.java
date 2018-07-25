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
package eu.uk.ncl.pet5o.esper.epl.agg.aggregator;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationMethodFactoryRate;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.schedule.TimeProvider;

import java.util.ArrayDeque;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

/**
 * Aggregation computing an event arrival rate for with and without data window.
 */
public class AggregatorRateEver implements AggregationMethod {

    protected final long interval;
    protected final long oneSecondTime;
    protected final ArrayDeque<Long> points;
    protected boolean hasLeave = false;
    protected final TimeProvider timeProvider;

    /**
     * Ctor.
     *
     * @param interval     rate interval
     * @param timeProvider time
     * @param oneSecondTime number of ticks for one second
     */
    public AggregatorRateEver(long interval, long oneSecondTime, TimeProvider timeProvider) {
        this.interval = interval;
        this.oneSecondTime = oneSecondTime;
        this.timeProvider = timeProvider;
        points = new ArrayDeque<Long>();
    }

    public static void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(column, boolean.class, "hasLeave");
        membersColumnized.addMember(column, ArrayDeque.class, "points");
        ctor.getBlock().assignRef(refCol("points", column), newInstance(ArrayDeque.class));
    }

    public void enter(Object object) {
        long timestamp = timeProvider.getTime();
        points.add(timestamp);
        boolean leave = removeFromHead(points, timestamp, interval);
        hasLeave |= leave;
    }

    public static void applyEnterCodegen(AggregationMethodFactoryRate forge, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        if (forge.getParent().getOptionalFilter() != null) {
            AggregatorCodegenUtil.prefixWithFilterCheck(forge.getParent().getOptionalFilter().getForge(), method, symbols, classScope);
        }

        CodegenMember timeProvider = classScope.makeAddMember(TimeProvider.class, forge.getTimeProvider());
        CodegenExpressionRef points = refCol("points", column);
        method.getBlock().declareVar(long.class, "timestamp", exprDotMethod(member(timeProvider.getMemberId()), "getTime"))
                .exprDotMethod(points, "add", ref("timestamp"))
                .declareVar(boolean.class, "leave", staticMethod(AggregatorRateEver.class, "removeFromHead", points, ref("timestamp"), constant(forge.getIntervalTime())))
                .assignCompound(refCol("hasLeave", column), "|", ref("leave"));
    }

    public void leave(Object object) {
        // This is an "ever" aggregator and is designed for use in non-window env
    }

    public void clear() {
        points.clear();
    }

    public static void clearCodegen(int column, CodegenMethodNode method) {
        method.getBlock().exprDotMethod(refCol("points", column), "clear");
    }

    public Object getValue() {
        if (!points.isEmpty()) {
            long newest = points.getLast();
            boolean leave = removeFromHead(points, newest, interval);
            hasLeave |= leave;
        }
        if (!hasLeave) {
            return null;
        }
        if (points.isEmpty()) {
            return 0d;
        }
        return (points.size() * oneSecondTime * 1d) / interval;
    }

    public static void getValueCodegen(AggregationMethodFactoryRate forge, int column, CodegenMethodNode method) {
        CodegenExpressionRef points = refCol("points", column);
        CodegenExpressionRef hasLeave = refCol("hasLeave", column);
        method.getBlock().ifCondition(not(exprDotMethod(points, "isEmpty")))
                    .declareVar(long.class, "newest", cast(Long.class, exprDotMethod(points, "getLast")))
                    .declareVar(boolean.class, "leave", staticMethod(AggregatorRateEver.class, "removeFromHead", points, ref("newest"), constant(forge.getIntervalTime())))
                    .assignCompound(refCol("hasLeave", column), "|", ref("leave"))
            .blockEnd()
            .ifCondition(not(hasLeave)).blockReturn(constantNull())
            .ifCondition(exprDotMethod(points, "isEmpty")).blockReturn(constant(0d))
            .methodReturn(op(op(op(exprDotMethod(points, "size"), "*", constant(forge.getTimeAbacus().getOneSecond())), "*", constant(1d)), "/", constant(forge.getIntervalTime())));
    }

    public long getInterval() {
        return interval;
    }

    public long getOneSecondTime() {
        return oneSecondTime;
    }

    public ArrayDeque<Long> getPoints() {
        return points;
    }

    public boolean isHasLeave() {
        return hasLeave;
    }

    public void setHasLeave(boolean hasLeave) {
        this.hasLeave = hasLeave;
    }

    public TimeProvider getTimeProvider() {
        return timeProvider;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param points points
     * @param timestamp timestamp
     * @param interval interval
     * @return hasLeave
     */
    public static boolean removeFromHead(ArrayDeque<Long> points, long timestamp, long interval) {
        boolean hasLeave = false;
        if (points.size() > 1) {
            while (true) {
                long first = points.getFirst();
                long delta = timestamp - first;
                if (delta >= interval) {
                    points.remove();
                    hasLeave = true;
                } else {
                    break;
                }
                if (points.isEmpty()) {
                    break;
                }
            }
        }
        return hasLeave;
    }
}
