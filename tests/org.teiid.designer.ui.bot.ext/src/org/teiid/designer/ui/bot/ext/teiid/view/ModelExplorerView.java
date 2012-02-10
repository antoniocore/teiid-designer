package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.condition.NonSystemJobRunsCondition;
import org.jboss.tools.ui.bot.ext.condition.ProgressInformationShellIsActiveCondition;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourceDialog;

/**
 * Represent Model Explorer View and equivalent to Package explorer from Java perspective. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class ModelExplorerView extends View {

	private static final String MODELING_MENU_ITEM = "Modeling";

	public ModelExplorerView() {
		super("Model Explorer");
	}
	
	public void newBaseTable(String project, String model, String tableName){
		SWTBot bot = getBot();
		SWTBotTreeItem model_node =  SWTEclipseExt.selectTreeLocation(bot, project, model);

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), model_node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "New Child", "Base Table");

		bot.text("NewBaseTable").setText(tableName);
		bot.tree().setFocus();
	}

	public void addTransformationSource(String project, String model, String tableName){
		SWTBot bot = getBot();
		SWTBotTreeItem table_node = SWTEclipseExt.selectTreeLocation(bot, project, model, tableName); 

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), table_node);
		ContextMenuHelper.clickContextMenu(bot.tree(), MODELING_MENU_ITEM, "Add Transformation Source(s)");
	}
	
	public DatasourceDialog createDataSource(String project, String model){
		SWTBot bot = getBot();
		SWTBotTreeItem table_node = SWTEclipseExt.selectTreeLocation(bot, project, model); 

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), table_node);
		ContextMenuHelper.clickContextMenu(bot.tree(), MODELING_MENU_ITEM, "Create Data Source");
		
		SWTBotShell shell = bot.shell("Create Data Source");
		shell.activate();
		return new DatasourceDialog(shell);
	}
	
	public void executeVDB(String project, String vdb){
		SWTBot bot = getBot();
		
		SWTBotTreeItem vdb_node =  SWTEclipseExt.selectTreeLocation(bot, project, vdb);
		
		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), vdb_node);
		ContextMenuHelper.clickContextMenu(bot.tree(), MODELING_MENU_ITEM, "Execute VDB");
		
		bot.waitWhile(new ProgressInformationShellIsActiveCondition(), TaskDuration.LONG.getTimeout());
		bot.waitWhile(new NonSystemJobRunsCondition(), TaskDuration.LONG.getTimeout());
	}
}