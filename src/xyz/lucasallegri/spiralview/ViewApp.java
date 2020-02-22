package xyz.lucasallegri.spiralview;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import com.threerings.util.ResourceUtil;

public class ViewApp implements Runnable {
	
	private static final String VERSION = "1.0";
	private static final String JVM_PATH = "java_vm" + File.separator + "bin" + File.separator + "java";
	public int chosen = 0;
	private static String targetClass = "xyz.lucasallegri.spiralview.ModelViewerHook";
	
	public static void main(String[] args) throws Exception {
		new Thread(new ViewApp()).start();
	}
	
	public void run() {
		verifyRunningDirectory();
		
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					chosen = chooseEditorDialog();
				}	
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		File rsrcdir = new File(new File(System.getProperty("user.dir")), "rsrc");
		ResourceUtil.setPreferredResourceDir(rsrcdir.getAbsolutePath());
		
		String psep = System.getProperty("path.separator");
		String classPath = "spiralview-" + VERSION + ".jar" + psep + "code" + File.separator + "lwjgl.jar" + psep + "code" + File.separator + "projectx-config.jar" + psep + "code" + File.separator + "projectx-pcode.jar" + psep + ".";
		
		resolveTargetedClass(chosen);
		List<String> commandList = new ArrayList<String>();
		commandList.add(JVM_PATH);
		commandList.add("-Djava.library.path=native");
		commandList.add("-cp");
		commandList.add(classPath);
		commandList.add(targetClass);
		if(chosen == 0) commandList.add("rsrc" + File.separator + "character" + File.separator + "pc" + File.separator + "model.dat");
		
		String[] command = new String[commandList.size()];
		commandList.toArray(command);
	    
		try {
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void verifyRunningDirectory() {
		if (!new File("getdown.txt").exists()) {
			JOptionPane.showMessageDialog(null, "You need to place this .jar inside your Spiral Knights main directory.");
			System.exit(1);
		}
	}
	
	private static int chooseEditorDialog() {
		Object[] editors = {"Model Viewer", "Level Editor", "Interface Tester", "Particle Editor"};

		int n = JOptionPane.showOptionDialog(null,
		    "Choose an editor to start",
		    "spiralview (" + VERSION + ")",
		    JOptionPane.DEFAULT_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    editors,
		    null);

		return n;
	}
	
	private static void resolveTargetedClass(int clazz) {
		switch(clazz) {
		case 0:
			break;
		case 1:
			targetClass = "xyz.lucasallegri.spiralview.SceneEditorHook";
			break;
		case 2:
			targetClass = "xyz.lucasallegri.spiralview.InterfaceTesterHook";
			break;
		case 3:
			targetClass = "xyz.lucasallegri.spiralview.ParticleEditorHook";
			break;
		}
	}

}
