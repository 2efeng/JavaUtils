package com.hzf.utils.RESTfulUtil;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

    private long id;
    private String name;
    private int price;
    private Date productionDate;

    public Product() {
    }

    public Product(long id, String name, int price, Date productionDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productionDate = productionDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }


    @Override
    public String toString() {
        return "com.hzf.service.Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", productionDate=" + productionDate +
                '}';
    }
}
