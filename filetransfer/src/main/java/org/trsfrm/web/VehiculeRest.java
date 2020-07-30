package org.trsfrm.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class VehiculeRest {

	@GetMapping("/car")
	public String getVehicule(){
		return "Audi";
	}

}
