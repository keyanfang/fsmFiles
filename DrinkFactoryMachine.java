package fr.univcotedazur.polytech.si4.fsm.project;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import fr.univcotedazur.polytech.si4.fsm.project.drink.DrinkStatemachine;
import fr.univcotedazur.polytech.si4.fsm.project.drink.IDrinkStatemachine.SCInterfaceListener;

enum MyDrink{
	COFFEE,EXPRESSO,TEA;
}

class NFCuser{
	int paidTimes=0;
	int paidSum=0;
	String name;
	
	NFCuser(String name){
		this.name = name;
	}
	
	//at 11th time,return the amount he should pay
	public int newNfcPay(int value) {
		//TODO 10
		if(paidTimes==2) {
			int discount = paidSum/paidTimes;
			paidTimes=0;
			paidSum = 0;
			if(discount>=value) {
				return 0;
			}else {
				return value-discount;
			}
		}else {
			paidTimes++;
			paidSum += value;
			return -1;
		}
	}
}
 
public class DrinkFactoryMachine extends JFrame{
	
	JLabel messagesToUser;
	JLabel labelForPictures;
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
	JButton addMilkButton;
	JButton addSirupButton;
	JButton addIceButton;
	JButton refillButton;
	JTextField NfcName;
	JProgressBar progressBar;
	
	MyDrink myDrink;
	int drinkPrice; 
	int paidCoinsValue;
	int myCoin;
	int refund;
	int cupValue;
	int optionPrice;
	int step;
	int coffeeNum=100;
	int teaNum=100;
	int expressoNum=100;
	int sugarNum=1000;
	int sirupNum=500;
	int milkNum=1000;
	int iceNum=500;
	boolean sirupTmpDis=false;
	boolean iceTmpDis=false;
	boolean sirup=false;
	boolean ice=false;
	boolean milk=false;
	boolean byNFC=false;
	boolean startPrepare = false; 
	List<NFCuser> NFCusers = new ArrayList<>(); 
	
	public void calculateOptionPrice() {
		optionPrice=0;
		if(milk)
			optionPrice += 10;
		if(sirup)
			optionPrice += 10;
		if(ice)
			optionPrice += 60;
	}
	
	public void initialDrinkButton() { 
		coffeeButton.setBackground(Color.DARK_GRAY);
		expressoButton.setBackground(Color.DARK_GRAY);
		teaButton.setBackground(Color.DARK_GRAY);
	}
	
	public void initialOptionButton() {
		addMilkButton.setBackground(Color.DARK_GRAY);
		addSirupButton.setBackground(Color.DARK_GRAY);
		addIceButton.setBackground(Color.DARK_GRAY);
		if(sirupTmpDis)
			addSirupButton.setEnabled(true);
		if(iceTmpDis)
			addIceButton.setEnabled(true);
	}
	
	public void initialSliders() { 
		sugarSlider.setValue(1);
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
				onConfirmCoinsRaised();
			} 	
		}

		void cleanInfos() {
			initialDrinkButton();
			initialOptionButton();
			initialSliders();
			nfcBiiiipButton.setBackground(Color.DARK_GRAY);
			myDrink = null;
			drinkPrice = 0;
			optionPrice = 0;
			paidCoinsValue = 0;
			cupValue = 0;
			refund = 0;
			step = 0;
			iceTmpDis=false;
			sirup=false;
			byNFC=false;
			sirup=false;
			ice=false;
			milk=false;
			startPrepare = false;
			progressBar.setValue(0);
			NfcName.setText("give name for NFC");
			NfcName.setEditable(true);
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
						+ "The order has been canceled <br>Your 0."+paidCoinsValue+"€ have been returned");
			}
			cleanInfos();
		}

		@Override
		public void onCancelOrderRaised() {
			messagesToUser.setText("<html>You canceled the order<br>Your 0."+paidCoinsValue+"€ have been returned");
			cleanInfos();
		}
 
		@Override
		public void onConfirmCoinsRaised() {
			if(drinkPrice==0)
				return;
			calculateOptionPrice();
			refund = paidCoinsValue+cupValue - optionPrice-drinkPrice;
			if(refund>0) {
				messagesToUser.setText("<html>Payment is successful, start to make drinks<br>You will get a refund of 0."+refund+"€");
				theFSM.raiseOrderSuccess();
			}else if(refund==0) {
				messagesToUser.setText("<html>Payment is successful, start to make drinks");
				theFSM.raiseOrderSuccess();
			}else if(refund<0) {
				DecimalFormat df = new DecimalFormat( "0.00");  
				String toPay = df.format(-0.01*refund);
				messagesToUser.setText("<html>You still need to pay "+toPay+"€");
			}
		}
		

		@Override
		public void onNFCSuccessRaised() {
			boolean exist = false;
			String nfcName = NfcName.getText(); 
			if(nfcName.equals("")||nfcName.equals("give name for NFC")) {
				messagesToUser.setText("<html>NFC payment successful, start to make your drink.");
				return;
			}
				
			
			for(NFCuser nfcuser:NFCusers) {
				if(nfcuser.name.equals(nfcName)) {
					int nfcPay = nfcuser.newNfcPay(drinkPrice); 
		 			  
					//TODO
					if(nfcPay==0) {
						drinkPrice = 0;
						messagesToUser.setText("<html>This is your 11th time using NFC. Your drink is free this time, start to make your drink.");
					}else if(nfcPay>0) {
						drinkPrice = nfcPay;
						messagesToUser.setText("<html>This is your 11th time using NFC. You only need to pay "+ 0.01*nfcPay +"€, start to make your drink.");
					}
					exist = true;
					break;
				}
			}
			
			if(!exist) {
				NFCuser user = new NFCuser(nfcName);
				NFCusers.add(user);
				user.newNfcPay(drinkPrice);
				
				//TODO
				messagesToUser.setText("<html>Hello, new nfc user, start to make your drink");
			}
			
		}

		@Override
		public void onCleanMachineRaised() {
			messagesToUser.setText("<html>Machine is cleaned");
			
			if (myDrink==MyDrink.COFFEE) {
				coffeeNum = coffeeNum -1;
			}
			if (myDrink==MyDrink.TEA) {
				teaNum = teaNum -1;
			}
			if (myDrink==MyDrink.EXPRESSO) {
				expressoNum = expressoNum -1;
			}
			if (sirup==true) {
				sirupNum=sirupNum - sugarSlider.getValue();
			}else {
				sugarNum=sugarNum - sugarSlider.getValue();
			}
			if (milk==true) {milkNum=milkNum-1;}
			if (ice ==true) {iceNum=iceNum-1;}
			
			cleanInfos();
		}

		@Override
		public void onPrepStartRaised() {
			startPrepare = true;
			switch(myDrink) {
				case COFFEE:
					theFSM.raiseIsCoffee();
					break;
				case EXPRESSO:
					theFSM.raiseIsEspresso();
					break;
				case TEA:
					theFSM.raiseIsTea();
					break;
			} 
			NfcName.setEditable(false);
		}


		@Override
		public void onBarRaised() {
			boolean type1=false;
			boolean type2=false;
			boolean type3=false;
			boolean type4=false;
			boolean type5=false;
			boolean type6=false;
			if (sugarSlider.getValue()!=0&&milk==false&&ice==false) {type1=true;}
			if (sugarSlider.getValue()!=0&&milk==true&&ice==true) {type2=true;}
			if (sugarSlider.getValue()!=0&&milk==false&&ice==true) {type3=true;}
			if (sugarSlider.getValue()!=0&&milk==true&&ice==false) {type3=true;}
			if (sugarSlider.getValue()==0&&milk==false&&ice==true) {type4=true;}
			if (sugarSlider.getValue()==0&&milk==true&&ice==false) {type4=true;}		
			if (sugarSlider.getValue()==0&&milk==true&&ice==true) {type5=true;}
			if (sugarSlider.getValue()==0&&milk==false&&ice==false) {type6=true;}
			switch(step) {		
			case 0:
				if (myDrink == MyDrink.COFFEE&&type1==true) {
				for (int i=1;i<38;i++) {
				progressBar.setValue(i);
						}
				step = step + 1;
					}
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					for (int i=1;i<31;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type3==true) {
					for (int i=1;i<34;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type4==true) {
					for (int i=1;i<34;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type5==true) {
					for (int i=1;i<31;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type6==true) {
					for (int i=1;i<38;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				
				if (myDrink == MyDrink.EXPRESSO&&type1==true) {
					
						try {
							
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for (int i=1;i<46;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					 {
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=1;i<39;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
				}
					
				if (myDrink == MyDrink.EXPRESSO&&type3) {
					 {
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=1;i<43;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type4==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=1;i<46;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=1;i<39;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					 {
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=1;i<43;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				
				if (myDrink == MyDrink.TEA&&type1==true) {
					for (int i=1;i<13;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					for (int i=1;i<12;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
					for (int i=1;i<13;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type6==true) {
					for (int i=11;i<12;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				break;
			case 1:
				if (myDrink == MyDrink.COFFEE&&type1==true) {
					for (int i=38;i<51;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					for (int i=31;i<41;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type3==true) {
					for (int i=34;i<45;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE && type4==true) {
					for (int i=34;i<45;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type5==true) {
					for (int i=31;i<41;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type6==true) {
					for (int i=38;i<51;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				
				if (myDrink == MyDrink.EXPRESSO&&type1==true) {
					{
						
						for (int i=46;i<65;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					{
						
						for (int i=39;i<59;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					 {
						
						for (int i=43;i<35;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type4==true) {
					 {
						
						for (int i=46;i<65;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					{
						
						for (int i=39;i<59;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					 {
						
						for (int i=43;i<35;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				
				
				if (myDrink == MyDrink.TEA&&type1==true) {
					for (int i=12;i<19;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					for (int i=13;i<18;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
					for (int i=12;i<19;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type6==true) {
					for (int i=13;i<18;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				break;
			case 2:
				if (myDrink == MyDrink.COFFEE&&type1==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=51;i<76;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=41;i<61;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type3==true&&temperatureSlider.getValue()<2) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=45;i<67;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type4==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=45;i<67;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type5==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=41;i<61;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type6==true) {
					 {
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=51;i<76;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				
				
				if (myDrink == MyDrink.EXPRESSO&&type1==true) {
					for (int i=65;i<83;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					for (int i=59;i<76;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					for (int i=55;i<70;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type4==true) {
					for (int i=65;i<83;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					for (int i=59;i<76;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					for (int i=55;i<70;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
				}
				if (myDrink == MyDrink.TEA&&type1==true&&temperatureSlider.getValue()<2) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=19;i<36;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=18;i<34;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
				 {
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=19;i<36;i++) {
							progressBar.setValue(i);
									}
					}
					step++;
				}
				if (myDrink == MyDrink.TEA&&type6==true) {
					{
						try {
							TimeUnit.SECONDS.sleep(temperatureSlider.getValue());
							theFSM.raiseHeatWater();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i=18;i<34;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				
				
				break;
			case 3:
				if (myDrink == MyDrink.COFFEE&&type1==true) {
					 {
						
						for (int i=76;i<88;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					 {
						
						for (int i=61;i<72;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type3==true) {
					 {
						
						for (int i=67;i<79;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type4==true) {
					{
						
						for (int i=67;i<89;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type5==true) {
					{
						
						for (int i=61;i<81;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type6==true) {
					{
						
						for (int i=76;i<101;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				
				
				if (myDrink == MyDrink.EXPRESSO&&type1==true) {
					for (int i=83;i<92;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					for (int i=76;i<84;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					for (int i=70;i<78;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type4==true) {
					for (int i=83;i<92;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					for (int i=76;i<84;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					for (int i=70;i<78;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				
				if (myDrink == MyDrink.TEA&&type1==true) {
					{
						
						for (int i=36;i<79;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					{
						
						for (int i=34;i<40;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
					{
						
						for (int i=36;i<46;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type6==true) {
					 {
						
						for (int i=34;i<48;i++) {
							progressBar.setValue(i);
									}
					}
					step = step + 1;
						}
				
				break;
				
			case 4:
				if (myDrink == MyDrink.COFFEE&&type1==true) {
					for (int i=88;i<100;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					for (int i=71;i<81;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type3==true) {
					for (int i=79;i<91;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type4==true) {
					for (int i=90;i<101;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type5==true) {
					for (int i=81;i<91;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				
				if (myDrink == MyDrink.EXPRESSO&&type1==true) {
					for (int i=92;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					for (int i=84;i<93;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					for (int i=78;i<86;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type4==true) {
					for (int i=92;i<100;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					for (int i=84;i<93;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					for (int i=78;i<86;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type1==true) {
					for (int i=43;i<49;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					for (int i=40;i<46;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
					for (int i=46;i<79;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type6==true) {
					for (int i=48;i<83;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				break;
			
			case 5:
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					for (int i=81;i<91;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.COFFEE&&type3==true) {
					for (int i=91;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.COFFEE&&type4==true) {
					progressBar.setValue(100);
						}
				if (myDrink == MyDrink.COFFEE&&type5==true) {
					for (int i=91;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					for (int i=93;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					for (int i=86;i<93;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					for (int i=93;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					for (int i=86;i<93;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type1==true) {
					for (int i=49;i<83;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					for (int i=46;i<79;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
					for (int i=79;i<96;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type6==true) {
					for (int i=83;i<101;i++) {
							}
					step = step + 1;
						}
				break;
			
			case 6:
				if (myDrink == MyDrink.COFFEE&&type2==true) {
					for (int i=91;i<100;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					for (int i=93;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					for (int i=93;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.TEA&&type1==true) {
					for (int i=83;i<100;i++) {
					progressBar.setValue(i);
							}
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					for (int i=79;i<96;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type4==true) {
					for (int i=96;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				break;
			case 7:
				if (myDrink == MyDrink.EXPRESSO&&type1==true) {
					for (int i=51;i<76;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type2==true) {
					for (int i=41;i<61;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type3==true) {
					for (int i=45;i<67;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type4==true) {
					for (int i=45;i<67;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type5==true) {
					for (int i=41;i<61;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.EXPRESSO&&type6==true) {
					for (int i=51;i<76;i++) {
					progressBar.setValue(i);
							}
					step = step + 1;
						}
				if (myDrink == MyDrink.TEA&&type3==true) {
					for (int i=96;i<101;i++) {
					progressBar.setValue(i);
							}
						}
				break;
			}
		}
			
		
		
		@Override
		public void onPrepFinishRaised() {
			progressBar.setValue(100);
			messagesToUser.setText("<html>Your drink is ready");	
			
			labelForPictures.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					theFSM.raiseTakeDrink();
				}
			});
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
			BufferedImage myPicture = null;
			try {
				myPicture = ImageIO.read(new File("./picts/gobeletPolluant.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			labelForPictures.setIcon(new ImageIcon(myPicture));
		}

		@Override
		public void onCheckIngredientsRaised() {
			if (coffeeNum==0) {
				refillButton.setEnabled(true);
				coffeeButton.setEnabled(false);
				messagesToUser.setText("<html>Sadly we can't offer you more coffee for now.");	
			}
 
			if (teaNum==0) {
				refillButton.setEnabled(true);
				teaButton.setEnabled(false);
				messagesToUser.setText("<html>Sadly we can't offer you more tea for now.");	
			}
	
			if (expressoNum==0) {
				refillButton.setEnabled(true);
				expressoButton.setEnabled(false);
				messagesToUser.setText("<html>Sadly we can't offer you more expresso for now.");	
			}

			if (sugarNum==0) {
				messagesToUser.setText("<html>Sadly we can't offer you sugar for now.");
				sugarSlider.setEnabled(false);
				refillButton.setEnabled(true);
			}
			if (sirupNum==0) {
				messagesToUser.setText("<html>Sadly we can't offer you sirup for now.");
				addSirupButton.setEnabled(false);
				refillButton.setEnabled(true);
			}
			if (milkNum==0) {
				messagesToUser.setText("<html>Sadly we can't offer you milk for now.");
				addMilkButton.setEnabled(false);
				refillButton.setEnabled(true);
			}
			if (iceNum==0) {
				messagesToUser.setText("<html>Sadly we can't offer you ice for now.");
				addIceButton.setEnabled(false);
				refillButton.setEnabled(true);
			}
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
            	DecimalFormat df = new DecimalFormat( "0.00");  
				String toPay = df.format(0.01*(drinkPrice+optionPrice));
            	messagesToUser.setText("<html>Please pay "+toPay+"€ for your coffee");
            	if(iceTmpDis) {
            		iceTmpDis = false;
            		addIceButton.setEnabled(true);
            	}
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
            	DecimalFormat df = new DecimalFormat( "0.00");  
				String toPay = df.format(0.01*(drinkPrice+optionPrice));
            	messagesToUser.setText("<html>Please pay "+toPay+"€ for your expresso");
            	if(iceTmpDis) {
            		iceTmpDis = false;
            		addIceButton.setEnabled(true);
            	}
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
            	DecimalFormat df = new DecimalFormat( "0.00");  
				String toPay = df.format(0.01*(drinkPrice+optionPrice));
            	messagesToUser.setText("<html>Please pay "+toPay+"€ for your tea");
            	teaButton.setBackground(Color.green);
            	
            	if(ice) {
            		ice = false;
                	calculateOptionPrice();
                	addIceButton.setBackground(Color.DARK_GRAY);
            		messagesToUser.setText("<html>You have chosen tea, vanilla ice cream is no longer available.");
            	}
            	iceTmpDis = true;
            	addIceButton.setEnabled(false);
            	theFSM.raiseChooseDrink();
            }
        });

//		JButton soupButton = new JButton("Soup");
//		soupButton.setForeground(Color.WHITE);
//		soupButton.setBackground(Color.DARK_GRAY);
//		soupButton.setBounds(12, 145, 96, 25);
//		contentPane.add(soupButton);

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
				  //TODO
		    	  if(sugarSlider.getValue()==0) {
		    		  sirupTmpDis=true;
		    		  if(sirup) {
		    			  sirup=false;
			    		  calculateOptionPrice();
			    		  addSirupButton.setBackground(Color.DARK_GRAY);
			    		  messagesToUser.setText("<html>You choose not to add sugar, syrup is no longer available.");
		    		  }
		    		  addSirupButton.setEnabled(false); 
		    	  }else {
		    		  addSirupButton.setEnabled(true);
		    	  }
		    	  
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

//		JButton icedTeaButton = new JButton("Iced Tea");
//		icedTeaButton.setForeground(Color.WHITE);
//		icedTeaButton.setBackground(Color.DARK_GRAY);
//		icedTeaButton.setBounds(12, 182, 96, 25);
//		contentPane.add(icedTeaButton);

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
		
		addMilkButton = new JButton("Add milk");
		addMilkButton.setForeground(Color.WHITE);
		addMilkButton.setBackground(Color.DARK_GRAY);
		addMilkButton.setBounds(45, 406, 96, 25);
		contentPane.add(addMilkButton); 
		addMilkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;

            	if(milk) {
            		milk = false;
                	addMilkButton.setBackground(Color.DARK_GRAY);
            	}else {
            		milk=true;
                	addMilkButton.setBackground(Color.green);
            	}
            	calculateOptionPrice();
            	theFSM.raiseChooseMilk();
            }
        });
		
		addSirupButton = new JButton("Add sirup");
		addSirupButton.setForeground(Color.WHITE);
		addSirupButton.setBackground(Color.DARK_GRAY);
		addSirupButton.setBounds(45, 446, 96, 25);
		contentPane.add(addSirupButton);  
		addSirupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;

            	if(sirup) {
	    			sirup=false;
		    		addSirupButton.setBackground(Color.DARK_GRAY);
	    		}else {
	    			sirup=true;  
	              	addSirupButton.setBackground(Color.green);
	    		}
            	
            	calculateOptionPrice();
            	theFSM.raiseChooseSirup();
            }
        });
		
		addIceButton = new JButton("<html>Add vanilla<br> ice cream");
		addIceButton.setForeground(Color.WHITE);
		addIceButton.setBackground(Color.DARK_GRAY);
		addIceButton.setBounds(45, 486, 96, 29);
		contentPane.add(addIceButton); 
		addIceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;
            	
            	if(ice) {
            		ice = false;
                	addIceButton.setBackground(Color.DARK_GRAY);
            	}else {
            		ice=true;
                	addIceButton.setBackground(Color.green);
            	}

            	calculateOptionPrice();
            	theFSM.raiseChooseIce();
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
		
		refillButton = new JButton("Refill");
		refillButton.setForeground(Color.WHITE);
		refillButton.setBackground(Color.DARK_GRAY);
		refillButton.setBounds(495, 446, 96, 25);
		contentPane.add(refillButton);  
		refillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(startPrepare)
            		return;

            	coffeeNum=100;
            	teaNum=100;
            	expressoNum=100;
            	sugarNum=1000;
            	iceNum=500;
            	sirupNum=500;
            	milkNum=1000;
            	
            	coffeeButton.setEnabled(true);
        		expressoButton.setEnabled(true);
        		teaButton.setEnabled(true);
            	addMilkButton.setEnabled(true);
        		addSirupButton.setEnabled(true);
        		addIceButton.setEnabled(true);
            	sugarSlider.setEnabled(true);
            	messagesToUser.setText("<html>We've refilled the machine and all kinds of drink are avaliable now!");
            }
        });
		refillButton.setEnabled(false);
		
		NfcName = new JTextField(10);
		NfcName.setEditable(true);
		NfcName.setColumns(11);
		NfcName.setBackground(Color.WHITE);
		NfcName.setBounds(495, 336, 106, 25);
		NfcName.setText("give name for NFC");
		contentPane.add(NfcName);
		NfcName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if(startPrepare)
            		return;
				NfcName.setText("");
				theFSM.raiseChangeText();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				theFSM.raiseChangeText();
			}
        });
	
		NfcName.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				theFSM.raiseChangeText();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				theFSM.raiseChangeText();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				theFSM.raiseChangeText();
			}
			
		});
		
		
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

		
		addCupButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(startPrepare)
            		return;
            	cupValue = 10;
            	theFSM.raiseAddCup();
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
