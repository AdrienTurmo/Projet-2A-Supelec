
import java.sql.*;


public class Chemin_ratp {



	/**
	 * decide de la distance à mettre entre deux noeuds adjascents
	 */
	public static double descide_dist(int crit_op, Noeud v, Noeud n, String type){
		double dist = 0.;
		if (crit_op == 1) {
			dist = 0.001;
		} else if (crit_op == 2) {
			dist = v.calcul_distance(n);
			if (type.hashCode()=="metro".hashCode()) {
				dist = dist*250;
			} else if (type.hashCode()=="rer".hashCode()) {
				dist = dist*28+1.8;
			} else if (type.hashCode()=="tram".hashCode()) {
				dist = dist*308;
			}
		} else if (crit_op == 3) {
			dist = v.calcul_distance(n);
		} else {
			System.out.println("ERREUR : critère d'optimisation");
		}
		return dist;
	}


	/**
	 * Ajout d'une ligne entière
	 * on part du noeud n, le vecteur v contient les noeud déjà présents dans le graphe
	 * place est la position du curseur dans v (pour remplir le vecteur correctement)
	 * centre_x, centre_y,rayons sont les coordonnées et le rayon du cerle qui limite les stations à un périmètre, permet un resultat rapide pour les petits trajets
	 */	
	public static int add_ligne (Noeud n, Noeud[] v, int place, double centre_x, double centre_y, double rayon, int crit_op) {
		if ((n.ligne.hashCode()=="10".hashCode())||(n.ligne.hashCode()=="7B".hashCode())||(n.ligne.hashCode()=="7".hashCode())||(n.ligne.hashCode()=="13".hashCode())||(n.ligne.hashCode()=="A".hashCode())||(n.ligne.hashCode()=="B".hashCode())) {
			return add_ligne_exeption(n,v,place,centre_x,centre_y,rayon,crit_op,n.sens);
		}
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		Statement stmt4 = null;
		try {

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			stmt2 = con.createStatement();
			stmt3 = con.createStatement();
			stmt4 = con.createStatement();

			//on récupère les infos sur n
			ResultSet RSn = stmt.executeQuery("SELECT sens1, sens2 FROM ratp_lignes WHERE identifiant =\""+n.get_ident()+"\" AND ligne =\""+n.get_ligne()+"\"");
			if ((Integer.parseInt(RSn.getString(1)) != -1)) {
				//sens1
				ResultSet RSv1 = stmt1.executeQuery("SELECT identifiant, sens1, type FROM ratp_lignes WHERE sens1 =\""+(Integer.parseInt(RSn.getString(1))+1)+"\" AND ligne =\""+n.get_ligne()+"\"");		
				ResultSet RSv3 = stmt3.executeQuery("SELECT identifiant, sens1, type FROM ratp_lignes WHERE sens1 =\""+(Integer.parseInt(RSn.getString(1))-1)+"\" AND ligne =\""+n.get_ligne()+"\"");
				if (RSv1.next()){
					Noeud v1 = new Noeud(RSv1.getString(1),n.get_ligne(),n.reseau);
					if ((v1.est_dans_cercle(centre_x, centre_y, rayon))&(Integer.parseInt(RSv1.getString(2))!=-1)) {
						String type = RSv1.getString(3);
						double dist = descide_dist(crit_op, v1, n, type);
						int ind_dans_v = v1.indice(v, n.get_ligne());
						if (ind_dans_v == -1){
							v[place]=v1;
							n.add_fils(v[place], dist);
							v[place].add_fils(n, dist);
							place++;
							place = add_ligne(v[place-1],v,place,centre_x,centre_y,rayon,crit_op);
						} else if (!v[ind_dans_v].est_dans(n.fils,n.sens)){
							n.add_fils(v[ind_dans_v], dist);
							v[ind_dans_v].add_fils(n, dist);
						}						
					}					
				}
				if (RSv3.next()){
					Noeud v3 = new Noeud(RSv3.getString(1),n.get_ligne(),n.reseau);
					if ((v3.est_dans_cercle(centre_x, centre_y, rayon))&(Integer.parseInt(RSv3.getString(2))!=-1)) {
						String type = RSv3.getString(3);
						double dist = descide_dist(crit_op, v3, n, type);
						int ind_dans_v = v3.indice(v, n.get_ligne(),1);
						if (ind_dans_v == -1){
							v[place]=v3;
							n.add_fils(v[place], dist);
							v[place].add_fils(n, dist);
							place++;
							place = add_ligne(v[place-1],v,place,centre_x,centre_y,rayon,crit_op);
						} else if (!v[ind_dans_v].est_dans(n.fils,n.sens)){
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
				stmt3.close();
				stmt4.close();

				con.close();
			} 
		catch(Exception e){}
		return place;
	}


	public static int add_ligne_exeption (Noeud n, Noeud[] v, int place, double centre_x, double centre_y, double rayon, int crit_op, int sens) {
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		Statement stmt4 = null;
		String s = "sens"+sens;
		int sens_bis = (sens==1) ? 2 : 1 ;
		String s_bis = (sens==1) ? "sens2" : "sens1" ;

		try {
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			stmt2 = con.createStatement();
			stmt3 = con.createStatement();
			stmt4 = con.createStatement();

			//on récupère les infos sur n
			ResultSet RSn = stmt.executeQuery("SELECT "+s+", "+s_bis+" FROM ratp_lignes WHERE identifiant =\""+n.get_ident()+"\" AND ligne =\""+n.ligne+"\"");

			if ((n.ligne.hashCode()=="10".hashCode())||(n.ligne.hashCode()=="7B".hashCode())) {
				if ((Integer.parseInt(RSn.getString(1)) != -1)) {
					ResultSet RSv1 = stmt1.executeQuery("SELECT identifiant, "+s+", type FROM ratp_lignes WHERE "+s+" =\""+(Integer.parseInt(RSn.getString(1))+1)+"\" AND ligne =\""+n.ligne+"\"");		
					if (RSv1.next()){
						Noeud v1 = new Noeud(RSv1.getString(1),n.ligne,n.sens,n.reseau);
						if ((v1.est_dans_cercle(centre_x, centre_y, rayon))&(Integer.parseInt(RSv1.getString(2))!=-1)) {
							String type = RSv1.getString(3);
							double dist = descide_dist(crit_op, v1, n, type);
							int ind_dans_v = v1.indice(v, n.ligne,n.sens);
							if (ind_dans_v == -1){
								v[place]=v1;
								n.add_fils(v[place], dist);
								place++;
								place = add_ligne_exeption(v[place-1],v,place,centre_x,centre_y,rayon,crit_op,sens);
							} else if (!v[ind_dans_v].est_dans(n.fils,n.sens)){
								n.add_fils(v[ind_dans_v], dist);
							}						
						}					
					}
				}
				if ((Integer.parseInt(RSn.getString(2)) != -1)) {
					Noeud n_bis = new Noeud(n.get_ident(),n.ligne,n.autre_sens(),n.reseau);
					if (n_bis.est_dans_cercle(centre_x, centre_y, rayon)) {
						int ind_dans_v = n_bis.indice(v, n_bis.ligne,n.autre_sens());
						if (ind_dans_v == -1){
							v[place]=n_bis;
							if (crit_op == 1) {
								v[place].add_fils(n, 1000.);
								n.add_fils(v[place], 1000.);
							} else if (crit_op == 2) {
								v[place].add_fils(n, 4.);
								n.add_fils(v[place], 4.);
							} else if (crit_op == 3) {
								v[place].add_fils(n, 0.0000001);
								n.add_fils(v[place], 0.0000001);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
							place++;
							place = add_ligne_exeption(v[place-1],v,place,centre_x,centre_y,rayon,crit_op,v[place-1].sens);
						} else if (!v[ind_dans_v].est_dans(n.fils,n.autre_sens())){
							if (crit_op == 1) {
								v[ind_dans_v].add_fils(n, 1000.);
								n.add_fils(v[ind_dans_v], 1000.);
							} else if (crit_op == 2) {
								v[ind_dans_v].add_fils(n, 4.);
								n.add_fils(v[ind_dans_v], 4.);
							} else if (crit_op == 3) {
								v[ind_dans_v].add_fils(n, 0.0000001);
								n.add_fils(v[ind_dans_v], 0.0000001);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						}											
					}
				} 
			} else {
				if ((Integer.parseInt(RSn.getString(1)) != -1)) {
					ResultSet RSv1 = stmt1.executeQuery("SELECT identifiant, "+s+", type FROM ratp_lignes WHERE "+s+" =\""+(Integer.parseInt(RSn.getString(1))+1)+"\" AND ligne =\""+n.ligne+"\"");		
					ResultSet RSv3 = stmt3.executeQuery("SELECT identifiant, "+s+", type FROM ratp_lignes WHERE "+s+" =\""+(Integer.parseInt(RSn.getString(1))-1)+"\" AND ligne =\""+n.ligne+"\"");
					if (RSv1.next()){
						Noeud v1 = new Noeud(RSv1.getString(1),n.ligne,sens,n.reseau);
						if ((v1.est_dans_cercle(centre_x, centre_y, rayon))&(Integer.parseInt(RSv1.getString(2))!=-1)) {
							String type = RSv1.getString(3);
							double dist = descide_dist(crit_op, v1, n, type);
							int ind_dans_v = v1.indice(v, n.ligne,sens);
							if (ind_dans_v == -1){
								v[place]=v1;
								n.add_fils(v[place], dist);
								v[place].add_fils(n, dist);
								place++;
								place = add_ligne_exeption(v[place-1],v,place,centre_x,centre_y,rayon,crit_op,sens);
							} else if (!v[ind_dans_v].est_dans(n.fils,sens)){
								n.add_fils(v[ind_dans_v], dist);
								v[ind_dans_v].add_fils(n, dist);
							}						
						}					
					}
					if (RSv3.next()){
						Noeud v3 = new Noeud(RSv3.getString(1),n.ligne,n.reseau);
						if ((v3.est_dans_cercle(centre_x, centre_y, rayon))&(Integer.parseInt(RSv3.getString(2))!=-1)) {
							String type = RSv3.getString(3);
							double dist = descide_dist(crit_op, v3, n, type);
							int ind_dans_v = v3.indice(v, n.ligne,sens);
							if (ind_dans_v == -1){
								v[place]=v3;
								n.add_fils(v[place], dist);
								v[place].add_fils(n, dist);
								place++;
								place = add_ligne_exeption(v[place-1],v,place,centre_x,centre_y,rayon,crit_op,sens);
							} else if (!v[ind_dans_v].est_dans(n.fils,sens)){
								n.add_fils(v[ind_dans_v], dist);
								v[ind_dans_v].add_fils(n, dist);
							}						
						}					
					}
					if ((Integer.parseInt(RSn.getString(2)) != -1)) {
						Noeud n_bis = new Noeud(n.get_ident(),n.ligne,sens_bis,n.reseau);
						if (n_bis.est_dans_cercle(centre_x, centre_y, rayon)) {
							int ind_dans_v = n_bis.indice(v, n_bis.ligne,sens_bis);
							if (ind_dans_v == -1){
								v[place]=n_bis;
								if (crit_op == 1) {
									v[place].add_fils(n, 1000.);
									n.add_fils(v[place], 1000.);
								} else if (crit_op == 2) {
									v[place].add_fils(n, 4.);
									n.add_fils(v[place], 4.);
								} else if (crit_op == 3) {
									v[place].add_fils(n, 0.0000001);
									n.add_fils(v[place], 0.0000001);
								} else {
									System.out.println("ERREUR : critère d'optimisation");
								}
								place++;
								place = add_ligne_exeption(v[place-1],v,place,centre_x,centre_y,rayon,crit_op,sens_bis);
							} else if (!v[ind_dans_v].est_dans(n.fils,sens_bis)){
								if (crit_op == 1) {
									v[ind_dans_v].add_fils(n, 1000.);
									n.add_fils(v[ind_dans_v], 1000.);
								} else if (crit_op == 2) {
									v[ind_dans_v].add_fils(n, 4.);
									n.add_fils(v[ind_dans_v], 4.);
								} else if (crit_op == 3) {
									v[ind_dans_v].add_fils(n, 0.0000001);
									n.add_fils(v[ind_dans_v], 0.0000001);
								} else {
									System.out.println("ERREUR : critère d'optimisation");
								}
							}											
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
				stmt3.close();
				stmt4.close();

				con.close();
			} 
		catch(Exception e){}
		return place;
	}


	/**
	 * Ajout des correspondances (d'une ligne différente)
	 * on part du noeud n, le vecteur v contient les noeud déjà présents dans le graphe
	 * place est la position du curseur dans v (pour remplir le vecteur correctement)
	 * centre_x, centre_y,rayons sont les coordonnées et le rayon du cerle qui limite les stations à un périmètre, permet un resultat rapide pour les petits trajets
	 * ctri_op est un entier qui décide de la méthode d'optimisation de la recherche du plus court chemin
	 */		

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
			ResultSet RScoress1 = stmt0.executeQuery("SELECT identifiant, ligne, sens1 FROM ratp_lignes WHERE identifiant =\""+n.get_ident()+"\" AND ligne <>\""+n.ligne+"\"");
			ResultSet RScoress2 = stmt1.executeQuery("SELECT ident2, type2, temps, reseau FROM Correspondances WHERE ident1 = \""+n.get_ident()+"\" AND (type2 <> \"bus\" OR reseau <> \"ratp\") AND (type2 <> \"bus\" OR reseau <> \"melange\") AND (reseau IN "+reseau_dispo+") AND ( type2 IN "+transport_dispo+")");
			ResultSet RScoress3 = stmt2.executeQuery("SELECT ident1, type1, temps, reseau FROM Correspondances WHERE ident2 = \""+n.get_ident()+"\" AND (type1 <> \"bus\" OR reseau <> \"ratp\") AND (reseau IN "+reseau_dispo+") AND ( type1 IN "+transport_dispo+")");
			int sens_corr = 1;

			//meme reseau
			while (RScoress1.next()) {
				if ((RScoress1.getString(3)).hashCode()=="-1".hashCode()) {
					sens_corr = 2;
				}
				Noeud Noe_corr = new Noeud(RScoress1.getString(1),RScoress1.getString(2),sens_corr,n.reseau);
				int ind_dans_v = Noe_corr.indice(v, Noe_corr.ligne, Noe_corr.sens);
				if (ind_dans_v == -1) {
					v[place] = Noe_corr;
					if (crit_op == 1) {
						v[place].add_fils(n, 1000.);
						n.add_fils(v[place], 1000.);
					} else if (crit_op == 2) {
						v[place].add_fils(n, 4.);
						n.add_fils(v[place], 4.);
					} else if (crit_op == 3) {
						v[place].add_fils(n, 0.0000001);
						n.add_fils(v[place], 0.0000001);
					} else {
						System.out.println("ERREUR : critère d'optimisation");
					}
					place++;
				} else if (!Noe_corr.est_dans(n.fils,Noe_corr.sens)) {
					if (crit_op == 1) {
						v[ind_dans_v].add_fils(n, 1000.);
						n.add_fils(v[ind_dans_v], 1000.);
					} else if (crit_op == 2) {
						v[ind_dans_v].add_fils(n, 4.);
						n.add_fils(v[ind_dans_v], 4.);
					} else if (crit_op == 3) {
						v[ind_dans_v].add_fils(n, 0.0000001);
						n.add_fils(v[ind_dans_v], 0.0000001);
					} else {
						System.out.println("ERREUR : critère d'optimisation");
					}
				}
				if (n.est_ligne_exception()){
					int ind_dans_v_nbis = n.indice(v, n.ligne,n.autre_sens());
					if (ind_dans_v_nbis!=-1){
						if (ind_dans_v == -1) {
							if (crit_op == 1) {
								v[place-1].add_fils(v[ind_dans_v_nbis], 1000.);
								v[ind_dans_v_nbis].add_fils(v[place-1], 1000.);
							} else if (crit_op == 2) {
								v[place-1].add_fils(v[ind_dans_v_nbis], 4.);
								v[ind_dans_v_nbis].add_fils(v[place-1], 4.);
							} else if (crit_op == 3) {
								v[place-1].add_fils(v[ind_dans_v_nbis], 0.0000001);
								v[ind_dans_v_nbis].add_fils(v[place-1], 0.0000001);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						} else if (!Noe_corr.est_dans(v[ind_dans_v_nbis].fils,Noe_corr.sens)) {
							if (crit_op == 1) {
								v[ind_dans_v].add_fils(v[ind_dans_v_nbis], 1000.);
								v[ind_dans_v_nbis].add_fils(v[ind_dans_v], 1000.);
							} else if (crit_op == 2) {
								v[ind_dans_v].add_fils(v[ind_dans_v_nbis], 4.);
								v[ind_dans_v_nbis].add_fils(v[ind_dans_v], 4.);
							} else if (crit_op == 3) {
								v[ind_dans_v].add_fils(v[ind_dans_v_nbis], 0.0000001);
								v[ind_dans_v_nbis].add_fils(v[ind_dans_v], 0.0000001);
							} else {
								System.out.println("ERREUR : critère d'optimisation");
							}
						}
					}
				}
			}

			//autre reseau 1
			while (RScoress2.next()) {
				String id = RScoress2.getString(1);
				String type = RScoress2.getString(2);
				String temps = RScoress2.getString(3);
				String reseau = RScoress2.getString(4);

				stmt3 = con.createStatement();

				if (reseau.hashCode()=="ratp".hashCode()) {
					ResultSet Noe_corress_RS = stmt3.executeQuery("SELECT ligne FROM ratp_lignes WHERE identifiant =\""+id+"\" AND type = \""+type+"\"");

					while (Noe_corress_RS.next()) {
						Noeud Noe_corr = new Noeud(id,Noe_corress_RS.getString(1),"ratp");
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
								" AND stops.station_parent = \""+id+"\"");
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

			//autre reseau 2
			while (RScoress3.next()) {
				String id = RScoress3.getString(1);
				String type = RScoress3.getString(2);
				String temps = RScoress3.getString(3);
				String reseau = RScoress3.getString(4);
				stmt4 = con.createStatement();

				if (reseau.hashCode()=="ratp".hashCode()) {
					ResultSet Noe_corress_RS = stmt4.executeQuery("SELECT ligne FROM ratp_lignes WHERE identifiant =\""+id+"\" AND type=\""+type+"\"");

					while (Noe_corress_RS.next()) {
						Noeud Noe_corr = new Noeud(id,Noe_corress_RS.getString(1),"ratp");
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
								" AND stops.station_parent = \""+id+"\"");
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
