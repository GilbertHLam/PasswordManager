import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import net.sf.image4j.codec.ico.ICODecoder;

import java.awt.datatransfer.*;

public class Manager {

	public static void main(String[] args) {
		LogInScreen object = new LogInScreen();
	}

}

class LogInScreen{
	public LogInScreen(){
		JFrame mainWindow = new JFrame("Password Manager");
		mainWindow.setUndecorated(true);
		JPanel contentPane = new JPanel(new MigLayout("fillx"));
		contentPane.setBackground(new Color(192,192,192));
		JLabel title = new JLabel("Password Manager");
		title.setFont(new Font("Consolas",0, 20));
		JLabel userName = new JLabel("Username: ");
		userName.setFont(new Font ("Consolas", 0, 15));
		JLabel passwordLabel = new JLabel("Password: ");
		passwordLabel.setFont(new Font ("Consolas", 0, 15));
		JTextField userInput = new JTextField();
		userInput.setColumns(10);
		JPasswordField passwordInput = new JPasswordField();
		passwordInput.setColumns(10);
		JButton submitButton = new JButton("Log In");
		submitButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				logIn(userInput, passwordInput, mainWindow);
			}
		});
		JButton newAccountButton = new JButton("Create a new user");
		newAccountButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(new JFrame(), ("You will be asked to enter a username. "
						+ "\nBe sure to remember your username!"));
				String name = JOptionPane.showInputDialog(mainWindow,
						"Enter a Username: ", null);
				JOptionPane.showMessageDialog(new JFrame(), ("You will be asked to enter a password. "
						+ "\nBe sure to remember your password!"));
				String pass = JOptionPane.showInputDialog(mainWindow,
						"Enter a Password:", null);
				if(new File("C:/PasswordManager/Users/"+name+"/login.pmf").exists())
					JOptionPane.showMessageDialog(new JFrame(), ("That user already exists!"));
				else{
					boolean success = new File("C:/PasswordManager/Users/"+name).mkdirs();
					File newUserData = new File("C:/PasswordManager/Users/"+name+"/login.pmf");
					HashingObject hash = new HashingObject(pass);
					PrintWriter writer;
					try {
						writer = new PrintWriter(newUserData);
						writer.println(name);
						writer.println(hash.hashAlgorithm());
						writer.close();
					} catch (IOException | NoSuchAlgorithmException e2) {

					}

				}

			}


		});
		JButton quitButton = new JButton("Quit Program");
		quitButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});

		contentPane.add(title, "span,grow,wrap");
		contentPane.add(userName, "align leading");
		contentPane.add(userInput, "align leading, wrap");
		contentPane.add(passwordLabel, "align leading");
		contentPane.add(passwordInput, "align leading, wrap");
		contentPane.add(submitButton, "span,grow,wrap");
		contentPane.add(newAccountButton, "span,grow,wrap");
		contentPane.add(quitButton, "span, grow, wrap");
		mainWindow.setVisible(true);
		mainWindow.setSize(240, 220);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.setLocation(dim.width/2-mainWindow.getWidth()/2, dim.height/2-mainWindow.getHeight()/2);
		mainWindow.setContentPane(contentPane);
	}

	public void logIn(JTextField userInput, JTextField passwordInput, JFrame mainWindow){
		File data = new File("C:/PasswordManager/Users/"+userInput.getText()+"/login.pmf");
		FileReader fileReader;
		try {
			fileReader = new FileReader(data);
			BufferedReader br = new BufferedReader(fileReader);
			String userNameInput = br.readLine();
			String hashedPass = br.readLine();
			String tempUser = userInput.getText();
			HashingObject x = new HashingObject(passwordInput.getText());
			if (userNameInput.equalsIgnoreCase(tempUser)){
				if (hashedPass.equals(x.hashAlgorithm())){
					mainWindow.setVisible(false);
					Interface session = new Interface(userInput.getText());
				}
				else {
					JOptionPane.showMessageDialog(new JFrame(), ("Incorrect Password"));
				}
			}
			else {
				JOptionPane.showMessageDialog(new JFrame(), ("Username does not exist"));
			}
			fileReader.close();
		} catch (FileNotFoundException e1) {

		} catch (IOException e1) {
			JOptionPane.showMessageDialog(new JFrame(), ("Username or Password Incorrect"));
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

class Interface {
	int counter = 0;
	JFrame mainWindow = new JFrame("Password Manager");
	JPanel contentPane = new JPanel(new MigLayout("fillx"));
	String sessionName;
	public Interface(String sessionName){
		this.sessionName = sessionName;
		mainWindow.setUndecorated(true);
		mainWindow.setVisible(true);
		contentPane.setBackground(new Color(192,192,192));
		mainWindow.setSize(300, 150);
		JButton quitButton = new JButton("Quit Program");
		quitButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});
		JButton viewDatabaseButton = new JButton("View Database");
		viewDatabaseButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if(!new File("C:/PasswordManager/Users/"+sessionName+"/accounts.pmf").exists()){
					JOptionPane.showMessageDialog(new JFrame(), ("No accounts have been saved!"));
				}
				else {
					try {
						viewDatabase(mainWindow);
					} catch (InvalidKeyException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalBlockSizeException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (BadPaddingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoSuchPaddingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		JButton makeNewAccountButton = new JButton("Create New Account");
		makeNewAccountButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				makeNewAccount();
			}
		});
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.setLocation(dim.width/2-mainWindow.getWidth()/2, dim.height/2-mainWindow.getHeight()/2);
		mainWindow.setContentPane(contentPane);
		contentPane.add(viewDatabaseButton, "span, grow, wrap");
		contentPane.add(makeNewAccountButton, "span, grow, wrap");
		contentPane.add(quitButton, "span, grow, wrap");
	}

	public void makeNewAccount () {
		contentPane.removeAll();
		mainWindow.setSize(300,250);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.setLocation(dim.width/2-mainWindow.getWidth()/2, dim.height/2-mainWindow.getHeight()/2);
		JLabel websitePrompt = new JLabel("Website URL: ");
		websitePrompt.setFont(new Font("Consolas", 0, 15));

		JTextField websiteInput = new JTextField();
		websiteInput.setText("");
		websiteInput.setColumns(20);

		JLabel passwordPrompt = new JLabel("Password: ");
		passwordPrompt.setFont(new Font("Consolas", 0, 15));

		JTextField passwordInput = new JTextField();
		passwordInput.setColumns(20);

		JLabel userNamePrompt = new JLabel("Username: ");
		userNamePrompt.setFont(new Font("Consolas", 0, 15));

		JTextField userNameInput = new JTextField();
		userNameInput.setColumns(20);

		JButton quitButton = new JButton("Go Back");
		quitButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				mainWindow.setVisible(false);
				Interface session = new Interface(sessionName);
			}
		});

		JButton randomPasswordButton = new JButton("Generate a Secure Password");
		randomPasswordButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				passwordInput.setText(generatePassword());
			}
		});

		JButton saveButton = new JButton("Save Account Details");
		saveButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				try {
					if(websiteInput.getText().equals("") || userNameInput.getText().equals("") || passwordInput.getText().equals("") ) {
						JOptionPane.showMessageDialog(new JFrame(), ("Some fields are empty!"));
					}
					else {
						saveDetails(websiteInput, userNameInput, passwordInput);
						JOptionPane.showMessageDialog(new JFrame(), ("Account details saved successfully!"));
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JButton copyToClipboardButton = new JButton("Copy Password to Clipboard");
		copyToClipboardButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(passwordInput);
			}
		});

		contentPane.add(websitePrompt, "align leading");
		contentPane.add(websiteInput, "align leading,wrap");
		contentPane.add(userNamePrompt, "align leading");
		contentPane.add(userNameInput, "align leading,wrap");
		contentPane.add(passwordPrompt, "align leading");
		contentPane.add(passwordInput, "align leading,wrap");
		contentPane.add(copyToClipboardButton, "span, grow, wrap");
		contentPane.add(randomPasswordButton, "span, grow, wrap");
		contentPane.add(saveButton, "span, grow, wrap");
		contentPane.add(quitButton, "span, grow, wrap");
	}

	public void copyToClipboard(JTextField passwordInput){
		String password = passwordInput.getText();
		StringSelection stringSelection = new StringSelection(password);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	public String generatePassword(){
		int randomNum = 0 + (int)(Math.random() * 10000000); 
		HashingObject randomPass = new HashingObject(Integer.toString(randomNum));
		String safePassword = null;
		try {
			safePassword = randomPass.hashAlgorithm();

		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return safePassword.substring(0, 10).toUpperCase() + safePassword.substring(10);
	}

	public void saveDetails(JTextField websiteInput, JTextField userNameInput, JTextField passwordInput) throws Exception{
		File data = new File("C:/PasswordManager/Users/"+sessionName+"/accounts.pmf");
		FileWriter writer = new FileWriter(data,true);
		String textData[] = { ("Website:"+websiteInput.getText()),
				("Username:"+userNameInput.getText()),
				("Password:"+passwordInput.getText())};
		for(int i = 0; i < 3; i++) {
			EncoderDecoder encodingObject = new EncoderDecoder(textData[i]);
			String encryptedText = encodingObject.encrypt();
			writer.write(encryptedText);
			writer.write(System.getProperty( "line.separator" ));
		}
		writer.close();
	}

	public void viewDatabase(JFrame mainWindow) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException{
		contentPane.removeAll();
		mainWindow.setSize(600,600);
		JPanel displayPane = new JPanel(new MigLayout("fillx"));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.setLocation(dim.width/2-mainWindow.getWidth()/2, dim.height/2-mainWindow.getHeight()/2);
		JScrollPane accountDisplay = new JScrollPane(displayPane);
		JButton quitButton = new JButton("Go Back");
		quitButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				mainWindow.setVisible(false);
				Interface session = new Interface(sessionName);
			}
		});
		JLabel accounts = new JLabel("Account Details");
		accounts.setFont(new Font("Consolas",0,30));
		contentPane.add(accounts, "align center, wrap");
		contentPane.add(accountDisplay,"width 590 , height 500, wrap");
		contentPane.add(quitButton, "span, grow, wrap");
		decodeData();
		String string;
		ArrayList <Object> siteTitle = new ArrayList<Object>();
		ArrayList <Account> accountsList = new ArrayList<Account>();
		
		
		JFrame passwordWindow = new JFrame("Password Window");
		JPanel passwordPane = new JPanel(new MigLayout("fillx"));
		passwordWindow.setSize(300, 300);
		passwordWindow.setLocation(dim.width/2-passwordWindow.getWidth()/2, dim.height/2-passwordWindow.getHeight()/2);
		passwordWindow.setUndecorated(true);
		passwordWindow.setContentPane(passwordPane);
		passwordWindow.setVisible(false);
		
		passwordPane.setBackground(new Color(192,192,192));
		JLabel passwordPrompt = new JLabel("Password: ");
		passwordPrompt.setFont(new Font("Consolas", 0, 15));

		JTextField passwordInput = new JTextField();
		passwordInput.setColumns(20);
		JLabel userNamePrompt = new JLabel("Username: ");
		userNamePrompt.setFont(new Font("Consolas", 0, 15));

		JTextField userNameInput = new JTextField();
		userNameInput.setColumns(20);
		JButton quitButton1 = new JButton("Go Back");
		quitButton1.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				passwordWindow.setVisible(false);
			}
		});
		
		JButton copyToClipboardButton = new JButton("Copy Password to Clipboard");
		copyToClipboardButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(passwordInput);
			}
		});
		passwordPane.add(userNamePrompt,"align leading");
		passwordPane.add(userNameInput,"align leading, wrap");
		passwordPane.add(passwordPrompt,"align leading");
		passwordPane.add(passwordInput,"align leading, wrap");
		passwordPane.add(copyToClipboardButton,"span,grow,wrap");
		passwordPane.add(quitButton1,"span,grow,wrap");
		
		for(int i = 0; i < counter;i++){
			int x = i;
			File temp = new File("C:/PasswordManager/Users/"+sessionName+"/details.tmp");
			FileReader fileReader = new FileReader(temp);
			BufferedReader br = new BufferedReader(fileReader);
			int tempCounter = 0;
			String pass = null, user = null, site =null;
			while((string = br.readLine())!= null ){

				if(string.contains("Website:")) {
					site = string.substring(8);

				}
				if(string.contains("Password:")) {
					pass = string.substring(9);
					if(tempCounter == i)
						break;
					tempCounter++;
				}
				if(string.contains("Username")){
					user = string.substring(9);
				}
			}
			accountsList.add(new Account(user, pass));
			System.out.println(accountsList.get(i).getPassword());
			
			
			ReadURLTitle title = new ReadURLTitle(site);

			try {
				InputStream is = new URL("https://"+site+"/favicon.ico").openStream();
				List<BufferedImage> image = ICODecoder.read(is);
				siteTitle.add(new JLabel(new ImageIcon(image.get(0))));
			}catch(IOException e){
				InputStream is = new URL("http://"+site+"/favicon.ico").openStream();
				List<BufferedImage> image = ICODecoder.read(is);
				siteTitle.add(new JLabel(new ImageIcon(image.get(0))));
			}
			((JLabel) siteTitle.get(i)).setText(title.getTitle());

			((JLabel) siteTitle.get(i)).addMouseListener(new MouseListener () {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					System.out.print(x);
					passwordWindow.setVisible(true);
					
					
					passwordInput.setText(accountsList.get(x).getPassword());
					
					//Wont work need OOP

					userNameInput.setText(accountsList.get(x).getUser());
					

					

					
					
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

			});
			displayPane.add((Component) siteTitle.get(i), "align center, wrap");
		}

	}

	public void decodeData() throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		File temp = new File("C:/PasswordManager/Users/"+sessionName+"/details.tmp");
		File encrypted = new File("C:/PasswordManager/Users/"+sessionName+"/accounts.pmf");
		temp.deleteOnExit();
		FileReader fileReader = new FileReader(encrypted);
		BufferedReader br = new BufferedReader(fileReader);
		FileWriter writer = new FileWriter(temp);
		EncoderDecoder decrypt = new EncoderDecoder();
		String string, decoded;
		while((string = br.readLine())!= null ){
			decoded = decrypt.decrypt(string);
			if(decoded.contains("www."))
				counter++;
			writer.write(decoded);
			writer.write(System.getProperty( "line.separator" ));
		}
		writer.close();
		fileReader.close();

	}
}

class Account{
	private String userName, password;
	public Account(String userName, String password){
		this.userName = userName;
		this.password = password;
	}
	public String getUser (){
		return userName;
	}
	public String getPassword(){
		return password;
	}
}

class ReadURLTitle{
	URL url;
	Scanner scan;
	public ReadURLTitle(String urlString) throws MalformedURLException {
		try {
			url = new URL("https://"+urlString);
			scan = new Scanner(url.openStream());
		} catch (MalformedURLException e) {

		} catch (IOException e) {
			url = new URL("http://"+urlString);
			try {
				scan = new Scanner(url.openStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public String getTitle()throws IOException{



		String content = new String();
		while (scan.hasNext()) {
			content += scan.nextLine();
		}
		scan.close();

		String tagOpen;
		if (String.valueOf(url).contains("facebook")){
			tagOpen = "<title id=\"pageTitle\">";
		}
		else {
			tagOpen = "<title>";
		}
		String tagClose = "</title>";
		int begin = content.indexOf(tagOpen) + tagOpen.length();
		int end = content.indexOf(tagClose);
		return content.substring(begin, end);
	}



}

class HashingObject{
	private String tempPassword;

	public HashingObject(String tempPassword) {
		this.tempPassword = tempPassword;
	}
	public String hashAlgorithm() throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(tempPassword.getBytes());
		byte[] digest = md.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		while(hashtext.length() < 32 ){
			hashtext = "0"+hashtext;
		}
		return hashtext;
	}
}

class EncoderDecoder {
	private String secretText = null;
	public EncoderDecoder (String textData)throws Exception{
		secretText = textData;
	}

	public EncoderDecoder (){

	}

	public Cipher getCipher(String synchro1, String synchro2, String synchro3, String synchro4,boolean isEncryptMode)throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		byte raw[] = (synchro1 + synchro2 + synchro3 + synchro4).getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		if(isEncryptMode){
			cipher.init(Cipher.ENCRYPT_MODE,skeySpec);
		}
		else{
			cipher.init(Cipher.DECRYPT_MODE,skeySpec);
		}
		return cipher;
	} 

	public byte[] hexToByte(String hex){
		byte bts[] = new byte[hex.length() / 2];
		for(int i = 0; i < bts.length; i++){
			bts[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}

		return bts;
	}

	public String toHexString(byte bytes[]) {
		StringBuffer retString = new StringBuffer();
		for(int i = 0; i < bytes.length; i++)
			retString.append(Integer.toHexString(256 + (bytes[i] & 0xff)).substring(1));

		return retString.toString();
	}

	public String encrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		javax.crypto.Cipher cipher = getCipher("db5","9523", "do2v7", "253f",true);
		return toHexString(cipher.doFinal(secretText.getBytes()));
	}

	public String decrypt(String text) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		javax.crypto.Cipher cipher = getCipher("db5","9523", "do2v7", "253f",false);
		String st=new String(cipher.doFinal(hexToByte(text)));      
		return st;
	}


}
