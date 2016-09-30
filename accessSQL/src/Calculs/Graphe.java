package Calculs;

public class Graphe {
	Noeud Depart;
	double temps;


	public Graphe(Noeud Depart) {
		this.Depart = Depart;
	}


	public int indice(Noeud n,Noeud[] v,String ligne,int sens) {
		int indice = -1;
		for (int i = 0; i < v.length ; i++) {
			if ((v[i] != null)) {
				if ((v[i].get_ident().hashCode() == n.get_ident().hashCode())&(v[i].ligne.hashCode() == ligne.hashCode())&(v[i].sens==sens)) {
					indice = i;
					return i;
				}
			}
		}
		return indice;
	}


	public int ind_min(double[] dist, int[] visite) {
		int result = 0;
		double d_min = 99999.;
		for (int i = 1; i < dist.length; i++) {
			if ((visite[i]==0)&(dist[i]*1.<d_min*1.)) {
				result = i;
				d_min = dist[i];
			}
		}
		return result;
	}


	public double get_temps() {
		return this.temps;
	}

	public String[][] dijkstra(String arrivee,int nb_noeud) {
		String resultString = "";
		Noeud[] f = new Noeud[nb_noeud];
		int[] precedent_indice = new int[nb_noeud];
		double[] dist = new double[nb_noeud];
		int[] vis = new int[nb_noeud];
		int place = 1;
		int num = 0;
		int nb_tours = 0;

		f[0] = this.Depart;
		dist[0]=0;
		vis[0]=1;

		for (int i=1; i<nb_noeud; i++) {
			dist[i]=999999.;
			vis[i]=0;
		}
		while ((f[num].get_ident().hashCode()!=arrivee.hashCode())&(nb_tours<nb_noeud)) {
			num = ind_min(dist, vis);
			Noeud N = f[num];
			vis[num] = 1;
			int l = N.get_nb_fils();
			//		System.out.println("nb_fils : "+l);


			for (int i = 0; i < l ; i++) {
				Noeud N_now = N.fils[i];
				//System.out.println(N_now.get_name());
				int where = indice(N_now,f,N_now.ligne,N_now.sens);

				if (where==-1) {
					f[place] = N_now;
					dist[place] = dist[num]+N.poids[i];
					precedent_indice[place]= num; //N.get_name();
					place++;				
				} else if((vis[where]==0)&(dist[where]*1.>(dist[num]*1.+N.poids[i]*1.))) {
					dist[where] = dist[num]+N.poids[i];
					precedent_indice[where]= num; //N.get_name();
				}
			}
			nb_tours++;
		}
		System.out.println("nombre de tours : " + nb_tours);
		System.out.println("nombre de noeud : " + nb_noeud);
		System.out.println("num final : " + num);
		System.out.println("place : " + place);
		int nN = num;//arrivee;
		Noeud Nn = f[num];
		resultString = f[nN].get_ident() + " " + resultString;
		double d = dist[num];
		//	int ii = indice(Nn,f);
		//	System.out.println("distance = " + dist[ii]);
		//	System.out.println("putain de nom : "+ Nn.get_name().hashCode());
		//	System.out.println(indice(Nn,f));
//			for (int i =0;i<Math.min(place,nb_tours);i++){
//				System.out.println("nom : " + f[i].get_name());
//				System.out.println("distance : " + dist[i]);
//				System.out.println("precedent : " + precedent_indice[i]);
//				System.out.println("visite : " + vis[i]);
//			}
		//	System.out.println("le nom est : " + Nn.get_name());
		
		
		String[][] result_temps = new String[4][nb_tours];
		
		if(Nn.get_ident().hashCode()!=arrivee.hashCode()) {
			result_temps[0][0] = "000";
			return result_temps;
		}
		int nb_station = 1;
		
		result_temps[0][0] = Nn.print_station();
		result_temps[1][0] = Nn.ligne;
		result_temps[2][0] = Nn.get_ident();
		
		while (f[nN].get_ident().hashCode() != this.Depart.get_ident().hashCode()) {
			//		System.out.println("id precedent : " + nN);
			//		int iii = indice(Nn,f);
			//		if (iii == 0) {
			//			break;
			//		}
			////		System.out.println("indice de l'id precedent : " + iii);
			//		nN = precedent_indice[iii];
			nN = precedent_indice[nN];
			System.out.println(Nn.print_station()+ " ligne " + Nn.ligne);
//			System.out.println(Nn.print_fils());
			Nn = f[nN];//Nn = f[indice(nN,f)];
			resultString = f[nN].get_ident() + " " + resultString;
			result_temps[0][nb_station] = Nn.print_station();
			result_temps[1][nb_station] = Nn.ligne;
			result_temps[2][nb_station] = Nn.get_ident();
			nb_station++;
		}
		
		String[][] result = new String[3][nb_station];
		
		result[0][0] = result_temps[0][nb_station-1];
		result[1][0] = result_temps[1][nb_station-1];
		result[2][0] = result_temps[2][nb_station-1];
		
		for (int i = 1;i<nb_station;i++) {
			String name = result_temps[0][nb_station-1-i];
			if (name.equals(result[0][i-1])) {
				result[0][i] = name+" ";
			} else {
				result[0][i] = name;
			}
			result[1][i] = result_temps[1][nb_station-1-i];
			result[2][i] = result_temps[2][nb_station-1-i];
		}
		
		System.out.println(Nn.print_station()+ " ligne " + Nn.ligne);
		double distance = d-dist[nN];
		this.temps = distance;
		System.out.println("distance estimée : "+distance);
		System.out.println("resultstring : "+resultString);
		System.out.println("nb-sta : "+nb_station);
		return result;
	}


}
