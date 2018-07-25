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
package eu.uk.ncl.pet5o.esper.epl.spec;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Descriptor generated by INSERT-INTO clauses specified in expressions to insert the
 * results of statement as a stream to further statements.
 */
public class InsertIntoDesc implements Serializable {
    private final SelectClauseStreamSelectorEnum streamSelector;
    private final String eventTypeName;
    private List<String> columnNames;
    private static final long serialVersionUID = 6204369134039715720L;

    /**
     * Ctor.
     *
     * @param streamSelector selects insert, remove or insert+remove stream
     * @param eventTypeName  is the event type name
     */
    public InsertIntoDesc(SelectClauseStreamSelectorEnum streamSelector, String eventTypeName) {
        this.streamSelector = streamSelector;
        this.eventTypeName = eventTypeName;
        columnNames = new LinkedList<String>();
    }

    /**
     * Returns the stream(s) selected for inserting into.
     *
     * @return stream selector
     */
    public SelectClauseStreamSelectorEnum getStreamSelector() {
        return streamSelector;
    }

    /**
     * Returns name of event type to use for insert-into stream.
     *
     * @return event type name
     */
    public String getEventTypeName() {
        return eventTypeName;
    }

    /**
     * Returns a list of column names specified optionally in the insert-into clause, or empty if none specified.
     *
     * @return column names or empty list if none supplied
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Add a column name to the insert-into clause.
     *
     * @param columnName to add
     */
    public void add(String columnName) {
        columnNames.add(columnName);
    }

    public static InsertIntoDesc fromColumns(String streamName, List<String> columns) {
        InsertIntoDesc insertIntoDesc = new InsertIntoDesc(SelectClauseStreamSelectorEnum.ISTREAM_ONLY, streamName);
        for (String col : columns) {
            insertIntoDesc.add(col);
        }
        return insertIntoDesc;
    }
}
