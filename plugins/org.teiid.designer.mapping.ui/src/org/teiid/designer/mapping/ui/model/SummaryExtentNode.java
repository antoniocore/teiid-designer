/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.mapping.ui.editor.MappingExtent;
import org.teiid.designer.mapping.ui.editor.SummaryExtent;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.DiagramEntity;
import org.teiid.designer.metamodels.transformation.FragmentMappingRoot;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;


/**
 * SummaryExtentModelNode
 *
 * @since 8.0
 */
public class SummaryExtentNode extends MappingExtentNode implements UiConstants {

    private Image image;
    private HashMap hmapMappingClasses = new HashMap();

    private static final String MAPPED_NODES_PREFIX = UiConstants.Util.getString("SummaryExtentNode.mappedNodes.prefix"); //$NON-NLS-1$
    private static final String UNMAPPED_NODES_PREFIX = UiConstants.Util.getString("SummaryExtentNode.unmappedNodes.prefix"); //$NON-NLS-1$

    public SummaryExtentNode( DiagramModelNode diagramModelNode,
                              EObject modelObject,
                              MappingExtent theExtent,
                              boolean isCoarse ) {
        super(diagramModelNode, modelObject, theExtent, isCoarse);
        // System.out.println("[SummaryExtentNode.ctor 2] BOT");
    }

    @Override
    protected DiagramEntity findDiagramEntity( Diagram diagram,
                                               Object secondaryObject ) {
        return null;
    }

    @Override
    protected void initialize( Diagram diagramModelObject,
                               Object secondObject ) {
        // Don't create a diagramEntity for this object.
    }

    @Override
    public String toString() {
        return "SummaryExtentNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void setMappingClass( EObject locationEObject ) {
        // this.mappingClassEObject = locationEObject;
    }

    @Override
    public EObject getMappingClass() {
        return null;
    }

    public Image getImage() {

        if (image == null) {
            image = UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_FOR_SUMMARY_EXTENT);
        }
        return image;
    }

    public int getImagePosition() {
        return Position.UPPER_LEFT;
    }

    public HashMap getMappingClasses() {
        return hmapMappingClasses;
    }

    public void setMappingClasses( HashMap hmapMappingClasses ) {
        this.hmapMappingClasses = hmapMappingClasses;
    }

    /*
     *  (non-Javadoc)
     * This concrete method sends back the "Mapping Class" or "Attribute" stored as the model
     * object.  The locationEObject is the "target" end.  This list can be used for hiliting, etc..
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#getDependencies()
     */
    @Override
    public List getDependencies() {

        if (getModelObject() instanceof TreeMappingRoot || getModelObject() instanceof FragmentMappingRoot) {
            List deps = new ArrayList(1);
            // EObject target = TransformationHelper.getTargetEObject( getModelObject() );
            // if( target != null ) {
            // List deps = new ArrayList(1);
            // deps.add(target);
            // return deps;
            // }
            Iterator connIter = getTargetConnections().iterator();
            NodeConnectionModel nextLink = null;
            while (connIter.hasNext()) {
                nextLink = (NodeConnectionModel)connIter.next();
                if (nextLink.getSourceNode() != null) deps.add(((DiagramModelNode)nextLink.getSourceNode()).getModelObject());
            }
            return deps;
        }
        // else {
        // // The case where the model object is an "attribute" not a mapping class
        // // We need to get the dependencies from the "Mappings".
        // return Collections.EMPTY_LIST;
        // }

        return Collections.EMPTY_LIST;
    }

    // jh Defect 20609: Use the base class' implementation of updateModelForExtent().
    // public void updateModelForExtent() {

    // jh Lyra: Implement This!
    @Override
    public List getAssociations( HashMap nodeMap ) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @return
     */
    @Override
    public MappingExtent getExtent() {
        return extent;
    }

    /**
     * @param extent
     */
    @Override
    public void setExtent( MappingExtent extent ) {
        this.extent = extent;
    }

    @Override
    public void setExtentPosition( int newYOrigin ) {
        double zoomFactor = DiagramEditorUtil.getCurrentZoomFactor();
        if (getExtent() != null) {
            int newY = (int)(getExtent().getOffset() / zoomFactor) + (int)(newYOrigin / zoomFactor);

            setPosition(new Point(X_ORIGIN, newY));
        } else setPosition(new Point(X_ORIGIN, (int)(newYOrigin / zoomFactor)));
    }

    @Override
    public String getReferenceName() {
        String extentReference = extent.getDocumentNodeReference().toString();
        return extentReference;
    }

    @Override
    public boolean isOnCoarseMappingDiagram() {
        return isCoarseExtent;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#getModelObject()
     */
    @Override
    public EObject getModelObject() {
        if (isOnCoarseMappingDiagram()) {
            return super.getModelObject();
        } else if (getExtent() != null && !(getExtent().getMappingReference() instanceof StagingTable)) {
            return super.getModelObject();
        }
        if (getExtent() != null) {
            return getExtent().getMappingReference();
        }
        return super.getModelObject();
    }

    @Override
    public List getToolTipStrings() {
        List returnList = new ArrayList();

        SummaryExtent seExtent = (SummaryExtent)getExtent();

        addToolTipStringsForLocation(returnList, seExtent);

        returnList.add(MAPPED_NODES_PREFIX + " " + seExtent.getMappingClassColumnCount()); //$NON-NLS-1$ 
        returnList.add(UNMAPPED_NODES_PREFIX + " " + seExtent.getUnmappedNodeCount()); //$NON-NLS-1$

        return returnList;
    }
}
