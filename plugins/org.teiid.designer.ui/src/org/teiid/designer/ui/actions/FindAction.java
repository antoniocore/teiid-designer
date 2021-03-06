/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;


//import org.eclipse.core.runtime.IStatus;
import java.util.ResourceBundle;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelObjectEditorPanel;


/**
 * The <code>FindAction</code> class is the action that handles the global Find.
 * @since 8.0
 */
public class FindAction extends AbstractAction 
                implements IPartListener,
                           FocusListener
{

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private FindReplaceAction actEclipseFindReplaceAction;
    private boolean bHasFocus;
    private ModelObjectEditorPanel moepModelEditorPanel = null;

    private FocusEvent feMostRecentFocusGainEvent;
//    private FocusEvent feMostRecentFocusLostEvent;
//    private int iCallCount = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public FindAction() {
        
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.FIND));
        setEnableState();                
//        System.out.println("[FindAction.ctor] Hash: " + this.hashCode() ); //$NON-NLS-1$
    }
        
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
//        System.out.println("[FindAction.selectionChanged] "); //$NON-NLS-1$
        super.selectionChanged(thePart, theSelection);
        setEnableState( thePart );
    }
    
    @Override
    protected void doRun() {

//        System.out.println("[FindAction.doRun] About to execute 'actEclipseFindReplaceAction..run()'"); //$NON-NLS-1$
        FindReplaceAction actFindReplace = getFindReplaceAction();
        actFindReplace.update();
        actFindReplace.run();
    }


    private void setEnableState( IWorkbenchPart part ) {
//        iCallCount++;
//        System.out.println("[FindAction.setEnableState] pass: " + iCallCount + " Hash: " + this.hashCode() ); //$NON-NLS-1$
        
        if ( part != null ) {
        
            IFindReplaceTarget target = (IFindReplaceTarget)part.getAdapter( IFindReplaceTarget.class );
            
            if ( target != null ) {
//                if ( hasFocus() ) {
//                    System.out.println("[FindAction.setEnableState] About to set state true (hasFocus() is true)"); //$NON-NLS-1$
//                } else {                    
//                    System.out.println("[FindAction.setEnableState] About to set state false (hasFocus() is false)"); //$NON-NLS-1$
//                }
                setEnabled( hasFocus() /*target.canPerformFind()*/ ); // jhTODO: retest and see if canPerformFind EVER works
            } else {
//                System.out.println("[FindAction.setEnableState] target is NULL, about to set state false"); //$NON-NLS-1$
                setEnabled( false );        
            }
        } else {
//            System.out.println("[FindAction.setEnableState] part is NULL, setting state false"); //$NON-NLS-1$
            setEnabled( false );
        }
    }

    private void setEnableState() {
        setEnableState( getCurrentWorkbenchPart() );
    }

    private FindReplaceAction getFindReplaceAction() {
        ResourceBundle rb = ResourceBundle.getBundle( UiConstants.PLUGIN_ID + ".i18n" ); //$NON-NLS-1$
        
        IWorkbenchPart workbenchPart = getCurrentWorkbenchPart();
        
        actEclipseFindReplaceAction 
            = new FindReplaceAction(rb, "ConstantEditor.title", workbenchPart); //$NON-NLS-1$

        actEclipseFindReplaceAction
            .setHelpContextId(IAbstractTextEditorHelpContextIds.FIND_ACTION);
        actEclipseFindReplaceAction
            .setActionDefinitionId(IWorkbenchActionDefinitionIds.FIND_REPLACE);
        
        return actEclipseFindReplaceAction;
    }

    private IWorkbenchPart getCurrentWorkbenchPart() {
    
        IWorkbenchPage activePage = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage != null) {
            IWorkbenchPart workbenchPart = activePage.getActiveEditor();
            return workbenchPart;
        } // endif
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partActivated(IWorkbenchPart part) {
//        System.out.println("\n\n + + + + + + [FindAction.partActivated] part hash: " + part.hashCode() ); //$NON-NLS-1$  part
//        System.out.println(" + + + + + + [FindAction.partActivated] part is: " + part.getTitle() ); //$NON-NLS-1$  part
        
        if ( part instanceof ModelEditor ) {
//            System.out.println("[FindAction.partActivated] About to retrieve MOEP " ); //$NON-NLS-1$  part
            moepModelEditorPanel = ((ModelEditor)part).getEditorContainer();
            
            if ( moepModelEditorPanel != null ) {            
//                System.out.println("[FindAction.partActivated] About to ADD focusListener  " ); //$NON-NLS-1$  part
                moepModelEditorPanel.addFocusListener( this );
            } else {
//                System.out.println("[FindAction.partActivated] NO MOEP FOUND!!! - will NOT ADD focusListening " ); //$NON-NLS-1$  part                
            }
        }
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partBroughtToTop(IWorkbenchPart part) {
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partClosed(IWorkbenchPart part) {
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partDeactivated(IWorkbenchPart part) {
//        System.out.println("\n - - - - - -[FindAction.partDeactivated] part hash: " + part.hashCode() ); //$NON-NLS-1$  part
//        System.out.println(" - - - - - -[FindAction.partDeactivated] part is: " + part.getTitle() ); //$NON-NLS-1$  part
        
        if ( part instanceof ModelEditor && moepModelEditorPanel == null) {
            moepModelEditorPanel = ((ModelEditor)part).getEditorContainer();
            
            if ( moepModelEditorPanel != null ) {            
//                System.out.println("[FindAction.partDeactivated] About to REMOVE focusListener " ); //$NON-NLS-1$  part                
                moepModelEditorPanel.removeFocusListener( this );
            } else {
//                System.out.println("[FindAction.partDeactivated] NO MOEP FOUND!!! - will NOT REMOVE focusListening " ); //$NON-NLS-1$  part                
            }
        }
        moepModelEditorPanel = null;
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partOpened(IWorkbenchPart part) {
        setEnableState();
    }
    
    public boolean hasFocus() {
//        System.out.println("[FindAction.hasFocus] returning: " + bHasFocus ); //$NON-NLS-1$
        return bHasFocus;        
    }
    
    @Override
	public void focusLost( FocusEvent fe ) {
//        System.out.println("[FindAction.focusLost] widget: " + fe.widget ); //$NON-NLS-1$
        
        // if this 'focusLost' is related to our current 'focusGain' widget,
        //  then we are now in a no-focus state:
        if( (feMostRecentFocusGainEvent) != null && (fe.widget == feMostRecentFocusGainEvent.widget) ) {
//            System.out.println("[FindAction.focusLost] this LOST closes our last GAINED; setting hasFocus to FALSE"); //$NON-NLS-1$
            bHasFocus = false;               
        }
        
        // save the new focusLost event
//        feMostRecentFocusLostEvent = fe;
        
        // reset enable state
        setEnableState();      
    }
    
    
    @Override
	public void focusGained( FocusEvent fe ) {
        
//        System.out.println("[FindAction.focusGained] widget: " + fe.widget ); //$NON-NLS-1$
        bHasFocus = true;   
                
        feMostRecentFocusGainEvent = fe;
        
        // reset enable state
        setEnableState();     
    } 
}
