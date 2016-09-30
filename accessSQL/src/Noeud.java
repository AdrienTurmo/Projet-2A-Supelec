import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Noeud {
	String ident; // identifiant de l'arret
	String ligne; // identifiant de la ligne
	String reseau; // reseau = "ratp" ou "ransilien" ou "ter" ou "intercite"
	int sens = 1; // pour le réseau ratp
	Noeud[] fils = new Noeud[200]; // vecteur des stations adjacentes (même ligne et correspondances)
	double[] poids = new double[200]; // distance aux stations adjacentes
	int nb_fils; // nombre de stations adjacentes

	/**
	 * Constructeurs
	 */
	public Noeud(String nom) {
		this.ident = nom;
		this.nb_fils = 0;
		this.ligne ="";
	}

	public Noeud(String nom, String ligne, String reseau) {
		this.ident = nom;
		this.nb_fils = 0;
		this.ligne = ligne;
		this.reseau = reseau;
		this.sens=1;
	}

	public Noeud(String nom, String ligne, int sens, String reseau) {
		this.ident = nom;
		this.nb_fils = 0;
		this.ligne = ligne;
		this.sens = sens;
		this.reseau = reseau;
	}
	
	
	/**
	 * extraction des attributs
	 */
	public String get_ident() {
		return this.ident;
	}
	
	public String get_ligne() {
		return this.ligne;
	}
	
	public String get_reseau() {
		return this.reseau;
	}

	public int get_sens() {
		return this.sens;
	}
	
	public Noeud[] get_fils() {
		return this.fils;
	}
	
	public double[] get_poids() {
		return this.poids;
	}

	public int get_nb_fils() {
		return this.nb_fils;
	}
	
	
	/**
	 * ajout d'un Noeud fils
	 */
	public void add_fils(Noeud f,String temps) {
		this.fils[this.nb_fils]=f;
		this.poids[this.nb_fils]=Double.parseDouble(temps);
		this.nb_fils++;
	}

	public void add_fils(Noeud f,double temps) {
		this.fils[this.nb_fils]=f;
		this.poids[this.nb_fils]=temps;
		this.nb_fils++;
	}

	
	/**
	 * petmet de savoir si la ligne est une des lignes à boucle ou a embranchement
	 */
	public boolean est_ligne_exception() {
		return (this.ligne.hashCode() == "10".hashCode())||(this.ligne.hashCode() == "7".hashCode())||(this.ligne.hashCode() == "13".hashCode())||(this.ligne.hashCode() == "7B".hashCode())||(this.ligne.hashCode() == "A".hashCode())||(this.ligne.hashCode() == "B".hashCode());
	}

	/**
	 * renvoit le sens opposé
	 */
	public int autre_sens() {
		if (this.sens == 1) {
			return 2;
		} else {
			return 1;
		}
	}

	
	/**
	 * permet d'avoir la coordonnée x d'un noeud
	 */
	public double get_coord_x() {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		double x=0.;

		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();

			if (this.reseau.hashCode()=="ratp".hashCode()){
				ResultSet RSresult = stmt.executeQuery("SELECT x FROM ratp_arrets WHERE identifiant =\""+this.ident+"\"");
				x = Double.parseDouble(RSresult.getString(1));
			} else {
				ResultSet RSresult = stmt.executeQuery("SELECT long FROM "+this.reseau+"_stops WHERE id_arret = \""+this.ident+"\"");
				x = Double.parseDouble(RSresult.getString(1));
			}
			
			
		}catch(Exception e) 
		{ System.err.println( e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close();
				con.close();
			} 
		catch(Exception e){}

		return x;
	}


	/**
	 * permet d'avoir la coordonnée y d'un noeud
	 */
	public double get_coord_y() {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		double y=0.;

		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();

			if (this.reseau.hashCode()=="ratp".hashCode()){
				ResultSet RSresult = stmt.executeQuery("SELECT y FROM ratp_arrets WHERE identifiant =\""+this.ident+"\"");
				y = Double.parseDouble(RSresult.getString(1));
			} else {
				ResultSet RSresult = stmt.executeQuery("SELECT lat FROM "+this.reseau+"_stops WHERE id_arret = \""+this.ident+"\"");
				y = Double.parseDouble(RSresult.getString(1));
			}

		}catch(Exception e) 
		{ System.err.println( e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close();
				con.close();
			} 
		catch(Exception e){}

		return y;			
	}

	/**
	 * calcul la distance entre deux Noeuds
	 */
	public double calcul_distance(Noeud n) {
		double x = this.get_coord_x();
		double y = this.get_coord_y();
		double nx = n.get_coord_x();
		double ny = n.get_coord_y();

		return Math.sqrt((x-nx)*(x-nx)+(y-ny)*(y-ny));
	}

	/**
	 *  indique si un noeud est dans le cercle de délimitation
	 */
	public boolean est_dans_cercle(double centre_x,double centre_y,double rayon){
		double x = this.get_coord_x();
		double y = this.get_coord_y();
		double d = Math.sqrt((x-centre_x)*(x-centre_x)+(y-centre_y)*(y-centre_y));
		return d<rayon;
	}

	
	/**
	 * trouve l'indice (la place) du noeud dans un vecteur de noeuds
	 */
	public int indice(Noeud[] v,String ligne,int sens) {
		int indice = -1;
		for (int i = 0; i < v.length ; i++) {
			if ((v[i] != null)) {
				if ((v[i].get_ident().hashCode() == this.get_ident().hashCode())&(v[i].ligne.hashCode() == ligne.hashCode())&(v[i].sens == sens)&(v[i].reseau==this.reseau)) {
					indice = i;
					return i;
				}
			}
		}
		return indice;
	}

	public int indice(Noeud[] v,String ligne) {
		int indice = -1;
		for (int i = 0; i < v.length ; i++) {
			if ((v[i] != null)) {
				if ((v[i].get_ident().hashCode() == this.get_ident().hashCode())&(v[i].ligne.hashCode() == ligne.hashCode())&(v[i].reseau==this.reseau)) {
					indice = i;
					return i;
				}
			}
		}
		return indice;
	}

	
	/**
	 * dit si oui ou nom le noeud se trouve dans un vecteur de noeud
	 */
	public boolean est_dans (Noeud[] v,int sens) {
		boolean result = false;
		for (int i = 0; i< v.length; i++) {
			if (v[i]!=null) {
				result = (v[i].ident.hashCode() == this.ident.hashCode())&(v[i].ligne.hashCode() == this.ligne.hashCode())&(v[i].sens == sens)&(v[i].reseau==this.reseau);
				if (result) {
					break;
				}
			}
		}
		return result;
	}
	
	public boolean est_dans (Noeud[] v) {
		boolean result = false;
		for (int i = 0; i< v.length; i++) {
			if (v[i]!=null) {
				result = (v[i].ident.hashCode() == this.ident.hashCode())&(v[i].ligne.hashCode() == this.ligne.hashCode())&(v[i].reseau==this.reseau);
				if (result) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * trouve le nom de la station associé à l'identifiant
	 */
	public String print_station() {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		String result = "";

		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			
			if (this.reseau.hashCode()=="ratp".hashCode()) {
				ResultSet RSresult = stmt.executeQuery("SELECT nom FROM ratp_arrets WHERE identifiant =\""+this.ident+"\"");
				result = RSresult.getString(1);
			} else {
				ResultSet RSresult = stmt.executeQuery("SELECT nom FROM "+this.reseau+"_stops WHERE id_arret =\""+this.ident+"\"");
				result = RSresult.getString(1);
			}
			
			

		}catch(Exception e) 
		{ System.err.println( e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close();
				con.close();
			} 
		catch(Exception e){}

		return result;
	}




}
