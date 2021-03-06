package base.workbench;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import modules.Module;

public class ModulePropertyEditor extends JDialog implements ActionListener {

	private static final long serialVersionUID = 325617515135351869L;
	private final JPanel contentPanel = new JPanel();
	private List<ModulePropertyPanel> propertyPanelList = new ArrayList<ModulePropertyPanel>();
	private Module module;

	/**
	 * Create the dialog.
	 * @param module Module to create the editor for
	 */
	public ModulePropertyEditor(Module module) {
		this.module = module;
		
		this.setTitle(module.getClass().getSimpleName()+" properties");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton fileSelectorButton = new JButton();
				fileSelectorButton.setActionCommand("File");
				fileSelectorButton.setIcon(ModuleWorkbenchGui.ICON_LOAD);
				fileSelectorButton.setToolTipText("Opens a dialogue that allows selection of a file path that gets copied to the system clipboard.");
				fileSelectorButton.addActionListener(this);
				buttonPane.add(fileSelectorButton);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
		
		// Determine module properties
		Properties properties = module.getProperties();
		Map<String,String> propertyDefaultMap = module.getPropertyDefaultValues();
		Map<String,String> propertyDescriptionMap = module.getPropertyDescriptions();
		
		// Convert properties into quadrupels and add edit panels for each
		Iterator<String> propertyDescriptionKeys = propertyDescriptionMap.keySet().iterator();
		while(propertyDescriptionKeys.hasNext()){
			
			// Determine property data
			String propertyKey = propertyDescriptionKeys.next();
			String propertyDefault = propertyDefaultMap.get(propertyKey);
			String propertyValue = properties.getProperty(propertyKey);
			String propertyDescription = propertyDescriptionMap.get(propertyKey);
			
			// Instantiate new quadrupel
			PropertyQuadrupel propertyQuadrupel = new PropertyQuadrupel();
			propertyQuadrupel.setKey(propertyKey);
			propertyQuadrupel.setDefaultValue(propertyDefault);
			propertyQuadrupel.setValue(propertyValue);
			propertyQuadrupel.setDescription(propertyDescription);
			
			// Create edit panel
			ModulePropertyPanel propertyPanel = new ModulePropertyPanel(propertyQuadrupel);
			
			// Add edit panel to list
			this.propertyPanelList.add(propertyPanel);
			
			// Add panel to editor
			contentPanel.add(propertyPanel);
		}
		
		// Set size of dialogue according to amount of properties displayed
		this.setSize(this.getSize().width, 65+(propertyDescriptionMap.keySet().size()*21));
		
	}

	/**
	 * @return the propertyPanelList
	 */
	public List<ModulePropertyPanel> getPropertyPanelList() {
		return propertyPanelList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("OK")){
			// User clicked "ok", we have to store the property values in the corresponding module now
			
			// Loop over the existing property panels
			Iterator<ModulePropertyPanel> modulePropertyPanels = this.propertyPanelList.iterator();
			while (modulePropertyPanels.hasNext()){
				
				// Determine the next property panel in line
				ModulePropertyPanel propertyPanel = modulePropertyPanels.next();
				
				// Set or remove the property, depending on its value being null or not
				if (propertyPanel.getProperty().getValue() != null)
					this.module.getProperties().setProperty(propertyPanel.getProperty().getKey(), propertyPanel.getProperty().getValue());
				else
					this.module.getProperties().remove(propertyPanel.getProperty().getKey());
			}
			// Apply module properties
			try {
				this.module.applyProperties();
				// Log message
				Logger.getLogger("").log(Level.INFO, "Updated properties for module "+module.getName()+".");
			} catch (Exception e1) {
				// Log error message
				Logger.getLogger("").log(Level.WARNING, "Sorry, but the properties for module "+module.getName()+" could not be updated.", e1);
			}
			
			
			
			// Close dialog
			this.dispose();
			
		} else if (e.getActionCommand().equals("File")){
			// Open file selector dialogue and copy selected path to the system clipboard.
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setDialogTitle("Copy file path to clipboard");
			fc.setApproveButtonText("Copy path");
			fc.setApproveButtonMnemonic('c');
			fc.setApproveButtonToolTipText("Copy selected file path to system clipboard");
			int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(file.getAbsolutePath());
                systemClip.setContents(selection, selection);
                Logger.getLogger("").log(Level.INFO, "The path '"+file.getAbsolutePath()+"' has been copied to the system clipboard.");
            }
		} else if (e.getActionCommand().equals("Cancel")){
			// Do nothing, just exit
			this.setVisible(false);
		}
	}

}
