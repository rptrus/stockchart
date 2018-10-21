package com.rohan.stockapp.json;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"stock",
"dateAdded",
"price"
})
public class StockAdd {

@JsonProperty("stock")
private String stock;
@JsonProperty("dateAdded")
private String dateAdded;
@JsonProperty("price")
private Float price; // change to BigDecimal later
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* No args constructor for use in serialization
* 
*/
public StockAdd() {
}

/**
* 
* @param price
* @param stock
* @param dateAdded
*/
public StockAdd(String stock, String dateAdded, Float price) {
super();
this.stock = stock;
this.dateAdded = dateAdded;
this.price = price;
}

@JsonProperty("stock")
public String getStock() {
return stock;
}

@JsonProperty("stock")
public void setStock(String stock) {
this.stock = stock;
}

public StockAdd withStock(String stock) {
this.stock = stock;
return this;
}

@JsonProperty("dateAdded")
public String getDateAdded() {
return dateAdded;
}

@JsonProperty("dateAdded")
public void setDateAdded(String dateAdded) {
this.dateAdded = dateAdded;
}

public StockAdd withDateAdded(String dateAdded) {
this.dateAdded = dateAdded;
return this;
}

@JsonProperty("price")
public Float getPrice() {
return price;
}

@JsonProperty("price")
public void setPrice(Float price) {
this.price = price;
}

public StockAdd withPrice(Float price) {
this.price = price;
return this;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

public StockAdd withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

@Override
public String toString() {
return new ToStringBuilder(this).append("stock", stock).append("dateAdded", dateAdded).append("price", price).append("additionalProperties", additionalProperties).toString();
}

@Override
public int hashCode() {
return new HashCodeBuilder().append(price).append(stock).append(additionalProperties).append(dateAdded).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof StockAdd) == false) {
return false;
}
StockAdd rhs = ((StockAdd) other);
return new EqualsBuilder().append(price, rhs.price).append(stock, rhs.stock).append(additionalProperties, rhs.additionalProperties).append(dateAdded, rhs.dateAdded).isEquals();
}

}