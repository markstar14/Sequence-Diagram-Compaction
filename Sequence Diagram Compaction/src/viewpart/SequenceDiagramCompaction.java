package viewpart;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import utilities.Util;
import calltree.CNode;
import calltree.CompactCallTrees;
import calltree.PlantUML;

import events.ContextShiftEvent;
import events.Event;
import events.EventSequence;
import events.MethodCallEvent;
import events.MethodExitEvent;
import events.NewObjectEvent;
import events.ThreadEndEvent;
import events.ThreadStartEvent;

public class SequenceDiagramCompaction extends ViewPart {
	
	private IStatusLineManager statusLineManager;
	private Display display;
	private ScrolledComposite rootScrollComposite;
	private Composite evComposite;
	private Composite compactComposite;
	private Composite mainComposite;
	private Label fileLabel;
	private Text fileText;
	private Button browseButton;
	private Button exportButton;
	private Button minimizeButton;
	private Button resetButton;
	private Composite imageComposite;
	private Label imageLabel;
	private Image image;
	private Combo minDropDown;
	private Button drawMinButton;
	private Button compactButton;
	
	private HashMap<String, StringBuffer> minimizedSectionMap = new HashMap<String, StringBuffer>();
	//private Canvas canvas;
	
	//<--------BEGIN
	private Label minimizeLabel;
	
	
	private Text endObjectText;

	public boolean horizontal;
	public boolean vertical;
	
	public int startCount;	
	public int endCount;	
	public int eventCount;	
	public int mergeCount;
	
	private int numLifelines;
	private int numInteractions;
	HashSet<String> lifelines; // = new HashSet<String>();
	private Label eventsLabel;
	private Text eventsCountText;
	private Label startLabel;
	private Text startCountText;
	private Label endLabel;
	private Text endCountText;
	private Button svgButton;
	private Button vcChkBox;
	private Button hcChkBox;
	private Label mergeLabel;
	private Text mergeCountText;
	private Composite hcComposite;

	
	//<--------END


	public SequenceDiagramCompaction() {
		// TODO Auto-generated constructor stub
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * 
	 * createPartControl is the method that creates the user interface for the plug in. The additional radio buttons will toggle the visibility of the controls
	 * for the original compaction plug in and the controls for the sub-diagram inspector. All parts are within the mainComposite. The browse options adn export button are within
	 * the browseComposite. radioComposite just holds the radio buttons. evComposite holds the controls for the sub-diagram inspector. compactComposite holds the first line of 
	 * controls for the diagram compactor, and hcComposite holds the second line, with the check boxes and redraw button.
	 */
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		
		display = parent.getDisplay();

		GridLayout layoutParent = new GridLayout();
		layoutParent.numColumns = 1;
		parent.setLayout(layoutParent);
		
		rootScrollComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		rootScrollComposite.setLayout(new GridLayout(1,false));
		rootScrollComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		rootScrollComposite.setExpandHorizontal(true);
		rootScrollComposite.setExpandVertical(true);
		
		mainComposite = new Composite(rootScrollComposite, SWT.NONE);
		rootScrollComposite.setContent(mainComposite);
		
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL, true,true));
		
		Composite browseComposite = new Composite(mainComposite, SWT.NONE);
		browseComposite.setLayout(new GridLayout(5,false));
		browseComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		
		
		exportButton = new Button(browseComposite, SWT.PUSH);
		exportButton.setText("Export");
		
		browseButton = new Button(browseComposite, SWT.PUSH);
		browseButton.setText("Browse");
		
		fileLabel = new Label(browseComposite, SWT.FILL);
		fileLabel.setText("Execution Trace CSV File : ");
		
		fileText = new Text(browseComposite, SWT.READ_ONLY);
		fileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		GridData gd = new GridData();
		gd.widthHint = 630;
		fileText.setLayoutData(gd);
		
		
		
		Composite radioComposite = new Composite(mainComposite, SWT.NONE);
		radioComposite.setLayout(new GridLayout(8, false));
		radioComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button[] radios = new Button[2];

	    radios[0] = new Button(radioComposite, SWT.RADIO);
	    radios[0].setSelection(true);
	    radios[0].setText("Compact Sequence Diagram");

	    radios[1] = new Button(radioComposite, SWT.RADIO);
	    radios[1].setText("Sub-Diagram Inspector");
		
		
		//<-------BEGIN
		
		

		evComposite = new Composite(mainComposite, SWT.NONE);
		evComposite.setLayout(new GridLayout(8, false));
		evComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		minimizeLabel = new Label(evComposite, SWT.FILL);
		minimizeLabel.setText("    Minimize:     ");
		
		endObjectText = new Text(evComposite, SWT.BORDER|SWT.FILL);
		GridData gd3 = new GridData();
		gd3.widthHint = 214;
		endObjectText.setLayoutData(gd3);

		minimizeButton = new Button(evComposite, SWT.PUSH);
		minimizeButton.setText("Draw");
		
		Label separator = new Label(evComposite, SWT.NONE);
		separator.setText("     ");

		resetButton = new Button (evComposite, SWT.PUSH);
		resetButton.setText("Reset");
		
		Label separator2 = new Label(evComposite, SWT.NONE);
		separator2.setText("     ");
		
		minDropDown = new Combo(evComposite, SWT.DROP_DOWN | SWT.BORDER);
		
		drawMinButton = new Button(evComposite, SWT.PUSH);
		drawMinButton.setText("Draw ");
		
		
		
		compactComposite = new Composite(mainComposite, SWT.NONE);
		compactComposite.setLayout(new GridLayout(8, false));
		compactComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		compactComposite.setVisible(false);
		
		

		eventsLabel = new Label(compactComposite, SWT.FILL);
		eventsLabel.setText("Total Events:     ");
		
		eventsCountText = new Text(compactComposite, SWT.READ_ONLY);
		eventsCountText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		GridData gd4 = new GridData();
		gd4.widthHint = 70;
		eventsCountText.setLayoutData(gd4);
		
		startLabel = new Label(compactComposite, SWT.FILL);
		startLabel.setText("     Start Event:     ");
		
		startCountText = new Text(compactComposite, SWT.BORDER|SWT.FILL);
		GridData gd2 = new GridData();
		gd2.widthHint = 70;
		startCountText.setLayoutData(gd2);

		endLabel = new Label(compactComposite, SWT.FILL);
		endLabel.setText("    End Event:     ");
		
		endCountText = new Text(compactComposite, SWT.BORDER|SWT.FILL);
		GridData gd5 = new GridData();
		gd5.widthHint = 70;
		endCountText.setLayoutData(gd5);

		Label separator3 = new Label(compactComposite, SWT.NONE);
		separator3.setText("     ");

		svgButton = new Button(compactComposite, SWT.PUSH);
		svgButton.setText("Redraw Sequence Diagram");

	
		//<---------END
		
		hcComposite = new Composite(mainComposite, SWT.NONE);
		hcComposite.setLayout(new GridLayout(6, false));
		hcComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		//Label vertSeparator = new Label(hcComposite, SWT.NONE);
		//vertSeparator.setText("   |   ");
	
		vcChkBox = new Button(hcComposite, SWT.CHECK);
		vcChkBox.setText("Vertical Compaction");

		Label vertSeparator = new Label(hcComposite, SWT.NONE);
		vertSeparator.setText("      |      ");
		
		hcChkBox = new Button(hcComposite, SWT.CHECK);
		hcChkBox.setText("Horizontal Compaction");
		
		vertSeparator = new Label(hcComposite, SWT.NONE);
		vertSeparator.setText("   |   ");
		
		mergeLabel = new Label(hcComposite, SWT.FILL);
		mergeLabel.setText("Merge lifelines for object instances greater than: ");
		
		mergeCountText = new Text(hcComposite, SWT.BORDER|SWT.FILL);
		GridData gd1 = new GridData();
		gd1.widthHint = 40;
		mergeCountText.setLayoutData(gd1);
		
		//<---------END
		
		evComposite.setVisible(false);
		compactComposite.setVisible(true);
		hcComposite.setVisible(true);
		
			
		imageComposite = new Composite(mainComposite, SWT.NONE);
		imageComposite.setLayout(new GridLayout(1,false));
		imageComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		
		
		imageLabel = new Label(imageComposite,SWT.NONE);
		rootScrollComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		radios[0].addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				browseButtonAction(e);
				compactComposite.setVisible(true);
				hcComposite.setVisible(true);
				evComposite.setVisible(false);

			}
		});
		
		radios[1].addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				browseButtonAction(e);
				compactComposite.setVisible(false);
				hcComposite.setVisible(false);
				evComposite.setVisible(true);
				
			}
		});
		
		
		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonAction(e);
			}
		});
		
		exportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportButtonAction(e);
			}
		});
		
		drawMinButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				drawMinButtonAction(e);
			}
		});
		
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetButtonAction(e);
			}
		});
		
		minimizeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				minimizeMainDrawButtonAction(e);

			}
		});
		
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetButtonAction(e);
			}
		});
		
		svgButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				drawButtonAction(e);
			}
		});
		
		imageLabel.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (image != null) { 
					if (!image.isDisposed()) {
						System.out.println("Image disposed");
						image.dispose();	
					}	
				}
			}
		});
	}
	
	
	/*
	 * browseButtonAction is largely unchanged from the original plugin. The only modification is that it does not horizontally compact by default anymore.
	 */
	private void browseButtonAction(SelectionEvent e) {
		

		horizontal = false;
		mergeCount = 1000;
		numLifelines = 0;
		numInteractions = 0;
		lifelines = new HashSet<String>();
		
		if (image != null) { 
			if (!image.isDisposed()) {
				System.out.println("Image disposedx");
				image.dispose();	
			}	
		}
		
		statusLineManager.setMessage(null);
		
		FileDialog fd = new FileDialog(new Shell(Display.getCurrent(),SWT.OPEN));
		fd.setText("Open CSV File");
		
		String[] filterExtensions = {"*.csv"};
		fd.setFilterExtensions(filterExtensions);
		
		String fileName = fd.open();
		if (fileName == null)
			return;
		
		//eventsCountText.setText(null);
		fileText.setText(fileName);
		
		//<---------BEGIN
		String startCountString = startCountText.getText(); // 1. event count
		if( startCountString.isEmpty() )
			startCount = 1;
		else { // if not empty {
			try {
				startCount = Integer.parseInt(startCountString);
			} catch(NumberFormatException nfe) {
				MessageDialog.openError(new Shell(Display.getCurrent()), 
						"Error", "ERROR: \'" + startCountString +"\' is not an integer");
				startCountText.setFocus();
			}
		}	
		
		String endCountString = endCountText.getText(); // 1. event count
		if( endCountString.isEmpty() )
			endCount = 2000000;
		else { // if not empty {
			try {
				endCount = Integer.parseInt(endCountString);
			} catch(NumberFormatException nfe) {
				MessageDialog.openError(new Shell(Display.getCurrent()), 
						"Error", "ERROR: \'" + endCountString +"\' is not an integer");
				endCountText.setFocus();
			}
		}	
		
		vertical = vcChkBox.getSelection();  // 2. vertical check box 
		
		horizontal = hcChkBox.getSelection();  // 3. horizontal check box
		if(horizontal == true)	{	// if checkbox is checked
			String mergeCountString = mergeCountText.getText();
			
			if( mergeCountString.isEmpty() ) {
				mergeCount = 1;		// Set to 1. i.e. merge lifelines of objects of more than 1 instance
				/*MessageDialog.openWarning(new Shell(Display.getCurrent()), 
						"Warning", "Please specify an integer value in the text box");
				mergeCountText.setFocus();
				return;*/
			} else {	// if not empty
				mergeCount = -1;
				try {
					mergeCount = Integer.parseInt(mergeCountString);
				} catch(NumberFormatException nfe) {
					MessageDialog.openError(new Shell(Display.getCurrent()), 
							"Error", "ERROR: \'" + mergeCountString +"\' is not an integer");
					mergeCountText.setFocus();
				}				
				
				if(mergeCount == -1)	// the specified value is not valid
					return;
			}
		}
		else
			mergeCount = 1000;	// Set to some high value
		
		//System.out.println("-------- horizontal=" + horizontal + ", mergeCount=" + mergeCount);
		//<---------END
		// Fix the name of the SVG file
		String svgFile = fileName.substring(0, fileName.lastIndexOf('.'));
		if (horizontal && vertical)
			svgFile = svgFile + "_hvcompact.svg";
		else if (horizontal)
			svgFile = svgFile + "_hcompact.svg";
		else if (vertical)
			svgFile = svgFile + "_vcompact.svg";
		else
			svgFile = svgFile + ".svg";
		System.out.println("SVG File: " + svgFile);

		long startTime = System.nanoTime();
		
		//PlantUML.clear();
		PlantUML p = new PlantUML();
		
		EventSequence events = new EventSequence();
		loadEvents(fileName,events);
		//printEvents(events);
		
		//LinkedHashMap<String,Node> tct = CallTrees.construct(events, p);
		//CallTrees.printObjects();
		//CallTrees.printTrees();
		//p.export(tct);

		LinkedHashMap<String,CNode> tcct = CompactCallTrees.construct(events, p);
		
		//CompactCallTrees.printObjects();
		if (horizontal) 
			CompactCallTrees.mergeLifelines(mergeCount);
		
		if (vertical) 
			CompactCallTrees.compact(p);
		//p.printRMC();
		
		int nodeCount = CompactCallTrees.countNodes();
		int rlifelines = Util.cLifelines.size();
		
		long compactTime = System.nanoTime();
		p.exportC(tcct, startCount, endCount, svgFile);

		//PlantUML.drawquSequenceDiagram();
		//PlantUML.drawCompactSeenceDiagram();
		
		try{
			IPath path = ResourcesPlugin.getPlugin().getStateLocation();
			File seqDiagFile = new File(path.toFile().getPath() + File.separator + "compact.png");
			//File seqDiagFile = new File(svgFile);
			image =  new Image(display, seqDiagFile.getPath());
			//GC gc = new GC(image);
			imageLabel.setImage(image);
			//canvas.setBackgroundImage(image);
			//canvas.setBounds(image.getBounds());
		} catch (Exception ie) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + ie.getMessage());
		}
		
		long drawTime = System.nanoTime();
		
		statusLineManager.setMessage("[# Interactions x # Lifelines]  "
					+ "   BEFORE: " + numInteractions + " x " + numLifelines
					+ "   |   AFTER: " + nodeCount + " x " + rlifelines
					+ "             Time to compact: " + Math.round((compactTime - startTime)/1000000) + " ms"
					+ "   |   Time to draw: " + Math.round((drawTime - compactTime)/1000000) + " ms"
					);
		
		imageComposite.pack();
		rootScrollComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mergeCount = 100;
		horizontal = false;
		
		
	}

	
	/*
	 * resetButtonAction is basically the same code as browseButtonAction, but excludes the initial file search. Used to reset the sequence diagram to its non-compacted state
	 */
	private void resetButtonAction(SelectionEvent e) {
		
		horizontal = false;
		mergeCount = 1000;
		numLifelines = 0;
		numInteractions = 0;
		lifelines = new HashSet<String>();
		
		if (image != null) { 
			if (!image.isDisposed()) {
				System.out.println("Image disposed");
				image.dispose();	
			}	
		}
		
		statusLineManager.setMessage(null);
		
		String fileName = fileText.getText();
		
		//<---------BEGIN
		startCount = 1;
		
		endCount = 2000000;
		vertical = false;
		horizontal = false;

		mergeCount = 1000;	// Set to some high value
		
		minimizedSectionMap = new HashMap<String, StringBuffer>();
		minDropDown.removeAll();
		
		//System.out.println("-------- horizontal=" + horizontal + ", mergeCount=" + mergeCount);
		//<---------END
		// Fix the name of the SVG file
		String svgFile = fileName.substring(0, fileName.lastIndexOf('.'));

			svgFile = svgFile + ".svg";
		System.out.println("SVG File: " + svgFile);

		long startTime = System.nanoTime();
		
		//PlantUML.clear();
		PlantUML p = new PlantUML();
		
		EventSequence events = new EventSequence();
		loadEvents(fileName,events);
		//printEvents(events);
		
		
		LinkedHashMap<String,CNode> tcct = CompactCallTrees.construct(events, p);
		
	
		p.printRMC();
		
		int nodeCount = CompactCallTrees.countNodes();
		int rlifelines = Util.cLifelines.size();
		
		long compactTime = System.nanoTime();
		p.exportC(tcct, startCount, endCount, svgFile);

		try{
			IPath path = ResourcesPlugin.getPlugin().getStateLocation();
			File seqDiagFile = new File(path.toFile().getPath() + File.separator + "compact.png");
			//File seqDiagFile = new File(svgFile);
			image =  new Image(display, seqDiagFile.getPath());
			//GC gc = new GC(image);
			imageLabel.setImage(image);
			//canvas.setBackgroundImage(image);
			//canvas.setBounds(image.getBounds());
		} catch (Exception ie) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + ie.getMessage());
		}
		
		long drawTime = System.nanoTime();
		
		statusLineManager.setMessage("[# Interactions x # Lifelines]  "
					+ "   BEFORE: " + numInteractions + " x " + numLifelines
					+ "   |   AFTER: " + nodeCount + " x " + rlifelines
					+ "             Time to compact: " + Math.round((compactTime - startTime)/1000000) + " ms"
					+ "   |   Time to draw: " + Math.round((drawTime - compactTime)/1000000) + " ms"
					);
		
		imageComposite.pack();
		rootScrollComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mergeCount = 100;
		horizontal = false;
		
	}
	
	/*
	 * Unchanged from the original plugin
	 */
	private void drawButtonAction(SelectionEvent e) {

		String fileName = fileText.getText();
		numLifelines = 0;
		numInteractions = 0;
		lifelines = new HashSet<String>();
				
		if (image != null) { 
			if (!image.isDisposed()) {
				System.out.println("Image disposedx");
				image.dispose();	
			}	
		}
		
		//statusLineManager.setMessage(null);
		
		//<---------BEGIN
		String startCountString = startCountText.getText(); // 1. event count
		if( startCountString.isEmpty() )
			startCount = 1;
		else { // if not empty {
			try {
				startCount = Integer.parseInt(startCountString);
			} catch(NumberFormatException nfe) {
				MessageDialog.openError(new Shell(Display.getCurrent()), 
						"Error", "ERROR: \'" + startCountString +"\' is not an integer");
				startCountText.setFocus();
			}
		}	
		
		String endCountString = endCountText.getText(); // 1. event count
		if( endCountString.isEmpty() )
			endCount = 2000000;
		else { // if not empty {
			try {
				endCount = Integer.parseInt(endCountString);
			} catch(NumberFormatException nfe) {
				MessageDialog.openError(new Shell(Display.getCurrent()), 
						"Error", "ERROR: \'" + endCountString +"\' is not an integer");
				endCountText.setFocus();
			}
		}	
		
		vertical = vcChkBox.getSelection();  // 2. vertical check box 
		
		horizontal = hcChkBox.getSelection();  // 3. horizontal check box
		if(horizontal == true)	{	// if checkbox is checked
			String mergeCountString = mergeCountText.getText();
			
			if( mergeCountString.isEmpty() ) {
				mergeCount = 1;		// Set to 1. i.e. merge lifelines of objects of more than 1 instance
				/*MessageDialog.openWarning(new Shell(Display.getCurrent()), 
						"Warning", "Please specify an integer value in the text box");
				mergeCountText.setFocus();
				return;*/
			} else {	// if not empty
				mergeCount = -1;
				try {
					mergeCount = Integer.parseInt(mergeCountString);
				} catch(NumberFormatException nfe) {
					MessageDialog.openError(new Shell(Display.getCurrent()), 
							"Error", "ERROR: \'" + mergeCountString +"\' is not an integer");
					mergeCountText.setFocus();
				}				
				
				if(mergeCount == -1)	// the specified value is not valid
					return;
			}
		}
		else
			mergeCount = 1000;	// Set to some high value
		
		//System.out.println("-------- horizontal=" + horizontal + ", mergeCount=" + mergeCount);
		//<---------END
		// Fix the name of the SVG file
		String svgFile = fileName.substring(0, fileName.lastIndexOf('.'));
		if (horizontal && vertical)
			svgFile = svgFile + "_hvcompact.svg";
		else if (horizontal)
			svgFile = svgFile + "_hcompact.svg";
		else if (vertical)
			svgFile = svgFile + "_vcompact.svg";
		else
			svgFile = svgFile + ".svg";
		System.out.println("SVG File: " + svgFile);

		long startTime = System.nanoTime();
		//PlantUML.clear();
		PlantUML p = new PlantUML();
		
		EventSequence events = new EventSequence();
		loadEvents(fileName,events);
		//printEvents(events);
		
		//LinkedHashMap<String,Node> tct = CallTrees.construct(events, p);
		//CallTrees.printObjects();
		//CallTrees.printTrees();
		//p.export(tct);

		LinkedHashMap<String,CNode> tcct = CompactCallTrees.construct(events, p);
		//CompactCallTrees.printObjects();
		if (horizontal) 
			CompactCallTrees.mergeLifelines(mergeCount);
		
		if (vertical) 
			CompactCallTrees.compact(p);
		//p.printRMC();
		
		int nodeCount = CompactCallTrees.countNodes();
		int rlifelines = Util.cLifelines.size();
		
		long compactTime = System.nanoTime();
		p.exportC(tcct, startCount, endCount, svgFile);

		//PlantUML.drawquSequenceDiagram();
		//PlantUML.drawCompactSeenceDiagram();
		
		try{
			IPath path = ResourcesPlugin.getPlugin().getStateLocation();
			File seqDiagFile = new File(path.toFile().getPath() + File.separator + "compact.png");
			//File seqDiagFile = new File(svgFile);
			image =  new Image(display, seqDiagFile.getPath());
			//GC gc = new GC(image);
			imageLabel.setImage(image);
			//canvas.setBackgroundImage(image);
			//canvas.setBounds(image.getBounds());
		} catch (Exception ie) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + ie.getMessage());
		}
		long drawTime = System.nanoTime();
		
		statusLineManager.setMessage("[Total Interactions x Total Lifelines]  "
				+ "   BEFORE: " + numInteractions + " x " + numLifelines
				+ "   |   AFTER: " + nodeCount + " x " + rlifelines
				+ "             Time to compact: " + Math.round((compactTime - startTime)/1000000) + " ms"
				+ "   |   Time to draw: " + Math.round((drawTime - compactTime)/1000000) + " ms"
				);
		
		imageComposite.pack();
		rootScrollComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mergeCount = 100;
		horizontal = false;
	}
	
	
	/*
	 * minimizeMainDrawButtonAction is used to emove specified sub-diagrams from the main sequence diagram. It removes the compaction functionality from the original
	 * plugin and uses a modified print method from PlantUML.java that will edit out certain parts of the diagram depending on what is in the text box.
	 */
	private void minimizeMainDrawButtonAction(SelectionEvent e) {

		String fileName = fileText.getText();
		numLifelines = 0;
		numInteractions = 0;
		lifelines = new HashSet<String>();
				
		if (image != null) { 
			if (!image.isDisposed()) {
				System.out.println("Image disposed");
				image.dispose();	
				
			}	
		}
		
		startCount = 1;
		endCount = 2000000;
		vertical = false;
		horizontal = false;

		mergeCount = 1000;	// Set to some high value
		
		//System.out.println("-------- horizontal=" + horizontal + ", mergeCount=" + mergeCount);
		//<---------END
		// Fix the name of the SVG file
		String svgFile = fileName.substring(0, fileName.lastIndexOf('.'));

		svgFile = svgFile + ".svg";
		System.out.println("SVG File: " + svgFile);

		long startTime = System.nanoTime();
		//PlantUML.clear();
		PlantUML p = new PlantUML();
		
		EventSequence events = new EventSequence();
		loadEvents(fileName,events);
		LinkedHashMap<String,CNode> tcct = CompactCallTrees.construct(events, p);
		
		
		int nodeCount = CompactCallTrees.countNodes();
		int rlifelines = Util.cLifelines.size();
		
		long compactTime = System.nanoTime();
		minimizedSectionMap = p.exportMin(tcct, startCount, endCount, svgFile, endObjectText.getText());
		
		
		//Add all minimized objects and methods to the drop down box
		minDropDown.removeAll();
		Iterator it = minimizedSectionMap.entrySet().iterator();
		ArrayList<String> sortedTermNames = new ArrayList<String>();
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
//			sortedTermNames.add((String) pair.getKey());
			minDropDown.add((String) pair.getKey());
		}
		
//		sortedTermNames = sortTermNames(sortedTermNames);
		for(int i = 0; i < sortedTermNames.size(); i++){
			
		}
		
		minDropDown.redraw();
//		System.out.println(minimizedSectionMap);
		
		try{
			IPath path = ResourcesPlugin.getPlugin().getStateLocation();
			File seqDiagFile = new File(path.toFile().getPath() + File.separator + "compact.png");
			image =  new Image(display, seqDiagFile.getPath());
			imageLabel.setImage(image);
			imageLabel.setBounds(100, 100, image.getBounds().width, image.getBounds().height);
			
		} catch (Exception ie) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + ie.getMessage());
		}
		long drawTime = System.nanoTime();
		
		statusLineManager.setMessage("[Total Interactions x Total Lifelines]  "
				+ "   BEFORE: " + numInteractions + " x " + numLifelines
				+ "   |   AFTER: " + nodeCount + " x " + rlifelines
				+ "             Time to compact: " + Math.round((compactTime - startTime)/1000000) + " ms"
				+ "   |   Time to draw: " + Math.round((drawTime - compactTime)/1000000) + " ms"
				);
		imageComposite.pack();
		rootScrollComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mergeCount = 100;
		horizontal = false;
	}
	
	/*
	 * drawMinButtonAction used to create the sub-diagrams within the pop-up windows. Similar to the previous method, it leaves out the compaction functionality
	 * and instead uses a modified export method within PlantUML.java
	 */
	private void drawMinButtonAction(SelectionEvent e) {

		String fileName = fileText.getText();
		numLifelines = 0;
		numInteractions = 0;
		lifelines = new HashSet<String>();
		
		JFrame frame = new JFrame();
		frame.setSize(400, 400);
		frame.setTitle(minDropDown.getText());
		
		JPanel panel = new JPanel();
	    JScrollPane scroll = new JScrollPane(panel);
	    
	   
	    frame.add(scroll);
	    
        frame.setVisible(true);
        frame.setResizable(true);
        
		
		vertical = false;
		
		horizontal = false;

		mergeCount = 1000;	// Set to some high value
		
		//System.out.println("-------- horizontal=" + horizontal + ", mergeCount=" + mergeCount);
		//<---------END
		// Fix the name of the SVG file
		String svgFile = fileName.substring(0, fileName.lastIndexOf('.'));

		svgFile = svgFile + ".svg";
		System.out.println("SVG File: " + svgFile);

		long startTime = System.nanoTime();
		//PlantUML.clear();
		PlantUML p = new PlantUML();
		
		EventSequence events = new EventSequence();
		loadEvents(fileName,events);
		//printEvents(events);
		LinkedHashMap<String,CNode> tcct = CompactCallTrees.construct(events, p);
		
		
		int nodeCount = CompactCallTrees.countNodes();
		int rlifelines = Util.cLifelines.size();
		
		long compactTime = System.nanoTime();
		p.exportMinSect(tcct, startCount, endCount, svgFile, minDropDown.getText(), minimizedSectionMap);
//		p.exportMin(tcct, startCount, endCount, svgFile, endObjectText.getText());
				
		try{
			IPath path = ResourcesPlugin.getPlugin().getStateLocation();
			File seqDiagFile = new File(path.toFile().getPath() + File.separator + "compact.png");
			//File seqDiagFile = new File(svgFile);
			
			BufferedImage minImage = ImageIO.read(seqDiagFile);
			JLabel label = new JLabel(new ImageIcon(minImage));
			panel.add(label);
		} catch (Exception ie) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + ie.getMessage());
		}
		long drawTime = System.nanoTime();
		
		statusLineManager.setMessage("[Total Interactions x Total Lifelines]  "
				+ "   BEFORE: " + numInteractions + " x " + numLifelines
				+ "   |   AFTER: " + nodeCount + " x " + rlifelines
				+ "             Time to compact: " + Math.round((compactTime - startTime)/1000000) + " ms"
				+ "   |   Time to draw: " + Math.round((drawTime - compactTime)/1000000) + " ms"
				);
		
		
		imageComposite.pack();
		frame.pack();
		
		rootScrollComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mergeCount = 100;
		horizontal = false;
		
	}
	
	/*
	 * Unchanged from the original compaction plug-in
	 */
	private void exportButtonAction(SelectionEvent e) {
		
		if (image == null) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Warning", "No image exists!");
			return;
		}
		
		FileDialog fd = new FileDialog(new Shell(Display.getCurrent()), SWT.SAVE);
		fd.setText("Export As");
		String[] filterExtensions = {"*.png", "*.bmp", "*.jpg"};
		fd.setFilterExtensions(filterExtensions);
		
		String fileName = fd.open();
		
		if (fileName == null)
			return;
			
		int fileType = fd.getFilterIndex();
		
		int format = -1;
		switch(fileType) {
			case 0: format = SWT.IMAGE_PNG;
					break;
					
			case 1: format = SWT.IMAGE_BMP;
					break;
					
			case 2: format = SWT.IMAGE_JPEG;
					break;		
		}
		
		try {
			//image = imageLabel.getImage();
			ImageLoader imageLoader = new ImageLoader();
			ImageData[] imageDatas = { image.getImageData() };
			imageLoader.data = imageDatas;
		
			imageLoader.save(fileName, format);
		} catch (Exception ex) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + ex.getMessage());
		} 
	}

	public void loadEvents(String csvFile, EventSequence events) {

		String strLine;
		BufferedReader br = Util.openInFile(csvFile);
		int count = 0;
		try {
			while ( ((strLine = Util.readLine(br,csvFile)) != null) ) {
				//	&& (++count < eventCount) ) {
				parseEvents(strLine, events);
				count++;
			}
//			System.out.println("count = " + count);
			eventsCountText.setText(Integer.toString(count));
		}
		catch (Exception e) {
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Exception", "An exception occured: " + e.getMessage());
			return;
		}
		Util.closeInFile(br, csvFile);
	}
	
	public void parseEvents(String strLine, EventSequence events) {
		
		String thread, source, eventType;			// Event fields
		int eventId=-1, lineNumber=-1;				// Event fields
		String classType;							// TypeLoadEvent fields
		String newObject;							// NewObjectEvent fields
		String cObject, cMethod, tObject, tMethod; 	// MethodCallEvent fields
		String ocObject=null, ocMethod=null, otObject, otMethod; // Out-of-model call fields
		String rObject, rMethod, rValue;			// MethodReturnEvent fields
		String frObject, fRead;						// FieldReadEvent fields
		String fwObject, fWritten, vWritten;		// FieldWriteEvent fields
		String vwObject, vwMethod, variable, value;	// VariableWriteEvent fields
		
		StringBuffer sb = null;
		String prevThread = "main";

		// This piece of code is added to remove double quotes
		sb = new StringBuffer(strLine);
		int i=0, pos;
		while((pos=sb.indexOf("\"", i)) != -1) {
			sb.deleteCharAt(pos);
			i = pos;
		}
		strLine = sb.toString();
		//System.out.println(strLine);
				
		StringTokenizer st = new StringTokenizer(strLine, ",");
			
		thread = st.nextToken().trim();					// 1. Thread name
		if ( !(thread.equals(prevThread)) && !(prevThread.equals("SYSTEM")) 
										&& !(thread.equals("SYSTEM")) ) {
			Event cs = new ContextShiftEvent(prevThread,-1,thread,-1,"Context Shift");
			events.add(cs);
			prevThread = thread;
		}

		eventId = Integer.parseInt(st.nextToken().trim());	// 2. Event number
			
		String slnField = st.nextToken().trim();		// 3. Source + 4. line number

		if ( !(slnField.equals("SYSTEM")) ) {
			StringTokenizer stsln = new StringTokenizer(slnField, ":");
			source = stsln.nextToken();			// 5. Source
			lineNumber = Integer.parseInt(stsln.nextToken());	// 6. Line number
		}
		else 
			source = slnField;		// 5. Source (no line number for SYSTEM)
			
		eventType = st.nextToken().trim();		// 7. Event type

		switch(eventType) {

			case ("System Start"):
				/*SystemStartEvent ss = new SystemStartEvent(thread, eventId,
									source, eventType);
				events.add(ss);*/
				//ss.printEvent();
				break;

			case ("System End"):
				/*SystemEndEvent se = new SystemEndEvent(thread, eventId,
									source, eventType);
				events.add(se);	*/		
				//se.printEvent();
				break;

			case ("Thread Start"):
				ThreadStartEvent ts = new ThreadStartEvent(thread, eventId,
									source, eventType);
				events.add(ts);
				numInteractions++;
				//ts.printEvent();
				break;

			case ("Thread End"):
				ThreadEndEvent te = new ThreadEndEvent(thread, eventId,
									source, eventType);
				events.add(te);
				//te.printEvent();
				break;

			case ("Type Load"):
				/*String tlField = st.nextToken().trim();
				StringTokenizer stt = new StringTokenizer(tlField,"=");
				stt.nextToken().trim();
				classType = stt.nextToken().trim();
				TypeLoadEvent tl = new TypeLoadEvent(thread, eventId,
									source, eventType, classType);
				events.add(tl);*/
				//tl.printEvent();
				break;

			case ("New Object"):
				String noField = st.nextToken().trim();
				StringTokenizer stn = new StringTokenizer(noField,"=");
				stn.nextToken().trim();
				newObject = stn.nextToken().trim();
				
				int doti;
				if ( (doti = newObject.lastIndexOf('.')) != -1)
					newObject = newObject.substring(doti+1);
				
				NewObjectEvent no = new NewObjectEvent(thread, eventId,
									source, eventType, newObject);
				events.add(no);
				//no.printEvent();
				break;

			case ("Method Call"):
				String cField = st.nextToken().trim();
				if ( cField.equals("caller=SYSTEM"))
					cField = "caller=SYSTEM#SYSTEM";
				String tField = st.nextToken().trim();
				
				if ( (cField.contains("#")) && (tField.contains("#")) ) {	// i.e. not out-of-model
					StringTokenizer stc = null;
					stc = new StringTokenizer(cField,"=#");
					stc.nextToken().trim();
					cObject = stc.nextToken();
					if (cObject.equals("SYSTEM"))
						cMethod = "SYSTEM";
					else
						cMethod = stc.nextToken().trim();
					
					StringTokenizer sttg = null;
					sttg = new StringTokenizer(tField,"=#");
					sttg.nextToken().trim();
					tObject = sttg.nextToken().trim();
					tMethod = sttg.nextToken().trim();
					
					int dot;
					if ( (dot = tObject.lastIndexOf('.')) != -1)
						tObject = tObject.substring(dot+1);
					if ( (dot = cObject.lastIndexOf('.')) != -1)
						cObject = cObject.substring(dot+1);
					//System.out.print("cObject = " + cObject);
					//System.out.println("tObject = " + tObject);

					MethodCallEvent mc = new MethodCallEvent(thread, eventId,
											source, lineNumber, eventType, 
											cObject, cMethod,
											tObject, tMethod);	
					events.add(mc);
					//mc.printEvent();
					numInteractions++;
					if ( !(lifelines.contains(cObject)) ) {
						numLifelines++;
						lifelines.add(cObject);
					}
					if ( !(lifelines.contains(tObject)) ) {
						numLifelines++;
						lifelines.add(tObject);
					}
				}
				else if ( (cField.contains("#")) && !(tField.contains("#")) ) {	// i.e. target out-of-model
							// Save the object for future i.e. when it makes an in-model call						
							/*StringTokenizer nstc = null;
							nstc = new StringTokenizer(cField,"=#");
							nstc.nextToken().trim();
							cObject = nstc.nextToken();
							if (cObject.equals("SYSTEM"))
								cMethod = "SYSTEM";
							else
								cMethod = nstc.nextToken().trim();
							System.out.print("cObject->cMethod = " + cObject + "->" + cMethod);
							
							// Code to deal with out-of-model properly below
							String nextLine = null;
							while ( (nextLine = Util.readLine(br,csvFile)) != null ) {
								//System.out.println(nextLine);
								// This piece of code is added to remove double quotes
								sb = new StringBuffer(nextLine);
								int ni=0, npos;
								while((npos=sb.indexOf("\"", ni)) != -1) {
									sb.deleteCharAt(npos);
									ni = npos;
								}
								nextLine = sb.toString();
								//System.out.println(nextLine);
														
								StringTokenizer nst = new StringTokenizer(nextLine, ",");
								for (int n=0; n<3; n++)	// skip thread, event id and source
									nst.nextToken();			
								String neventType = nst.nextToken().trim();	
								//System.out.println(neventType);
								if ( !(neventType.contains("Method Call")) )
									continue;
								
								// If caller and target are both out of model, ignore
								String ncField = nst.nextToken().trim();
								String ntField = nst.nextToken().trim();
								if ( !(ncField.contains("#")) && !(ntField.contains("#")) ) 
									continue;	// out-of-model to out-of-model, ignore and continue
								else if  ( !(ncField.contains("#")) && (ntField.contains("#")) ) {
									StringTokenizer nsttg = null;
									nsttg = new StringTokenizer(ntField,"=#");
									nsttg.nextToken().trim();
									tObject = nsttg.nextToken().trim();
									tMethod = nsttg.nextToken().trim();
									System.out.println(" : tObject->tMethod = " + tObject + "->" + tMethod);
									
									Event mc = new MethodCallEvent(thread, eventId,
														source, lineNumber, eventType, 
														cObject, cMethod,
														tObject, tMethod);	
									events.add(mc);
									break;
								}
							}*/
							//tObject = "SYSTEM";
							//tMethod = "SYSTEM";
							
							/*Event mc = new MethodCallEvent(thread, eventId,
									source, lineNumber, eventType, 
									cObject, cMethod,
									tObject, tMethod);	
							events.add(mc);
							mc.printEvent();*/

						}
				else if ( !(cField.contains("#")) && (tField.contains("#")) ) {	// i.e. caller out-of-model
					cObject = "SYSTEM";
					cMethod = "SYSTEM";
							
					StringTokenizer sttg = null;
					sttg = new StringTokenizer(tField,"=#");
					sttg.nextToken().trim();
					tObject = sttg.nextToken().trim();
					tMethod = sttg.nextToken().trim();
					
					int dot;
					if ( (dot = tObject.lastIndexOf('.')) != -1)
						tObject = tObject.substring(dot+1);
					//System.out.println("tObject = " + tObject);
							
					MethodCallEvent mc = new MethodCallEvent(thread, eventId,
											source, lineNumber,eventType, 
											cObject, cMethod,
											tObject, tMethod);	
					events.add(mc);
					//mc.printEvent();
					numInteractions++;
					if ( !(lifelines.contains(tObject)) ) {
						numLifelines++;
						lifelines.add(tObject);
					}
				}
				break;

			case ("Method Entered"):
				/*MethodEnteredEvent men = new MethodEnteredEvent(thread, eventId,
									source, lineNumber, eventType);
				events.add(men);*/
				//men.printEvent();
				break;
	
			case ("Method Exit"):
				String rField = st.nextToken().trim();
				StringTokenizer str = null;
				if (rField.contains("#")) {
					str = new StringTokenizer(rField,"=#");
					
					str.nextToken().trim();
					rObject = str.nextToken().trim();
					rMethod = str.nextToken().trim();

					if ( !st.hasMoreTokens() )
						break; // Sometimes return field does not exist

					String vField = st.nextToken().trim();
					StringTokenizer stv = new StringTokenizer(vField,"=");
					stv.nextToken().trim();
					if (stv.hasMoreTokens())
						rValue = stv.nextToken().trim();
					else
						rValue = null;

					MethodExitEvent me = new MethodExitEvent(thread, eventId,
									source, lineNumber,eventType, 
									rObject, rMethod, rValue);
					events.add(me);
					//me.printEvent();
				}

				break;

			case ("Method Returned"):
				/*MethodReturnedEvent mr = new MethodReturnedEvent(thread, eventId,
									source, lineNumber, eventType);
				events.add(mr);*/
				//mr.printEvent();
				break;

			case ("Field Read"):
				/*String froField = st.nextToken().trim();
				StringTokenizer stfro = new StringTokenizer(froField, "=");
				stfro.nextToken().trim();
				frObject = stfro.nextToken().trim();

				fRead = st.nextToken().trim();
				Event fr = new FieldReadEvent(thread, eventId,
								source, lineNumber, eventType,
								frObject, fRead);
				events.add(fr);*/
				//fr.printEvent();
				break;

			case ("Field Write"):
				/*String fwoField = st.nextToken().trim();
				StringTokenizer stfwo = new StringTokenizer(fwoField, "=");
				stfwo.nextToken().trim();
				fwObject = stfwo.nextToken().trim();

				String fwField = st.nextToken().trim();
				StringTokenizer stfw = new StringTokenizer(fwField,"= ");
				fWritten = stfw.nextToken().trim();
				if (stfw.hasMoreTokens())
					vWritten = stfw.nextToken().trim();
				else
				vWritten = null;
				Event fw = new FieldWriteEvent(thread, eventId,
									source, lineNumber, eventType,
									fwObject, fWritten, vWritten);
				events.add(fw);*/
				//fw.printEvent();
				break;

			case ("Variable Write"):
				/*String vwoField = st.nextToken();
				StringTokenizer stvwo = new StringTokenizer(vwoField, "=#");
				stvwo.nextToken();
				vwObject = stvwo.nextToken();
				vwMethod = stvwo.nextToken();

				String vwField = st.nextToken();
				StringTokenizer stvw = new StringTokenizer(vwField,"= ");
				variable = stvw.nextToken();
				if (stvw.hasMoreTokens())
					value = stvw.nextToken().trim();
				else
					value = null;
				Event vw = new VariableWriteEvent(thread, eventId,
									source, lineNumber, eventType,
									vwObject, vwMethod,
									variable, value);
				events.add(vw);*/
				//vw.printEvent();
				break;

			case ("Variable Delete"):
				/*Event vd = new VariableDeleteEvent(thread, eventId,
									source, lineNumber, eventType);
				events.add(vd);*/
				//vd.printEvent();
				break;

			case ("Exception Throw"):
				/*Event et = new ExceptionThrowEvent(thread, eventId,
									source, lineNumber, eventType);
				events.add(et);*/
				//et.printEvent();
				break;

			case ("Line Step"):
				/*Event ls= new LineStepEvent(thread, eventId,
									source, lineNumber, eventType);
				events.add(ls);*/
				//ls.printEvent();
				break;

			default:
				/*Event def = new LineStepEvent(thread, eventId,
									source, lineNumber, eventType);
				events.add(def);*/
				//def.printEvent();
				break;
		}
	}

	public void printEvents(EventSequence events) {

		Iterator<Event> itr = events.iterator();
		while ( itr.hasNext() ) {
			Event e = (Event) itr.next();
			e.printEvent();
		}
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}


