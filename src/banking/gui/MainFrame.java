package banking.gui;

import banking.primitive.core.Account;
import banking.primitive.core.AccountServer;
import banking.primitive.core.AccountServerFactory;

import java.io.*;
import java.util.*;
import java.awt.FlowLayout;
import java.awt.Container;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
class MainFrame extends JFrame {
	AccountServer	myServer;
	Properties		props;
	JLabel			typeLabel;
	JLabel			nameLabel;
	JLabel			balanceLabel;
	JComboBox		typeOptions;
	JTextField		nameField;
	JTextField		balanceField;
	//ID#18 CS.10 - Identifiers too long
	JButton 		depositB;
	JButton 		withdrawB;
	JButton			newAccountB;
	JButton			displayAccountsB;
	JButton			displayODAccountsB;

	public MainFrame(String propertyFile) throws IOException {

		//** initialize myServer
		myServer = AccountServerFactory.getMe().lookup();

		props = new Properties();

		FileInputStream fis = null; 
		try {
			fis =  new FileInputStream(propertyFile);
			props.load(fis);
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		}
		constructForm();
	}

	
	private void constructForm() {
		//*** Make these read from properties
		typeLabel		= new JLabel(props.getProperty("TypeLabel"));
		nameLabel		= new JLabel(props.getProperty("NameLabel"));
		balanceLabel	= new JLabel(props.getProperty("BalanceLabel"));

		Object[] accountTypes = {"Savings", "Checking"};
		typeOptions = new JComboBox(accountTypes);
		nameField = new JTextField(20);
		balanceField = new JTextField(20);

		newAccountB = new JButton("New Account");
		JButton depositB = new JButton("Deposit");
		JButton withdrawB = new JButton("Withdraw");
		JButton saveButton = new JButton("Save Accounts");
		displayAccountsB = new JButton("List Accounts");
		//ID#18 CS.10 - Identifiers too long
		JButton displayAllAccountsB = new JButton("All Accounts");

		this.addWindowListener(new FrameHandler());
		newAccountB.addActionListener(new NewAccountHandler());
		displayAccountsB.addActionListener(new DisplayHandler());
		displayAllAccountsB.addActionListener(new DisplayHandler());
		depositB.addActionListener(new DepositHandler());
		withdrawB.addActionListener(new WithdrawHandler());
		saveButton.addActionListener(new SaveAccountsHandler());		
		
		Container pane = getContentPane();
		pane.setLayout(new FlowLayout());
		
		JPanel panel1 = new JPanel();
		panel1.add(typeLabel);
		panel1.add(typeOptions);
		
		JPanel panel2 = new JPanel();
		panel2.add(displayAccountsB);
		panel2.add(displayAllAccountsB);
		panel2.add(saveButton);
		
		JPanel panel3 = new JPanel();
		panel3.add(nameLabel);
		panel3.add(nameField);
		
		JPanel panel4 = new JPanel();
		panel4.add(balanceLabel);
		panel4.add(balanceField);
		
		JPanel panel5 = new JPanel();
		panel5.add(newAccountB);
		panel5.add(depositB);
		panel5.add(withdrawB);

		pane.add(panel1);
		pane.add(panel2);
		pane.add(panel3);
		pane.add(panel4);
		pane.add(panel5);
		
		setSize(400, 250);
	}

	class DisplayHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<Account> accounts = null;
			if (e.getSource() == displayAccountsB) {
				accounts = myServer.getActiveAccounts();
			} else {
				accounts = myServer.getAllAccounts();
			}
			StringBuffer sb = new StringBuffer();
			Account thisAcct = null;
			for (Iterator<Account> li = accounts.iterator(); li.hasNext();) {
				thisAcct = (Account)li.next();
				sb.append(thisAcct.toString()+"\n");
			}

			JOptionPane.showMessageDialog(null, sb.toString());
		}
	}

	// Complete a handler for new account button
	class NewAccountHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String type = typeOptions.getSelectedItem().toString();
			String name = nameField.getText();
			String balance = balanceField.getText();

			if (myServer.newAccount(type, name, Float.parseFloat(balance))) {
				JOptionPane.showMessageDialog(null, "Account created successfully");
			} else {
				JOptionPane.showMessageDialog(null, "Account not created!");
			}
		}
	}
	
	// Complete a handler for new account button
	class SaveAccountsHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				myServer.saveAccounts();
				JOptionPane.showMessageDialog(null, "Accounts saved");
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(null, "Error saving accounts");
			}
		}
	}

	// Complete a handler for deposit button
	class DepositHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = nameField.getText();
			String balance = balanceField.getText();
			Account acc = myServer.getAccount(name);
			if (acc != null && acc.deposit(Float.parseFloat(balance))) {
				JOptionPane.showMessageDialog(null, "Deposit successful");
			} else {
				JOptionPane.showMessageDialog(null, "Deposit unsuccessful");
			}		
		}
	}
	// Complete a handler for deposit button
	class WithdrawHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = nameField.getText();
			String balance = balanceField.getText();
			Account acc = myServer.getAccount(name);
			if (acc != null && acc.withdraw(Float.parseFloat(balance))) {
				JOptionPane.showMessageDialog(null, "Withdrawal successful");
			} else {
				JOptionPane.showMessageDialog(null, "Withdrawal unsuccessful");
			}		
		}
	}
	
	//** Complete a handler for the Frame that terminates 
	//** (System.exit(1)) on windowClosing event

	static class FrameHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {

			System.exit(0);
		}
	}
}
