/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.Schema;

/**
 * TableFinder
 *
 * @since 8.0
 */
public class ColumnFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public ColumnFinder() {
        super();
    }

    /**
     * This method accumulates the {@link Column} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean visit( final EObject object ) {
        if (object instanceof Column) {
            found((Column)object);
            return false;
        }
        // Columns are contained by Tables and ProcedureResults
        if (object instanceof ColumnSet) { // Table and ProcedureResult
            return true;
        }
        if (object instanceof Catalog) {
            return true;
        }
        if (object instanceof Schema) {
            return true;
        }

        return false;
    }

}
