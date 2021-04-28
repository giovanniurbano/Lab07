package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {
	
	private PowerOutageDAO podao;
	private List<PowerOutage> partenza;
	private List<PowerOutage> result;
	private int totCustomersOttimo;
	private float totOreGuasto;
	
	public Model() {
		podao = new PowerOutageDAO();
	}
	
	public List<Nerc> getNercList() {
		return podao.getNercList();
	}
	
	public int getTotCustomersOttimo() {
		return this.totCustomersOttimo;
	}
	public float getTotOreGuasto() {
		return this.totOreGuasto;
	}
	
	public List<PowerOutage> getSelectedPowerOutages(int nercId, int anni, int ore){
		partenza = podao.getPowerOutages(nercId, ore);
		List<PowerOutage> parziale = new ArrayList<PowerOutage>();
		result = new ArrayList<PowerOutage>();
		totCustomersOttimo = 0;
		
		cerca(parziale, 0, anni, ore);
		
		return result;
	}

	private void cerca(List<PowerOutage> parziale, int livello, int nAnni, int nOre) {
		//casi terminali
		float totaleOre = this.sommaOre(parziale);
		if(totaleOre > nOre)	//ho superato le ore richieste
			return;
		else { //controllo se la sequenza è ottima
			int totCustomers = this.sommaCustomers(parziale);
			if(totCustomers > totCustomersOttimo) {
				result = new ArrayList<PowerOutage>(parziale);
				totCustomersOttimo = totCustomers;
				totOreGuasto = totaleOre;
			}
		}
		
		if(livello == partenza.size()) //non ho più outages da aggiungere
			return;
		
		//genero sottoproblemi	
		parziale.add(partenza.get(livello)); //provo con il PowerOutage corrente
		if(this.differenzaAnniAccettabile(parziale, nAnni))	
			cerca(parziale, livello+1, nAnni, nOre);
		
		parziale.remove(partenza.get(livello)); //e senza
		cerca(parziale, livello+1, nAnni, nOre);
		
		
	}

	private boolean differenzaAnniAccettabile(List<PowerOutage> parziale, int nAnni) {
		if(parziale.size() == 0)
			return true;
		
		int max = 0;
		int min = 2200;

		for(PowerOutage po : parziale) {
			if(po.getDateEventBegan().getYear() < min)
				min = po.getDateEventBegan().getYear();
			if(po.getDateEventFinished().getYear() > max)
				max = po.getDateEventFinished().getYear();
		}
		
		if((max - min) <= nAnni)
			return true;
		
		return false;
	}

	private float sommaOre(List<PowerOutage> parziale) {
		float tot = 0;
		for(PowerOutage po : parziale){
			long f = po.getDateEventFinished().toEpochSecond(ZoneOffset.UTC);
			long b = po.getDateEventBegan().toEpochSecond(ZoneOffset.UTC);
			//long differenza = Duration.between(po.getDateEventBegan(), po.getDateEventFinished()).getSeconds();
			float diff = (float) (f - b);
			tot += (diff/3600);
		}
		
		return tot;
	}

	private int sommaCustomers(List<PowerOutage> parziale) {
		int tot = 0;
		for(PowerOutage po : parziale)
			tot += po.getCustomersAffected();
		
		return tot;
	}
	
}
