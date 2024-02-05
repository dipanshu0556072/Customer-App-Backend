package com.deepanshu.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.deepanshu.modal.Pincode;
import com.deepanshu.repository.PincodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.deepanshu.exception.ProductException;
import com.deepanshu.modal.Category;
import com.deepanshu.modal.Product;
import com.deepanshu.repository.CategoryRepository;
import com.deepanshu.repository.ProductRepository;
import com.deepanshu.request.CreateProductRequest;

@Service
public class ProductServiceImplementation implements ProductService {

	private ProductRepository productRepository;
	private UserService userService;
	private CategoryRepository categoryRepository;

	private PincodeRepository pincodeRepository;

	public ProductServiceImplementation(ProductRepository productRepository,UserService userService,CategoryRepository categoryRepository, PincodeRepository pincodeRepository) {
		this.productRepository=productRepository;
		this.userService=userService;
		this.categoryRepository=categoryRepository;
		this.pincodeRepository=pincodeRepository;
	}


	@Override
	public Product createProduct(CreateProductRequest req) {

		Category topLevel=categoryRepository.findByName(req.getTopLavelCategory());

		if(topLevel==null) {

			Category topLavelCategory=new Category();
			topLavelCategory.setName(req.getTopLavelCategory());
			topLavelCategory.setLevel(1);

			topLevel= categoryRepository.save(topLavelCategory);
		}

		Category secondLevel=categoryRepository.
				findByNameAndParant(req.getSecondLavelCategory(),topLevel.getName());
		if(secondLevel==null) {

			Category secondLavelCategory=new Category();
			secondLavelCategory.setName(req.getSecondLavelCategory());
			secondLavelCategory.setParentCategory(topLevel);
			secondLavelCategory.setLevel(2);

			secondLevel= categoryRepository.save(secondLavelCategory);
		}

		Category thirdLevel=categoryRepository.findByNameAndParant(req.getThirdLavelCategory(),secondLevel.getName());
		if(thirdLevel==null) {

			Category thirdLavelCategory=new Category();
			thirdLavelCategory.setName(req.getThirdLavelCategory());
			thirdLavelCategory.setParentCategory(secondLevel);
			thirdLavelCategory.setLevel(3);

			thirdLevel=categoryRepository.save(thirdLavelCategory);
		}

		Optional <Pincode> optionalPincode = pincodeRepository.findByPincode(req.getPincode());
		Pincode pincode;
		if (optionalPincode.isPresent()) {
			pincode = optionalPincode.get();
		}else{
			pincode = new Pincode();
			pincode.setPincode(req.getPincode());
			// Optionally, you might want to save the Pincode entity to the database
			pincode = pincodeRepository.save(pincode);
		}


		Product product=new Product();
		product.setTitle(req.getTitle());
		product.setColor(req.getColor());
		product.setDescription(req.getDescription());
		product.setDiscountedPrice(req.getDiscountedPrice());
		product.setDiscountPercent(req.getDiscountPercent());
		product.setImageUrl(req.getImageUrl());
		product.setBrand(req.getBrand());
		product.setPrice(req.getPrice());
		product.setSizes(req.getSize());
		product.setQuantity(req.getQuantity());
		product.setCategory(thirdLevel);
		product.setCountry(req.getCountry());
		product.setWearType(req.getWearType());
		product.setFabric(req.getFabric());
		product.setSleeves(req.getSleeves());
		product.setFit(req.getFit());
		product.setMaterialCare(req.getMaterialCare());
		product.setProductCode(req.getProductCode());
		product.setSeller(req.getSeller());
		product.setPincode(pincode);


		product.setCreatedAt(LocalDateTime.now());

		Product savedProduct= productRepository.save(product);

		System.out.println("products - "+product);

		return savedProduct;
	}

	@Override
	public String deleteProduct(Long productId) throws ProductException {

		Product product=findProductById(productId);

		System.out.println("delete product "+product.getId()+" - "+productId);
		product.getSizes().clear();
//		productRepository.save(product);
//		product.getCategory().
		productRepository.delete(product);

		return "Product deleted Successfully";
	}

	@Override
	public Product updateProduct(Long productId,Product req) throws ProductException {
		Product product=findProductById(productId);

		if(req.getQuantity()!=0) {
			product.setQuantity(req.getQuantity());
		}
		if(req.getDescription()!=null) {
			product.setDescription(req.getDescription());
		}




		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product findProductById(Long id) throws ProductException {
		Optional<Product> opt=productRepository.findById(id);

		if(opt.isPresent()) {
			return opt.get();
		}
		throw new ProductException("product not found with this id "+id);
	}

	@Override
	public List<Product> findProductByCategory(String category) {

		System.out.println("category --- "+category);

		List<Product> products = productRepository.findByCategory(category);

		return products;
	}

	@Override
	public List<Product> searchProduct(String query) {
		List<Product> products=productRepository.searchProduct(query);
		return products;
	}





	@Override
	public Page<Product> getAllProduct(String category, List<String>colors,
			List<String> sizes, Integer minPrice, Integer maxPrice,
			Integer minDiscount,String sort, String stock, Integer pageNumber, Integer pageSize,
									   String country,String wearType,String fabric,String sleeves,String fit,
									   String materialCare,String productCode,String seller) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		List<Product> products = productRepository.filterProducts(category, minPrice, maxPrice, minDiscount, sort);


		if (!colors.isEmpty()) {
			products = products.stream()
			        .filter(p -> colors.stream().anyMatch(c -> c.equalsIgnoreCase(p.getColor())))
			        .collect(Collectors.toList());


		}

		if(stock!=null) {

			if(stock.equals("in_stock")) {
				products=products.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList());
			}
			else if (stock.equals("out_of_stock")) {
				products=products.stream().filter(p->p.getQuantity()<1).collect(Collectors.toList());
			}


		}
		int startIndex = (int) pageable.getOffset();
		int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

		List<Product> pageContent = products.subList(startIndex, endIndex);
		Page<Product> filteredProducts = new PageImpl<>(pageContent, pageable, products.size());
	    return filteredProducts;


	}

	@Override
	public List<Product> findProductsByPincode(String pincode) {
		List<Product> products = productRepository.findByPincode(pincode);
		return products;
	}

}