package estore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import estore.repository.Product;
import estore.repository.Share;
import estore.repository.service.ProductService;
import estore.repository.service.ShareService;
import estore.service.mail.MailerService;

@Controller
public class ProductController {
	@Autowired
	ProductService productService;
	
	@RequestMapping("/product/category/{id}")
	public String category(Model model, @PathVariable("id") Integer id) {
		Page<Product> page = productService.findByCategoryId(id, Pageable.unpaged());
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/search")
	public String search(Model model, @RequestParam("keywords") String keywords) {
		Page<Product> page = productService.findByKeywords(keywords, Pageable.unpaged());
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/discount")
	public String discount(Model model) {
		Page<Product> page = productService.findByDiscount(Pageable.unpaged());
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/latest")
	public String latest(Model model) {
		Pageable pageable = PageRequest.of(0, 12);
		Page<Product> page = productService.findByLatest(pageable);
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/favorite")
	public String favorite(Model model) {
		Pageable pageable = PageRequest.of(0, 12);
		Page<Product> page = productService.findByFavorite(pageable);
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/special")
	public String special(Model model) {
		Pageable pageable = PageRequest.of(0, 12);
		Page<Product> page = productService.findBySpecial(pageable);
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/share")
	public String share(Model model) {
		Pageable pageable = PageRequest.of(0, 12);
		Page<Product> page = productService.findByShare(pageable);
		model.addAttribute("list", page.getContent());
		return "product/list";
	}
	
	@RequestMapping("/product/best")
	public String best(Model model) {
		Pageable pageable = PageRequest.of(0, 12);
		List<Product> list = productService.findByBestSeller(pageable);
		model.addAttribute("list", list);
		return "product/list";
	}
	
	@RequestMapping("/product/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id) {
		Product product = productService.getById(id);
		model.addAttribute("item", product);
		return "product/detail";
	}
	
	@ResponseBody
	@RequestMapping("/product/like/{id}")
	public Integer like(Model model, @PathVariable("id") Integer id) {
		Product product = productService.getById(id);
		product.setLikeCount(product.getLikeCount() + 1);
		productService.update(product);
		return product.getLikeCount();
	}
	
	@Autowired
	ShareService shareService;
	
	@Autowired
	MailerService mailerService;
	
	@ResponseBody
	@RequestMapping("/product/share-send")
	public void share(Model model, @RequestBody Share share) {
		shareService.create(share);
		mailerService.sendShare(share);
	}
}
