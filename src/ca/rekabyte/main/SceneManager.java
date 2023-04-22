package ca.rekabyte.main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class SceneManager extends JFrame{

	private final String url = "jdbc:postgresql://localhost/projet";
	private final String user = "postgres";
	private final String password = "admin";

	private final String query1 = 	"SELECT adherent.no_adherent, adherent.nom, adherent.prenom, emprunte.date_retour, COUNT(*) " +
									"FROM adherent JOIN emprunte ON emprunte.no_adherent = adherent.no_adherent " +
									"WHERE emprunte.date_retour < CURRENT_DATE " +
									"GROUP BY adherent.no_adherent, adherent.nom, adherent.prenom, emprunte.date_retour;";
	private final String query2 = 	"SELECT livre.livre_id, livre.titre, COUNT(*) " +
									"FROM livre " +
									"JOIN emprunte ON emprunte.livre_id = livre.livre_id " +
									"GROUP BY livre.livre_id, livre.titre;";
	private final String query3 = 	"SELECT EXTRACT(YEAR FROM date_emprunt), COUNT(*) " +
									"FROM emprunte " +
									"GROUP BY EXTRACT(YEAR FROM date_emprunt);";
	private final String query4 = 	"SELECT adherent.no_adherent, adherent.nom, adherent.prenom, livre.livre_id, livre.titre, commande.date_commande, commande.quantite " +
									"FROM adherent " +
									"JOIN commande ON commande.no_adherent = adherent.no_adherent " +
									"JOIN livre ON commande.livre_id = livre.livre_id " +
									"ORDER BY commande.date_commande;";

	private final String popup_msg = 	"Ce logiciel a été développé afin d'aider les bibliothèques a gérer leurs bases de données" +
										" et leur permettre d'accèder 4 requetes utiles et essentiels en un seul clic.";

	private JButton question1, question2, question3, question4;
	private JMenu menu_fichier, menu_edition, menu_aide;
	private JPanel panel_gauche, panel_centre, panel_haut;
	private JTextArea textArea;
	private JMenuBar menuBar;
	private JMenuItem item_exit, item_effacer, item_infos;
	private JScrollPane scrollPane;
	private JLabel titre;
	
	public SceneManager() {

		//Initialisation des components:
		question1 = new JButton("Question 1");
		question2 = new JButton("Question 2");
		question3 = new JButton("Question 3");
		question4 = new JButton("Question 4");

		menu_fichier = new JMenu("Fichier");
		menu_edition = new JMenu("Effacer");
		menu_aide = new JMenu("Infos");

		item_exit = new JMenuItem("Fermer");
		item_effacer = new JMenuItem("Effacer le contenu");
		item_infos = new JMenuItem("Informations");

		panel_centre = new JPanel();
		panel_gauche = new JPanel();
		panel_haut = new JPanel();
		
		textArea = new JTextArea();
		menuBar = new JMenuBar();
		scrollPane = new JScrollPane(textArea);
		titre = new JLabel("Gestion bibliotheque");

		//Menu Bar config:
		menu_fichier.add(item_exit);
		menu_edition.add(item_effacer);
		menu_aide.add(item_infos);

		menuBar.add(menu_fichier);
		menuBar.add(menu_edition);
		menuBar.add(menu_aide);

		//Text area config:
		textArea.setEditable(true);

		//Alignement horizontal des boutons questions et du titre:
		titre.setFont(titre.getFont().deriveFont(18f));
		titre.setAlignmentX(Component.CENTER_ALIGNMENT);

		question1.setAlignmentX(Component.CENTER_ALIGNMENT);
		question2.setAlignmentX(Component.CENTER_ALIGNMENT);
		question3.setAlignmentX(Component.CENTER_ALIGNMENT);
		question4.setAlignmentX(Component.CENTER_ALIGNMENT);

		//Affichage des infobulles lors du survole des boutons/questions:
		question1.setToolTipText("Quel est le nombre de livres empruntés qui ont des retards éventuels?");
		question2.setToolTipText("Combien de fois chaque livre a-t-il été emprunté ?\n");
		question3.setToolTipText("Nombre de livres empruntés chaque année.");
		question4.setToolTipText("L'auteur plus populaire compte tenu du nombre total de livres empruntés à la bibliothèque?");


		//Parametres du jframe:

		this.setSize(720, 480);
		this.setMinimumSize(new Dimension(550,400));
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Projet - Gestionnaire de la bibliotheque");
		this.setLayout(new BorderLayout());
		this.setJMenuBar(menuBar);
		this.add(panel_gauche, "West");
		this.add(panel_centre, "Center");
		this.add(panel_haut, "North");

		//Config des layouts:
		panel_haut.setBorder(BorderFactory.createRaisedBevelBorder());
		panel_haut.setPreferredSize(new Dimension(0,50));
		panel_gauche.setLayout(new BoxLayout(panel_gauche, BoxLayout.Y_AXIS));
		panel_gauche.setBorder(BorderFactory.createRaisedBevelBorder());
		panel_gauche.setPreferredSize(new Dimension(150,0));
		panel_centre.setBorder(BorderFactory.createRaisedBevelBorder());
		
		//Ajout des components dans les layouts:
		panel_haut.add(Box.createRigidArea(new Dimension(0, 30)));
		panel_haut.add(titre);

		panel_gauche.add(Box.createRigidArea(new Dimension(0, 20)));
		panel_gauche.add(question1);
		panel_gauche.add(Box.createRigidArea(new Dimension(0, 20)));
		panel_gauche.add(question2);
		panel_gauche.add(Box.createRigidArea(new Dimension(0, 20)));
		panel_gauche.add(question3);
		panel_gauche.add(Box.createRigidArea(new Dimension(0, 20)));
		panel_gauche.add(question4);
		panel_gauche.add(Box.createVerticalGlue());

		panel_centre.add(scrollPane, BorderLayout.CENTER);

		//ScrollPane config:
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(400, 400));

		//Les Listeners des menu items:
		item_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});

		item_effacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});

		item_infos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, popup_msg, "Informations", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		//Les listeners des boutons/questions:

		question1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textArea.setText(displayActor(query1));
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		question2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textArea.setText(displayActor(query2));
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		question3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textArea.setText(displayActor(query3));
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		question4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textArea.setText(displayActor(query4));
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});


		//Finition
		ToolTipManager.sharedInstance().setEnabled(true);
		this.pack();
		this.setVisible(true);
	}

	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			System.out.println(e.getStackTrace());
		}
		return conn;
	}

	private String displayActor(String query) throws SQLException {
		try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			String resultat = new String();
			resultat +="On affiche les colonnes suivantes:\t";
			for (int i = 1; i <= columnCount; i++ ) {
				String name = rsmd.getColumnName(i);
				resultat += name +",";
			}
			resultat += "\n";
			while(rs.next()) {
				for (int i = 1; i <= columnCount; i++ ) {
					resultat += rs.getString(rsmd.getColumnName(i)) + ",\t";
				}
				resultat += "\n";
			}
			return resultat;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return new String("Aucun resultat");
	}

}
