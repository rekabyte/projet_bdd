package ca.rekabyte.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;

@SuppressWarnings("serial")
public class SceneManager extends JFrame{
	private String url = "jdbc:postgresql://localhost/projet2";
	private String user = "postgres";
	private String password = "admin";

	private final String query1 = 	"SELECT "
									+ "	CONCAT (aa.Prenom, ' ', aa.Nom) Adherent,"
									+ "	ee.Date_emprunt,"
									+ "	ll.Titre,"
									+ "	NOW(),"
									+ "	EXTRACT(DAY FROM COALESCE(ee.Date_retour,NOW())-ee.Date_emprunt) AS DateDifference "
									+ "FROM Emprunte ee, Livre ll, Adherent aa WHERE ee.Livre_id = ll.Livre_id AND aa.No_adherent = ee.No_adherent "
									+ "AND EXTRACT(DAY FROM COALESCE(ee.Date_retour,NOW())-ee.Date_emprunt) >14";

	private final String query2 = 	"SELECT ee.No_adherent, COUNT(ee.No_adherent) Books_borrowed, MAX(aa.Nom), MAX(aa.Prenom) FROM Emprunte ee, Adherent aa "
									+ "WHERE ee.No_adherent = aa.No_adherent "
									+ "GROUP BY ee.No_adherent ";

	private final String query3 = 	"SELECT aa.Nom, aa.Prenom, "
									+ "EXTRACT(YEAR FROM ee.Date_emprunt) yyyy, COUNT(*) Number_of_books "
									+ "FROM Adherent aa LEFT JOIN Emprunte ee ON aa.No_adherent = ee.No_adherent "
									+ "GROUP BY  aa.No_adherent, EXTRACT(YEAR FROM ee.Date_emprunt) "
									+ "ORDER BY EXTRACT(YEAR FROM ee.Date_emprunt)";

	private final String query4 = 	"SELECT aa.Nom, aa.Prenom, ll.Titre, COUNT(*) Borrowings_per_book FROM Emprunte ee, Livre ll, Adherent aa  " +
									"WHERE ee.Livre_id = ll.Livre_id AND aa.No_adherent = ee.No_adherent " +
									"GROUP BY aa.No_adherent, ll.Livre_id " +
									"ORDER BY COUNT(*) DESC ";

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
		question4.setToolTipText("Nombre d’emprunts par livre");


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

		//Pour se connecter a la base de donnees:
		Connection connTest = null;
		String err_msg = "Impossible de se connecter a la DB, les infos saisies sont erronn�es, reessayer.";
		while(connTest == null) {
			url = JOptionPane.showInputDialog("Adresse de la BD: (Generalement de la forme: \"jdbc:postgresql://localhost/[inserer_nom_db_ici]\")");
			user = JOptionPane.showInputDialog("Nom de l'user: (Generalement \"postgres\")");
			password = JOptionPane.showInputDialog("Password de l'user:  ");
			connTest = connect();
			if(connTest == null) {
				JOptionPane.showMessageDialog(null, err_msg, "Informations", JOptionPane.ERROR_MESSAGE);
			}
		}


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
