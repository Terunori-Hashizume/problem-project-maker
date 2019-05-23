package problemprojectmaker.actions;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

public class ProjectCreator {
	public static void create(IProject project, String templateCode) {
		try {
			if (!project.exists()) {
				project.create(null);
				project.open(null);
				
//				Set project description
				IProjectDescription description = project.getDescription();
				description.setNatureIds(new String[] { JavaCore.NATURE_ID });
				project.setDescription(description, null);
			
//				Make the project a Java project
				IJavaProject javaProject = JavaCore.create(project);
		
//				Create bin folder
				IFolder binFolder = project.getFolder("bin");
				binFolder.create(false, true, null);
				javaProject.setOutputLocation(binFolder.getFullPath(), null);
			
//				About class path config
				List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
				IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
				LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
				for (LibraryLocation element : locations) {
				 entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
				}
//				add libs to project class path
				javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
				
//				Create src folder
				IFolder sourceFolder = project.getFolder("src");
				sourceFolder.create(false, true, null);

				IPackageFragmentRoot jroot = javaProject.getPackageFragmentRoot(sourceFolder);
				IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
				IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
				System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
				newEntries[oldEntries.length] = JavaCore.newSourceEntry(jroot.getPath());
				javaProject.setRawClasspath(newEntries, null);
				
//				Create a package and Java class (Main)
				IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment("mypackage", false, null);
				pack.createCompilationUnit("Main.java", templateCode, false, null);
				
//				Append test case file
				IFile testFile = project.getFile("test_cases.txt");
				testFile.create(new ByteArrayInputStream("".getBytes()), false, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
