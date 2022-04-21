package estore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import estore.repository.Product;
import estore.repository.service.ProductService;

@Controller
public class HomeController {
	@Autowired
	ProductService productService;
	
	@RequestMapping("/home/index")
	public String index(Model model) {
		List<Product> best = productService.findByBestSeller(PageRequest.of(0, 4));
		model.addAttribute("best", best);
		
		Pageable pageable = PageRequest.of(0, 4, Direction.DESC, "discount");
		List<Product> prom = productService.findByDiscount(pageable).getContent();
		model.addAttribute("prom", prom);
		
		return "home/index";
	}
	@RequestMapping("/home/about")
	public String about() {
		return "home/about";
	}
}
