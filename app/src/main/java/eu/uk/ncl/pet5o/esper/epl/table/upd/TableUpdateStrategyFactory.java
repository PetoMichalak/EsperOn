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
package eu.uk.ncl.pet5o.esper.epl.table.upd;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventTableIndexMetadataEntry;
import eu.uk.ncl.pet5o.esper.epl.lookup.IndexMultiKey;
import eu.uk.ncl.pet5o.esper.epl.lookup.IndexedPropDesc;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateHelper;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateItem;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TableUpdateStrategyFactory {
    public static TableUpdateStrategy validateGetTableUpdateStrategy(TableMetadata tableMetadata, EventBeanUpdateHelper updateHelper, boolean isOnMerge)
            throws ExprValidationException {
        // determine affected indexes
        Set<String> affectedIndexNames = null;
        boolean uniqueIndexUpdated = false;

        for (Map.Entry<IndexMultiKey, EventTableIndexMetadataEntry> index :
                tableMetadata.getEventTableIndexMetadataRepo().getIndexes().entrySet()) {
            for (EventBeanUpdateItem updateItem : updateHelper.getUpdateItems()) {
                if (updateItem.getOptionalPropertyName() != null) {
                    boolean match = determineUpdatesIndex(updateItem, index.getKey());
                    if (match) {
                        if (affectedIndexNames == null) {
                            affectedIndexNames = new LinkedHashSet<String>();
                        }
                        affectedIndexNames.add(index.getValue().getOptionalIndexName());
                        uniqueIndexUpdated |= index.getKey().isUnique();
                    }
                }
            }
        }

        // with affected indexes and with uniqueness : careful updates, may need to rollback
        if (affectedIndexNames != null && uniqueIndexUpdated) {
            if (isOnMerge) {
                throw new ExprValidationException("On-merge statements may not update unique keys of tables");
            }
            return new TableUpdateStrategyWUniqueConstraint(updateHelper, affectedIndexNames);
        }
        // with affected indexes and without uniqueness : update indexes without unique key violation and rollback
        if (affectedIndexNames != null) {
            return new TableUpdateStrategyIndexNonUnique(updateHelper, affectedIndexNames);
        }
        // no affected indexes, the fasted means of updating
        return new TableUpdateStrategyNonIndex(updateHelper);
    }

    private static boolean determineUpdatesIndex(EventBeanUpdateItem updateItem, IndexMultiKey key) {
        for (IndexedPropDesc prop : key.getHashIndexedProps()) {
            if (prop.getIndexPropName().equals(updateItem.getOptionalPropertyName())) {
                return true;
            }
        }
        for (IndexedPropDesc prop : key.getRangeIndexedProps()) {
            if (prop.getIndexPropName().equals(updateItem.getOptionalPropertyName())) {
                return true;
            }
        }
        return false;
    }
}
