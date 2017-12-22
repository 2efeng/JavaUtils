package com.hzf.utils.RESTfulUtil;

import net.sf.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

public class ProductServiceImpl implements ProductService {
    private static final List<Product> productList = new ArrayList<Product>();

    static {
        productList.add(new Product(1, "iphone 5s", 5000, new Date()));
        productList.add(new Product(2, "ipad mini", 2500, new Date()));
    }

    public List<Product> retrieveAllProducts() {
        productList.sort((product1, product2) -> (product2.getId() > product1.getId()) ? 1 : -1);
        return productList;
    }

    public Product retrieveProductById(long id) {
        Product targetProduct = null;
        for (Product product : productList) {
            if (product.getId() == id) {
                targetProduct = product;
                break;
            }
        }
        return targetProduct;
    }

    public List<Product> retrieveProductsByName(String name) {
        List<Product> targetProductList = new ArrayList<Product>();
        for (Product product : productList) {
            if (product.getName().contains(name)) {
                targetProductList.add(product);
            }
        }
        return targetProductList;
    }

    public List<Product> createProduct(String jsonStr,String id) throws ParseException {

        JSONObject obj = JSONObject.fromObject(jsonStr);

        Product product = new Product();
        product.setId(obj.optLong("id"));
        product.setName(obj.optString("name"));
        product.setPrice((obj.optInt("price")));

//        product.setProductionDate(date);
        productList.add(product);
        return productList;
    }

    public Product updateProductById(long id, Map<String, Object> fieldMap) {
        Product product = retrieveProductById(id);
        if (product != null) {
            try {
                for (Map.Entry<String, Object> fieldEntry : fieldMap.entrySet()) {
                    Field field = Product.class.getDeclaredField(fieldEntry.getKey());
                    field.setAccessible(true);
                    field.set(product, fieldEntry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return product;
    }

    public Product deleteProductById(long id) {
        Product targetProduct = null;
        Iterator<Product> productIterator = productList.iterator();
        while (productIterator.hasNext()) {
            Product product = productIterator.next();
            if (product.getId() == id) {
                targetProduct = retrieveProductById(id);
                productIterator.remove();
                break;
            }
        }
        return targetProduct;
    }
}
