package Calculs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Trouve_chemin {
	
	static double temps = 0.;
	
	public static double get_temps() {
		return temps;
	}

	public static String get_reseau_sncf(String type) {
		if (type.hashCode()=="intercite".hashCode()) {
			return "intercite";
		} else if (type.hashCode()=="ter".hashCode()) {
			return "ter";
		} else {
			return "transilien";
		}
	}

	public static double get_coord_x(String id, String reseau) {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		double x=0.;

		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();

			if (reseau.hashCode()=="ratp".hashCode()) {
				ResultSet RSresult = stmt.executeQuery("SELECT x FROM ratp_arrets WHERE identifiant =\""+id+"\"");
				x = Double.parseDouble(RSresult.getString(1));
			} else {
				ResultSet RSresult = stmt.executeQuery("SELECT long FROM "+reseau+"_stops WHERE id_arret =\""+id+"\"");
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


	public static double get_coord_y(String id,String reseau) {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		double y=0.;

		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();

			if (reseau.hashCode()=="ratp".hashCode()) {
				ResultSet RSresult = stmt.executeQuery("SELECT y FROM ratp_arrets WHERE identifiant =\""+id+"\"");
				y = Double.parseDouble(RSresult.getString(1));
			} else {
				ResultSet RSresult = stmt.executeQuery("SELECT lat FROM "+reseau+"_stops WHERE id_arret =\""+id+"\"");
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



	public static String[][] chemin(String depart, String reseau_dep, String arrivee, String reseau_arr, int crit_op, String transport_dispo, String reseau_dispo) {
		String ligne_depart ="";
		int sens = 1;

		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			if (reseau_dep.hashCode()=="ratp".hashCode()) {
				ResultSet RSdep = stmt.executeQuery("SELECT ligne, sens1, sens2 FROM ratp_lignes WHERE identifiant =\""+depart+"\" AND type <> \"bus\"");
				if (RSdep.next()) {
					ligne_depart = RSdep.getString(1);
					if (RSdep.getString(2).hashCode()=="-1".hashCode()){
						sens = 2;
						if (RSdep.getString(3).hashCode()=="-1".hashCode()) {
							sens = 0;
						}
					}
				}
			} else if(reseau_dep.hashCode()=="transilien".hashCode()) {
				String type_dispo = "(";
				int comp = 0;
				if (transport_dispo.contains("tram")) {
					type_dispo = type_dispo+"\"0\"";
					comp++;
				}
				if (transport_dispo.contains("rer")) {
					if (comp>0){
						type_dispo = type_dispo+",";
					}
					type_dispo = type_dispo+"\"2\"";
					comp++;
				}
				if (transport_dispo.contains("bus")) {
					if (comp>0){
						type_dispo = type_dispo+",";
					}
					type_dispo = type_dispo+"\"3\"";
					comp++;
				}
				type_dispo = type_dispo+")";

				ResultSet RSdep = stmt.executeQuery("SELECT seq.id_ligne" +
						" FROM transilien_seq seq, transilien_stops stops, transilien_routes routes" +
						" WHERE seq.id_station = stops.id_arret" +
						" AND stops.station_parent = \""+depart+"\"" +
						" AND routes.id_ligne = seq.id_ligne" +
						" AND routes.type_ligne IN "+type_dispo);			
				ligne_depart = RSdep.getString(1);
			} else {
				ResultSet RSdep = stmt.executeQuery("SELECT seq.id_ligne" +
						" FROM "+reseau_dep+"_seq seq, "+reseau_dep+"_stops stops" +
						" WHERE seq.id_station = stops.id_arret" +
						" AND stops.station_parent = \""+depart+"\"");			
				ligne_depart = RSdep.getString(1);
			}

		}
		catch(Exception e) 
		{ System.err.println( e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close();
				con.close();
			} 
		catch(Exception e){}

		if (ligne_depart.hashCode()=="".hashCode()) {
			System.out.println("ERREUR : pas de ligne de départ");
			String[][] result_erreur = new String[1][1];
			result_erreur[0][0] = "erreur";
			return result_erreur;
		}
		if (sens == 0) {
			System.out.println("ERREUR : problème de sens de ligne");
			String[][] result_erreur = new String[1][1];
			result_erreur[0][0] = "erreur";
			return result_erreur;
		}




		long t0 =  System.currentTimeMillis();

		Noeud N1 = new Noeud(depart,ligne_depart,sens,reseau_dep);
		
		Noeud[] v = new Noeud[20000];
		v[0]=N1;
		int place = 1;
		int place_avant = 0;
		int tours = 0;

		double depart_x = get_coord_x(depart,reseau_dep);
		double depart_y = get_coord_y(depart,reseau_dep);
		double arrivee_x = get_coord_x(arrivee,reseau_arr);
		double arrivee_y = get_coord_y(arrivee,reseau_arr);

		double centre_x = (depart_x+arrivee_x)/2.;
		double centre_y = (depart_y+arrivee_y)/2.;
		System.out.println("distance dep-arr:"+Math.sqrt((depart_x-arrivee_x)*(depart_x-arrivee_x)+(depart_y-arrivee_y)*(depart_y-arrivee_y)));
		double rayon = Math.sqrt((depart_x-arrivee_x)*(depart_x-arrivee_x)+(depart_y-arrivee_y)*(depart_y-arrivee_y));
		if (rayon < 0.007) {
			rayon = 8.*rayon;
		} else if (rayon < 0.01) {
			rayon = 4.*rayon;
		}else if (rayon < 0.04) {
			rayon = 1.5*rayon;
		} else {
			rayon = 0.75*rayon;
		}

		while ((place<10000)&(tours<10)) {
			int place_temp = place;

			for (int i = place_avant; i<place;i++) {
				if (v[i].reseau.hashCode()=="ratp".hashCode()) {
					place = Chemin_ratp.add_ligne(v[i], v, place,centre_x,centre_y,rayon,crit_op);
					place = Chemin_ratp.add_correspondance(v[i], v, place,crit_op,reseau_dispo,transport_dispo);
				} else {
					place = Chemin_sncf.add_ligne(v[i], v, place,centre_x,centre_y,rayon,crit_op);
					place = Chemin_sncf.add_correspondance(v[i], v, place,crit_op,reseau_dispo,transport_dispo);
				}
			}
			place_avant = place_temp;
			tours++;
		}

		System.out.println("temps1:"+(System.currentTimeMillis()-t0));

		Graphe g = new Graphe(v[0]);
		String[][] result = g.dijkstra(arrivee,place);
		System.out.println("temps2"+(System.currentTimeMillis()-t0));
		for (int i = 0; i< result[0].length;i++) {
			System.out.println("Station "+result[0][i]+" de la ligne "+result[1][i]+" d'indentifiant "+result[2][i]);
		}
		temps = g.get_temps();
		return result;
	}




}
