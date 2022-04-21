package estore.repository.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import estore.repository.Product;
import estore.repository.ProductDAO;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	ProductDAO dao;

	@Override
	public Product getById(Integer id) {
		return dao.getById(id);
	}

	@Override
	public void create(Product item) {
		dao.save(item);
	}

	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	@Override
	public List<Product> findAll() {
		return dao.findAll();
	}

	@Override
	public void update(Product item) {
		dao.save(item);
	}

	@Override
	public Page<Product> findByCategoryId(Integer cid, Pageable pageable) {
		return dao.findByCategoryId(cid, pageable);
	}

	@Override
	public Page<Product> findByKeywords(String keywords, Pageable pageable) {
		return dao.findByKeywords("%"+keywords+"%", pageable);
	}

	@Override
	public Page<Product> findByDiscount(Pageable pageable) {
		return dao.findByDiscount(pageable);
	}

	@Override
	public Page<Product> findByLatest(Pageable pageable) {
		return dao.findByLatest(pageable);
	}
	
	@Override
	public Page<Product> findByFavorite(Pageable pageable) {
		return dao.findByFavorite(pageable);
	}
	
	@Override
	public Page<Product> findBySpecial(Pageable pageable) {
		return dao.findBySpecial(pageable);
	}

	@Override
	public Page<Product> findByShare(Pageable pageable) {
		return dao.findByShare(pageable);
	}
	
	@Override
	public List<Product> findByBestSeller(Pageable pageable) {
		Page<Integer> ids = dao.findByBestSellerIds(pageable);
		return dao.findAllById(ids.getContent());
	}

	@Override
	public List<Product> findByUsername(String username) {
		return dao.findByUsername(username);
	}
}
