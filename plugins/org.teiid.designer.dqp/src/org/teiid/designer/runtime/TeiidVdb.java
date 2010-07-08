package org.teiid.designer.runtime;


import org.teiid.adminapi.VDB;

import com.metamatrix.core.util.CoreArgCheck;

public class TeiidVdb  implements Comparable<TeiidVdb> {

	private final VDB vdb;

    private final ExecutionAdmin admin;
    
	public TeiidVdb( VDB vdb, ExecutionAdmin admin) {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$

        this.vdb = vdb;
        this.admin = admin;
    }
    public ExecutionAdmin getAdmin() {
		return admin;
	}

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( TeiidVdb vdb ) {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        return getName().compareTo(vdb.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        TeiidVdb other = (TeiidVdb)obj;

        if (getName().equals(other.getName())) return true;

        return false;
    }
    



    public String getName() {
        return this.vdb.getName();
    }
    
    public VDB getVdb() {
    	return this.vdb;
    }
}