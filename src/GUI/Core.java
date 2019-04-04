package GUI;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Core extends JFrame {

	private static final long serialVersionUID = 1L;

	//Text Area
	JTextArea textArea;
	
	//Text Fields
	//Not editable
	JTextField tTotalNrOfDisplays;
	JTextField tSelectedDisplay;
	//Editable
	JTextField tScreenshotName;
	
	//Labels
	JLabel lNrOfDisplays;
	JLabel lSelectedDisplay;
	JLabel lScreenshotName;
	
	//ScrollPane
	JScrollPane scrollableTextArea;	
	
	//Buttons
	JButton fileChooseButton;
	JButton screenshotButton;
	JButton selectScreenLeft;
	JButton selectScreenRight;
	
	//File Chooser
	JFileChooser jfc;
	
	//Robot
	Robot robo;
	
	//Display Stuff
	Rectangle selectedDisplayRectangle;
	GraphicsEnvironment ge;
	GraphicsDevice[] gd;
	
	//Strings
	public String currentPath;
	
	//Integers
	private int totalNrOfDisplays;
	private int selectedDisplay;
	
	public Core(String title, int width, int height) {
		
		super(title);		
		Color mainColor = new Color(128, 195, 221);
		getContentPane().setBackground(mainColor);
		
		//BUTTONS
		fileChooseButton = addButton("Output...", 250, 360, 100, 20);
		choosePath(fileChooseButton);
		
		screenshotButton = addButton("Take", 250, 400, 100, 20);
		takeScreenshot(screenshotButton);
		
		selectScreenLeft = addButton("<", 430, 20, 20, 20);
		selectScreenLeft.setMargin(new Insets(0, 0, 0, 0));	
		selectScreenLeft.setBorder(null);
		decrementSelectedDisplay(selectScreenLeft);
		
		selectScreenRight = addButton(">", 480, 20, 20, 20);
		selectScreenRight.setMargin(new Insets(0, 0, 0, 0));
		selectScreenRight.setBorder(null);
		incrementSelectedDisplay(selectScreenRight);

		//TEXT AREA
		textArea = addTextArea(0, 0, 0, 0, false);
		scrollableTextArea = addScrollPane(textArea, 100, 50, 400, 260);
		scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		//LABELS
		lNrOfDisplays = addLabel("Number of Displays Detected: ", 100 , 20, 180, 20);
		lSelectedDisplay = addLabel("Selected Display: ", 325, 20, 180, 20);
		lScreenshotName = addLabel("Screenshot Name: ", 100, 320, 120, 20);
		
		//TEXT FIELDs
		//NOT EDITABLE
		tTotalNrOfDisplays = addTextField("0", 275, 20, 20, 20, false);		
		tTotalNrOfDisplays.setHorizontalAlignment(JTextField.CENTER);
		
		tSelectedDisplay = addTextField("0", 455, 20, 20, 20, false);
		tSelectedDisplay.setHorizontalAlignment(JTextField.CENTER);
		//EDITABLE
		tScreenshotName = addTextField("screen", 240, 320, 160, 20, true);
		
		//File Chooser
		jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setCurrentDirectory(new File(System.getProperty("user.home")+System.getProperty("file.separator")+"Desktop"));
		
		//Robot
		try {
			robo = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		//DISPLAY Stuff
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getScreenDevices();
		selectedDisplay = 0;
		selectedDisplayRectangle = new Rectangle(gd[selectedDisplay].getDefaultConfiguration().getBounds().x, gd[selectedDisplay].getDefaultConfiguration().getBounds().y);
		tSelectedDisplayUpdate();	
		totalNrOfDisplays = Integer.parseInt(String.valueOf(gd.length));		
		tTotalNrOfDisplays.setText(String.valueOf(gd.length));
		
		//Other Stuff
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setResizable(false);
		setLayout(null);
		setVisible(true);
		
	}
	
	private JButton addButton(String text, int xPosition, int yPosition, int width, int height) {
		JButton button = new JButton(text);
		button.setBounds(xPosition, yPosition, width, height);
		button.setFocusPainted(false);
		super.add(button);
		return button;
	}
	
	private JTextField addTextField(String text, int xPosition, int yPosition, int width, int height, boolean editable) {
		JTextField textField = new JTextField(text);
		textField.setBounds(xPosition, yPosition, width, height);
		textField.setEditable(editable);
		super.add(textField);
		return textField;
	}
	
	private JTextArea addTextArea(int xPosition, int yPosition, int width, int height, boolean editable) {
		JTextArea textArea = new JTextArea();
		textArea.setBounds(xPosition, yPosition, width, height);
		textArea.setEditable(editable);
		super.add(textArea);
		return textArea;
	}
	
	private JScrollPane addScrollPane(JTextArea textArea, int x, int y, int width, int height) {
		JScrollPane scrollPane = new JScrollPane(textArea); 
		scrollPane.setBounds(x, y, width, height);		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		super.add(scrollPane);
		return scrollPane;
	}
	
	private JLabel addLabel(String text, int xPosition, int yPosition, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(xPosition, yPosition, width, height);
		super.add(label);
		return label;
	}
	
	//Button Actions
	private void takeScreenshot(JButton button) {
		button.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) {  
				BufferedImage screen;
				if(currentPath != null) {					
					screen = robo.createScreenCapture(new Rectangle(gd[selectedDisplay].getDefaultConfiguration().getBounds().x, gd[selectedDisplay].getDefaultConfiguration().getBounds().y, gd[selectedDisplay].getDefaultConfiguration().getBounds().width, gd[selectedDisplay].getDefaultConfiguration().getBounds().height));
					try {
						File filePath;
						for(int i=0; i<999999; i++) {
							filePath = new File(currentPath+"/"+tScreenshotName.getText()+"_"+String.valueOf(i)+".png");
							if(!filePath.isFile()) {
								ImageIO.write(screen, "png", filePath);
								//[DEBUG]write("graphics devices: "+gd.length+"; Rectangle: x:"+gd[selectedDisplay].getDefaultConfiguration().getBounds().x+"; y: "+gd[selectedDisplay].getDefaultConfiguration().getBounds().y);
								write("Screen '"+tScreenshotName.getText()+"_"+i+"' captured.");
								break;
							}
						}						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else {
					write("Output Directory not selected!");
				}
			}  
		});  
	}
	
	private void choosePath(JButton button) {
		button.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) {  
				
				int jfcReturn = jfc.showOpenDialog(Core.this);
				 
				if(jfcReturn == JFileChooser.APPROVE_OPTION) {
					currentPath = jfc.getSelectedFile().toString();					
					write("Current selected path: "+currentPath);
				}
					
			}  
		});  
	}
	
	private void incrementSelectedDisplay(JButton button) {
		button.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) { 
				if(selectedDisplay < totalNrOfDisplays - 1) {
					selectedDisplay++;
					tSelectedDisplayUpdate();
				}					
			}  
		}); 
	}
	
	private void decrementSelectedDisplay(JButton button) {
		button.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) { 
				if(selectedDisplay > 0) {
					selectedDisplay--;
					tSelectedDisplayUpdate();
				}					
			}  
		}); 
	}

	private void tSelectedDisplayUpdate() {
		tSelectedDisplay.setText(String.valueOf(selectedDisplay+1));
	}
	
	public void write(String input) {
		textArea.append(input+"\n");		
	}
}
