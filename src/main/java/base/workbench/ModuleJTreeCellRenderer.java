package base.workbench;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import modules.Module;

/**
 * Lets a JTree containing modules display the selected item's description as tooltip.
 * @author Marcel Boeing
 *
 */
public class ModuleJTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 8321180041458513120L;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		// Determine whether a node carrying a module is selected
		if (value != null
				&& DefaultMutableTreeNode.class.isAssignableFrom(value.getClass())
				&& ((DefaultMutableTreeNode)value).getUserObject() != null
				&& Module.class.isAssignableFrom(((DefaultMutableTreeNode)value).getUserObject().getClass())){
			Module module = (Module)((DefaultMutableTreeNode)value).getUserObject();
			// Set tooltip to selected module's description
			setToolTipText("<html><div style='background-color:FFFFFF;' width=\"500\"><h1 style='font-size:12px;'>"+module.getName()+"</h1><p>"+module.getDescription()+"</p></div></html>");
		} else
			setToolTipText(null);
		
		// Call super method
		return super.getTreeCellRendererComponent(tree, value, sel,
				expanded, leaf, row, hasFocus);
	}

}
