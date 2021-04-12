package mynt.ian.product.repository;

import mynt.ian.product.payload.ProductEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntry, String> {

}
