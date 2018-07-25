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
package eu.uk.ncl.pet5o.esper.epl.enummethod.eval;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeService;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotEvalParam;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotEvalParamLambda;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotForgeEnumMethodBase;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotNodeUtility;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ExprDotForgeAverage extends ExprDotForgeEnumMethodBase {

    public EventType[] getAddStreamTypes(String enumMethodUsedName, List<String> goesToNames, EventType inputEventType, Class collectionComponentType, List<ExprDotEvalParam> bodiesAndParameters, EventAdapterService eventAdapterService) {
        return ExprDotNodeUtility.getSingleLambdaParamEventType(enumMethodUsedName, goesToNames, inputEventType, collectionComponentType, eventAdapterService);
    }

    public EnumForge getEnumForge(EngineImportService engineImportService, EventAdapterService eventAdapterService, StreamTypeService streamTypeService, int statementId, String enumMethodUsedName, List<ExprDotEvalParam> bodiesAndParameters, EventType inputEventType, Class collectionComponentType, int numStreamsIncoming, boolean disablePropertyExpressionEventCollCache) {

        if (bodiesAndParameters.isEmpty()) {
            if (collectionComponentType == BigDecimal.class || collectionComponentType == BigInteger.class) {
                super.setTypeInfo(EPTypeHelper.singleValue(BigDecimal.class));
                return new EnumAverageBigDecimalScalarForge(numStreamsIncoming, engineImportService.getDefaultMathContext());
            }
            super.setTypeInfo(EPTypeHelper.singleValue(Double.class));
            return new EnumAverageScalarForge(numStreamsIncoming);
        }

        ExprDotEvalParamLambda first = (ExprDotEvalParamLambda) bodiesAndParameters.get(0);
        Class returnType = first.getBodyForge().getEvaluationType();

        if (returnType == BigDecimal.class || returnType == BigInteger.class) {
            super.setTypeInfo(EPTypeHelper.singleValue(BigDecimal.class));
            if (inputEventType == null) {
                return new EnumAverageBigDecimalScalarLambdaForge(first.getBodyForge(), first.getStreamCountIncoming(),
                        (ObjectArrayEventType) first.getGoesToTypes()[0], engineImportService.getDefaultMathContext());
            }
            return new EnumAverageBigDecimalEventsForge(first.getBodyForge(), first.getStreamCountIncoming(), engineImportService.getDefaultMathContext());
        }
        super.setTypeInfo(EPTypeHelper.singleValue(Double.class));
        if (inputEventType == null) {
            return new EnumAverageScalarLambdaForge(first.getBodyForge(), first.getStreamCountIncoming(),
                    (ObjectArrayEventType) first.getGoesToTypes()[0]);
        }
        return new EnumAverageEventsForge(first.getBodyForge(), first.getStreamCountIncoming());
    }
}
