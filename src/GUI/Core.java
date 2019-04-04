package GUI;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
	
	public Core(String title, int width, int height) {
		
		super(title);		
		Color mainColor = new Color(0, 189, 172);
		getContentPane().setBackground(mainColor);
		
		//BUTTONS
		fileChooseButton = addButton("Output...", 250, 360, 100, 20);
		choosePath(fileChooseButton);
		
		screenshotButton = addButton("Take", 250, 400, 100, 20);
		takeScreenshot(screenshotButton);

		//TEXT AREA
		textArea = addTextArea(0, 0, 0, 0, false);
		scrollableTextArea = addScrollPane(textArea, 100, 50, 400, 260);
		scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		//LABELS
		lNrOfDisplays = addLabel("Number of Displays Detected: ", 100 , 20, 180, 20);
		lSelectedDisplay = addLabel("Selected Display: ", 340, 20, 180, 20);
		lScreenshotName = addLabel("Screenshot Name: ", 100, 320, 120, 20);
		
		//TEXT FIELDs
		//UNEDITABLE
		tTotalNrOfDisplays = addTextField("0", 280, 20, 20, 20, false);		
		tSelectedDisplay = addTextField("0", 450, 20, 20, 20, false);
		//EDITABLE
		tScreenshotName = addTextField("screenshot", 240, 320, 160, 20, true);
		
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
		selectedDisplayRectangle = new Rectangle(gd[0].getDefaultConfiguration().getBounds().x, gd[0].getDefaultConfiguration().getBounds().y);
		tSelectedDisplay.setText("1");	
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
					write("graphics devices: "+gd.length+"; Rectangle: x:"+gd[0].getDefaultConfiguration().getBounds().x+"; y: "+gd[0].getDefaultConfiguration().getBounds().y);
					screen = robo.createScreenCapture(new Rectangle(gd[0].getDefaultConfiguration().getBounds().x, gd[0].getDefaultConfiguration().getBounds().y, gd[0].getDefaultConfiguration().getBounds().width, gd[0].getDefaultConfiguration().getBounds().height));
					try {
						File filePath;
						for(int i=0; i<999999; i++) {
							filePath = new File(currentPath+"/"+tScreenshotName.getText()+"_"+String.valueOf(i)+".png");
							if(!filePath.isFile()) {
								ImageIO.write(screen, "png", filePath);
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

	public void write(String input) {
		textArea.append(input+"\n");		
	}
}
