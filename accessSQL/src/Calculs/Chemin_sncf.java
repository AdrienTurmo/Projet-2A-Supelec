package Calculs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Chemin_sncf {

	public static double descide_dist(int crit_op, Noeud v, Noeud n, String temps_next){
		double dist = 0.;
		if (crit_op == 1) {
			dist = 0.001;
		} else if (crit_op == 2) {
			dist = Double.parseDouble(temps_next);
		} else if (crit_op == 3) {
			dist = v.calcul_distance(n);
		} else {
			System.out.println("ERREUR : critère d'optimisation");
		}
		return dist;
	}


	public static int add_ligne (Noeud n, Noeud[] v, int place, double centre_x, double centre_y, double rayon, int crit_op) {

		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;

		try {

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			stmt2 = con.createStatement();

			//on récupère les infos sur n
			ResultSet RSn = stmt.executeQuery("SELECT seq.ordre, seq.temps_next " +
					" FROM "+n.reseau+"_seq seq, "+n.reseau+"_stops stops " +
					" WHERE seq.id_station = stops.id_arret " +
					" AND stops.station_parent = \""+n.get_ident()+"\"" +
					" AND seq.id_ligne = \""+n.ligne+"\"");

			if (RSn.getString(2).hashCode()!="0".hashCode()) {
				ResultSet RSfils1 = stmt1.executeQuery("SELECT stops.station_parent" +
						" FROM "+n.reseau+"_seq seq, "+n.reseau+"_stops stops" +
						" WHERE seq.id_ligne = \""+n.ligne+"\"" +
						" AND seq.ordre = \""+(Integer.parseInt(RSn.getString(1))+1)+"\"" +
						" AND seq.id_station = stops.id_arret");			
				if (RSfils1.next()){
					Noeud fils1 = new Noeud(RSfils1.getString(1),n.ligne,n.reseau);
					if (fils1.est_dans_cercle(centre_x, centre_y, rayon)) {
						int ind_dans_v = fils1.indice(v, fils1.ligne);
						double dist = descide_dist(crit_op, fils1, n, RSn.getString(2));
						if (ind_dans_v == -1){
							v[place]=fils1;
							n.add_fils(v[place], dist);
							v[place].add_fils(n, dist);
							place++;
							place = add_ligne(v[place-1],v,place,centre_x,centre_y,rayon,crit_op);
						} else if (!v[ind_dans_v].est_dans(n.fils)){
							n.add_fils(v[ind_dans_v], dist);
							v[ind_dans_v].add_fils(n, dist);
						}						
					}					
				}				
			}
			if (RSn.getString(1).hashCode()!="0".hashCode()) {
				ResultSet RSfils2 = stmt2.executeQuery("SELECT stops.station_parent, seq.temps_next" +
						" FROM "+n.reseau+"_seq seq, "+n.reseau+"_stops stops" +
						" WHERE seq.id_ligne = \""+n.ligne+"\"" +
						" AND seq.ordre = \""+(Integer.parseInt(RSn.getString(1))-1)+"\"" +
						" AND seq.id_station = stops.id_arret");			
				if (RSfils2.next()){
					Noeud fils1 = new Noeud(RSfils2.getString(1),n.ligne,n.reseau);
					if (fils1.est_dans_cercle(centre_x, centre_y, rayon)) {
						int ind_dans_v = fils1.indice(v, fils1.ligne);
						double dist = descide_dist(crit_op, fils1, n, RSfils2.getString(2));
						if (ind_dans_v == -1){
							v[place]=fils1;
							n.add_fils(v[place], dist);
							v[place].add_fils(n, dist);
							place++;
							place = add_ligne(v[place-1],v,place,centre_x,centre_y,rayon,crit_op);
						} else if (!v[ind_dans_v].est_dans(n.fils)){
							n.add_fils(v[ind_dans_v], dist);
							v[ind_dans_v].add_fils(n, dist);
						}						
					}					
				}				
			}

			return place;
		} 
		// gestion des cas d'erreur
		catch(Exception e) 
		{ System.err.println( e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close();
				stmt1.close();
				stmt2.close();

				con.close();
			} 
		catch(Exception e){}
		return place;
	}


	public static int add_correspondance (Noeud n, Noeud[] v, int place, int crit_op, String reseau_dispo, String transport_dispo) {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;

		Statement stmt0 = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		Statement stmt4 = null;

		try {

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");

			stmt0 = con.createStatement();
			stmt1 = con.createStatement();
			stmt2 = con.createStatement();

			//RScoress1 : les correspondances de même type de transport de lignes différentes
			//RScoress2 et 3 : correspondances faites grace à la table correspondance (entre type de transports différents)

			ResultSet RScoress1;

			if (n.reseau.hashCode() == "transilien".hashCode()) {
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

				RScoress1 = stmt0.executeQuery("SELECT seq.id_ligne" +
						" FROM transilien_seq seq, transilien_stops stops, transilien_routes routes" +
						" WHERE seq.id_station = stops.id_arret" +
						" AND stops.station_parent = \""+n.get_ident()+"\"" +
						" AND seq.id_ligne <> \""+n.ligne+"\"" +
						" AND routes.id_ligne = seq.id_ligne" +
						" AND routes.type_ligne IN "+type_dispo);
			} else {
				RScoress1 = stmt0.executeQuery("SELECT seq.id_ligne" +
						" FROM "+n.reseau+"_seq seq, "+n.reseau+"_stops stops" +
						" WHERE seq.id_station = stops.id_arret" +
						" AND stops.station_parent = \""+n.get_ident()+"\"" +
						" AND seq.id_ligne <> \""+n.ligne+"\"");
			}


			ResultSet RScoress2 = stmt1.executeQuery("SELECT ident2, type2, temps, reseau FROM Correspondances WHERE ident1 = \""+n.get_ident()+"\" AND (type2 <> \"bus\" OR reseau <> \"ratp\") AND (type2 <> \"bus\" OR reseau <> \"melange\") AND (reseau IN "+reseau_dispo+") AND ( type2 IN "+transport_dispo+")");
			ResultSet RScoress3 = stmt2.executeQuery("SELECT ident1, type1, temps, reseau FROM Correspondances WHERE ident2 = \""+n.get_ident()+"\" AND (type1 <> \"bus\" OR reseau <> \"ratp\") AND (reseau IN "+reseau_dispo+") AND ( type1 IN "+transport_dispo+")");

			//corres même reseau
			while (RScoress1.next()) {
				Noeud Noe_corr = new Noeud(n.get_ident(),RScoress1.getString(1),n.reseau);
				int ind_dans_v = Noe_corr.indice(v, Noe_corr.ligne);
				if (ind_dans_v == -1) {
					v[place] = Noe_corr;
					if (crit_op == 1) {
						v[place].add_fils(n, 1000.);
						n.add_fils(v[place], 1000.);
					} else if (crit_op == 2) {
						v[place].add_fils(n, 5.);
						n.add_fils(v[place], 5.);
					} else if (crit_op == 3) {
						v[place].add_fils(n, 0.0000001);
						n.add_fils(v[place], 0.0000001);
					} else {
						System.out.println("ERREUR : critère d'optimisation");
					}
					place++;
				} else if (!Noe_corr.est_dans(n.fils)) {
					if (crit_op == 1) {
						v[ind_dans_v].add_fils(n, 1000.);
						n.add_fils(v[ind_dans_v], 1000.);
					} else if (crit_op == 2) {
						v[ind_dans_v].add_fils(n, 5.);
						n.add_fils(v[ind_dans_v], 5.);
					} else if (crit_op == 3) {
						v[ind_dans_v].add_fils(n, 0.0000001);
						n.add_fils(v[ind_dans_v], 0.0000001);
					} else {
						System.out.println("ERREUR : critère d'optimisation");
					}
				}
			}

			//corres autre reseau 1
			while (RScoress2.next()) {
				String id = RScoress2.getString(1);
				String type = RScoress2.getString(2);
				String temps = RScoress2.getString(3);
				String reseau = RScoress2.getString(4);
				stmt3 = con.createStatement();

				if (reseau.hashCode()=="melange".hashCode()) {
					ResultSet Noe_corress_RS = stmt3.executeQuery("SELECT ligne, sens1, sens2 FROM ratp_lignes WHERE identifiant =\""+id+"\" AND type = \""+type+"\"");
					int sens = 1;
					if (Noe_corress_RS.getString(2).hashCode()=="-1".hashCode()) {
						sens = 2;
					} else if (Noe_corress_RS.getString(3).hashCode()=="-1".hashCode()) {
						System.out.println("probleme de correspondance sncf/ratp");
					}

					while (Noe_corress_RS.next()) {
						Noeud Noe_corr = new Noeud(id,Noe_corress_RS.getString(1),sens,"ratp");
						int ind_dans_v = Noe_corr.indice(v, Noe_corr.ligne,Noe_corr.sens);
						if (ind_dans_v == -1) {
							v[place] = Noe_corr;
							if (crit_op == 1) {
								v[place].add_fils(n, 1000.);
								n.add_fils(v[place], 1000.);
							} else if (crit_op == 2) {
								v[place].add_fils(n, temps);
								n.add_fils(v[place], temps);
							} else if (crit_op == 3) {
								double dist = v[place].calcul_distance(n);
								v[place].add_fils(n, dist);
								n.add_fils(v[place], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
							place++;
						} else if (!Noe_corr.est_dans(n.fils,Noe_corr.sens)) {
							if (crit_op == 1) {
								v[ind_dans_v].add_fils(n, 1000.);
								n.add_fils(v[ind_dans_v], 1000.);
							} else if (crit_op == 2) {
								v[ind_dans_v].add_fils(n, temps);
								n.add_fils(v[ind_dans_v], temps);
							} else if (crit_op == 3) {
								double dist = v[ind_dans_v].calcul_distance(n);
								v[ind_dans_v].add_fils(n, dist);
								n.add_fils(v[ind_dans_v], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						}
					}
					stmt3.close();
				} else {
					ResultSet Noe_corress_RS;

					if (Trouve_chemin.get_reseau_sncf(type).hashCode()=="transilien".hashCode()) {					
						String type_trans = "\"0\"";
						if (type.hashCode()=="tram".hashCode()) {
							type_trans = "\"0\"";
						} else if (type.hashCode()=="rer".hashCode()) {
							type_trans = "\"2\"";
						} else if (type.hashCode()=="bus".hashCode()) {
							type_trans = "\"3\"";
						}

						Noe_corress_RS = stmt3.executeQuery("SELECT seq.id_ligne" +
								" FROM transilien_seq seq, transilien_stops stops, transilien_routes routes" +
								" WHERE seq.id_station = stops.id_arret" +
								" AND stops.station_parent = \""+id+"\"" +
								" AND routes.id_ligne = seq.id_ligne" +
								" AND routes.type_ligne IN ("+type_trans+")");
					} else {
						Noe_corress_RS = stmt3.executeQuery("SELECT seq.id_ligne" +
								" FROM "+Trouve_chemin.get_reseau_sncf(type)+"_seq seq, "+Trouve_chemin.get_reseau_sncf(type)+"_stops stops" +
								" WHERE seq.id_station = stops.id_arret" +
								" AND stops.station_parent = \""+id+"\"" +
								" AND seq.id_ligne <> \""+n.ligne+"\"");
					}

					while (Noe_corress_RS.next()) {
						Noeud Noe_corr = new Noeud(id,Noe_corress_RS.getString(1),Trouve_chemin.get_reseau_sncf(type));
						int ind_dans_v = Noe_corr.indice(v, Noe_corr.ligne);
						if (ind_dans_v == -1) {
							v[place] = Noe_corr;
							if (crit_op == 1) {
								v[place].add_fils(n, 1000.);
								n.add_fils(v[place], 1000.);
							} else if (crit_op == 2) {
								v[place].add_fils(n, temps);
								n.add_fils(v[place], temps);
							} else if (crit_op == 3) {
								double dist = v[place].calcul_distance(n);
								v[place].add_fils(n, dist);
								n.add_fils(v[place], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
							place++;
						} else if (!Noe_corr.est_dans(n.fils)) {
							if (crit_op == 1) {
								v[ind_dans_v].add_fils(n, 1000.);
								n.add_fils(v[ind_dans_v], 1000.);
							} else if (crit_op == 2) {
								v[ind_dans_v].add_fils(n, temps);
								n.add_fils(v[ind_dans_v], temps);
							} else if (crit_op == 3) {
								double dist = v[ind_dans_v].calcul_distance(n);
								v[ind_dans_v].add_fils(n, dist);
								n.add_fils(v[ind_dans_v], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						}
					}				
					stmt3.close();
				}
			}

			//corres autre reseau 2
			while (RScoress3.next()) {
				String id = RScoress3.getString(1);
				String type = RScoress3.getString(2);
				String temps = RScoress3.getString(3);
				String reseau = RScoress3.getString(4);
				stmt4 = con.createStatement();

				if (reseau.hashCode()=="melange".hashCode()) {
					ResultSet Noe_corress_RS = stmt4.executeQuery("SELECT ligne, sens1, sens2 FROM ratp_lignes WHERE identifiant =\""+id+"\" AND type = \""+type+"\"");
					int sens = 1;
					if (Noe_corress_RS.getString(2).hashCode()=="-1".hashCode()) {
						sens = 2;
					} else if (Noe_corress_RS.getString(3).hashCode()=="-1".hashCode()) {
						System.out.println("probleme de correspondance sncf/ratp");
					}

					while (Noe_corress_RS.next()) {
						Noeud Noe_corr = new Noeud(id,Noe_corress_RS.getString(1),sens,"ratp");
						int ind_dans_v = Noe_corr.indice(v, Noe_corr.ligne);
						if (ind_dans_v == -1) {
							v[place] = Noe_corr;
							if (crit_op == 1) {
								v[place].add_fils(n, 1000.);
								n.add_fils(v[place], 1000.);
							} else if (crit_op == 2) {
								v[place].add_fils(n, temps);
								n.add_fils(v[place], temps);
							} else if (crit_op == 3) {
								double dist = v[place].calcul_distance(n);
								v[place].add_fils(n, dist);
								n.add_fils(v[place], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
							place++;
						} else if (!Noe_corr.est_dans(n.fils)) {
							if (crit_op == 1) {
								v[ind_dans_v].add_fils(n, 1000.);
								n.add_fils(v[ind_dans_v], 1000.);
							} else if (crit_op == 2) {
								v[ind_dans_v].add_fils(n, temps);
								n.add_fils(v[ind_dans_v], temps);
							} else if (crit_op == 3) {
								double dist = v[ind_dans_v].calcul_distance(n);
								v[ind_dans_v].add_fils(n, dist);
								n.add_fils(v[ind_dans_v], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						}
					}
					stmt4.close();
				} else {
					ResultSet Noe_corress_RS;
					if (Trouve_chemin.get_reseau_sncf(type).hashCode()=="transilien".hashCode()) {					
						String type_trans = "\"0\"";
						if (type.hashCode()=="tram".hashCode()) {
							type_trans = "\"0\"";
						} else if (type.hashCode()=="rer".hashCode()) {
							type_trans = "\"2\"";
						} else if (type.hashCode()=="bus".hashCode()) {
							type_trans = "\"3\"";
						}

						Noe_corress_RS = stmt4.executeQuery("SELECT seq.id_ligne" +
								" FROM transilien_seq seq, transilien_stops stops, transilien_routes routes" +
								" WHERE seq.id_station = stops.id_arret" +
								" AND stops.station_parent = \""+id+"\"" +
								" AND routes.id_ligne = seq.id_ligne" +
								" AND routes.type_ligne IN ("+type_trans+")");
					} else {
						Noe_corress_RS = stmt4.executeQuery("SELECT seq.id_ligne" +
								" FROM "+Trouve_chemin.get_reseau_sncf(type)+"_seq seq, "+Trouve_chemin.get_reseau_sncf(type)+"_stops stops" +
								" WHERE seq.id_station = stops.id_arret" +
								" AND stops.station_parent = \""+id+"\"" +
								" AND seq.id_ligne <> \""+n.ligne+"\"");
					}

					while (Noe_corress_RS.next()) {
						Noeud Noe_corr = new Noeud(id,Noe_corress_RS.getString(1),Trouve_chemin.get_reseau_sncf(type));
						int ind_dans_v = Noe_corr.indice(v, Noe_corr.ligne);
						if (ind_dans_v == -1) {
							v[place] = Noe_corr;
							if (crit_op == 1) {
								v[place].add_fils(n, 1000.);
								n.add_fils(v[place], 1000.);
							} else if (crit_op == 2) {
								v[place].add_fils(n, temps);
								n.add_fils(v[place], temps);
							} else if (crit_op == 3) {
								double dist = v[place].calcul_distance(n);
								v[place].add_fils(n, dist);
								n.add_fils(v[place], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
							place++;
						} else if (!Noe_corr.est_dans(n.fils)) {
							if (crit_op == 1) {
								v[ind_dans_v].add_fils(n, 1000.);
								n.add_fils(v[ind_dans_v], 1000.);
							} else if (crit_op == 2) {
								v[ind_dans_v].add_fils(n, temps);
								n.add_fils(v[ind_dans_v], temps);
							} else if (crit_op == 3) {
								double dist = v[ind_dans_v].calcul_distance(n);
								v[ind_dans_v].add_fils(n, dist);
								n.add_fils(v[ind_dans_v], dist);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						}
					}				
					stmt4.close();
				}			
			}

			return place;
		} 
		// gestion des cas d'erreur
		catch(Exception e) 
		{ System.err.println( e.getMessage()); }
		if (con!=null) 
			try {
				stmt0.close();
				stmt1.close();
				stmt2.close();
				stmt3.close();
				stmt4.close();

				con.close();
			} 
		catch(Exception e){}
		return place;
	}





}
