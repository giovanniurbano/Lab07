package it.polito.tdp.poweroutages.DAO;

import java.sql.Connection;

import it.polito.tdp.poweroutages.model.PowerOutage;

public class TestPowerOutagesDAO {

	public static void main(String[] args) {
		
		try {
			Connection connection = ConnectDB.getConnection();
			connection.close();
			System.out.println("Connection Test PASSED");
			
			PowerOutageDAO dao = new PowerOutageDAO() ;
			
			for(PowerOutage po : dao.getPowerOutages(14, 10))
				System.out.println(po.getId() + " " + po.getDateEventBegan().getHour() + " " + po.getDateEventFinished());

		} catch (Exception e) {
			System.err.println("Test FAILED " + e);
		}

	}

}
