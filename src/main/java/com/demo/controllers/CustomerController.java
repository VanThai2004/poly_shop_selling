package com.demo.controllers;

import com.demo.models.Account;
import com.demo.models.Order;
import com.demo.models.OrderDetail;
import com.demo.models.Product;
import com.demo.repositories.AccountRepository;
import com.demo.repositories.CategoryRepository;
import com.demo.repositories.OrderDetailRepository;
import com.demo.repositories.OrderRepository;
import com.demo.repositories.ProductRepository;
import com.demo.services.CartService;
import com.demo.services.CategoryService;
import com.demo.services.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class CustomerController {
	@Autowired
	HttpSession session;

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@Autowired
	CartService cartService;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	ProductRepository productRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	OrderRepository orderRepo;

	@Autowired
	OrderDetailRepository orderDetailRepo;

	@ModelAttribute("cart")
	CartService getCartService(){
		return cartService;
	}

	@Data @AllArgsConstructor
	public static class PriceRange{
		int id;
		String display;
	}

	List<PriceRange> priceRangeList = Arrays.asList(
		new PriceRange(1, "Dưới 10 triệu"),
		new PriceRange(2, "Từ 10-20 triệu"),
		new PriceRange(3, "Trên 20 triệu")
	);

	@RequestMapping("/")
	public String index(Model model) {

		model.addAttribute("priceRangeList", priceRangeList);
		model.addAttribute("categoryList", categoryRepo.findAll());
		model.addAttribute("productList", productRepo.findAll());

		return "customer/index";
	}

	@GetMapping("/detail/{id}")
	public String viewProduct(@PathVariable int id, Model model) {
		Product product = productRepo.getReferenceById(id);
		model.addAttribute("product", product);
		return "customer/detail";
	}

	@RequestMapping("/add-to-cart/{id}")
	public String addToCart(@PathVariable int id){
		cartService.add(id);
		return "redirect:/cart";
	}

	@RequestMapping("/remove-cart/{id}")
	public String removeCart(@PathVariable int id) {
		cartService.remove(id);
		if(cartService.getTotal() == 0){
			return "redirect:/";
		}
		return "redirect:/cart";
	}

	@RequestMapping("/update-cart/{id}")
	public String updateCart(@PathVariable int id, int quantity) {
		cartService.update(id, quantity);
		return "redirect:/cart";
	}

	@GetMapping("/cart")
	public String cart(){
		return "customer/cart";
	}

	@GetMapping("/checkout")
	public String confirm(){
		if(session.getAttribute("account") == null){
			return  "redirect:/login";
		}
		return "customer/checkout";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		return "customer/about";
	}

	@GetMapping("/product/search")
	public String search(@RequestParam("keyword") String keyword, @RequestParam("categoryId") String categoryId, @RequestParam("priceRangeId") Integer priceRangeId, Model model){
		int max_price = 999999999;
		int min_price = 0;
		if(priceRangeId == 1){
			max_price = 999999;
			min_price = 0;
		}else if(priceRangeId == 2){
			max_price = 20000000;
			min_price = 10000000;
		}else if(priceRangeId == 3){
			max_price = 999999999;
			min_price = 20000001;
		}
		if(keyword.isEmpty() && categoryId.isEmpty()){
			model.addAttribute("productList", productRepo.searchPrice(max_price,min_price));
			model.addAttribute("priceRangeList", priceRangeList);
			model.addAttribute("categoryList", categoryRepo.findAll());
		}else if(keyword.isEmpty() && !categoryId.isEmpty()){
			model.addAttribute("productList", productRepo.searchCategory(categoryId,max_price,min_price));
			model.addAttribute("priceRangeList", priceRangeList);
			model.addAttribute("categoryList", categoryRepo.findAll());
		}else if(!keyword.isEmpty() && categoryId.isEmpty()){
			model.addAttribute("productList", productRepo.searchKeyword(keyword,max_price,min_price));
			model.addAttribute("priceRangeList", priceRangeList);
			model.addAttribute("categoryList", categoryRepo.findAll());
		}else {
			model.addAttribute("productList", productRepo.search(keyword,categoryId,max_price,min_price));
			model.addAttribute("priceRangeList", priceRangeList);
			model.addAttribute("categoryList", categoryRepo.findAll());
		}
		return "customer/index";
	}

	@GetMapping("/login")
	public String login(){
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, Model model) {
		// TODO: Check if user/password exists in database

		if(accountRepo.getAccount(username,password) != null) {
			Account acc = new Account();
			acc.setUsername(username);
			session.setAttribute("account", acc);
			return "redirect:/";
		}
		model.addAttribute("message", "Tên đăng nhập/mật khẩu không đúng");
		return "login";
	}

	@PostMapping("/purchase")
	public String purchase(@RequestParam String address){
		System.out.println("items=" + cartService.getItems());
		Account acc = (Account) session.getAttribute("account");
		if(acc != null) {
			Order order = new Order();
			order.setAccount(acc);
			order.setAddress(address);
			orderRepo.save(order);

			//TODO: Save order

			for(OrderDetail item : cartService.getItems()){
				item.setOrder(order);
				// TODO: Save order detail
				orderDetailRepo.save(item);
			}
			// TODO :clear cart
		}
		cartService.clear();
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(){
		session.removeAttribute("username");
		return "redirect:/login";
	}
}
