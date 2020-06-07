/*
 * Description: Client main
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
 * */

package Client;
 
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.CENTER;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import java.rmi.Remote;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import Remote.WhiteBoardServerInterface;
import Remote.WhiteBoardClientInterface;
import Remote.WhiteBoardMsgInterface;
 
public class StartClient extends UnicastRemoteObject implements WhiteBoardClientInterface, Remote {
	
	protected StartClient() throws RemoteException {
		super();
		userList = new DefaultListModel<>();
		isManager = false;
		isRemoved = false;
	}

	private static final long serialVersionUID = 1L;
	static WhiteBoardServerInterface server;
	private boolean isManager;
	private boolean isRemoved;
	private JFrame frame;
	private DefaultListModel<String> userList;
	private JButton clearBtn, saveBtn, openBtn, blackBtn, blueBtn, greenBtn, redBtn, orangeBtn, yellowBtn, cyanBtn;
	private JButton pointBtn, lineBtn, rectBtn, circleBtn, ovalBtn, textBtn, eraserBtn;
	private PaintBoard clientGUI;
	private Color selfColor;
	private String selfMode;
	private String clientName;
	private Hashtable<String, Point> lastPoints = new Hashtable<String, Point>();
	//Listen to the window action and do
	//select mode, set color
	//display the selection feedback on the gui
	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			LineBorder border = new LineBorder(new Color(238,238,238), 2);
			LineBorder borderself = new LineBorder(selfColor, 2);
		    if (e.getSource() == clearBtn) {
		      clientGUI.clear();
		      if (isManager) {
		    	  try {
					clientGUI.notifyClear();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
		      }
		    } else if (e.getSource() == saveBtn) {
		    	try {
		    		save();
		    	} catch (IOException e1) {
				e1.printStackTrace();
				}
		    } else if (e.getSource() == openBtn) {
		    	try {
		    		open();
		    	} catch (IOException e1) {
				e1.printStackTrace();
				}
		    } else if (e.getSource() == blackBtn) {
		      clientGUI.black();
		    } else if (e.getSource() == blueBtn) {
		      clientGUI.blue();
		    } else if (e.getSource() == greenBtn) {
		      clientGUI.green();
		    } else if (e.getSource() == redBtn) {
		      clientGUI.red();
		    } else if (e.getSource() == orangeBtn) {
			  clientGUI.orange();
			} else if (e.getSource() == yellowBtn) {
		      clientGUI.yellow();
		    } else if (e.getSource() == cyanBtn) {
		      clientGUI.cyan();
		    } else if (e.getSource() == pointBtn) {
		      clientGUI.point();
		      pointBtn.setBorder(borderself);
		      lineBtn.setBorder(border);
		      rectBtn.setBorder(border);
		      circleBtn.setBorder(border);
		      ovalBtn.setBorder(border);
		      textBtn.setBorder(border);
		      eraserBtn.setBorder(border);
		    } else if (e.getSource() == lineBtn) {
		      clientGUI.line();
		      pointBtn.setBorder(border);
		      lineBtn.setBorder(borderself);
		      rectBtn.setBorder(border);
		      circleBtn.setBorder(border);
		      ovalBtn.setBorder(border);
		      textBtn.setBorder(border);
		      eraserBtn.setBorder(border);
		    } else if (e.getSource() == rectBtn) {
		      clientGUI.rect();
		      pointBtn.setBorder(border);
		      lineBtn.setBorder(border);
		      rectBtn.setBorder(borderself);
		      circleBtn.setBorder(border);
		      ovalBtn.setBorder(border);
		      textBtn.setBorder(border);
		      eraserBtn.setBorder(border);
		    } else if (e.getSource() == circleBtn) {
		      clientGUI.circle();
		      pointBtn.setBorder(border);
		      lineBtn.setBorder(border);
		      rectBtn.setBorder(border);
		      circleBtn.setBorder(borderself);
		      ovalBtn.setBorder(border);
		      textBtn.setBorder(border);
		      eraserBtn.setBorder(border);
		    } else if (e.getSource() == ovalBtn) {
		      clientGUI.oval();
		      pointBtn.setBorder(border);
		      lineBtn.setBorder(border);
		      rectBtn.setBorder(border);
		      circleBtn.setBorder(border);
		      ovalBtn.setBorder(borderself);
		      textBtn.setBorder(border);
		      eraserBtn.setBorder(border);
		    } else if (e.getSource() == textBtn) {
		      clientGUI.text();
		      pointBtn.setBorder(border);
		      lineBtn.setBorder(border);
		      rectBtn.setBorder(border);
		      circleBtn.setBorder(border);
		      ovalBtn.setBorder(border);
		      textBtn.setBorder(borderself);
		      eraserBtn.setBorder(border);
		    } else if (e.getSource() == eraserBtn) {
		      clientGUI.eraser();
		      pointBtn.setBorder(border);
		      lineBtn.setBorder(border);
		      rectBtn.setBorder(border);
		      circleBtn.setBorder(border);
		      ovalBtn.setBorder(border);
		      textBtn.setBorder(border);
		      eraserBtn.setBorder(borderself);
		    }
		    
		    selfColor = clientGUI.gerCurrColor();
			selfMode = clientGUI.gerCurrMode();
			if (e.getSource() == blackBtn || e.getSource() == blueBtn || e.getSource() == greenBtn || e.getSource() == redBtn
					|| e.getSource() == orangeBtn || e.getSource() == yellowBtn || e.getSource() == cyanBtn) {
				LineBorder border1 = new LineBorder(selfColor, 2);
				switch (selfMode) {
					case "point":
						pointBtn.setBorder(border1);
						break;
					case "line":
						lineBtn.setBorder(border1);
						break;
					case "rect":
						rectBtn.setBorder(border1);
						break;
					case "circle":
						circleBtn.setBorder(border1);
						break;
					case "oval":
						ovalBtn.setBorder(border1);
						break;
					case "text":
						textBtn.setBorder(border1);
						break;
					case "eraser":
						eraserBtn.setBorder(border1);
						break;
					default :
						break;
				}
			}
		}
	};
 
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, ServerNotActiveException {
		try {//Look up the WhiteBoardInterface server from the RMI name registry
			String serverAddress = "//" + "localhost" + "/WhiteBoardInterface";
			server = (WhiteBoardServerInterface)Naming.lookup(serverAddress);
			WhiteBoardClientInterface client = new StartClient();
			//show user register GUI and register the user name to server
			//If no user name entered, assign a random name to the client
			String client_name = JOptionPane.showInputDialog("Please type in your name:");
			if (client_name.length() == 0) {
				int min = 1000;
				int max = 9999;
				int random_int = (int)(Math.random() * (max - min + 1) + min);
				client_name = "user_" + Integer.toString(random_int);
			}
			
			client.setName(client_name);
			try {
	            server.register(client);
	            System.out.println("Registered with remote sever");

	        } catch(RemoteException e) {
	            System.err.println("Error registering with remote server");
	            e.printStackTrace();
	        }
			System.out.println(client_name + " joined!");
			//launch the White Board GUI and start drawing
			client.drawBoard(server);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void save() throws IOException {
		FileDialog savedialog = new FileDialog(frame, "save image", FileDialog.SAVE);
        savedialog.setVisible(true);
        if (savedialog.getFile() != null) {
            String filePath = savedialog.getDirectory();
            String fileName = savedialog.getFile();
            ImageIO.write(clientGUI.getImage(), "png", new File(filePath + fileName + ".png"));
        }
	}
	
	private void open() throws IOException {
		FileDialog opendialog = new FileDialog(frame, "open an image", FileDialog.LOAD);
        opendialog.setVisible(true);
        if (opendialog.getFile() != null) {
            String filePath = opendialog.getDirectory();
            String fileName = opendialog.getFile();
            BufferedImage image = ImageIO.read(new File(filePath + fileName));
            clientGUI.drawImage(image);
            ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
    	    ImageIO.write(image, "png", imageArray);
            server.sendOpenedImage(imageArray.toByteArray());
        }
	}

	public void drawBoard(WhiteBoardServerInterface server) {
		//build the GUI 
		frame = new JFrame(clientName + "'s WhiteBoard");
	    Container content = frame.getContentPane();
	    
	    clientGUI = new PaintBoard(clientName, server, isManager);
	    
	    blackBtn = new JButton();
	    blackBtn.setBackground(Color.black);
	    blackBtn.setBorderPainted(false);
	    blackBtn.setOpaque(true);
	    blackBtn.setMaximumSize(new Dimension(20, 20));
	    blackBtn.addActionListener(actionListener);
	    blueBtn = new JButton();
	    blueBtn.setBackground(Color.blue);
	    blueBtn.setBorderPainted(false);
	    blueBtn.setOpaque(true);
	    blueBtn.addActionListener(actionListener);
	    greenBtn = new JButton();
	    greenBtn.setBackground(Color.green);
	    greenBtn.setBorderPainted(false);
	    greenBtn.setOpaque(true);
	    greenBtn.addActionListener(actionListener);
	    redBtn = new JButton();
	    redBtn.setBackground(Color.red);
	    redBtn.setBorderPainted(false);
	    redBtn.setOpaque(true);
	    redBtn.addActionListener(actionListener);
	    orangeBtn = new JButton();
	    orangeBtn.setBackground(Color.orange);
	    orangeBtn.setBorderPainted(false);
	    orangeBtn.setOpaque(true);
	    orangeBtn.addActionListener(actionListener);
	    yellowBtn = new JButton();
	    yellowBtn.setBackground(Color.yellow);
	    yellowBtn.setBorderPainted(false);
	    yellowBtn.setOpaque(true);
	    yellowBtn.addActionListener(actionListener);
	    cyanBtn = new JButton();
	    cyanBtn.setBackground(Color.cyan);
	    cyanBtn.setBorderPainted(false);
	    cyanBtn.setOpaque(true);
	    cyanBtn.addActionListener(actionListener);
	    
	    LineBorder border = new LineBorder(Color.black, 2);
	    Icon icon = new ImageIcon("src/icon/1.gif");
	    pointBtn = new JButton(icon);
	    pointBtn.setToolTipText("Free draw");
	    pointBtn.setBorder(border);
	    pointBtn.addActionListener(actionListener);
	    border = new LineBorder(new Color(238,238,238), 2);
	    icon = new ImageIcon("src/icon/2.gif");
	    lineBtn = new JButton(icon);
	    lineBtn.setToolTipText("Draw line");
	    lineBtn.setBorder(border);
	    lineBtn.addActionListener(actionListener);
	    icon = new ImageIcon("src/icon/3.gif");
	    rectBtn = new JButton(icon);
	    rectBtn.setToolTipText("Draw rectangle");
	    rectBtn.setBorder(border);
	    rectBtn.addActionListener(actionListener);
	    icon = new ImageIcon("src/icon/4.gif");
	    circleBtn = new JButton(icon);
	    circleBtn.setToolTipText("Draw circle");
	    circleBtn.setBorder(border);
	    circleBtn.addActionListener(actionListener);
	    icon = new ImageIcon("src/icon/5.gif");
	    ovalBtn = new JButton(icon);
	    ovalBtn.setToolTipText("Draw oval");
	    ovalBtn.setBorder(border);
	    ovalBtn.addActionListener(actionListener);
	    icon = new ImageIcon("src/icon/6.gif");
	    textBtn = new JButton(icon);
	    textBtn.setToolTipText("Put text box");
	    textBtn.setBorder(border);
	    textBtn.addActionListener(actionListener);
	    icon = new ImageIcon("src/icon/7.gif");
	    eraserBtn = new JButton(icon);
	    eraserBtn.setToolTipText("Eraser");
	    eraserBtn.setBorder(border);
	    eraserBtn.addActionListener(actionListener);
	    
	    clearBtn = new JButton("New Board");
	    clearBtn.setToolTipText("Create a new board");
	    clearBtn.addActionListener(actionListener);
	    saveBtn = new JButton("Save Image");
	    saveBtn.setToolTipText("Save as image file");
	    saveBtn.addActionListener(actionListener);
	    openBtn = new JButton("Open Image");
	    openBtn.setToolTipText("Open an image file");
	    openBtn.addActionListener(actionListener);
	    // if the client is the manager, he can save, open and clear the white board image
	    if (isManager == false) {
	    	clearBtn.setVisible(false);
	    	saveBtn.setVisible(false);
	    	openBtn.setVisible(false);
	    }
	    //List the other users. The client can see the list of user name of other users. The manager's user name is displayed with a flag "*"
	    JList<String> list = new JList<>(userList);
        JScrollPane currUsers = new JScrollPane(list);
        currUsers.setMinimumSize(new Dimension(100, 100));
        // if the client is the manager, he has the right to remove the client
        if (isManager) {
	        list.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent evt) {
	                @SuppressWarnings("unchecked")
					JList<String> list = (JList<String>)evt.getSource();
	                if (evt.getClickCount() == 2) {
	                    int index = list.locationToIndex(evt.getPoint());
	                    String selectName = list.getModel().getElementAt(index);
	                    int dialogResult = JOptionPane.showConfirmDialog (frame, "Are you sure to remove " + selectName + "?",
	                    		"Warning", JOptionPane.YES_NO_OPTION);
	                    if(dialogResult == JOptionPane.YES_OPTION) {
	                    	try {
								server.removeClient(selectName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                    }
	                }
	            }
	        });
        }
	    
	    GroupLayout layout = new GroupLayout(content);
        content.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
    		.addGroup(layout.createParallelGroup(CENTER)
    			.addComponent(pointBtn)
                .addComponent(lineBtn)
                .addComponent(rectBtn)
    			.addComponent(circleBtn)
    			.addComponent(ovalBtn)
    			.addComponent(textBtn)
    			.addComponent(eraserBtn)
    		)
            .addGroup(layout.createParallelGroup(CENTER)
                .addComponent(clientGUI)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(blackBtn)
                    .addComponent(redBtn)
                    .addComponent(greenBtn)
                    .addComponent(blueBtn)
                    .addComponent(orangeBtn)
                    .addComponent(yellowBtn)
                    .addComponent(cyanBtn)
                )
            )
            .addGroup(layout.createParallelGroup(CENTER)
            	.addComponent(clearBtn)
            	.addComponent(openBtn)
            	.addComponent(saveBtn)
            	.addComponent(currUsers)
            )
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(BASELINE)
                .addGroup(layout.createSequentialGroup()
                	.addComponent(pointBtn)
                	.addComponent(lineBtn)
                	.addComponent(rectBtn)
                	.addComponent(circleBtn)
                	.addComponent(ovalBtn)
                	.addComponent(textBtn)
                	.addComponent(eraserBtn)
                ) 
                .addComponent(clientGUI)
                .addGroup(layout.createSequentialGroup()
                	.addComponent(clearBtn)
                	.addComponent(openBtn)
                	.addComponent(saveBtn)
                	.addComponent(currUsers)
                )
            )
            .addGroup(layout.createParallelGroup(BASELINE)
                .addComponent(blackBtn)
                .addComponent(redBtn) 
            	.addComponent(greenBtn)
            	.addComponent(blueBtn)
            	.addComponent(orangeBtn)
            	.addComponent(yellowBtn)
                .addComponent(cyanBtn)
            )
        );
        
        layout.linkSize(SwingConstants.HORIZONTAL, clearBtn, saveBtn, openBtn);
        
	    if (isManager) frame.setMinimumSize(new Dimension(730, 440));
	    else frame.setMinimumSize(new Dimension(710, 440));
	    frame.setLocationRelativeTo(null);
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frame.setVisible(true);
	    //if the manager close the window, all other client are removed and the client window are force closed
	    frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            	if (isManager) {
            		if (JOptionPane.showConfirmDialog(frame, 
        	                "You are the manager? Are you sure close the application?", "Close Application?", 
        	                JOptionPane.YES_NO_OPTION,
        	                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            			try {
    						server.removeAll();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					} finally {
    						System.exit(0);
    					}
            		}
            	} else {
            		if (JOptionPane.showConfirmDialog(frame, 
        	                "Are you sure you want to quit?", "Close Paint Board?", 
        	                JOptionPane.YES_NO_OPTION,
        	                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            			try {
    						server.removeMe(clientName);
    					} catch (RemoteException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					} finally {
    						System.exit(0);
    					}
            		}
            	}
            }
	    });
	}

	public void sendMessage(String message) throws RemoteException {
		System.out.println("Message from Server: " + message);
	}
	//get the info sent from the other clients, and update the white board accordingly
	public boolean updateWithOthers(WhiteBoardMsgInterface msg) throws RemoteException {
		// skip msg from itself
		if (msg.getName().compareTo(clientName) == 0) {
			return true;
		}
		CreateShape newShape = new CreateShape();
		//System.out.println("Drawing board from " + msg.getName() + " State: " + msg.getState());
		if (msg.getState().compareTo("start") == 0) {
			lastPoints.put(msg.getName(), msg.getPoint());
			return true;
		} 
		
		selfColor = clientGUI.gerCurrColor();
		Point lastPt = (Point)lastPoints.get(msg.getName());
		clientGUI.getGraphic().setPaint(msg.getColor());
		
		if (msg.getState().compareTo("drawing") == 0) {
			if (msg.getMode().compareTo("eraser") == 0) {
				clientGUI.getGraphic().setStroke(new BasicStroke(15.0f));
			}
			newShape.makeLine(lastPt, msg.getPoint());
			clientGUI.getGraphic().draw(newShape.getShape());
			clientGUI.repaint();
			lastPoints.put(msg.getName(), msg.getPoint());
			clientGUI.getGraphic().setPaint(selfColor);
			return true;
		}
		
		if (msg.getState().compareTo("end") == 0) {
			if (msg.getMode().compareTo("point") == 0 || msg.getMode().compareTo("line") == 0) {
				newShape.makeLine(lastPt, msg.getPoint());
			} else if (msg.getMode().compareTo("eraser") == 0) {
				clientGUI.getGraphic().setStroke(new BasicStroke(1.0f));
			} else if (msg.getMode().compareTo("rect") == 0) {
				newShape.makeRect(lastPt, msg.getPoint());
			} else if (msg.getMode().compareTo("circle") == 0) {
				newShape.makeCircle(lastPt, msg.getPoint());
			} else if (msg.getMode().compareTo("oval") == 0) {
				newShape.makeOval(lastPt, msg.getPoint());
			} else if (msg.getMode().compareTo("text") == 0) {
				clientGUI.getGraphic().setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
				clientGUI.getGraphic().drawString(msg.getText(), msg.getPoint().x, msg.getPoint().y);
			}
			if (msg.getMode().compareTo("text") != 0 && msg.getMode().compareTo("eraser") != 0)
				clientGUI.getGraphic().draw(newShape.getShape());
			clientGUI.repaint();
			lastPoints.remove(msg.getName());
			clientGUI.getGraphic().setPaint(selfColor);
			return true;
		}
		return false;
	}
	
	public String getName() throws RemoteException {
        return this.clientName;
	}

	public void setName(String name) throws RemoteException {
		this.clientName = name;
	}
	
	public void reName(String name) throws RemoteException {
		this.clientName = name;
		JOptionPane.showMessageDialog(this.frame, "Username has been used by others, renamed to: " + name, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	//if the user is removed by the manager, a dialog will pop up in the client GUI. 
	//The client can still draw the image locally, but will not synchronize the image with the other client
	public void setRemoved() throws IOException {
		isRemoved = true;
		userList.removeAllElements();
		frame.setTitle("You are removed by the manager");
		Thread t = new Thread(new Runnable(){
	        public void run(){
	        	JOptionPane.showMessageDialog(frame, "You are removed by the manager." + "\n" +
	    				"Though you can continue your drawing," + "\n" +
	    				"others will not see your update." + "\n" +
	    				"And you will not see others' update",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
	        }
	    });
		t.start();
	}
	//close the application due to manager quit
	public void closeApp() throws IOException {
		isRemoved = true;
		frame.setTitle("The manager has quit");
		Thread t = new Thread(new Runnable(){
	        public void run(){
	        	JOptionPane.showMessageDialog(frame, "The manager has quit." + "\n" +
	    				"Your application will be closed.",
	    				"Error", JOptionPane.ERROR_MESSAGE);
	        	System.exit(0);
	        }
	    });
		t.start();
	}
	
	public void addUser(String name) throws RemoteException {
		this.userList.addElement(name);
	}
	
	public void removeUser(String name) throws RemoteException {
		this.userList.removeElement(name);
	}
	
	public double sendResponse() {
        return System.currentTimeMillis();
    }
	
	public void assignManager() {
		this.isManager = true;
	}
	
	public boolean isManager() {
		return this.isManager;
	}
	
	public boolean isRemoved() {
		return this.isRemoved;
	}
	
	public void clearBoard() {
		if (this.isManager == false)
			this.clientGUI.clear();
	}
	
	public byte[] sendImage() throws IOException {
	    ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
	    ImageIO.write(this.clientGUI.getImage(), "png", imageArray);
	    return imageArray.toByteArray();
	}
	
	public void drawOpenedImage(byte[] rawImage) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(rawImage));
	    this.clientGUI.drawImage(image);
	}
	
	public void resyncClientNameList() throws RemoteException {
		/*for(IWhiteboardClientListener listener : this.clientListeners){
            listener.updateClientList(this.getContext().getClientNameList());
        }*/
	}
}
