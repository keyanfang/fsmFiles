package fr.univcotedazur.polytech.si4.fsm.project;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.univcotedazur.polytech.si4.fsm.project.drink.DrinkStatemachine;
import fr.univcotedazur.polytech.si4.fsm.project.drink.IDrinkStatemachine.SCInterfaceListener;

enum MyDrink{
	COFFEE,EXPRESSO,TEA;
}

public class DrinkFactoryMachine extends JFrame {
	JLabel messagesToUser;
	JSlider sugarSlider;
	JSlider sizeSlider;
	JSlider temperatureSlider;
	
	JButton coffeeButton;
	JButton expressoButton;
	JButton teaButton;
	JButton money50centsButton;
	JButton money25centsButton;
	JButton money10centsButton;
	JButton nfcBiiiipButton;
	JLabel labelForPictures;
	JProgressBar progressBar;
	
	MyDrink myDrink;
	boolean byNFC;
	int drinkPrice; 
	int paidCoinsValue;
	int myCoin;
	int refund;
	int cupValue;
	int step;
	boolean startPrepare = false; 
	
	
	public void initialDrinkButton() { 
		coffeeButton.setBackground(Color.DARK_GRAY);
		expressoButton.setBackground(Color.DARK_GRAY);
		teaButton.setBackground(Color.DARK_GRAY);
	}
	
	public void initialSliders() { 
		sugarSlider.setValue(0);
		sizeSlider.setValue(1);
		temperatureSlider.setValue(2);
	}
	
	public void myWait(int myTime) { 
		try {
			TimeUnit.MILLISECONDS.sleep(myTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	 
	class DrinkInterfaceImplementation implements SCInterfaceListener {
		DrinkFactoryMachine thedm;
		
		public DrinkInterfaceImplementation(DrinkFactoryMachine dm) {
			thedm = dm;
		}
		
		@Override
		public void onWaitCoinRaised() {
			messagesToUser.setText("Please choose your drink");

			myWait(500);
			paidCoinsValue += myCoin;
			if(drinkPrice!=0) {
				onComfirmCoinsRaised();
			} 	
		}

		void cleanInfos() {
			initialDrinkButton();
			initialSliders();
			nfcBiiiipButton.setBackground(Color.DARK_GRAY);
			myDrink = null;
			drinkPrice = 0;
			paidCoinsValue = 0;
			cupValue = 0;
			refund = 0;
			step = 0;
			byNFC=false;
			startPrepare = false;
			progressBar.setValue(0);
			BufferedImage myPicture = null;
			try {
				myPicture = ImageIO.read(new File("./picts/vide2.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			labelForPictures.setIcon(new ImageIcon(myPicture));
		}
		
		@Override
		public void onInitialRaised() { 
			messagesToUser.setText("<html>You have not operated for 45 seconds<br>"
					+ "The order has been canceled");
			if(paidCoinsValue>0) {
				messagesToUser.setText("<html>You have not operated for 45 seconds<br>"
						+ "The order has been canceled <br>Your coins of 0."+paidCoinsValue+"€ have been returned");
			}
			cleanInfos();
		}

		@Override
		public void onCancelOrderRaised() {
			messagesToUser.setText("<html>You canceled the order<br>Your coins of 0."+paidCoinsValue+"€ have been returned");
			cleanInfos();
		}
 
		@Override
		public void onComfirmCoinsRaised() {
			refund = paidCoinsValue + cupValue - drinkPrice;
			if(refund>0) {
				messagesToUser.setText("<html>Payment is successful, start to make drinks<br>You will get a refund of 0."+refund+"€");
				theFSM.raiseOrderSuccess();
			}else if(refund==0) {
				messagesToUser.setText("<html>Payment is successful, start to make drinks");
				theFSM.raiseOrderSuccess();
			}else if(refund<0) {
				messagesToUser.setText("<html>You still need to pay "+(-0.01*refund)+"€");
			}
		}

		@Override
		public void onCleanMachineRaised() {
			messagesToUser.setText("<html>Machine is cleaned");
			cleanInfos();
			sugarSlider.setEnabled(true);
			sizeSlider.setEnabled(true);
			temperatureSlider.setEnabled(true);
		}

		@Override
		public void onPrepStartRaised() {
			startPrepare = true;
			if(byNFC)
				messagesToUser.setText("<html>Payment is successful, start to make drinks");
			switch(drinkPrice) {
				case 35:
					theFSM.raiseIsCoffee();
					break;
				case 50:
					theFSM.raiseIsEspresso();
					break;
				case 40:
					theFSM.raiseIsTea();
					break;
			} 
			sugarSlider.setEnabled(false);
			sizeSlider.setEnabled(false);
			temperatureSlider.setEnabled(false);
		}


		@Override
		public void onBarRaised() {
			switch(step) {
			case 0:
				if (drinkPrice == 35&&cupValue==0) {
				for (int i=1;i<51;i++) {
				progressBar.setValue(i);
						}
				step = step + 1;
					}
				else if (drinkPrice ==35 && cupValue!=0) {
					for (int i=1;i<61;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				else if (drinkPrice == 40&&cupValue==0) {
					for (int i=1;i<13;i++) {
						progressBar.setValue(i);
								}
					step = step + 1;
							}
				else if (drinkPrice ==40 && cupValue !=0) {
					for (int i=1;i<14;i++) {progressBar.setValue(i);}
					step ++;
				}
				else if (drinkPrice == 50) {
					for (int i=1;i<15;i++) {
						progressBar.setValue(i);
								}
					step = step + 1;
							}
				
				
				break;
			case 1:
				if (drinkPrice ==35&&cupValue==0) {
					for (int i=50;i<64;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				if (drinkPrice == 35 && cupValue !=0 ) {step = step + 1;}
				if (drinkPrice ==40&&cupValue==0) {
					for (int i=12;i<19;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				if (drinkPrice ==40 && cupValue !=0) {
					
					step ++;
				}
				if (drinkPrice == 50) {
					for (int i=1;i<46;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				break;
			case 2:
				if (drinkPrice == 35&&cupValue==0 ) {
					for (int i=63;i<=100;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				if (drinkPrice ==35 &&cupValue!=0) {
					for (int i=60;i<=100;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				
				if (drinkPrice ==40&&cupValue==0) {
					for (int i=18;i<31;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				if (drinkPrice ==40 && cupValue !=0) {
					for (int i=13;i<27;i++) {progressBar.setValue(i);}
					step ++;
				}
				if (drinkPrice==50) {
					for (int i=45;i<92;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				break;
			case 3:
				if (drinkPrice ==35) {progressBar.setValue(100);}
				if (drinkPrice ==40 && cupValue==0) {
					for (int i=30;i<81;i++) {progressBar.setValue(i);}
					step ++;
				}
				if (drinkPrice ==40 && cupValue !=0) {
					for (int i=26;i<81;i++) {progressBar.setValue(i);}
					step ++;
				}
				if (drinkPrice ==50) {
					for (int i=91;i<100;i++) {progressBar.setValue(i);}
					step = step + 1;
				}
				break;
			case 4:
				if(drinkPrice ==40 &&cupValue==0) {
					for (int i=80;i<101;i++) {progressBar.setValue(i);}
				}
				if (drinkPrice ==40 && cupValue !=0) {
					for (int i=80;i<100;i++) {progressBar.setValue(i);}
				}
				else {progressBar.setValue(100);}
				break;
			}
			
			
			// TODO Auto-generated method stub
			}
		
		
		@Override
		public void onPrepFinishRaised() {
			progressBar.setValue(100);
			messagesToUser.setText("<html>Your drink is ready");
		}

		@Override
		public void onIsActiveRaised() {
			// nothing
		}
		
		@Override
		public void onWaterReadyRaised() {
			// nothing
		}

		@Override
		public void onCupReadyRaised() {
			// nothing
		}
	}
	
	
	protected DrinkStatemachine theFSM;
	
	private static final long serialVersionUID = 2030629304432075314L;
	private JPanel contentPane;
	/**
	 * @wbp.nonvisual location=311,475
	 */
	//private final ImageIcon imageIcon = new ImageIcon();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DrinkFactoryMachine frame = new DrinkFactoryMachine();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public DrinkFactoryMachine() {
		
		theFSM = new DrinkStatemachine();
	    TimerService timer = new TimerService();
	    theFSM.setTimer(timer);
	    theFSM.init();
	    theFSM.enter();
	    theFSM.getSCInterface().getListeners().add(
				new DrinkInterfaceImplementation(this));

		
		setForeground(Color.WHITE);
		setFont(new Font("Cantarell", Font.BOLD, 22));
		setBackground(Color.DARK_GRAY);
		setTitle("Drinking Factory Machine");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 650);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		messagesToUser = new JLabel("<html>This is<br>place to communicate <br> with the user");
		messagesToUser.setForeground(Color.WHITE);
		messagesToUser.setHorizontalAlignment(SwingConstants.LEFT);
		messagesToUser.setVerticalAlignment(SwingConstants.TOP);
		messagesToUser.setToolTipText("message to the user");
		messagesToUser.setBackground(Color.WHITE);
		messagesToUser.setBounds(126, 34, 165, 175);
		contentPane.add(messagesToUser);

		JLabel lblCoins = new JLabel("Coins");
		lblCoins.setForeground(Color.WHITE);
		lblCoins.setHorizontalAlignment(SwingConstants.CENTER);
		lblCoins.setBounds(538, 12, 44, 15);
		contentPane.add(lblCoins);

		coffeeButton = new JButton("Coffee");
		coffeeButton.setForeground(Color.WHITE);
		coffeeButton.setBackground(Color.DARK_GRAY);
		coffeeButton.setBounds(12, 34, 96, 25);
		contentPane.add(coffeeButton);
		coffeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;
            	
            	myDrink = MyDrink.COFFEE;
            	drinkPrice = 35;
            	initialDrinkButton();
            	messagesToUser.setText("<html>Please pay 0.35€ for coffee");
            	coffeeButton.setBackground(Color.green);
            	theFSM.raiseChooseDrink();
            }
        });

		expressoButton = new JButton("Expresso");
		expressoButton.setForeground(Color.WHITE);
		expressoButton.setBackground(Color.DARK_GRAY);
		expressoButton.setBounds(12, 71, 96, 25);
		contentPane.add(expressoButton);
		expressoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;
            	initialDrinkButton();
            	myDrink = MyDrink.EXPRESSO;
            	drinkPrice = 50;
            	messagesToUser.setText("<html>Please pay 0.50€ for expresso");
            	expressoButton.setBackground(Color.green);
            	theFSM.raiseChooseDrink();
            }
        });

		teaButton = new JButton("Tea");
		teaButton.setForeground(Color.WHITE);
		teaButton.setBackground(Color.DARK_GRAY);
		teaButton.setBounds(12, 108, 96, 25);
		contentPane.add(teaButton);
		teaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;
            	myDrink = MyDrink.TEA;
            	initialDrinkButton();
            	drinkPrice = 40;
            	messagesToUser.setText("<html>Please pay 0.40€ for tea");
            	teaButton.setBackground(Color.green);
            	theFSM.raiseChooseDrink();
            }
        });

		JButton soupButton = new JButton("Soup");
		soupButton.setForeground(Color.WHITE);
		soupButton.setBackground(Color.DARK_GRAY);
		soupButton.setBounds(12, 145, 96, 25);
		contentPane.add(soupButton);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setForeground(Color.LIGHT_GRAY);
		progressBar.setBackground(Color.DARK_GRAY);
		progressBar.setBounds(12, 254, 622, 26);
		contentPane.add(progressBar);

		sugarSlider = new JSlider();
		sugarSlider.setValue(1);
		sugarSlider.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		sugarSlider.setBackground(Color.DARK_GRAY);
		sugarSlider.setForeground(Color.WHITE);
		sugarSlider.setPaintTicks(true);
		sugarSlider.setMinorTickSpacing(1);
		sugarSlider.setMajorTickSpacing(1);
		sugarSlider.setMaximum(4);
		sugarSlider.setBounds(301, 51, 200, 36);
		contentPane.add(sugarSlider);
		sugarSlider.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
		    	  if(startPrepare)
	            		return;
		    	  theFSM.raiseChooseSlide();
		      }
		});

		sizeSlider = new JSlider();
		sizeSlider.setPaintTicks(true);
		sizeSlider.setValue(1);
		sizeSlider.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		sizeSlider.setBackground(Color.DARK_GRAY);
		sizeSlider.setForeground(Color.WHITE);
		sizeSlider.setMinorTickSpacing(1);
		sizeSlider.setMaximum(2);
		sizeSlider.setMajorTickSpacing(1);
		sizeSlider.setBounds(301, 125, 200, 36);
		contentPane.add(sizeSlider);
		sizeSlider.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
		    	  if(startPrepare)
	            		return;
		    	  theFSM.raiseChooseSlide();
		      }
		});
		
		temperatureSlider = new JSlider();
		temperatureSlider.setPaintLabels(true);
		temperatureSlider.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		temperatureSlider.setValue(2);
		temperatureSlider.setBackground(Color.DARK_GRAY);
		temperatureSlider.setForeground(Color.WHITE);
		temperatureSlider.setPaintTicks(true);
		temperatureSlider.setMajorTickSpacing(1);
		temperatureSlider.setMaximum(3);
		temperatureSlider.setBounds(301, 188, 200, 54);
		temperatureSlider.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
		    	  if(startPrepare)
	            		return;
		    	  theFSM.raiseChooseSlide();
		      }
		});

		Hashtable<Integer, JLabel> temperatureTable = new Hashtable<Integer, JLabel>();
		temperatureTable.put(0, new JLabel("20°C"));
		temperatureTable.put(1, new JLabel("35°C"));
		temperatureTable.put(2, new JLabel("60°C"));
		temperatureTable.put(3, new JLabel("85°C"));
		for (JLabel l : temperatureTable.values()) {
			l.setForeground(Color.WHITE);
		}
		temperatureSlider.setLabelTable(temperatureTable);

		contentPane.add(temperatureSlider);

		JButton icedTeaButton = new JButton("Iced Tea");
		icedTeaButton.setForeground(Color.WHITE);
		icedTeaButton.setBackground(Color.DARK_GRAY);
		icedTeaButton.setBounds(12, 182, 96, 25);
		contentPane.add(icedTeaButton);

		JLabel lblSugar = new JLabel("Sugar");
		lblSugar.setForeground(Color.WHITE);
		lblSugar.setBackground(Color.DARK_GRAY);
		lblSugar.setHorizontalAlignment(SwingConstants.CENTER);
		lblSugar.setBounds(380, 34, 44, 15);
		contentPane.add(lblSugar);

		JLabel lblSize = new JLabel("Size");
		lblSize.setForeground(Color.WHITE);
		lblSize.setBackground(Color.DARK_GRAY);
		lblSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblSize.setBounds(380, 113, 44, 15);
		contentPane.add(lblSize);

		JLabel lblTemperature = new JLabel("Temperature");
		lblTemperature.setForeground(Color.WHITE);
		lblTemperature.setBackground(Color.DARK_GRAY);
		lblTemperature.setHorizontalAlignment(SwingConstants.CENTER);
		lblTemperature.setBounds(363, 173, 96, 15);
		contentPane.add(lblTemperature);

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		lblCoins.setLabelFor(panel);
		panel.setBounds(538, 25, 96, 97);
		contentPane.add(panel);

		money50centsButton = new JButton("0.50 €");
		money50centsButton.setForeground(Color.WHITE);
		money50centsButton.setBackground(Color.DARK_GRAY);
		panel.add(money50centsButton);
		money50centsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare||byNFC)
            		return;
            	myCoin = 50;
            	theFSM.raiseChooseCoin();
            }
        });

		money25centsButton = new JButton("0.25 €");
		money25centsButton.setForeground(Color.WHITE);
		money25centsButton.setBackground(Color.DARK_GRAY);
		panel.add(money25centsButton);
		money25centsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare||byNFC)
            		return;
            	myCoin = 25;
            	theFSM.raiseChooseCoin();
            } 
        });

		money10centsButton = new JButton("0.10 €");
		money10centsButton.setForeground(Color.WHITE);
		money10centsButton.setBackground(Color.DARK_GRAY);
		panel.add(money10centsButton);
		money10centsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare||byNFC)
            		return;
            	myCoin = 10;
            	theFSM.raiseChooseCoin();
            }
        });

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		panel_1.setBounds(538, 154, 96, 40);
		contentPane.add(panel_1);

		nfcBiiiipButton = new JButton("biiip");
		nfcBiiiipButton.setForeground(Color.WHITE);
		nfcBiiiipButton.setBackground(Color.DARK_GRAY);
		panel_1.add(nfcBiiiipButton);
		nfcBiiiipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare||paidCoinsValue>0)
            		return;
            	byNFC=true;
            	messagesToUser.setText("<html>Your NFC information is saved");
            	theFSM.raiseChooseNFC();
            }
        });

		JLabel lblNfc = new JLabel("NFC");
		lblNfc.setForeground(Color.WHITE);
		lblNfc.setHorizontalAlignment(SwingConstants.CENTER);
		lblNfc.setBounds(541, 139, 41, 15);
		contentPane.add(lblNfc);

		JSeparator separator = new JSeparator();
		separator.setBounds(12, 292, 622, 15);
		contentPane.add(separator);

		JButton addCupButton = new JButton("Add cup");
		addCupButton.setForeground(Color.WHITE);
		addCupButton.setBackground(Color.DARK_GRAY);
		addCupButton.setBounds(45, 336, 96, 25);
		contentPane.add(addCupButton);
		addCupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;
            	cupValue = 10;
            	theFSM.raiseAddCup();
            }
        });

		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("./picts/vide2.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		labelForPictures = new JLabel(new ImageIcon(myPicture));
		labelForPictures.setBounds(175, 319, 286, 260);
		contentPane.add(labelForPictures);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.DARK_GRAY);
		panel_2.setBounds(538, 217, 96, 33);
		contentPane.add(panel_2);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setForeground(Color.WHITE);
		cancelButton.setBackground(Color.DARK_GRAY);
		panel_2.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;
            	theFSM.raiseCancel();
            }
        });

		// listeners
		addCupButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BufferedImage myPicture = null;
				try {
					myPicture = ImageIO.read(new File("./picts/ownCup.jpg"));
				} catch (IOException ee) {
					ee.printStackTrace();
				}
				labelForPictures.setIcon(new ImageIcon(myPicture));
			}
		});

	}
}
