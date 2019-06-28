package com.NumberFact;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class NumberServices {
	@Autowired
	private NumberRepository numRep;

	public List<Number> ListAllNumbers() {

		return numRep.findAll();
	}

	public List<Number> ListAllNumbersByID(String number) {
		// empty list where all the found foxes will be put
		List<Number> returnList = new ArrayList<Number>();
		// list of all foxes
		List<Number> foxList = ListAllNumbers();

		for (int i = 0; i < foxList.size(); i++) {

			if (foxList.get(i).getNumber().equals(number)) {

				returnList.add(foxList.get(i));
			}

		}

		return returnList;
	}

	public Number FindById(Long id) {

		return numRep.findById(id).get();
	}

	public void DeleteByID(Long id) {

		numRep.deleteById(id);
	}

	public void save(Number numb) {
		numRep.save(numb);
	}

	public void call_me(String id, Model model) throws Exception {
		String url = "http://numbersapi.com/" + id + "?json";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// print in String
		System.out.println(response.toString());
		// Read JSON response and print
		JSONObject myResponse = new JSONObject(response.toString());
		System.out.println("result after Reading JSON Response");
		Number number = new Number();
		number.setNumber(myResponse.getString("number"));
		number.setFound(myResponse.getString("found"));
		number.setText(myResponse.getString("text"));
		number.setType(myResponse.getString("type"));

		if (myResponse.getString("found") == "true") {

			save(number);
		}
		model.addAttribute("Number", number);

	}

	public boolean checkIfNumberStoryExists(Number numberToCheck) {
		boolean success=false;
		List<Number> ListToCheck = new ArrayList<Number>();
		ListToCheck = ListAllNumbers();
		for (int i = 0; i < ListToCheck.size(); i++) {

			if (ListToCheck.get(i).getText().equals(numberToCheck.getText())) {
				success=true;				
			}

		}

		return success;
	}

}
