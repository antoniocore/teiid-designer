/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import org.eclipse.core.runtime.IStatus;
import org.osgi.framework.Bundle;

/**
 *
 *
 * @since 8.0
 */
public class ExtensionDescriptorImpl implements ExtensionDescriptor {

    protected static final ExtensionDescriptor[] EMPTY_ARRAY = new ExtensionDescriptor[0];

    protected Object id;
    protected DescriptorClassLoader extensionClassLoader;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ExtensionDescriptorImpl( final Object id,
	                                final String className,
	                                final Bundle bundle ) {
        if (id == null) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("ExtensionDescriptorImpl.The_ID_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        this.id = id;
        if (className != null && bundle != null) {
			this.extensionClassLoader = new DescriptorClassLoader(className, bundle);
        }
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getId()
     */
    @Override
	public Object getId() {
        return this.id;
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getClassName()
     * @since 4.3
     */
    @Override
	public String getClassName() {
        return (this.extensionClassLoader != null ? this.extensionClassLoader.getClassName() : null);
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getExtensionClass()
     */
    @Override
	public Class getExtensionClass() {
        return (this.extensionClassLoader != null ? this.extensionClassLoader.getLoadedClass() : null);
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getExtensionClassInstance()
     */
    @Override
	public Object getExtensionClassInstance() {
        return (this.extensionClassLoader != null ? this.extensionClassLoader.getClassInstance() : null);
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getNewExtensionClassInstance()
     */
    @Override
	public Object getNewExtensionClassInstance() {
        return (this.extensionClassLoader != null ? this.extensionClassLoader.getNewClassInstance() : null);
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getChildren()
     */
    @Override
	public ExtensionDescriptor[] getChildren() {
        return EMPTY_ARRAY;
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#isMultiDescriptor()
     */
    @Override
	public boolean isMultiDescriptor() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.ExtensionDescriptor#getChildDescriptor(java.lang.Object)
     */
    @Override
	public ExtensionDescriptor getChildDescriptor(final Object id) {
        return null;
    }

//    /**
//     * @see java.lang.Object#equals(java.lang.Object)
//     * @since 4.3
//     */
//    public boolean equals(Object obj) {
//        if (!(obj instanceof MetamodelRootClassDescriptorImpl)) {
//            return false;
//        }
//        ExtensionDescriptorImpl that = (ExtensionDescriptorImpl)obj;
//        boolean equalIds = (this.id == null
//                            ? that.id == null
//                            : this.id.equals(that.id));
//        boolean equalExtensionLoaders = (this.extensionClassLoader == null
//                                         ? that.extensionClassLoader == null
//                                         : this.extensionClassLoader.equals(that.extensionClassLoader));
//        if (equalIds && equalExtensionLoaders) {
//            return true;
//        }
//        return super.equals(obj);
//    }

    // ==================================================================================
    //                        I N N E R   C L A S S
    // ==================================================================================

    private class DescriptorClassLoader {
        private final String className;
        private final Bundle bundle;
        private boolean loadClassFailure;
        private boolean newInstanceFailure;
        private Class loadedClass;
        private Object classInstance;

        public DescriptorClassLoader( final String className,
		                              final Bundle bundle ) {
            if (className == null) {
                throw new IllegalArgumentException(ModelerCore.Util.getString("ExtensionDescriptorImpl.The_class_name_string_may_not_be_null_1"));  //$NON-NLS-1$
            }
            if (className.length() == 0) {
                throw new IllegalArgumentException(ModelerCore.Util.getString("ExtensionDescriptorImpl.The_class_name_string_may_not_be_zero_length_3"));  //$NON-NLS-1$
            }
            if (bundle == null) {
                throw new IllegalArgumentException(
				                                   ModelerCore.Util.getString("ExtensionDescriptorImpl.The_bundle_reference_may_not_be_null_2")); //$NON-NLS-1$
            }
            this.className          = className;
            this.bundle = bundle;
            this.loadClassFailure   = false;
            this.newInstanceFailure = false;
            this.loadedClass        = null;
            this.classInstance      = null;
        }

        public String getClassName() {
            return this.className;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DescriptorClassLoader)) {
                return false;
            }
            DescriptorClassLoader that = (DescriptorClassLoader)obj;
            if (this.className.equals(that.className) && this.bundle.equals(that.bundle)) {
                return true;
            }
            return super.equals(obj);
        }

        /**
         * Return the loaded class associated with this instance
         * @return Class
         */
        public Class getLoadedClass() {
            if (!loadClassFailure && loadedClass == null) {
                try {
                    loadedClass = bundle.loadClass(className);
                    loadClassFailure = false;
                } catch (ClassNotFoundException e) {
                    ModelerCore.Util.log(IStatus.ERROR,
					                     ModelerCore.Util.getString("ExtensionDescriptorImpl.Unable_to_load_class_using_bundle_4", className, bundle)); //$NON-NLS-1$
                    loadedClass = null;
                    loadClassFailure = true;
                }
            }
            return loadedClass;
        }

        /**
         * Get instance of the class
         * @return Object
         */
        public Object getClassInstance() {
            if (!newInstanceFailure && classInstance == null && getLoadedClass() != null) {
                try {
                    classInstance = getLoadedClass().newInstance();
                    newInstanceFailure = false;
                } catch (InstantiationException e) {
                    ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ExtensionDescriptorImpl.Creating_an_instance_of_5",loadedClass,e.getMessage())); //$NON-NLS-1$
                    classInstance = null;
                    newInstanceFailure = true;
                } catch (IllegalAccessException e) {
                    ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ExtensionDescriptorImpl.Creating_an_instance_of_6",loadedClass,e.getMessage())); //$NON-NLS-1$
                    classInstance = null;
                    newInstanceFailure = true;
                }
            }
            return classInstance;
        }

        /**
         * Get instance of the class
         * @return Object
         */
        public Object getNewClassInstance() {
            Object newClassInstance = null;
            if (!newInstanceFailure && getLoadedClass() != null) {
                try {
                    newClassInstance = getLoadedClass().newInstance();
                    newInstanceFailure = false;
                } catch (InstantiationException e) {
                    ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ExtensionDescriptorImpl.Creating_an_instance_of_5",loadedClass,e.getMessage())); //$NON-NLS-1$
                    newInstanceFailure = true;
                } catch (IllegalAccessException e) {
                    ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ExtensionDescriptorImpl.Creating_an_instance_of_6",loadedClass,e.getMessage())); //$NON-NLS-1$
                    newInstanceFailure = true;
                }
            }
            return newClassInstance;
        }

        @Override
        public String toString() {
            final Object[] params = new Object[] {className, bundle, new Boolean(loadedClass != null)};
            return ModelerCore.Util.getString(ModelerCore.Util.getString("ExtensionDescriptorImpl.ExtensionDescriptor___className,_bundle,_isLoaded_7", params)); //$NON-NLS-1$
        }
    }

}
