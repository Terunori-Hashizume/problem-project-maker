package problemprojectmaker.actions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		String prefix = null;
		String[] problems = {};
		
		InputDialog dialog = new InputDialog(window.getShell(), "Prefix", "Enter the prefix of project name.", null, null);
		if (dialog.open() == org.eclipse.jface.window.Window.OK) {
			prefix = dialog.getValue();
		}
		
		dialog = new InputDialog(window.getShell(), "Problem", "Enter the name of problem(s).\n (Separated with comma)", null, null);
		if (dialog.open() == org.eclipse.jface.window.Window.OK) {
			problems = dialog.getValue().split(",");
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
//		Load template java file
		String templateFile = System.getProperty("user.home") + "/Main.java";
		StringBuilder templateCode = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(templateFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				templateCode.append(line).append('\n');
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String problem : problems) {
			String projectName = prefix + "_" + problem;
			IProject project = root.getProject(projectName);
			ProjectCreator.create(project, templateCode.toString());
		}
		
		MessageDialog.openInformation(
			window.getShell(),
			"Problem project maker",
			"Successfully created.");
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}