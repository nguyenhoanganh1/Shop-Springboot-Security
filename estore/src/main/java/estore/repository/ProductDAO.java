package estore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductDAO extends JpaRepository<Product, Integer>{
	@Query("SELECT o FROM Product o WHERE o.category.id=?1")
	Page<Product> findByCategoryId(Integer cid, Pageable pageable);

	@Query("SELECT o FROM Product o "
			+ " WHERE o.name LIKE ?1 OR o.category.name LIKE ?1 OR o.category.nameVn LIKE ?1")
	Page<Product> findByKeywords(String keywords, Pageable pageable);

	@Query("SELECT o FROM Product o WHERE o.discount > 0")
	Page<Product> findByDiscount(Pageable pageable);

	@Query("SELECT o FROM Product o ORDER BY o.productDate DESC")
	Page<Product> findByLatest(Pageable pageable);
	
	@Query("SELECT o FROM Product o WHERE o.likeCount > 0 ORDER BY o.likeCount DESC")
	Page<Product> findByFavorite(Pageable pageable);
	
	@Query("SELECT o FROM Product o WHERE o.special=true")
	Page<Product> findBySpecial(Pageable pageable);

	@Query("SELECT o FROM Product o WHERE o.shares IS NOT EMPTY ORDER BY size(o.shares) DESC")
	Page<Product> findByShare(Pageable pageable);

	@Query("SELECT d.product.id "
			+ " FROM OrderDetail d "
			+ " GROUP BY d.product.id"
			+ " ORDER BY sum(d.unitPrice * d.quantity * (1 - d.discount)) DESC")
	Page<Integer> findByBestSellerIds(Pageable pageable);

	@Query("SELECT DISTINCT o.product FROM OrderDetail o "
			+ " WHERE o.order.account.username=?1")
	List<Product> findByUsername(String username);
}
