package jjj.entropy.cardcreator;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Choice;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class Main extends JFrame 
{

	private static final long serialVersionUID = -3608511120384226146L;
	private JPanel contentPane;
	private JTextField txfTitle;
	private JTextField txfRaceCost;
	private JTextField txfCost2;
	private JTextField txfStrength;
	private JTextField txfVitality;
	private JTextField txfCost3;
	private JTextField txfIntelligence;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
					
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
	
	//	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
				DatabaseManager.GetInstance().Disconnect();
                System.exit(0);
            }
        });
		
		DatabaseManager.GetInstance().Connect();
		
		
		setBounds(100, 100, 420, 418);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
	
		
		
		JLabel lblCardTitle = new JLabel("Card title:");
		lblCardTitle.setBounds(56, 112, 61, 14);
		contentPane.add(lblCardTitle);
		
		JLabel lblResources = new JLabel("Resources:");
		lblResources.setBounds(10, 193, 68, 14);
		contentPane.add(lblResources);
		
		JLabel lblAnyCost = new JLabel("Race cost:");
		lblAnyCost.setBounds(129, 168, 81, 14);
		contentPane.add(lblAnyCost);
		
		JLabel lblCost2 = new JLabel("Cost 2:");
		lblCost2.setBounds(194, 168, 72, 14);
		contentPane.add(lblCost2);
		
		txfTitle = new JTextField();
		txfTitle.setBounds(22, 137, 135, 20);
		contentPane.add(txfTitle);
		txfTitle.setColumns(10);
		
		txfRaceCost = new JTextField();
		txfRaceCost.setText("1");
		txfRaceCost.setBounds(137, 190, 31, 20);
		contentPane.add(txfRaceCost);
		txfRaceCost.setColumns(10);
		
		txfCost2 = new JTextField();
		txfCost2.setText("0");
		txfCost2.setColumns(10);
		txfCost2.setBounds(203, 190, 31, 20);
		contentPane.add(txfCost2);
		
		JLabel lblStats = new JLabel("Stats:");
		lblStats.setBounds(17, 294, 61, 14);
		contentPane.add(lblStats);
		
		JLabel lblStrength = new JLabel("Strength:");
		lblStrength.setBounds(160, 271, 81, 14);
		contentPane.add(lblStrength);
		
		txfStrength = new JTextField();
		txfStrength.setText("0");
		txfStrength.setColumns(10);
		txfStrength.setBounds(170, 291, 31, 20);
		contentPane.add(txfStrength);
		
		txfVitality = new JTextField();
		txfVitality.setText("0");
		txfVitality.setColumns(10);
		txfVitality.setBounds(293, 291, 31, 20);
		contentPane.add(txfVitality);
		
		JLabel lblVitality = new JLabel("Vitality:");
		lblVitality.setBounds(293, 271, 81, 14);
		contentPane.add(lblVitality);
		
		JLabel lblEntropyCardCreator = new JLabel("Entropy Card Creator");
		lblEntropyCardCreator.setFont(new Font("LilyUPC", Font.BOLD, 32));
		lblEntropyCardCreator.setBounds(22, 11, 223, 29);
		contentPane.add(lblEntropyCardCreator);
		
		
		final JLabel lblMessageArea = new JLabel("...");
		lblMessageArea.setBounds(58, 52, 164, 17);
		contentPane.add(lblMessageArea);
		

		JLabel lblCardType = new JLabel("Card type:");
		lblCardType.setBounds(194, 112, 61, 14);
		contentPane.add(lblCardType);
		
		

	//	String[] cardTypes = {"Creature", "Structure", "Resource", "Resource Structure", "Spell", "Effect", "Zone Effect"};
		final Choice cardTypeChoice = new Choice();
		cardTypeChoice.add("Creature");
		cardTypeChoice.add("Structure");
		cardTypeChoice.add("Resource");
		cardTypeChoice.add("Resource Structure");
		cardTypeChoice.add("Spell");
		cardTypeChoice.add("Effect");
		cardTypeChoice.add("Zone Effect");
		cardTypeChoice.setBounds(165, 137, 125, 20);
		contentPane.add(cardTypeChoice);
		
		final Choice cardRaceChoice = new Choice();
		cardRaceChoice.add("Orc");
		cardRaceChoice.add("Crawnid");
		cardRaceChoice.setBounds(298, 137, 96, 20);
		contentPane.add(cardRaceChoice);
		
		final Choice cardRarityChoice = new Choice();
		cardRarityChoice.add("Common");
		cardRarityChoice.add("Uncommon");
		cardRarityChoice.add("Rare");
		cardRarityChoice.add("Legendary");
		cardRarityChoice.setBounds(308, 193, 96, 20);
		contentPane.add(cardRarityChoice);
		
		JButton btnNewButton = new JButton("Add card");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (!DatabaseManager.GetInstance().CreateCard(txfTitle.getText(), cardRaceChoice.getSelectedIndex(), cardTypeChoice.getSelectedIndex(), cardRarityChoice.getSelectedIndex(), Integer.parseInt(txfRaceCost.getText()), //TODO: Make anycost a thing and differentiate
									Integer.parseInt(txfCost2.getText()), Integer.parseInt(txfStrength.getText()), 
									Integer.parseInt(txfIntelligence.getText()), Integer.parseInt(txfVitality.getText())
									))
				{
					lblMessageArea.setText("An error occoured!");
				}
				else
				{
					lblMessageArea.setText("Card created!");
				}
					
				
			}
		});
		btnNewButton.setBounds(78, 333, 89, 23);
		contentPane.add(btnNewButton);
		
		JButton btnCreateList = new JButton("Create texture list");
		btnCreateList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				List<String> textureList = DatabaseManager.GetInstance().GenerateTextures();
				FileWriter outFile;
				try {
					outFile = new FileWriter("TextureList.etxl");
					PrintWriter out = new PrintWriter(outFile);
					for (String s : textureList)
					{
						out.println(s);
					}
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				lblMessageArea.setText("Texture list created!");
				             
			}
		});
		btnCreateList.setBounds(194, 333, 139, 23);
		contentPane.add(btnCreateList);
		
		JLabel lblCardRace = new JLabel("Card race:");
		lblCardRace.setBounds(313, 112, 61, 14);
		contentPane.add(lblCardRace);
		
		JLabel lblCost3 = new JLabel("Cost 3:");
		lblCost3.setBounds(259, 168, 72, 14);
		contentPane.add(lblCost3);
		
		txfCost3 = new JTextField();
		txfCost3.setText("0");
		txfCost3.setColumns(10);
		txfCost3.setBounds(259, 190, 31, 20);
		contentPane.add(txfCost3);
		
		Choice cost2race = new Choice();
		cost2race.add("Orc");
		cost2race.add("Crwn");
		cost2race.add("Humn");
		cost2race.add("Undd");
		cost2race.setBounds(194, 227, 47, 20);
		contentPane.add(cost2race);
		
		Choice cost3race = new Choice();
		cost3race.add("Crwn");
		cost3race.add("Humn");
		cost3race.add("Orc");
		cost3race.add("Undd");
		cost3race.setBounds(252, 227, 49, 20);
		contentPane.add(cost3race);
		
		JLabel lblIntelligence = new JLabel("Intelligence:");
		lblIntelligence.setBounds(220, 271, 81, 14);
		contentPane.add(lblIntelligence);
		
		txfIntelligence = new JTextField();
		txfIntelligence.setText("0");
		txfIntelligence.setColumns(10);
		txfIntelligence.setBounds(230, 291, 31, 20);
		contentPane.add(txfIntelligence);
		
		
		
		JLabel lblRarity = new JLabel("Rarity:");
		lblRarity.setBounds(333, 168, 61, 14);
		contentPane.add(lblRarity);
		
	
		
		
		
	
		
	}
}
