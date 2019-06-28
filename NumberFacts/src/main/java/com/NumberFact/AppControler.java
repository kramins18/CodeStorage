package com.NumberFact;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppControler {
	@Autowired
	private NumberServices service;

	@RequestMapping("/")
	public String home() {
		return "redirect:index/1";
	}	
	@RequestMapping("/roadmap")
	
	public String roadmap() {
		return "roadmap";
	}


	@RequestMapping("/view_DataBase")
	public String viewHomePage(Model model) {
		System.out.println("esmu viev database");
		List<Number> Numberlist = service.ListAllNumbers();
		model.addAttribute("NumberList", Numberlist);
		return "view_DataBase";

	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public String deletePost(@PathVariable(name = "id") Long id) {
		System.out.println("esmu delete");
		
		service.DeleteByID(id);
		return "redirect:/view_DataBase";
	}

	@RequestMapping("/notFound")
	public String AddNewNumber(Model model) {
		Number number = new Number();
		model.addAttribute("number", number);
		return "notFound";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("number") Number numb) {
		service.save(numb);
		return "redirect:/";

	}

	@RequestMapping("/update/{id}")
	
	public ModelAndView ShowEditProductForm(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("AddNumber");
		Number numb = service.FindById(id);
		mav.addObject("number", numb);
		return mav;
		
	}

	
	@RequestMapping("/findbyid/{id}")
	public String findbyid(@PathVariable(name = "id") String id,Model model) {
		String toReturn="view_Database";
		List<Number> Numberlist = service.ListAllNumbersByID(id);
		model.addAttribute("NumberList", Numberlist);
			
		if (Numberlist.size()==0) {
			
			toReturn="redirect:/notFound";
		}
		return toReturn;
	}
	@RequestMapping("/findNumber")
	public String findNumber(Model model) {
		Number number = new Number();
		model.addAttribute("number", number);
		return "findNumber";
	}

	@RequestMapping("index/{id}")
	public  String call_me(@PathVariable(name = "id") String id,Model model) throws Exception {
		 String returnURL="index";
		 String url = "http://numbersapi.com/"+id+"?json";
	     URL obj = new URL(url);
	     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	     // optional default is GET
	     con.setRequestMethod("GET");
	     //add request header
	     con.setRequestProperty("User-Agent", "Mozilla/5.0");
	     int responseCode = con.getResponseCode();
	     System.out.println("\nSending 'GET' request to URL : " + url);
	     System.out.println("Response Code : " + responseCode);
	     BufferedReader in = new BufferedReader(
	             new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     StringBuffer response = new StringBuffer();
	     while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	     }
	     in.close();
	     //print in String
	     System.out.println(response.toString());
	     //Read JSON response and print
	     JSONObject myResponse = new JSONObject(response.toString());
	     System.out.println("result after Reading JSON Response");
	     Number number =new Number();
	     number.setNumber(myResponse.getString("number"));
	     number.setFound(myResponse.getString("found"));
	     number.setText(myResponse.getString("text"));
	     number.setType(myResponse.getString("type"));
	     
	     if(myResponse.getString("found")=="true") {
	    	 
	    	 if(service.checkIfNumberStoryExists(number)) {  		 
	    		 System.out.println("Ieraksts jau eksistee");	    	 
	     }
	     else {
	    	 service.save(number);
	     }
	    	
	    	
		}
	     else {
	    	 returnURL="redirect:/notFound";
	    	 
	     }
	     
	     model.addAttribute("Number", number);
	     
		return returnURL;
	}
	
	
}
